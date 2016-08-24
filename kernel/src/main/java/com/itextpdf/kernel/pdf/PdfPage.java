/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -952395541908379500L;
	private PdfResources resources = null;
    private int mcid = -1;
    private int structParents = -1;
    PdfPages parentPages;
    private List<PdfName> excludedKeys = new ArrayList<>(Arrays.asList(
            PdfName.Parent,
            PdfName.Annots,
            PdfName.StructParents,
            // This key contains reference to all articles, while this articles could reference to lots of pages.
            // See DEVSIX-191
            PdfName.B));

    /**
     * Automatically rotate new content if the page has a rotation ( is disabled by default )
     */
    private boolean ignorePageRotationForContent = false;
    /**
     * See {@link #isPageRotationInverseMatrixWritten()}.
     */
    private boolean pageRotationInverseMatrixWritten = false;

    protected PdfPage(PdfDictionary pdfObject) {
        super(pdfObject);
        setForbidRelease();
        ensureObjectIsAddedToDocument(pdfObject);
    }

    protected PdfPage(PdfDocument pdfDocument, PageSize pageSize) {
        this(new PdfDictionary().makeIndirect(pdfDocument));
        PdfStream contentStream = new PdfStream().makeIndirect(pdfDocument);
        getPdfObject().put(PdfName.Contents, contentStream);
        getPdfObject().put(PdfName.Type, PdfName.Page);
        getPdfObject().put(PdfName.MediaBox, new PdfArray(pageSize));
        getPdfObject().put(PdfName.TrimBox, new PdfArray(pageSize));
        if (pdfDocument.isTagged()) {
            structParents = (int) pdfDocument.getNextStructParentIndex();
            getPdfObject().put(PdfName.StructParents, new PdfNumber(structParents));
        }
    }

    protected PdfPage(PdfDocument pdfDocument) {
        this(pdfDocument, pdfDocument.getDefaultPageSize());
    }

    /**
     * Gets page size, defined by media box object. This method doesn't take page rotation into account.
     *
     * @return {@link Rectangle} that specify page size.
     */
    public Rectangle getPageSize() {
        return getMediaBox();
    }

    /**
     * Gets page size, considering page rotation.
     *
     * @return {@link Rectangle} that specify size of rotated page.
     */
    public Rectangle getPageSizeWithRotation() {
        PageSize rect = new PageSize(getPageSize());
        int rotation = getRotation();
        while (rotation > 0) {
            rect = rect.rotate();
            rotation -= 90;
        }
        return rect;
    }

    /**
     * Gets the number of degrees by which the page shall be rotated clockwise when displayed or printed.
     * Shall be a multiple of 90.
     *
     * @return {@code int} number of degrees. Default value: 0
     */
    public int getRotation() {
        PdfNumber rotate = getPdfObject().getAsNumber(PdfName.Rotate);

        if (rotate == null) {
            return 0;
        } else {
            int n = rotate.intValue();
            n %= 360;
            return n < 0 ? n + 360 : n;
        }
    }

    /**
     * Sets the page rotation.
     *
     * @param degAngle the {@code int}  number of degrees by which the page shall be rotated clockwise
     *                 when displayed or printed. Shall be a multiple of 90.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setRotation(int degAngle) {
        getPdfObject().put(PdfName.Rotate, new PdfNumber(degAngle));
        return this;
    }

    /**
     * Gets the content stream at specified 0-based index in the Contents object {@link PdfArray}.
     * The situation when Contents object is a {@link PdfStream} is treated like a one element array.
     *
     * @param index the {@code int} index of returned {@link PdfStream}.
     * @return {@link PdfStream} object at specified index.
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public PdfStream getContentStream(int index) {
        int count = getContentStreamCount();
        if (index >= count)
            throw new IndexOutOfBoundsException(MessageFormat.format("Index: {0}, Size: {1}", index, count));
        PdfObject contents = getPdfObject().get(PdfName.Contents);
        if (contents instanceof PdfStream)
            return (PdfStream) contents;
        else if (contents instanceof PdfArray) {
            PdfArray a = (PdfArray) contents;
            return (PdfStream) a.get(index);
        } else {
            return null;
        }
    }

    /**
     * Gets the size of Contents object {@link PdfArray}.
     * The situation when Contents object is a {@link PdfStream} is treated like a one element array.
     *
     * @return the {@code int} size of Contents object, or 1 if Contents object is a {@link PdfStream}.
     */
    public int getContentStreamCount() {
        PdfObject contents = getPdfObject().get(PdfName.Contents);
        if (contents instanceof PdfStream)
            return 1;
        else if (contents instanceof PdfArray) {
            return ((PdfArray) contents).size();
        } else {
            return 0;
        }
    }

    /**
     * Returns the Contents object if it is {@link PdfStream}, or first stream in the array if it is {@link PdfArray}.
     *
     * @return first {@link PdfStream} in Contents object, or {@code null} if Contents is empty.
     */
    public PdfStream getFirstContentStream() {
        if (getContentStreamCount() > 0)
            return getContentStream(0);
        return null;
    }

    /**
     * Returns the Contents object if it is {@link PdfStream}, or last stream in the array if it is {@link PdfArray}.
     *
     * @return first {@link PdfStream} in Contents object, or {@code null} if Contents is empty.
     */
    public PdfStream getLastContentStream() {
        int count = getContentStreamCount();
        if (count > 0)
            return getContentStream(count - 1);
        return null;
    }

    /**
     * Creates new {@link PdfStream} object and puts it at the beginning of Contents array
     * (if Contents object is {@link PdfStream} it will be replaced with one-element array).
     *
     * @return Created {@link PdfStream} object.
     */
    public PdfStream newContentStreamBefore() {
        return newContentStream(true);
    }

    /**
     * Creates new {@link PdfStream} object and puts it at the end of Contents array
     * (if Contents object is {@link PdfStream} it will be replaced with one-element array).
     *
     * @return Created {@link PdfStream} object.
     */
    public PdfStream newContentStreamAfter() {
        return newContentStream(false);
    }

    /**
     * Gets the {@link PdfResources} wrapper object for this page resources.
     * If page doesn't have resource object, then it will be inherited from page's parents.
     * If neither parents nor page has the resource object, then the new one is created and added to page dictionary.
     * <br/><br/>
     * NOTE: If you'll try to modify the inherited resources, then the new resources object will be created,
     * so you won't change the parent's resources.
     * This new object under the wrapper will be added to page dictionary on {@link PdfPage#flush()},
     * or you can add it manually with this line, if needed:<br/>
     * {@code getPdfObject().put(PdfName.Resources, getResources().getPdfObject());}
     *
     * @return {@link PdfResources} wrapper of the page.
     */
    public PdfResources getResources() {

        if (this.resources == null) {
            boolean readOnly = false;
            PdfDictionary resources = getPdfObject().getAsDictionary(PdfName.Resources);
            if (resources == null) {
                initParentPages();
                resources = (PdfDictionary) getParentValue(this.parentPages, PdfName.Resources);
                if (resources != null) {
                    readOnly = true;
                }
            }
            if (resources == null) {
                resources = new PdfDictionary();
                getPdfObject().put(PdfName.Resources, resources);
            }
            this.resources = new PdfResources(resources);
            this.resources.setReadOnly(readOnly);
        }
        return this.resources;
    }

    /**
     * Sets {@link PdfResources} object.
     *
     * @param pdfResources {@link PdfResources} to set.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setResources(PdfResources pdfResources) {
        getPdfObject().put(PdfName.Resources, pdfResources.getPdfObject());
        this.resources = pdfResources;
        return this;
    }


    /**
     * Sets the XMP Metadata.
     *
     * @param xmpMetadata the {@code byte[]} of XMP Metadata to set.
     * @throws IOException in case of writing error.
     */
    public void setXmpMetadata(byte[] xmpMetadata) throws IOException {
        PdfStream xmp = new PdfStream().makeIndirect(getDocument());
        xmp.getOutputStream().write(xmpMetadata);
        xmp.put(PdfName.Type, PdfName.Metadata);
        xmp.put(PdfName.Subtype, PdfName.XML);
        getPdfObject().put(PdfName.Metadata, xmp);
    }

    /**
     * Serializes XMP Metadata to byte array and sets it.
     *
     * @param xmpMeta the {@link XMPMeta} object to set.
     * @param serializeOptions the {@link SerializeOptions} used while serialization.
     * @throws XMPException in case of XMP Metadata serialization error.
     * @throws IOException in case of writing error.
     */
    public void setXmpMetadata(XMPMeta xmpMeta, SerializeOptions serializeOptions) throws XMPException, IOException {
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    /**
     * Serializes XMP Metadata to byte array and sets it. Uses padding equals to 2000.
     *
     * @param xmpMeta the {@link XMPMeta} object to set.
     * @throws XMPException in case of XMP Metadata serialization error.
     * @throws IOException in case of writing error.
     */
    public void setXmpMetadata(XMPMeta xmpMeta) throws XMPException, IOException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
    }

    /**
     * Gets the XMP Metadata object.
     *
     * @return {@link PdfStream} object, that represent XMP Metadata.
     * @throws XMPException
     */
    public PdfStream getXmpMetadata() throws XMPException {
        return getPdfObject().getAsStream(PdfName.Metadata);
    }

    /**
     * Copies page to the specified document.
     * <br/><br/>
     * NOTE: Works only for pages from the document opened in reading mode, otherwise an exception is thrown.
     *
     * @param toDocument a document to copy page to.
     * @return copied {@link PdfPage}.
     */
    public PdfPage copyTo(PdfDocument toDocument) {
        return copyTo(toDocument, null);
    }

    /**
     * Copies page to the specified document.
     * <br/><br/>
     * NOTE: Works only for pages from the document opened in reading mode, otherwise an exception is thrown.
     *
     * @param toDocument a document to copy page to.
     * @param copier     a copier which bears a specific copy logic. May be {@code null}
     * @return copied {@link PdfPage}.
     */
    public PdfPage copyTo(PdfDocument toDocument, IPdfPageExtraCopier copier) {
        PdfDictionary dictionary = getPdfObject().copyTo(toDocument, excludedKeys, true);
        PdfPage page = new PdfPage(dictionary);
        copyInheritedProperties(page, toDocument);
        for (PdfAnnotation annot : getAnnotations()) {
            if (annot.getSubtype().equals(PdfName.Link)) {
                getDocument().storeLinkAnnotation(page, (PdfLinkAnnotation) annot);
            } else if (annot.getSubtype().equals(PdfName.Widget)){
                page.addAnnotation(-1, PdfAnnotation.makeAnnotation(annot.getPdfObject().copyTo(toDocument, false)), false);
            } else {
                page.addAnnotation(-1, PdfAnnotation.makeAnnotation(annot.getPdfObject().copyTo(toDocument, true)), false);
            }
        }
        if (toDocument.isTagged()) {
            page.structParents = (int) toDocument.getNextStructParentIndex();
            page.getPdfObject().put(PdfName.StructParents, new PdfNumber(page.structParents));
        }

        if (copier != null) {
            copier.copy(this, page);
        } else {
            if (!toDocument.getWriter().isUserWarnedAboutAcroFormCopying && getDocument().getCatalog().getPdfObject().containsKey(PdfName.AcroForm)) {
                Logger logger = LoggerFactory.getLogger(PdfPage.class);
                logger.warn(LogMessageConstant.SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY);
                toDocument.getWriter().isUserWarnedAboutAcroFormCopying = true;
            }
        }

        return page;
    }


    /**
     * Copies page as FormXObject to the specified document.
     *
     * @param toDocument a document to copy to.
     * @return copied {@link PdfFormXObject} object.
     */
    public PdfFormXObject copyAsFormXObject(PdfDocument toDocument) throws IOException {
        PdfFormXObject xObject = new PdfFormXObject(getCropBox());
        List<PdfName> excludedKeys = new ArrayList<>(Arrays.asList(PdfName.MediaBox,
                PdfName.CropBox,
                PdfName.Contents));
        excludedKeys.addAll(this.excludedKeys);
        PdfDictionary dictionary = getPdfObject().copyTo(toDocument, excludedKeys, true);

        xObject.getPdfObject().getOutputStream().write(getContentBytes());
        xObject.getPdfObject().mergeDifferent(dictionary);

        return xObject;
    }

    /**
     * Gets the {@link PdfDocument} that owns that page, or {@code null} if such document isn't exist.
     *
     * @return {@link PdfDocument} that owns that page, or {@code null} if such document isn't exist.
     */
    public PdfDocument getDocument() {
        if (getPdfObject().getIndirectReference() != null)
            return getPdfObject().getIndirectReference().getDocument();
        return null;
    }

    /**
     * Flushes page and it's content stream.
     * <br>
     * <br>
     * If the page belongs to the document which is tagged, page flushing also triggers flushing of the tags,
     * which are considered to belong to the page. The logic that defines if the given tag (structure element) belongs
     * to the page is the following: if all the marked content references (dictionary or number references), that are the
     * descenders of the given structure element, belong to the current page - the tag is considered
     * to belong to the page. If tag has descenders from several pages - it is flushed, if all other pages except the
     * current one are flushed.
     */
    @Override
    public void flush() {
        flush(false);
    }

    /**
     * Flushes page and its content stream. If <code>flushContentStreams</code> is true, all content streams that are
     * rendered on this page (like FormXObjects, annotation appearance streams, patterns) and also all images associated
     * with this page will also be flushed.
     * <br>
     * For notes about tag structure flushing see {@link PdfPage#flush() PdfPage#flush() method}.
     * <br>
     * <br>
     * If <code>PdfADocument</code> is used, flushing will be applied only if <code>flushContentStreams</code> is true.
     *
     * @param flushContentStreams if true all content streams that are rendered on this page (like form xObjects,
     *                            annotation appearance streams, patterns) and also all images associated with this page
     *                            will be flushed.
     */
    public void flush(boolean flushContentStreams) {
        // TODO log warning in case of failed flush in pdfa document case
        if (isFlushed()) {
            return;
        }
        getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.END_PAGE, this));

        if (getDocument().isTagged() && !getDocument().getStructTreeRoot().isFlushed()) {
            tryFlushPageTags();
        }
        if (resources != null && resources.isModified() && !resources.isReadOnly()) {
            getPdfObject().put(PdfName.Resources, resources.getPdfObject());
        }
        if (flushContentStreams) {
            getDocument().checkIsoConformance(this, IsoKey.PAGE);
            flushContentStreams();
        }
        int contentStreamCount = getContentStreamCount();
        for (int i = 0; i < contentStreamCount; i++) {
            getContentStream(i).flush(false);
        }

        resources = null;

        super.flush();
    }

    public Rectangle getMediaBox() {
        initParentPages();
        PdfArray mediaBox = getPdfObject().getAsArray(PdfName.MediaBox);
        if (mediaBox == null) {
            mediaBox = (PdfArray) getParentValue(parentPages, PdfName.MediaBox);
        }
        if (mediaBox == null) {
            throw new PdfException(PdfException.CannotRetrieveMediaBoxAttribute);
        }
        if (mediaBox.size() != 4) {
            throw new PdfException(PdfException.WrongMediaBoxSize1).setMessageParams(mediaBox.size());
        }
        PdfNumber llx = mediaBox.getAsNumber(0);
        PdfNumber lly = mediaBox.getAsNumber(1);
        PdfNumber urx = mediaBox.getAsNumber(2);
        PdfNumber ury = mediaBox.getAsNumber(3);
        if (llx == null || lly == null || urx == null || ury == null) {
            throw new PdfException(PdfException.InvalidMediaBoxValue);
        }
        return new Rectangle(Math.min(llx.floatValue(), urx.floatValue()),
                Math.min(lly.floatValue(), ury.floatValue()),
                Math.abs(urx.floatValue() - llx.floatValue()),
                Math.abs(ury.floatValue() - lly.floatValue()));
    }

    public PdfPage setMediaBox(Rectangle rectangle) {
        getPdfObject().put(PdfName.MediaBox, new PdfArray(rectangle));
        return this;
    }


    public Rectangle getCropBox() {
        initParentPages();
        PdfArray cropBox = getPdfObject().getAsArray(PdfName.CropBox);
        if (cropBox == null) {
            cropBox = (PdfArray) getParentValue(parentPages, PdfName.CropBox);
            if (cropBox == null) {
                return getMediaBox();
            }
        }
        return cropBox.toRectangle();
    }

    public PdfPage setCropBox(Rectangle rectangle) {
        getPdfObject().put(PdfName.CropBox, new PdfArray(rectangle));
        return this;
    }

    public PdfPage setArtBox(Rectangle rectangle) {
        if (getPdfObject().getAsRectangle(PdfName.TrimBox) != null) {
            getPdfObject().remove(PdfName.TrimBox);
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.warn(LogMessageConstant.ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE);
        }
        getPdfObject().put(PdfName.ArtBox, new PdfArray(rectangle));
        return this;
    }

    public Rectangle getArtBox() {
        Rectangle artBox = getPdfObject().getAsRectangle(PdfName.ArtBox);
        return artBox == null ? getCropBox() : artBox;
    }

    public PdfPage setTrimBox(Rectangle rectangle) {
        if (getPdfObject().getAsRectangle(PdfName.ArtBox) != null) {
            getPdfObject().remove(PdfName.ArtBox);
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.warn(LogMessageConstant.ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE);
        }
        getPdfObject().put(PdfName.TrimBox, new PdfArray(rectangle));
        return this;
    }

    public Rectangle getTrimBox() {
        Rectangle trimBox = getPdfObject().getAsRectangle(PdfName.TrimBox);
        return trimBox == null ? getCropBox() : trimBox;
    }

    /**
     * Get decoded bytes for the whole page content.
     *
     * @return byte array.
     * @throws PdfException in case of any {@link IOException).
     */
    public byte[] getContentBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int streamCount = getContentStreamCount();
            byte[] streamBytes;
            for (int i = 0; i < streamCount; i++) {
                streamBytes = getStreamBytes(i);
                baos.write(streamBytes);
                if (0 != streamBytes.length && !Character.isWhitespace((char) streamBytes[streamBytes.length-1])) {
                    baos.write('\n');
                }
            }
            return baos.toByteArray();
        } catch (IOException ioe) {
            throw new PdfException(PdfException.CannotGetContentBytes, ioe, this);
        }
    }

    /**
     * Gets decoded bytes of a certain stream of a page content.
     *
     * @param index index of stream inside Content.
     * @return byte array.
     * @throws PdfException in case of any {@link IOException).
     */
    public byte[] getStreamBytes(int index) {
        return getContentStream(index).getBytes();
    }

    /**
     * Calculates and returns next available MCID reference.
     *
     * @return calculated MCID reference.
     * @throws PdfException in case of not tagged document.
     */
    public int getNextMcid() {
        if (!getDocument().isTagged()) {
            throw new PdfException(PdfException.MustBeATaggedDocument);
        }
        if (mcid == -1) {
            PdfStructTreeRoot structTreeRoot = getDocument().getStructTreeRoot();
            mcid = structTreeRoot.getNextMcidForPage(this);
        }
        return mcid++;
    }

    public Integer getStructParentIndex() {
        if (structParents == -1) {
            PdfNumber n = getPdfObject().getAsNumber(PdfName.StructParents);
            if (n != null) {
                structParents = n.intValue();
            } else {
                structParents = (int) getDocument().getNextStructParentIndex();
            }
        }
        return structParents;
    }

    public PdfPage setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    public List<PdfAnnotation> getAnnotations() {
        List<PdfAnnotation> annotations = new ArrayList<>();
        PdfArray annots = getPdfObject().getAsArray(PdfName.Annots);
        if (annots != null) {
            for (int i = 0; i < annots.size(); i++) {
                PdfDictionary annot = annots.getAsDictionary(i);
                annotations.add(PdfAnnotation.makeAnnotation(annot).setPage(this));
            }
        }
        return annotations;
    }

    public boolean containsAnnotation(PdfAnnotation annotation) {
        for (PdfAnnotation a : getAnnotations()) {
            if (a.getPdfObject().equals(annotation.getPdfObject())) {
                return true;
            }
        }
        return false;
    }

    public PdfPage addAnnotation(PdfAnnotation annotation) {
        return addAnnotation(-1, annotation, true);
    }

    public PdfPage addAnnotation(int index, PdfAnnotation annotation, boolean tagAnnotation) {
        if (getDocument().isTagged() && tagAnnotation) {
            TagTreePointer tagPointer = getDocument().getTagStructureContext().getAutoTaggingPointer();
            PdfPage prevPage = tagPointer.getCurrentPage(); // TODO what about if current tagging stream is set
            tagPointer.setPageForTagging(this).addAnnotationTag(annotation);
            if (prevPage != null) {
                tagPointer.setPageForTagging(prevPage);
            }
        }

        PdfArray annots = getAnnots(true);
        if (index == -1) {
            annots.add(annotation.setPage(this).getPdfObject());
        } else {
            annots.add(index, annotation.setPage(this).getPdfObject());
        }

        if (annots.getIndirectReference() == null) {
            setModified();
        }

        return this;
    }

    /**
     * Removes an annotation from the page.
     * <br><br>
     * NOTE: If document is tagged, PdfDocument's PdfTagStructure instance will point at annotation tag parent after method call.
     *
     * @param annotation an annotation to be removed.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage removeAnnotation(PdfAnnotation annotation) {
        PdfArray annots = getAnnots(false);
        if (annots != null) {
            if (annots.contains(annotation.getPdfObject())) {
                annots.remove(annotation.getPdfObject());
            } else {
                annots.remove(annotation.getPdfObject().getIndirectReference());
            }

            if (annots.isEmpty()) {
                getPdfObject().remove(PdfName.Annots);
            }
        }

        if (getDocument().isTagged()) {
            TagTreePointer tagPointer = getDocument().getTagStructureContext().removeAnnotationTag(annotation);
            if (tagPointer != null) {
                boolean standardAnnotTagRole = tagPointer.getRole().equals(PdfName.Annot)
                        || tagPointer.getRole().equals(PdfName.Form);
                if (tagPointer.getKidsRoles().size() == 0 && standardAnnotTagRole) {
                    tagPointer.removeTag();
                }
            }
        }
        return this;
    }

    public int getAnnotsSize() {
        PdfArray annots = getAnnots(false);
        if (annots == null)
            return 0;
        return annots.size();
    }

    /**
     * This method gets outlines of a current page
     *
     * @param updateOutlines
     * @return return all outlines of a current page
     * @throws PdfException
     */
    public List<PdfOutline> getOutlines(boolean updateOutlines) {
        getDocument().getOutlines(updateOutlines);
        return getDocument().getCatalog().getPagesWithOutlines().get(getPdfObject());
    }

    /**
     * @return true - if in case the page has a rotation, then new content will be automatically rotated in the
     * opposite direction. On the rotated page this would look like if new content ignores page rotation.
     */
    public boolean isIgnorePageRotationForContent() {
        return ignorePageRotationForContent;
    }

    /**
     * If true - defines that in case the page has a rotation, then new content will be automatically rotated in the
     * opposite direction. On the rotated page this would look like if new content ignores page rotation.
     * Default value - {@code false}.
     * @param ignorePageRotationForContent - true to ignore rotation of the new content on the rotated page.
     */
    public PdfPage setIgnorePageRotationForContent(boolean ignorePageRotationForContent) {
        this.ignorePageRotationForContent = ignorePageRotationForContent;
        return this;
    }

    /**
     * This method adds or replaces a page label.
     * @param numberingStyle The numbering style that shall be used for the numeric portion of each page label.
     *                       May be NULL
     * @param labelPrefix The label prefix for page labels in this range. May be NULL
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setPageLabel(PageLabelNumberingStyleConstants numberingStyle, String labelPrefix) {
        return setPageLabel(numberingStyle, labelPrefix, 1);
    }

    /**
     * This method adds or replaces a page label.
     * @param numberingStyle The numbering style that shall be used for the numeric portion of each page label.
     *                       May be NULL
     * @param labelPrefix The label prefix for page labels in this range. May be NULL
     * @param firstPage The value of the numeric portion for the first page label in the range. Must be greater or
     *                  equal 1.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setPageLabel(PageLabelNumberingStyleConstants numberingStyle, String labelPrefix, int firstPage) {
        if (firstPage < 1)
            throw new PdfException(PdfException.InAPageLabelThePageNumbersMustBeGreaterOrEqualTo1);
        PdfDictionary pageLabel = new PdfDictionary();
        if (numberingStyle != null) {
            switch (numberingStyle) {
                case DECIMAL_ARABIC_NUMERALS:
                    pageLabel.put(PdfName.S, PdfName.D);
                    break;
                case UPPERCASE_ROMAN_NUMERALS:
                    pageLabel.put(PdfName.S, PdfName.R);
                    break;
                case LOWERCASE_ROMAN_NUMERALS:
                    pageLabel.put(PdfName.S, PdfName.r);
                    break;
                case UPPERCASE_LETTERS:
                    pageLabel.put(PdfName.S, PdfName.A);
                    break;
                case LOWERCASE_LETTERS:
                    pageLabel.put(PdfName.S, PdfName.a);
                    break;
                default:
            }
        }
        if (labelPrefix != null) {
            pageLabel.put(PdfName.P, new PdfString(labelPrefix));
        }

        if (firstPage != 1) {
            pageLabel.put(PdfName.St, new PdfNumber(firstPage));
        }
        getDocument().getCatalog().getPageLabelsTree(true).addEntry(getDocument().getPageNumber(this) - 1, pageLabel);
        return this;
    }

    public PdfPage put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    /**
     * This flag is meaningful for the case, when page rotation is applied and ignorePageRotationForContent
     * is set to true. NOTE: It is needed for the internal usage.
     * <br/><br/>
     * This flag defines if inverse matrix (which rotates content into the opposite direction from page rotation
     * direction in order to give the impression of the not rotated text) is already applied to the page content stream.
     * See {@link #setIgnorePageRotationForContent(boolean)}
     * @return true, if inverse matrix is already applied, false otherwise.
     */
    public boolean isPageRotationInverseMatrixWritten() {
        return pageRotationInverseMatrixWritten;
    }

    /**
     * NOTE: For internal usage! Use this method only if you know what you are doing.
     * <br/><br/>
     * This method is called when inverse matrix (which rotates content into the opposite direction from page rotation
     * direction in order to give the impression of the not rotated text) is applied to the page content stream.
     * See {@link #setIgnorePageRotationForContent(boolean)}
     */
    public void setPageRotationInverseMatrixWritten() {
        // this method specifically return void to discourage it's unintended usage
        pageRotationInverseMatrixWritten = true;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private PdfArray getAnnots(boolean create) {
        PdfArray annots = getPdfObject().getAsArray(PdfName.Annots);
        if (annots == null && create) {
            annots = new PdfArray();
            put(PdfName.Annots, annots);
            setModified();
        }
        return annots;
    }

    private PdfObject getParentValue(PdfPages parentPages, PdfName pdfName) {
        if (parentPages != null) {
            PdfDictionary parentDictionary = parentPages.getPdfObject();
            PdfObject value = parentDictionary.get(pdfName);
            if (value != null) {
                return value;
            } else {
                return getParentValue(parentPages.getParent(), pdfName);
            }
        }
        return null;
    }

    private PdfStream newContentStream(boolean before) {
        PdfObject contents = getPdfObject().get(PdfName.Contents);
        PdfArray array;
        if (contents instanceof PdfStream) {
            array = new PdfArray();
            array.add(contents);
            getPdfObject().put(PdfName.Contents, array);
            setModified();
        } else if (contents instanceof PdfArray) {
            array = (PdfArray) contents;
        } else {
            throw new PdfException(PdfException.PdfPageShallHaveContent);
        }
        PdfStream contentStream = new PdfStream().makeIndirect(getDocument());
        if (before) {
            array.add(0, contentStream);
        } else {
            array.add(contentStream);
        }
        if (null != array.getIndirectReference()) {
            array.setModified();
        } else {
            setModified();
        }
        return contentStream;
    }

    private void tryFlushPageTags() {
        try {
            getDocument().getTagStructureContext().flushPageTags(this);
            getDocument().getStructTreeRoot().createParentTreeEntryForPage(this);
        } catch (Exception ex) {
            throw new PdfException(PdfException.TagStructureFlushingFailedItMightBeCorrupted, ex);
        }
    }

    private void flushContentStreams() {
        flushContentStreams(getResources().getPdfObject());

        PdfArray annots = getAnnots(false);
        if (annots != null) {
            for (int i = 0; i < annots.size(); ++i) {
                PdfDictionary apDict = annots.getAsDictionary(i).getAsDictionary(PdfName.AP);
                if (apDict != null) {
                    flushAppearanceStreams(apDict);
                }
            }
        }
    }

    private void flushContentStreams(PdfDictionary resources) {
        if (resources != null) {
            flushWithResources(resources.getAsDictionary(PdfName.XObject));
            flushWithResources(resources.getAsDictionary(PdfName.Pattern));
            flushWithResources(resources.getAsDictionary(PdfName.Shading));
        }
    }

    private void flushWithResources(PdfDictionary objsCollection) {
        if (objsCollection == null) {
            return;
        }

        for (PdfObject obj : objsCollection.directValues()) {
            if (obj.isFlushed())
                continue;
            flushContentStreams(((PdfDictionary) obj).getAsDictionary(PdfName.Resources));
            flushMustBeIndirectObject(obj);
        }
    }

    private void flushAppearanceStreams(PdfDictionary appearanceStreamsDict) {
        for (PdfObject val : appearanceStreamsDict.directValues()) {
            if (val instanceof PdfDictionary) {
                PdfDictionary ap = (PdfDictionary) val;
                if (ap.isDictionary()) {
                    flushAppearanceStreams(ap);
                } else if (ap.isStream()) {
                    flushMustBeIndirectObject(ap);
                }
            }
        }
    }

    private void flushMustBeIndirectObject(PdfObject obj) {
        // TODO DEVSIX-744
        obj.makeIndirect(getDocument()).flush();
    }

    /*
    * initialization <code>parentPages</code> if needed
    */
    private void initParentPages() {
        if (this.parentPages == null) {
            this.parentPages = getDocument().getCatalog().getPageTree().findPageParent(this);
        }
    }

    private void copyInheritedProperties(PdfPage copyPdfPage, PdfDocument pdfDocument) {
        if (copyPdfPage.getPdfObject().get(PdfName.Resources) == null) {
            PdfObject copyResource = pdfDocument.getWriter().copyObject(getResources().getPdfObject(), pdfDocument, false);
            copyPdfPage.getPdfObject().put(PdfName.Resources, copyResource);
        }
        if (copyPdfPage.getPdfObject().get(PdfName.MediaBox) == null) {
            copyPdfPage.setMediaBox(getMediaBox());
        }
        if (copyPdfPage.getPdfObject().get(PdfName.CropBox) == null) {
            initParentPages();
            PdfArray cropBox = (PdfArray) getParentValue(parentPages, PdfName.CropBox);
            if (cropBox != null) {
                copyPdfPage.setCropBox(cropBox.toRectangle());
            }
        }
    }
}

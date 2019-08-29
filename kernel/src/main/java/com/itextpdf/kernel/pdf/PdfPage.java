/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -952395541908379500L;
    private PdfResources resources = null;
    private int mcid = -1;
    PdfPages parentPages;
    private static final List<PdfName> PAGE_EXCLUDED_KEYS = new ArrayList<>(Arrays.asList(
            PdfName.Parent,
            PdfName.Annots,
            PdfName.StructParents,
            // This key contains reference to all articles, while this articles could reference to lots of pages.
            // See DEVSIX-191
            PdfName.B));

    private static final List<PdfName> XOBJECT_EXCLUDED_KEYS;

    static {
        XOBJECT_EXCLUDED_KEYS = new ArrayList<>(Arrays.asList(PdfName.MediaBox,
                PdfName.CropBox,
                PdfName.TrimBox,
                PdfName.Contents));
        XOBJECT_EXCLUDED_KEYS.addAll(PAGE_EXCLUDED_KEYS);
    }


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
        this((PdfDictionary) new PdfDictionary().makeIndirect(pdfDocument));
        PdfStream contentStream = (PdfStream) new PdfStream().makeIndirect(pdfDocument);
        getPdfObject().put(PdfName.Contents, contentStream);
        getPdfObject().put(PdfName.Type, PdfName.Page);
        getPdfObject().put(PdfName.MediaBox, new PdfArray(pageSize));
        getPdfObject().put(PdfName.TrimBox, new PdfArray(pageSize));
        if (pdfDocument.isTagged()) {
            setTabOrder(PdfName.S);
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
        int rotateValue = 0;
        if (rotate == null) {
            rotate = (PdfNumber) getInheritedValue(PdfName.Rotate, PdfObject.NUMBER);
        }
        if (rotate != null) {
            rotateValue = rotate.intValue();
        }
        rotateValue %= 360;
        return rotateValue < 0 ? rotateValue + 360 : rotateValue;
    }

    /**
     * Sets the page rotation.
     *
     * @param degAngle the {@code int}  number of degrees by which the page shall be rotated clockwise
     *                 when displayed or printed. Shall be a multiple of 90.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setRotation(int degAngle) {
        put(PdfName.Rotate, new PdfNumber(degAngle));
        return this;
    }

    /**
     * Gets the content stream at specified 0-based index in the Contents object {@link PdfArray}.
     * The situation when Contents object is a {@link PdfStream} is treated like a one element array.
     *
     * @param index the {@code int} index of returned {@link PdfStream}.
     * @return {@link PdfStream} object at specified index;
     * will return null in case page dictionary doesn't adhere to the specification, meaning that the document is an invalid PDF.
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public PdfStream getContentStream(int index) {
        int count = getContentStreamCount();
        if (index >= count || index < 0)
            throw new IndexOutOfBoundsException(MessageFormatUtil.format("Index: {0}, Size: {1}", index, count));
        PdfObject contents = getPdfObject().get(PdfName.Contents);
        if (contents instanceof PdfStream)
            return (PdfStream) contents;
        else if (contents instanceof PdfArray) {
            PdfArray a = (PdfArray) contents;
            return a.getAsStream(index);
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
     * <br><br>
     * NOTE: If you'll try to modify the inherited resources, then the new resources object will be created,
     * so you won't change the parent's resources.
     * This new object under the wrapper will be added to page dictionary on {@link PdfPage#flush()},
     * or you can add it manually with this line, if needed:<br>
     * {@code getPdfObject().put(PdfName.Resources, getResources().getPdfObject());}
     *
     * @return {@link PdfResources} wrapper of the page.
     */
    public PdfResources getResources() {
        return getResources(true);
    }

    PdfResources getResources(boolean initResourcesField) {
        if (this.resources == null && initResourcesField) {
            initResources(true);
        }
        return this.resources;
    }

    PdfDictionary initResources(boolean initResourcesField) {
        boolean readOnly = false;
        PdfDictionary resources = getPdfObject().getAsDictionary(PdfName.Resources);
        if (resources == null) {
            resources = (PdfDictionary) getInheritedValue(PdfName.Resources, PdfObject.DICTIONARY);
            if (resources != null) {
                readOnly = true;
            }
        }
        if (resources == null) {
            resources = new PdfDictionary();
            getPdfObject().put(PdfName.Resources, resources); // not marking page as modified because of this change
        }
        if (initResourcesField) {
            this.resources = new PdfResources(resources);
            this.resources.setReadOnly(readOnly);
        }
        return resources;
    }

    /**
     * Sets {@link PdfResources} object.
     *
     * @param pdfResources {@link PdfResources} to set.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setResources(PdfResources pdfResources) {
        put(PdfName.Resources, pdfResources.getPdfObject());
        this.resources = pdfResources;
        return this;
    }

    /**
     * Sets the XMP Metadata.
     *
     * @param xmpMetadata the {@code byte[]} of XMP Metadata to set.
     * @return this {@link PdfPage} instance.
     * @throws IOException in case of writing error.
     */
    public PdfPage setXmpMetadata(byte[] xmpMetadata) throws IOException {
        PdfStream xmp = (PdfStream) new PdfStream().makeIndirect(getDocument());
        xmp.getOutputStream().write(xmpMetadata);
        xmp.put(PdfName.Type, PdfName.Metadata);
        xmp.put(PdfName.Subtype, PdfName.XML);
        put(PdfName.Metadata, xmp);
        return this;
    }

    /**
     * Serializes XMP Metadata to byte array and sets it.
     *
     * @param xmpMeta          the {@link XMPMeta} object to set.
     * @param serializeOptions the {@link SerializeOptions} used while serialization.
     * @return this {@link PdfPage} instance.
     * @throws XMPException in case of XMP Metadata serialization error.
     * @throws IOException  in case of writing error.
     */
    public PdfPage setXmpMetadata(XMPMeta xmpMeta, SerializeOptions serializeOptions) throws XMPException, IOException {
        return setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    /**
     * Serializes XMP Metadata to byte array and sets it. Uses padding equals to 2000.
     *
     * @param xmpMeta the {@link XMPMeta} object to set.
     * @return this {@link PdfPage} instance.
     * @throws XMPException in case of XMP Metadata serialization error.
     * @throws IOException  in case of writing error.
     */
    public PdfPage setXmpMetadata(XMPMeta xmpMeta) throws XMPException, IOException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        return setXmpMetadata(xmpMeta, serializeOptions);
    }

    /**
     * Gets the XMP Metadata object.
     *
     * @return {@link PdfStream} object, that represent XMP Metadata.
     */
    public PdfStream getXmpMetadata() {
        return getPdfObject().getAsStream(PdfName.Metadata);
    }

    /**
     * Copies page to the specified document.
     * <br><br>
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
     * <br><br>
     * NOTE: Works only for pages from the document opened in reading mode, otherwise an exception is thrown.
     *
     * @param toDocument a document to copy page to.
     * @param copier     a copier which bears a special copy logic. May be null.
     *                   It is recommended to use the same instance of {@link IPdfPageExtraCopier}
     *                   for the same output document.
     * @return copied {@link PdfPage}.
     */
    public PdfPage copyTo(PdfDocument toDocument, IPdfPageExtraCopier copier) {
        PdfDictionary dictionary = getPdfObject().copyTo(toDocument, PAGE_EXCLUDED_KEYS, true);
        PdfPage page = new PdfPage(dictionary);
        copyInheritedProperties(page, toDocument);
        for (PdfAnnotation annot : getAnnotations()) {
            if (annot.getSubtype().equals(PdfName.Link)) {
                getDocument().storeLinkAnnotation(page, (PdfLinkAnnotation) annot);
            } else {
                PdfAnnotation newAnnot = PdfAnnotation.makeAnnotation(
                        annot.getPdfObject().copyTo(toDocument, Arrays.asList(PdfName.P, PdfName.Parent), true)
                );
                if (PdfName.Widget.equals(annot.getSubtype())) {
                    rebuildFormFieldParent(annot.getPdfObject(), newAnnot.getPdfObject(), toDocument);
                }

                // P will be set in PdfPage#addAnnotation; Parent will be regenerated in PdfPageExtraCopier.
                page.addAnnotation(-1, newAnnot, false);
            }
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

        for (PdfName key : getPdfObject().keySet()) {
            if (XOBJECT_EXCLUDED_KEYS.contains(key)) {
                continue;
            }
            PdfObject obj = getPdfObject().get(key);
            if (!xObject.getPdfObject().containsKey(key)) {
                PdfObject copyObj = obj.copyTo(toDocument, false);
                xObject.getPdfObject().put(key, copyObj);
            }
        }
        xObject.getPdfObject().getOutputStream().write(getContentBytes());
        //Copy inherited resources
        if (!xObject.getPdfObject().containsKey(PdfName.Resources)) {
            PdfObject copyResource = getResources().getPdfObject().copyTo(toDocument, true);
            xObject.getPdfObject().put(PdfName.Resources, copyResource);
        }

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
     * Flushes page dictionary, its content streams, annotations and thumb image.
     * <p>
     * If the page belongs to the document which is tagged, page flushing also triggers flushing of the tags,
     * which are considered to belong to the page. The logic that defines if the given tag (structure element) belongs
     * to the page is the following: if all the marked content references (dictionary or number references), that are the
     * descendants of the given structure element, belong to the current page - the tag is considered
     * to belong to the page. If tag has descendants from several pages - it is flushed, if all other pages except the
     * current one are flushed.
     */
    @Override
    public void flush() {
        flush(false);
    }

    /**
     * Flushes page dictionary, its content streams, annotations and thumb image. If <code>flushResourcesContentStreams</code> is true,
     * all content streams that are rendered on this page (like FormXObjects, annotation appearance streams, patterns)
     * and also all images associated with this page will also be flushed.
     * <p>
     * For notes about tag structure flushing see {@link PdfPage#flush() PdfPage#flush() method}.
     * <p>
     * If <code>PdfADocument</code> is used, flushing will be applied only if <code>flushResourcesContentStreams</code> is true.
     * <p>
     * Be careful with handling document in which some of the pages are flushed. Keep in mind that flushed objects are
     * finalized and are completely written to the output stream. This frees their memory but makes
     * it impossible to modify or read data from them. Whenever there is an attempt to modify or to fetch
     * flushed object inner contents an exception will be thrown. Flushing is only possible for objects in the writing
     * and stamping modes, also its possible to flush modified objects in append mode.
     *
     * @param flushResourcesContentStreams if true all content streams that are rendered on this page (like form xObjects,
     *                                     annotation appearance streams, patterns) and also all images associated with this page
     *                                     will be flushed.
     */
    public void flush(boolean flushResourcesContentStreams) {
        // TODO log warning in case of failed flush in pdfa document case
        if (isFlushed()) {
            return;
        }
        getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.END_PAGE, this));

        if (getDocument().isTagged() && !getDocument().getStructTreeRoot().isFlushed()) {
            tryFlushPageTags();
        }

        if (resources == null) {
            // ensure that either resources are inherited or add empty resources dictionary
            initResources(false);
        } else if (resources.isModified() && !resources.isReadOnly()) {
            put(PdfName.Resources, resources.getPdfObject());
        }
        if (flushResourcesContentStreams) {
            getDocument().checkIsoConformance(this, IsoKey.PAGE);
            flushResourcesContentStreams();
        }

        PdfArray annots = getAnnots(false);
        if (annots != null && !annots.isFlushed()) {
            for (int i = 0; i < annots.size(); ++i) {
                PdfObject a = annots.get(i);
                if (a != null) {
                    a.makeIndirect(getDocument()).flush();
                }
            }
        }

        PdfStream thumb = getPdfObject().getAsStream(PdfName.Thumb);
        if (thumb != null) {
            thumb.flush();
        }

        PdfObject contentsObj = getPdfObject().get(PdfName.Contents);
        // avoid trying to operate with flushed /Contents array
        if (contentsObj != null && !contentsObj.isFlushed()) {
            int contentStreamCount = getContentStreamCount();
            for (int i = 0; i < contentStreamCount; i++) {
                PdfStream contentStream = getContentStream(i);
                if (contentStream != null) {
                    contentStream.flush(false);
                }
            }
        }
        releaseInstanceFields();

        super.flush();
    }

    /**
     * Gets {@link Rectangle} object specified by page's Media Box, that defines the boundaries of the physical medium
     * on which the page shall be displayed or printed
     *
     * @return {@link Rectangle} object specified by page Media Box, expressed in default user space units.
     * @throws PdfException in case of any error while reading MediaBox object.
     */
    public Rectangle getMediaBox() {
        PdfArray mediaBox = getPdfObject().getAsArray(PdfName.MediaBox);
        if (mediaBox == null) {
            mediaBox = (PdfArray) getInheritedValue(PdfName.MediaBox, PdfObject.ARRAY);
        }
        if (mediaBox == null) {
            throw new PdfException(PdfException.CannotRetrieveMediaBoxAttribute);
        }
        int mediaBoxSize;
        if ((mediaBoxSize = mediaBox.size()) != 4) {
            if (mediaBoxSize > 4) {
                Logger logger = LoggerFactory.getLogger(PdfPage.class);
                if (logger.isErrorEnabled()) {
                    logger.error(MessageFormatUtil.format(LogMessageConstant.WRONG_MEDIABOX_SIZE_TOO_MANY_ARGUMENTS, mediaBoxSize));

                }
            }
            if (mediaBoxSize < 4) {
                throw new PdfException(PdfException.WRONGMEDIABOXSIZETOOFEWARGUMENTS).setMessageParams(mediaBox.size());
            }
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

    /**
     * Sets the Media Box object, that defines the boundaries of the physical medium
     * on which the page shall be displayed or printed.
     *
     * @param rectangle the {@link Rectangle} object to set, expressed in default user space units.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setMediaBox(Rectangle rectangle) {
        put(PdfName.MediaBox, new PdfArray(rectangle));
        return this;
    }

    /**
     * Gets the {@link Rectangle} specified by page's CropBox, that defines the visible region of default user space.
     * When the page is displayed or printed, its contents shall be clipped (cropped) to this rectangle
     * and then shall be imposed on the output medium in some implementation-defined manner.
     *
     * @return the {@link Rectangle} object specified by pages's CropBox, expressed in default user space units.
     * MediaBox by default.
     */
    public Rectangle getCropBox() {
        PdfArray cropBox = getPdfObject().getAsArray(PdfName.CropBox);
        if (cropBox == null) {
            cropBox = (PdfArray) getInheritedValue(PdfName.CropBox, PdfObject.ARRAY);
            if (cropBox == null) {
                return getMediaBox();
            }
        }
        return cropBox.toRectangle();
    }

    /**
     * Sets the CropBox object, that defines the visible region of default user space.
     * When the page is displayed or printed, its contents shall be clipped (cropped) to this rectangle
     * and then shall be imposed on the output medium in some implementation-defined manner.
     *
     * @param rectangle the {@link Rectangle} object to set, expressed in default user space units.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setCropBox(Rectangle rectangle) {
        put(PdfName.CropBox, new PdfArray(rectangle));
        return this;
    }

    /**
     * Sets the BleedBox object, that defines the region to which the contents of the page shall be clipped
     * when output in a production environment.
     *
     * @param rectangle the {@link Rectangle} object to set, expressed in default user space units.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setBleedBox(Rectangle rectangle) {
        put(PdfName.BleedBox, new PdfArray(rectangle));
        return this;
    }

    /**
     * Gets the {@link Rectangle} object specified by page's BleedBox, that define the region to which the
     * contents of the page shall be clipped when output in a production environment.
     *
     * @return the {@link Rectangle} object specified by page's BleedBox, expressed in default user space units.
     * CropBox by default.
     */
    public Rectangle getBleedBox() {
        Rectangle bleedBox = getPdfObject().getAsRectangle(PdfName.BleedBox);
        return bleedBox == null ? getCropBox() : bleedBox;
    }

    /**
     * Sets the ArtBox object, that define the extent of the page’s meaningful content
     * (including potential white space) as intended by the page’s creator.
     *
     * @param rectangle the {@link Rectangle} object to set, expressed in default user space units.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setArtBox(Rectangle rectangle) {
        if (getPdfObject().getAsRectangle(PdfName.TrimBox) != null) {
            getPdfObject().remove(PdfName.TrimBox);
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.warn(LogMessageConstant.ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE);
        }
        put(PdfName.ArtBox, new PdfArray(rectangle));
        return this;
    }

    /**
     * Gets the {@link Rectangle} object specified by page's ArtBox, that define the extent of the page’s
     * meaningful content (including potential white space) as intended by the page’s creator.
     *
     * @return the {@link Rectangle} object specified by page's ArtBox, expressed in default user space units.
     * CropBox by default.
     */
    public Rectangle getArtBox() {
        Rectangle artBox = getPdfObject().getAsRectangle(PdfName.ArtBox);
        return artBox == null ? getCropBox() : artBox;
    }

    /**
     * Sets the TrimBox object, that define the intended dimensions of the finished page after trimming.
     *
     * @param rectangle the {@link Rectangle} object to set, expressed in default user space units.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setTrimBox(Rectangle rectangle) {
        if (getPdfObject().getAsRectangle(PdfName.ArtBox) != null) {
            getPdfObject().remove(PdfName.ArtBox);
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.warn(LogMessageConstant.ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE);
        }
        put(PdfName.TrimBox, new PdfArray(rectangle));
        return this;
    }

    /**
     * Gets the {@link Rectangle} object specified by page's TrimBox object,
     * that define the intended dimensions of the finished page after trimming.
     *
     * @return the {@link Rectangle} object specified by page's TrimBox, expressed in default user space units.
     * CropBox by default.
     */
    public Rectangle getTrimBox() {
        Rectangle trimBox = getPdfObject().getAsRectangle(PdfName.TrimBox);
        return trimBox == null ? getCropBox() : trimBox;
    }

    /**
     * Get decoded bytes for the whole page content.
     *
     * @return byte array.
     * @throws PdfException in case of any {@link IOException}.
     */
    public byte[] getContentBytes() {
        try {
            MemoryLimitsAwareHandler handler = getDocument().memoryLimitsAwareHandler;
            long usedMemory = null == handler ? -1 : handler.getAllMemoryUsedForDecompression();

            MemoryLimitsAwareOutputStream baos = new MemoryLimitsAwareOutputStream();
            int streamCount = getContentStreamCount();
            byte[] streamBytes;
            for (int i = 0; i < streamCount; i++) {
                streamBytes = getStreamBytes(i);
                // usedMemory has changed, that means that some of currently processed pdf streams are suspicious
                if (null != handler && usedMemory < handler.getAllMemoryUsedForDecompression()) {
                    baos.setMaxStreamSize(handler.getMaxSizeOfSingleDecompressedPdfStream());
                }
                baos.write(streamBytes);
                if (0 != streamBytes.length && !Character.isWhitespace((char) streamBytes[streamBytes.length - 1])) {
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
     * @throws PdfException in case of any {@link IOException}.
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

    /**
     * Gets the key of the page’s entry in the structural parent tree.
     *
     * @return the key of the page’s entry in the structural parent tree.
     * If page has no entry in the structural parent tree, returned value is -1.
     */
    public int getStructParentIndex() {
        return getPdfObject().getAsNumber(PdfName.StructParents) != null ? getPdfObject().getAsNumber(PdfName.StructParents).intValue() : -1;
    }

    /**
     * Helper method to add an additional action to this page.
     * May be used in chain.
     *
     * @param key    a {@link PdfName} specifying the name of an additional action
     * @param action the {@link PdfAction} to add as an additional action
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    /**
     * Gets array of annotation dictionaries that shall contain indirect references
     * to all annotations associated with the page.
     *
     * @return the {@link List}&lt;{@link PdfAnnotation}&gt; containing all page's annotations.
     */
    public List<PdfAnnotation> getAnnotations() {
        List<PdfAnnotation> annotations = new ArrayList<>();
        PdfArray annots = getPdfObject().getAsArray(PdfName.Annots);
        if (annots != null) {
            for (int i = 0; i < annots.size(); i++) {
                PdfDictionary annot = annots.getAsDictionary(i);
                if (annot == null) {
                    continue;
                }
                PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annot);
                if (annotation == null) {
                    continue;
                }
                boolean hasBeenNotModified = annot.getIndirectReference() != null && !annot.getIndirectReference().checkState(PdfObject.MODIFIED);
                annotations.add(annotation.setPage(this));
                if (hasBeenNotModified) {
                    annot.getIndirectReference().clearState(PdfObject.MODIFIED);
                    annot.clearState(PdfObject.FORBID_RELEASE);
                }
            }
        }
        return annotations;
    }

    /**
     * Checks if page contains the specified annotation.
     *
     * @param annotation the {@link PdfAnnotation} to check.
     * @return {@code true} if page contains specified annotation and {@code false} otherwise.
     */
    public boolean containsAnnotation(PdfAnnotation annotation) {
        for (PdfAnnotation a : getAnnotations()) {
            if (a.getPdfObject().equals(annotation.getPdfObject())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds specified annotation to the end of annotations array and tagged it.
     * May be used in chain.
     *
     * @param annotation the {@link PdfAnnotation} to add.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage addAnnotation(PdfAnnotation annotation) {
        return addAnnotation(-1, annotation, true);
    }

    /**
     * Adds specified {@link PdfAnnotation} to specified index in annotations array with or without autotagging.
     * May be used in chain.
     *
     * @param index         the index at which specified annotation will be added. If {@code -1} then annotation will be added
     *                      to the end of array.
     * @param annotation    the {@link PdfAnnotation} to add.
     * @param tagAnnotation if {@code true} the added annotation will be autotagged. <p>
     *                      (see {@link com.itextpdf.kernel.pdf.tagutils.TagStructureContext#getAutoTaggingPointer()})
     * @return this {@link PdfPage} instance.
     */
    public PdfPage addAnnotation(int index, PdfAnnotation annotation, boolean tagAnnotation) {
        if (getDocument().isTagged()) {
            if (tagAnnotation) {
                TagTreePointer tagPointer = getDocument().getTagStructureContext().getAutoTaggingPointer();
                PdfPage prevPage = tagPointer.getCurrentPage();
                tagPointer.setPageForTagging(this).addAnnotationTag(annotation);
                if (prevPage != null) {
                    tagPointer.setPageForTagging(prevPage);
                }
            }
            if (getTabOrder() == null) {
                setTabOrder(PdfName.S);
            }
        }

        PdfArray annots = getAnnots(true);
        if (index == -1) {
            annots.add(annotation.setPage(this).getPdfObject());
        } else {
            annots.add(index, annotation.setPage(this).getPdfObject());
        }

        if (annots.getIndirectReference() == null) {
            //Annots are not indirect so page needs to be marked as modified
            setModified();
        } else {
            //Annots are indirect so need to be marked as modified
            annots.setModified();
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
            annots.remove(annotation.getPdfObject());

            if (annots.isEmpty()) {
                getPdfObject().remove(PdfName.Annots);
                setModified();
            } else if (annots.getIndirectReference() == null) {
                setModified();
            }
        }

        if (getDocument().isTagged()) {
            TagTreePointer tagPointer = getDocument().getTagStructureContext().removeAnnotationTag(annotation);
            if (tagPointer != null) {
                boolean standardAnnotTagRole = tagPointer.getRole().equals(StandardRoles.ANNOT)
                        || tagPointer.getRole().equals(StandardRoles.FORM);
                if (tagPointer.getKidsRoles().size() == 0 && standardAnnotTagRole) {
                    tagPointer.removeTag();
                }
            }
        }
        return this;
    }

    /**
     * Gets the number of {@link PdfAnnotation} associated with this page.
     *
     * @return the {@code int} number of {@link PdfAnnotation} associated with this page.
     */
    public int getAnnotsSize() {
        PdfArray annots = getAnnots(false);
        if (annots == null)
            return 0;
        return annots.size();
    }

    /**
     * This method gets outlines of a current page
     *
     * @return return all outlines of a current page
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
     *
     * @param ignorePageRotationForContent - true to ignore rotation of the new content on the rotated page.
     */
    public PdfPage setIgnorePageRotationForContent(boolean ignorePageRotationForContent) {
        this.ignorePageRotationForContent = ignorePageRotationForContent;
        return this;
    }

    /**
     * This method adds or replaces a page label.
     *
     * @param numberingStyle The numbering style that shall be used for the numeric portion of each page label.
     *                       May be NULL
     * @param labelPrefix    The label prefix for page labels in this range. May be NULL
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setPageLabel(PageLabelNumberingStyle numberingStyle, String labelPrefix) {
        return setPageLabel(numberingStyle, labelPrefix, 1);
    }

    /**
     * This method adds or replaces a page label.
     *
     * @param numberingStyle The numbering style that shall be used for the numeric portion of each page label.
     *                       May be NULL
     * @param labelPrefix    The label prefix for page labels in this range. May be NULL
     * @param firstPage      The value of the numeric portion for the first page label in the range. Must be greater or
     *                       equal 1.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setPageLabel(PageLabelNumberingStyle numberingStyle, String labelPrefix, int firstPage) {
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

    /**
     * Sets a name specifying the tab order that shall be used for annotations on the page.
     * The possible values are {@link PdfName#R} (row order), {@link PdfName#C} (column order), and {@link PdfName#S} (structure order).
     * Beginning with PDF 2.0, the possible values also include {@link PdfName#A} (annotations array order) and {@link PdfName#W} (widget order).
     * See ISO 32000 12.5, "Annotations" for details.
     *
     * @param tabOrder a {@link PdfName} specifying the annotations tab order. See method description for the allowed values.
     * @return this {@link PdfPage} instance.
     */
    public PdfPage setTabOrder(PdfName tabOrder) {
        put(PdfName.Tabs, tabOrder);
        return this;
    }

    /**
     * Gets a name specifying the tab order that shall be used for annotations on the page.
     * The possible values are {@link PdfName#R} (row order), {@link PdfName#C} (column order), and {@link PdfName#S} (structure order).
     * Beginning with PDF 2.0, the possible values also include {@link PdfName#A} (annotations array order) and {@link PdfName#W} (widget order).
     * See ISO 32000 12.5, "Annotations" for details.
     *
     * @return a {@link PdfName} specifying the annotations tab order or null if tab order is not defined.
     */
    public PdfName getTabOrder() {
        return getPdfObject().getAsName(PdfName.Tabs);
    }

    /**
     * Sets a stream object that shall define the page’s thumbnail image. Thumbnail images represent the contents of
     * its pages in miniature form
     *
     * @param thumb the thumbnail image
     * @return this {@link PdfPage} object
     */
    public PdfPage setThumbnailImage(PdfImageXObject thumb) {
        return put(PdfName.Thumb, thumb.getPdfObject());
    }

    /**
     * Sets a stream object that shall define the page’s thumbnail image. Thumbnail images represent the contents of
     * its pages in miniature form
     *
     * @return the thumbnail image, or <code>null</code> if it is not present
     */
    public PdfImageXObject getThumbnailImage() {
        PdfStream thumbStream = getPdfObject().getAsStream(PdfName.Thumb);
        return thumbStream != null ? new PdfImageXObject(thumbStream) : null;
    }

    /**
     * Adds {@link PdfOutputIntent} that shall specify the colour characteristics of output devices
     * on which the page might be rendered.
     *
     * @param outputIntent {@link PdfOutputIntent} to add.
     * @return this {@link PdfPage} object
     * @see PdfOutputIntent
     */
    public PdfPage addOutputIntent(PdfOutputIntent outputIntent) {
        if (outputIntent == null)
            return this;

        PdfArray outputIntents = getPdfObject().getAsArray(PdfName.OutputIntents);
        if (outputIntents == null) {
            outputIntents = new PdfArray();
            put(PdfName.OutputIntents, outputIntents);
        }
        outputIntents.add(outputIntent.getPdfObject());
        return this;
    }

    /**
     * Helper method that associate specified value with specified key in the underlined {@link PdfDictionary}.
     * May be used in chain.
     *
     * @param key   the {@link PdfName} key with which the specified value is to be associated.
     * @param value the {@link PdfObject} value to be associated with the specified key.
     * @return this {@link PdfPage} object.
     */
    public PdfPage put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }

    /**
     * This flag is meaningful for the case, when page rotation is applied and ignorePageRotationForContent
     * is set to true. NOTE: It is needed for the internal usage.
     * <br><br>
     * This flag defines if inverse matrix (which rotates content into the opposite direction from page rotation
     * direction in order to give the impression of the not rotated text) is already applied to the page content stream.
     * See {@link #setIgnorePageRotationForContent(boolean)}
     *
     * @return true, if inverse matrix is already applied, false otherwise.
     */
    public boolean isPageRotationInverseMatrixWritten() {
        return pageRotationInverseMatrixWritten;
    }

    /**
     * NOTE: For internal usage! Use this method only if you know what you are doing.
     * <br><br>
     * This method is called when inverse matrix (which rotates content into the opposite direction from page rotation
     * direction in order to give the impression of the not rotated text) is applied to the page content stream.
     * See {@link #setIgnorePageRotationForContent(boolean)}
     */
    public void setPageRotationInverseMatrixWritten() {
        // this method specifically return void to discourage it's unintended usage
        pageRotationInverseMatrixWritten = true;
    }

    /**
     * Adds file associated with PDF page and identifies the relationship between them.
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the page dictionary.
     * If description is provided, it also will add file description to catalog Names tree.
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     *
     * @param description the file description
     * @param fs          file specification dictionary of associated file
     */
    public void addAssociatedFile(String description, PdfFileSpec fs) {
        if (null == ((PdfDictionary) fs.getPdfObject()).get(PdfName.AFRelationship)) {
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.error(LogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
        }
        if (null != description) {
            getDocument().getCatalog().addNameToNameTree(description, fs.getPdfObject(), PdfName.EmbeddedFiles);
        }
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null) {
            afArray = new PdfArray();
            put(PdfName.AF, afArray);
        }
        afArray.add(fs.getPdfObject());
    }

    /**
     * <p>
     * Adds file associated with PDF page and identifies the relationship between them.
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method adds file to array value of the AF key in the page dictionary.
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     *
     * @param fs file specification dictionary of associated file
     */
    public void addAssociatedFile(PdfFileSpec fs) {
        addAssociatedFile(null, fs);
    }

    /**
     * Returns files associated with PDF page.
     *
     * @param create iText will create AF array if it doesn't exist and create value is true
     * @return associated files array.
     */
    public PdfArray getAssociatedFiles(boolean create) {
        PdfArray afArray = getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null && create) {
            afArray = new PdfArray();
            put(PdfName.AF, afArray);
        }
        return afArray;
    }

    void tryFlushPageTags() {
        try {
            if (!getDocument().isClosing) {
                getDocument().getTagStructureContext().flushPageTags(this);
            }
            getDocument().getStructTreeRoot().savePageStructParentIndexIfNeeded(this);
        } catch (Exception ex) {
            throw new PdfException(PdfException.TagStructureFlushingFailedItMightBeCorrupted, ex);
        }
    }

    void releaseInstanceFields() {
        resources = null;
        parentPages = null;
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
        }
        return annots;
    }

    private PdfObject getInheritedValue(PdfName pdfName, int type) {
        if (this.parentPages == null) {
            this.parentPages = getDocument().getCatalog().getPageTree().findPageParent(this);
        }
        PdfObject val = getInheritedValue(this.parentPages, pdfName);
        return val != null && val.getType() == type ? val : null;
    }

    private static PdfObject getInheritedValue(PdfPages parentPages, PdfName pdfName) {
        if (parentPages != null) {
            PdfDictionary parentDictionary = parentPages.getPdfObject();
            PdfObject value = parentDictionary.get(pdfName);
            if (value != null) {
                return value;
            } else {
                return getInheritedValue(parentPages.getParent(), pdfName);
            }
        }
        return null;
    }

    private PdfStream newContentStream(boolean before) {
        PdfObject contents = getPdfObject().get(PdfName.Contents);
        PdfArray array;
        if (contents instanceof PdfStream) {
            array = new PdfArray();
            if (contents.getIndirectReference() != null) {
                // Explicitly using object indirect reference here in order to correctly process released objects.
                array.add(contents.getIndirectReference());
            } else {
                array.add(contents);
            }
            put(PdfName.Contents, array);
        } else if (contents instanceof PdfArray) {
            array = (PdfArray) contents;
        } else {
            array = null;
        }
        PdfStream contentStream = (PdfStream) new PdfStream().makeIndirect(getDocument());
        if (array != null) {
            if (before) {
                array.add(0, contentStream);
            } else {
                array.add(contentStream);
            }
            if (array.getIndirectReference() != null) {
                array.setModified();
            } else {
                setModified();
            }
        } else {
            put(PdfName.Contents, contentStream);
        }
        return contentStream;
    }

    private void flushResourcesContentStreams() {
        flushResourcesContentStreams(getResources().getPdfObject());

        PdfArray annots = getAnnots(false);
        if (annots != null && !annots.isFlushed()) {
            for (int i = 0; i < annots.size(); ++i) {
                PdfDictionary apDict = annots.getAsDictionary(i).getAsDictionary(PdfName.AP);
                if (apDict != null) {
                    flushAppearanceStreams(apDict);
                }
            }
        }
    }

    private void flushResourcesContentStreams(PdfDictionary resources) {
        if (resources != null && !resources.isFlushed()) {
            flushWithResources(resources.getAsDictionary(PdfName.XObject));
            flushWithResources(resources.getAsDictionary(PdfName.Pattern));
            flushWithResources(resources.getAsDictionary(PdfName.Shading));
        }
    }

    private void flushWithResources(PdfDictionary objsCollection) {
        if (objsCollection == null || objsCollection.isFlushed()) {
            return;
        }

        for (PdfObject obj : objsCollection.values()) {
            if (obj.isFlushed())
                continue;
            flushResourcesContentStreams(((PdfDictionary) obj).getAsDictionary(PdfName.Resources));
            flushMustBeIndirectObject(obj);
        }
    }

    private void flushAppearanceStreams(PdfDictionary appearanceStreamsDict) {
        if (appearanceStreamsDict.isFlushed()) {
            return;
        }
        for (PdfObject val : appearanceStreamsDict.values()) {
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

    private void copyInheritedProperties(PdfPage copyPdfPage, PdfDocument pdfDocument) {
        if (copyPdfPage.getPdfObject().get(PdfName.Resources) == null) {
            PdfObject copyResource = pdfDocument.getWriter().copyObject(getResources().getPdfObject(), pdfDocument, false);
            copyPdfPage.getPdfObject().put(PdfName.Resources, copyResource);
        }
        if (copyPdfPage.getPdfObject().get(PdfName.MediaBox) == null) {
            //media box shall be in any case
            copyPdfPage.setMediaBox(getMediaBox());
        }
        if (copyPdfPage.getPdfObject().get(PdfName.CropBox) == null) {
            //original pdfObject don't have CropBox, otherwise copyPdfPage will contain it
            PdfArray cropBox = (PdfArray) getInheritedValue(PdfName.CropBox, PdfObject.ARRAY);
            //crop box is optional, we shall not set default value.
            if (cropBox != null) {
                copyPdfPage.put(PdfName.CropBox, cropBox.copyTo(pdfDocument));
            }
        }
        if (copyPdfPage.getPdfObject().get(PdfName.Rotate) == null) {
            //original pdfObject don't have Rotate, otherwise copyPdfPage will contain it
            PdfNumber rotate = (PdfNumber) getInheritedValue(PdfName.Rotate, PdfObject.NUMBER);
            //rotate is optional, we shall not set default value.
            if (rotate != null) {
                copyPdfPage.put(PdfName.Rotate, rotate.copyTo(pdfDocument));
            }
        }
    }

    private void rebuildFormFieldParent(PdfDictionary field, PdfDictionary newField, PdfDocument toDocument) {
        if (newField.containsKey(PdfName.Parent)) {
            return;
        }
        PdfDictionary oldParent = field.getAsDictionary(PdfName.Parent);
        if (oldParent != null) {
            PdfDictionary newParent = oldParent.copyTo(toDocument, Arrays.asList(PdfName.P, PdfName.Kids, PdfName.Parent), false);
            if (newParent.isFlushed()) {
                newParent = oldParent.copyTo(toDocument, Arrays.asList(PdfName.P, PdfName.Kids, PdfName.Parent), true);
            }
            rebuildFormFieldParent(oldParent, newParent, toDocument);

            PdfArray kids = newParent.getAsArray(PdfName.Kids);
            if (kids == null) {
                newParent.put(PdfName.Kids, new PdfArray());
            }
            newField.put(PdfName.Parent, newParent);
        }
    }
}

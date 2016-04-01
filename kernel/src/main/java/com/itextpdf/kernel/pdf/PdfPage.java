package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.tagging.*;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -952395541908379500L;
	private PdfResources resources = null;
    private Integer mcid = null;
    private Integer structParents = null;
    PdfPages parentPages;
    private List<PdfName> excludedKeys = new ArrayList<>(Arrays.asList(
            PdfName.Parent,
            PdfName.Annots,
            PdfName.StructParents,
            // TODO This key contains reference to all articles, while this articles could reference to lots of pages.
            // See DEVSIX-191
            PdfName.B));
    /**
     * Automatically rotate new content if the page has a rotation ( is disabled by default )
     */
    private boolean ignoreContentRotation = true;

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
            structParents = pdfDocument.getNextStructParentIndex();
            getPdfObject().put(PdfName.StructParents, new PdfNumber(structParents));
        }
    }

    protected PdfPage(PdfDocument pdfDocument) {
        this(pdfDocument, pdfDocument.getDefaultPageSize());
    }

    public Rectangle getPageSize() {
        PdfArray box = getPdfObject().getAsArray(PdfName.MediaBox);
        if (box == null || box.size() != 4) {
            throw new IllegalArgumentException("MediaBox");
        }
        Float llx = box.getAsFloat(0);
        Float lly = box.getAsFloat(1);
        Float urx = box.getAsFloat(2);
        Float ury = box.getAsFloat(3);
        if (llx == null || lly == null || urx == null || ury == null) {
            throw new IllegalArgumentException("MediaBox");
        }
        return new Rectangle(Math.min(llx, urx), Math.min(lly, ury), Math.abs(urx - llx), Math.abs(ury - lly));
    }

    /**
     * Gets the rotated page.
     *
     * @return the rotated rectangle
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

    public int getRotation() {
        PdfNumber rotate = getPdfObject().getAsNumber(PdfName.Rotate);

        if (rotate == null) {
            return 0;
        } else {
            int n = rotate.getIntValue();
            n %= 360;
            return n < 0 ? n + 360 : n;
        }
    }

    public void setRotation(int degAngle) {
        getPdfObject().put(PdfName.Rotate, new PdfNumber(degAngle));
    }

    public PdfStream getContentStream(int index) {
        int count = getContentStreamCount();
        if (index >= count)
            throw new IndexOutOfBoundsException(String.format("Index: %d, Size: %d", index, count));
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

    public PdfStream getFirstContentStream() {
        if (getContentStreamCount() > 0)
            return getContentStream(0);
        return null;
    }

    public PdfStream getLastContentStream() {
        int count = getContentStreamCount();
        if (count > 0)
            return getContentStream(count - 1);
        return null;
    }


    public PdfStream newContentStreamBefore() {
        return newContentStream(true);
    }

    public PdfStream newContentStreamAfter() {
        return newContentStream(false);
    }

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

    public void setResources(PdfResources pdfResources) {
        getPdfObject().put(PdfName.Resources, pdfResources.getPdfObject());
        this.resources = pdfResources;
    }


    /**
     * Use this method to set the XMP Metadata for each page.
     *
     * @param xmpMetadata The xmpMetadata to set.
     * @throws IOException
     */
    public void setXmpMetadata(final byte[] xmpMetadata) throws IOException {
        PdfStream xmp = new PdfStream().makeIndirect(getDocument());
        xmp.getOutputStream().write(xmpMetadata);
        xmp.put(PdfName.Type, PdfName.Metadata);
        xmp.put(PdfName.Subtype, PdfName.XML);
        getPdfObject().put(PdfName.Metadata, xmp);
    }

    public void setXmpMetadata(final XMPMeta xmpMeta, final SerializeOptions serializeOptions) throws XMPException, IOException {
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    public void setXmpMetadata(final XMPMeta xmpMeta) throws XMPException, IOException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
    }


    public PdfStream getXmpMetadata() throws XMPException {
        return getPdfObject().getAsStream(PdfName.Metadata);
    }

    /**
     * Copies page to the specified document.
     * <br/><br/>
     * NOTE: Works only for pages from the document opened in reading mode, otherwise an exception is thrown.
     *
     * @param toDocument a document to copy page to.
     * @return copied page.
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
     * @param copier     a copier which bears a specific copy logic. May be NULL
     * @return copied page.
     */
    public PdfPage copyTo(PdfDocument toDocument, IPdfPageExtraCopier copier) {
        PdfDictionary dictionary = getPdfObject().copyTo(toDocument, excludedKeys, true);
        PdfPage page = new PdfPage(dictionary);
        copyInheritedProperties(page, toDocument);
        for (PdfAnnotation annot : getAnnotations()) {
            if (annot.getSubtype().equals(PdfName.Link)) {
                getDocument().storeLinkAnnotations(this, (PdfLinkAnnotation) annot);
            } else {
                page.addAnnotation(-1, PdfAnnotation.makeAnnotation(annot.getPdfObject().copyTo(toDocument, false)), false);
            }
        }
        if (toDocument.isTagged()) {
            page.structParents = toDocument.getNextStructParentIndex();
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
     * @return resultant XObject.
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
     * Flushes page and its content stream. If <code>flushXObjects</code> is true the images and FormXObjects
     * associated with this page will also be flushed.
     * <br>
     * For notes about tag structure flushing see {@link PdfPage#flush() PdfPage#flush() method}.
     * <br>
     * <br>
     * If <code>PdfADocument</code> is used, flushing will be applied only if <code>flushXObjects</code> is true.
     *
     * @param flushXObjects if true the images and FormXObjects associated with this page will also be flushed.
     */
    public void flush(boolean flushXObjects) {
        // TODO log warning in case of failed flush in pdfa document case
        if (isFlushed()) {
            return;
        }
        if (getDocument().isTagged() && !getDocument().getStructTreeRoot().isFlushed()) {
            getDocument().getTagStructureContext().flushPageTags(this);
            getDocument().getStructTreeRoot().getMcrManager().createParentTreeEntryForPage(this);
        }
        getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.END_PAGE, this));
        if (flushXObjects) {
            getDocument().checkIsoConformance(this, IsoKey.PAGE);
        }
        int contentStreamCount = getContentStreamCount();
        for (int i = 0; i < contentStreamCount; i++) {
            getContentStream(i).flush(false);
        }

        Collection<PdfObject> xObjects = null;
        if (resources != null) {
            if (resources.isReadOnly() && !resources.isModified()) {
                getPdfObject().remove(PdfName.Resources);
            } else if (flushXObjects) {
                PdfDictionary xObjectsDict = getPdfObject().getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.XObject);
                xObjects = xObjectsDict != null ? xObjectsDict.values() : null;
            }
        }

        resources = null;
        super.flush();

        if (flushXObjects && xObjects != null) {
            flushXObjects(xObjects);
        }
    }

    public Rectangle getMediaBox() {
        initParentPages();
        PdfArray mediaBox = getPdfObject().getAsArray(PdfName.MediaBox);
        if (mediaBox == null) {
            mediaBox = (PdfArray) getParentValue(parentPages, PdfName.MediaBox);
        }
        return mediaBox.toRectangle();
    }

    public void setMediaBox(Rectangle rectangle) {
        getPdfObject().put(PdfName.MediaBox, new PdfArray(rectangle));
    }


    public Rectangle getCropBox() {
        initParentPages();
        PdfArray cropBox = getPdfObject().getAsArray(PdfName.CropBox);
        if (cropBox == null) {
            cropBox = (PdfArray) getParentValue(parentPages, PdfName.CropBox);
            if (cropBox == null) {
                cropBox = new PdfArray(getMediaBox());
            }
        }
        return cropBox.toRectangle();
    }

    public void setCropBox(Rectangle rectangle) {
        getPdfObject().put(PdfName.CropBox, new PdfArray(rectangle));
    }

    public void setArtBox(Rectangle rectangle) {
        if (getPdfObject().getAsRectangle(PdfName.TrimBox) != null) {
            getPdfObject().remove(PdfName.TrimBox);
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.warn(LogMessageConstant.ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE);
        }
        getPdfObject().put(PdfName.ArtBox, new PdfArray(rectangle));
    }

    public Rectangle getArtBox() {
        return getPdfObject().getAsRectangle(PdfName.ArtBox);
    }

    public void setTrimBox(Rectangle rectangle) {
        if (getPdfObject().getAsRectangle(PdfName.ArtBox) != null) {
            getPdfObject().remove(PdfName.ArtBox);
            Logger logger = LoggerFactory.getLogger(PdfPage.class);
            logger.warn(LogMessageConstant.ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE);
        }
        getPdfObject().put(PdfName.TrimBox, new PdfArray(rectangle));
    }

    public Rectangle getTrimBox() {
        return getPdfObject().getAsRectangle(PdfName.TrimBox);
    }

    /**
     * Get decoded bytes for the whole page content.
     *
     * @return byte array.
     * @throws PdfException in case any @see IOException.
     */
    public byte[] getContentBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int streamCount = getContentStreamCount();
            for (int i = 0; i < streamCount; i++) {
                baos.write(getStreamBytes(i));
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
     * @throws PdfException in case any @see IOException.
     */
    public byte[] getStreamBytes(int index) {
        return getContentStream(index).getBytes();
    }

    /**
     * Calculates and returns next available MCID reference.
     *
     * @return calculated MCID reference.
     * @throws PdfException
     */
    public int getNextMcid() {
        if (!getDocument().isTagged()) {
            throw new PdfException(PdfException.MustBeATaggedDocument);
        }
        if (mcid == null) {
            PdfStructTreeRoot structTreeRoot = getDocument().getStructTreeRoot();
            List<PdfMcr> mcrs = structTreeRoot.getMcrManager().getPageMarkedContentReferences(this);
            mcid = getMcid(mcrs);
        }
        return mcid++;
    }

    public Integer getStructParentIndex() {
        if (structParents == null) {
            PdfNumber n = getPdfObject().getAsNumber(PdfName.StructParents);
            if (n != null) {
                structParents = n.getIntValue();
            } else {
                structParents = 0;
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
     * @return this PdfPage instance.
     */
    public PdfPage removeAnnotation(PdfAnnotation annotation) {
        PdfArray annots = getAnnots(false);
        if (annots != null) {
            if (!annots.remove(annotation.getPdfObject())) {
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
                if (tagPointer.getKidsRoles().isEmpty() && standardAnnotTagRole) {
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
    public boolean isIgnoreContentRotation() {
        return ignoreContentRotation;
    }

    /**
     * If true - defines that in case the page has a rotation, then new content will be automatically rotated in the
     * opposite direction. On the rotated page this would look like if new content ignores page rotation.
     * Default value - {@code false}.
     * @param ignoreContentRotation - true to ignore rotation of the new content on the rotated page.
     */
    public void setIgnoreContentRotation(boolean ignoreContentRotation) {
        this.ignoreContentRotation = ignoreContentRotation;
    }

    /**
     * This method adds or replaces a page label.
     * @param numberingStyle The numbering style that shall be used for the numeric portion of each page label.
     *                       May be NULL
     * @param labelPrefix The label prefix for page labels in this range. May be NULL
     * @return
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
     * @return
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

    private Integer getMcid(List<PdfMcr> mcrs) {
        Integer maxMcid = null;
        if (mcrs == null)
            return 0;

        for (PdfMcr mcr : mcrs) {
            Integer mcid = mcr.getMcid();
            if (maxMcid == null || (mcid != null && mcid > maxMcid))
                maxMcid = mcid;
        }
        return maxMcid == null ? 0 : maxMcid + 1;
    }

    private void flushXObjects(Collection<PdfObject> xObjects) {
        for (PdfObject obj : xObjects) {
            PdfStream xObject = (PdfStream) obj;

            PdfDictionary innerResources = xObject.getAsDictionary(PdfName.Resources);
            Collection<PdfObject> innerXObjects = null;
            if (innerResources != null) {
                PdfDictionary innerXObjectsDict = innerResources.getAsDictionary(PdfName.XObject);
                innerXObjects = innerXObjectsDict != null ? innerXObjectsDict.values() : null;
            }

            obj.flush();
            if (innerXObjects != null) {
                flushXObjects(innerXObjects);
            }
        }
    }

    /*
    * initialization <code>parentPages</code> if needed
    */
    private void initParentPages() {
        if (this.parentPages == null) {
            PdfPagesTree pageTree = getDocument().getCatalog().pageTree;
            this.parentPages = pageTree.findPageParent(this);
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

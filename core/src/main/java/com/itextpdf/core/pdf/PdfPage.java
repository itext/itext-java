package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.tagging.*;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.core.xmp.XMPMeta;
import com.itextpdf.core.xmp.XMPMetaFactory;
import com.itextpdf.core.xmp.options.SerializeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    private PdfResources resources = null;
    private Integer mcid = null;
    private Integer structParents = null;
    PdfPages parentPages;

    protected PdfPage(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject);
        makeIndirect(pdfDocument);
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    protected PdfPage(PdfDocument pdfDocument, PageSize pageSize) {
        super(new PdfDictionary());
        makeIndirect(pdfDocument);
        PdfStream contentStream = new PdfStream().makeIndirect(pdfDocument);
        getPdfObject().put(PdfName.Contents, contentStream);
        getPdfObject().put(PdfName.Type, PdfName.Page);
        getPdfObject().put(PdfName.MediaBox, new PdfArray(pageSize));
        getPdfObject().put(PdfName.TrimBox, new PdfArray(pageSize));
        if (pdfDocument.isTagged()) {
            structParents = pdfDocument.getNextStructParentIndex();
            getPdfObject().put(PdfName.StructParents, new PdfNumber(structParents));
        }
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
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
        return new Rectangle(llx, lly, urx - llx, ury - lly);
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
                if (parentPages == null) {
                    PdfPagesTree pageTree = getDocument().getCatalog().pageTree;
                    parentPages = pageTree.findPageParent(this);
                }

                resources = (PdfDictionary) getParentValue(parentPages, PdfName.Resources);
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
        return resources;
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

    public void setCropBox(){

    }

    public PdfStream getXmpMetadata() throws XMPException {
        return getPdfObject().getAsStream(PdfName.Metadata);
    }

    /**
     * Copies page to the specified document.
     *
     * @param toDocument a document to copy page to.
     * @return copied page.
     */
    @Override
    public PdfPage copy(PdfDocument toDocument) {
        return copy(toDocument, null);
    }

    /**
     * Copies page to the specified document.
     *
     * @param toDocument a document to copy page to.
     * @param copier a copier which bears a specific copy logic. May be NULL
     * @return copied page.
     */
    public PdfPage copy(PdfDocument toDocument, IPdfPageExtraCopier copier) {
        PdfDictionary dictionary = getPdfObject().copyToDocument(toDocument, Arrays.asList(
                PdfName.Parent,
                PdfName.Annots,
                PdfName.StructParents,
                // TODO This key contains reference to all articles, while this articles could reference to lots of pages.
                // See DEVSIX-191
                PdfName.B
        ), true);
        PdfPage page = new PdfPage(dictionary, toDocument);
        for (PdfAnnotation annot : getAnnotations()) {
            page.addAnnotation(PdfAnnotation.makeAnnotation(annot.getPdfObject().copyToDocument(toDocument), toDocument));
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
                logger.warn("Source document has AcroForm dictionary. The pages you're going to copy may have FormFields, but they won't be copied, " +
                        "because you haven't used any IPdfPageExtraCopier.");
                toDocument.getWriter().isUserWarnedAboutAcroFormCopying = true;
            }
        }

        return page;
    }


    /**
     * Copies page as FormXObject to the specified document.
     * @param toDocument a document to copy to.
     * @return resultant XObject.
     */
    public PdfFormXObject copyAsFormXObject(PdfDocument toDocument) {
        // TODO
        throw new IllegalStateException("not implemented");
//        PdfFormXObject xObject = new PdfFormXObject(toDocument, getMediaBox());
//        getResources().getPdfObject().copy(toDocument);
    }

    @Override
    public void flush() {
        if (getDocument().isTagged() && structParents == null) {
            PdfNumber n = getPdfObject().getAsNumber(PdfName.StructParents);
            if (n != null)
                structParents = n.getIntValue();
        }
        getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.EndPage, this));
        int contentStreamCount = getContentStreamCount();
        for (int i = 0; i < contentStreamCount; i++) {
            getContentStream(i).flush(false);
        }


        if (resources != null) {
            if (resources.isReadOnly() && !resources.isModified()) {
                getPdfObject().remove(PdfName.Resources);
            }
        }

        resources = null;
        super.flush();
    }

    public Rectangle getMediaBox() {
        PdfArray mediaBox = getPdfObject().getAsArray(PdfName.MediaBox);
        if (mediaBox == null) {
            mediaBox = (PdfArray) getParentValue(parentPages, PdfName.MediaBox);
        }
        return mediaBox.toRectangle();
    }

    public void setMediaBox(Rectangle rectangle){
        getPdfObject().put(PdfName.MediaBox, new PdfArray(rectangle));
    }


    public Rectangle getCropBox() {
        PdfArray cropBox = getPdfObject().getAsArray(PdfName.CropBox);
        if (cropBox == null) {
            cropBox = (PdfArray) getParentValue(parentPages, PdfName.CropBox);
            if (cropBox == null) {
                cropBox = new PdfArray(getMediaBox());
            }
        }
        return cropBox.toRectangle();
    }

    public void setCropBox(Rectangle rectangle){
        getPdfObject().put(PdfName.CropBox, new PdfArray(rectangle));
    }

    public void setArtBox(Rectangle rectangle){
        if(getPdfObject().getAsRectangle(PdfName.TrimBox) != null)
            throw new PdfException(PdfException.OnlyOneOfArtboxOrTrimBoxCanExistInThePage);
        getPdfObject().put(PdfName.ArtBox, new PdfArray(rectangle));
    }

    public Rectangle getArtBox(){
        return getPdfObject().getAsRectangle(PdfName.ArtBox);
    }

    public void setTrimBox(Rectangle rectangle){
        if(getPdfObject().getAsRectangle(PdfName.ArtBox) != null)
            throw new PdfException(PdfException.OnlyOneOfArtboxOrTrimBoxCanExistInThePage);
        getPdfObject().put(PdfName.TrimBox, new PdfArray(rectangle));
    }

    public Rectangle getTrimBox(){
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
        if (mcid == null) {
            getPageTags();
        }
        return mcid++;
    }

    public Integer getStructParentIndex() {
        if (structParents == null) {
            structParents = getPdfObject().getAsNumber(PdfName.StructParents).getIntValue();
        }
        return structParents;
    }

    /**
     * Gets a list of tags on this page.
     * Please use this method very carefully as it rereads all structure tree and is slow.
     *
     * @return
     * @throws PdfException
     */
    public List<IPdfTag> getPageTags() {
        if (getDocument().getStructTreeRoot() == null)
            return null;
        List<IPdfTag> tags = new ArrayList<IPdfTag>();
        getPageTags(getDocument().getStructTreeRoot().getPdfObject(), tags);
        mcid = getMcid(tags);
        return tags;
    }

    public PdfPage setAdditionalAction(PdfName key, PdfAction action) {
        PdfAction.setAdditionalAction(this, key, action);
        return this;
    }

    public List<PdfAnnotation> getAnnotations() {
        List<PdfAnnotation> annotations = new ArrayList<PdfAnnotation>();
        PdfArray annots = getPdfObject().getAsArray(PdfName.Annots);
        if (annots != null) {
            for (int i = 0; i < annots.size(); i++) {
                PdfDictionary annot = annots.getAsDictionary(i);
                annotations.add(PdfAnnotation.makeAnnotation(annot, getDocument()).setPage(this));
            }
        }
        return annotations;
    }

    public PdfPage addAnnotation(PdfAnnotation annotation) {
        PdfArray annots = getAnnots(true);
        annots.add(annotation.setPage(this).getPdfObject());
        return this;
    }

    public PdfPage addAnnotation(int index, PdfAnnotation annotation) {
        if (getAnnotsSize() <= index)
            return addAnnotation(annotation);
        else {
            PdfArray annots = getAnnots(true);
            annots.add(index, annotation.setPage(this).getPdfObject());
            return this;
        }
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
        return getDocument().getCatalog().getPagesWithOutlines().get(getPdfObject().getIndirectReference());
    }

    private PdfArray getAnnots(boolean create) {
        PdfArray annots = getPdfObject().getAsArray(PdfName.Annots);
        if (annots == null && create) {
            annots = new PdfArray();
            put(PdfName.Annots, annots);
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
                getParentValue(parentPages.getParent(), pdfName);
            }
        }
        return null;
    }

    private void getPageTags(PdfDictionary getFrom, List<IPdfTag> putTo) {
        PdfObject k = getFrom.get(PdfName.K);
        if (k == null)
            return;
        switch (k.getType()) {
            case PdfObject.Number:
                if (getFrom.getAsDictionary(PdfName.Pg) == getPdfObject())
                    putTo.add(new PdfMcrNumber((PdfNumber) k, new PdfStructElem(getFrom, getDocument())));
                break;
            case PdfObject.Dictionary:
                PdfDictionary d = (PdfDictionary) k;
                if (PdfName.MCR.equals(d.getAsName(PdfName.Type)) && getPdfObject() == d.getAsDictionary(PdfName.Pg))
                    putTo.add(new PdfMcrDictionary(d, new PdfStructElem(getFrom, getDocument())));
                else if (getFrom.getAsDictionary(PdfName.Pg) == getPdfObject() && PdfName.OBJR.equals(d.getAsName(PdfName.Type))) {
                    PdfDictionary pg = d.getAsDictionary(PdfName.Pg);
                    if (pg == null || pg == getPdfObject())
                        putTo.add(new PdfObjRef(d, new PdfStructElem(getFrom, getDocument())));
                } else
                    getPageTags(d, putTo);
                break;
            case PdfObject.Array:
                PdfArray a = (PdfArray) k;
                for (int i = 0; i < a.size(); i++) {
                    PdfObject aItem = a.get(i);
                    switch (aItem.getType()) {
                        case PdfObject.Number:
                            if (getFrom.getAsDictionary(PdfName.Pg) == getPdfObject())
                                putTo.add(new PdfMcrNumber((PdfNumber) aItem, new PdfStructElem(getFrom, getDocument())));
                            break;
                        case PdfObject.Dictionary:
                            PdfDictionary dItem = (PdfDictionary) aItem;
                            if (PdfName.MCR.equals(dItem.getAsName(PdfName.Type)) && getPdfObject() == dItem.getAsDictionary(PdfName.Pg))
                                putTo.add(new PdfMcrDictionary(dItem, new PdfStructElem(getFrom, getDocument())));
                            else if (getFrom.getAsDictionary(PdfName.Pg) == getPdfObject() && PdfName.OBJR.equals(dItem.getAsName(PdfName.Type))) {
                                PdfDictionary pg = dItem.getAsDictionary(PdfName.Pg);
                                if (pg == null || pg == getPdfObject())
                                    putTo.add(new PdfObjRef(dItem, new PdfStructElem(getFrom, getDocument())));
                            } else
                                getPageTags(dItem, putTo);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }

    private PdfStream newContentStream(boolean before) {
        PdfObject contents = getPdfObject().get(PdfName.Contents);
        PdfArray array;
        if (contents instanceof PdfStream) {
            array = new PdfArray();
            array.add(contents);
            getPdfObject().put(PdfName.Contents, array);
        } else if (contents instanceof PdfArray) {
            array = (PdfArray) contents;
        } else {
            throw new PdfException(PdfException.PdfPageShallHaveContent);
        }
        PdfStream contentStream = new PdfStream().makeIndirect(getPdfObject().getDocument());
        if (before) {
            array.add(0, contentStream);
        } else {
            array.add(contentStream);
        }
        return contentStream;
    }

    private Integer getMcid(List<IPdfTag> tags) {
        Integer maxMcid = null;
        for (IPdfTag tag : tags) {
            if (maxMcid == null || tag.getMcid() > maxMcid)
                maxMcid = tag.getMcid();
        }
        return maxMcid == null ? 0 : maxMcid + 1;
    }


}

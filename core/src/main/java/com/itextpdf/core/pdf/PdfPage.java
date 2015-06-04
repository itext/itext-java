package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfAnnotation;
import com.itextpdf.core.pdf.tagging.*;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.core.xmp.XMPMeta;
import com.itextpdf.core.xmp.XMPMetaFactory;
import com.itextpdf.core.xmp.options.SerializeOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    private PdfResources resources = null;
    private Integer mcid = null;
    private Integer structParents = null;

    protected PdfPage(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    protected PdfPage(PdfDocument pdfDocument, PageSize pageSize) {
        super(new PdfDictionary(), pdfDocument);
        PdfStream contentStream = new PdfStream(pdfDocument);
        getPdfObject().put(PdfName.Contents, contentStream);
        getPdfObject().put(PdfName.Type, PdfName.Page);
        getPdfObject().put(PdfName.MediaBox, new PdfArray(pageSize));
        if (pdfDocument.isTagged()) {
            structParents = pdfDocument.getNextStructParentIndex();
            getPdfObject().put(PdfName.StructParents, new PdfNumber(structParents));
        }
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    protected PdfPage(PdfDocument pdfDocument) {
        this(pdfDocument, pdfDocument.getDefaultPageSize());
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
            PdfDictionary resources = getPdfObject().getAsDictionary(PdfName.Resources);
            if (resources == null) {
                resources = new PdfDictionary();
                getPdfObject().put(PdfName.Resources, resources);
            }
            this.resources = new PdfResources(resources);
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
        PdfStream xmp = new PdfStream(getDocument());
        xmp.getOutputStream().write(xmpMetadata);
        xmp.put(PdfName.Type, PdfName.Metadata);
        xmp.put(PdfName.Subtype, PdfName.XML);
        getPdfObject().put(PdfName.Metadata, xmp);
    }

    public void setXmpMetadata(final XMPMeta xmpMeta, final SerializeOptions serializeOptions) throws XMPException, IOException, PdfRuntimeException {
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    public void setXmpMetadata(final XMPMeta xmpMeta) throws XMPException, IOException, PdfRuntimeException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
    }

    public PdfStream getXmpMetadata() throws XMPException, PdfRuntimeException {
        return getPdfObject().getAsStream(PdfName.Metadata);
    }

    /**
     * Copies page to a specified document.
     *
     * @param toDocument a document to copy page to.
     * @return copied page.
     * @throws PdfRuntimeException
     */
    @Override
    public PdfPage copy(PdfDocument toDocument) {
        PdfDictionary dictionary = getPdfObject().copy(toDocument, new ArrayList<PdfName>() {{
            add(PdfName.Parent);
            add(PdfName.StructParents);
        }}, true);
        PdfPage page = new PdfPage(dictionary, toDocument);
        if (toDocument.isTagged()) {
            page.structParents = toDocument.getNextStructParentIndex();
            page.getPdfObject().put(PdfName.StructParents, new PdfNumber(page.structParents));
        }
        return page;
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
        resources = null;
        super.flush();
    }

    public Rectangle getMediaBox() {
        return getPdfObject().getAsRectangle(PdfName.MediaBox);
    }

    public Rectangle getCropBox() {
        Rectangle cropBox = getPdfObject().getAsRectangle(PdfName.CropBox);
        if (cropBox == null)
            cropBox = getMediaBox();
        return cropBox;
    }

    /**
     * Get decoded bytes for the whole page content.
     *
     * @return byte array.
     * @throws PdfRuntimeException in case any @see IOException.
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
            throw new PdfRuntimeException(PdfRuntimeException.CannotGetContentBytes, ioe, this);
        }
    }

    /**
     * Gets decoded bytes of a certain stream of a page content.
     *
     * @param index index of stream inside Content.
     * @return byte array.
     * @throws PdfRuntimeException in case any @see IOException.
     */
    public byte[] getStreamBytes(int index) {
        return getContentStream(index).getBytes();
    }

    /**
     * Calculates and returns next available MCID reference.
     *
     * @return calculated MCID reference.
     * @throws PdfRuntimeException
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
     * @throws PdfRuntimeException
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
     * @param updateOutlines
     * @return return all outlines of a current page
     * @throws PdfRuntimeException
     */
    public List<PdfOutline> getOutlines(boolean updateOutlines) {
        getDocument().getOutlines(updateOutlines);
        return getDocument().getCatalog().getPagesWithOutlines().get(getPdfObject().getIndirectReference());
    }

    protected void makeIndirect(PdfDocument pdfDocument) {
        getPdfObject().makeIndirect(pdfDocument);
    }

    private PdfArray getAnnots(boolean create) {
        PdfArray annots = getPdfObject().getAsArray(PdfName.Annots);
        if (annots == null && create) {
            annots = new PdfArray();
            put(PdfName.Annots, annots);
        }
        return annots;
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
            throw new PdfRuntimeException(PdfRuntimeException.PdfPageShallHaveContent);
        }
        PdfStream contentStream = new PdfStream(getPdfObject().getDocument());
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

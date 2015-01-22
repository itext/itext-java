package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.core.xmp.XMPMeta;
import com.itextpdf.core.xmp.XMPMetaFactory;
import com.itextpdf.core.xmp.options.SerializeOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    public final static int FirstPage = 1;
    public final static int LastPage = Integer.MAX_VALUE;

    private PdfResources resources = null;
    private int mcid = 0;
    private Integer structParentIndex = null;

    protected PdfPage(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
        if (pdfDocument.isTagged()) {
            PdfNumber structParents = getPdfObject().getAsNumber(PdfName.StructParents);
            if (structParents != null)
                structParentIndex = structParents.getIntValue();
        }
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    protected PdfPage(PdfDocument pdfDocument, PageSize pageSize) throws PdfException {
        super(new PdfDictionary(), pdfDocument);
        PdfStream contentStream = new PdfStream(pdfDocument);
        getPdfObject().put(PdfName.Contents, contentStream);
        getPdfObject().put(PdfName.Type, PdfName.Page);
        getPdfObject().put(PdfName.MediaBox, new PdfArray(pageSize));
        if (pdfDocument.isTagged()) {
            structParentIndex = pdfDocument.structParentIndex++;
            getPdfObject().put(PdfName.StructParents, new PdfNumber(structParentIndex));
        }
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    protected PdfPage(PdfDocument pdfDocument) throws PdfException {
        this(pdfDocument, pdfDocument.getDefaultPageSize());
    }

    public PdfStream getContentStream(int index) throws PdfException {
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

    public int getContentStreamCount() throws PdfException {
        PdfObject contents = getPdfObject().get(PdfName.Contents);
        if (contents instanceof PdfStream)
            return 1;
        else if (contents instanceof PdfArray) {
            return ((PdfArray) contents).size();
        } else {
            return 0;
        }
    }

    public PdfStream getFirstContentStream() throws PdfException {
        if (getContentStreamCount() > 0)
            return getContentStream(0);
        return null;
    }

    public PdfStream getLastContentStream() throws PdfException {
        int count = getContentStreamCount();
        if (count > 0)
            return getContentStream(count - 1);
        return null;
    }


    public PdfStream newContentStreamBefore() throws PdfException {
        return newContentStream(true);
    }

    public PdfStream newContentStreamAfter() throws PdfException {
        return newContentStream(false);
    }

    public PdfResources getResources() throws PdfException {
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
    public void setXmpMetadata(final byte[] xmpMetadata) throws IOException, PdfException {
        PdfStream xmp = new PdfStream(getDocument());
        xmp.getOutputStream().write(xmpMetadata);
        xmp.put(PdfName.Type, PdfName.Metadata);
        xmp.put(PdfName.Subtype, PdfName.XML);
        getPdfObject().put(PdfName.Metadata, xmp);
    }

    public void setXmpMetadata(final XMPMeta xmpMeta, final SerializeOptions serializeOptions) throws XMPException, IOException, PdfException {
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    public void setXmpMetadata(final XMPMeta xmpMeta) throws XMPException, IOException, PdfException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
    }

    public PdfStream getXmpMetadata() throws XMPException, PdfException {
        return getPdfObject().getAsStream(PdfName.Metadata);
    }

    /**
     * Copies page to a specified document.
     *
     * @param pdfDocument a document to copy page to.
     * @return copied page.
     * @throws PdfException
     */
    @Override
    public PdfPage copy(PdfDocument pdfDocument) throws PdfException {
        PdfDictionary dictionary = getPdfObject().copy(pdfDocument, new ArrayList<PdfName>() {{
            add(PdfName.Parent);
        }}, true);
        return new PdfPage(dictionary, pdfDocument);
    }

    @Override
    public void flush() throws PdfException {
        getPdfObject().remove(PdfName.MCID);
        getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.EndPage, this));
        int contentStreamCount = getContentStreamCount();
        for (int i = 0; i < contentStreamCount; i++) {
            getContentStream(i).flush(false);
        }
        resources = null;
        super.flush();
    }

    public Rectangle getMediaBox() throws PdfException {
        return getPdfObject().getAsRectangle(PdfName.MediaBox);
    }

    public Rectangle getCropBox() throws PdfException {
        Rectangle cropBox = getPdfObject().getAsRectangle(PdfName.CropBox);
        if (cropBox == null)
            cropBox = getMediaBox();
        return cropBox;
    }

    /**
     * Get decoded bytes for the whole page content.
     *
     * @return byte array.
     * @throws PdfException in case any @see IOException.
     */
    public byte[] getContentBytes() throws PdfException {
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
    public byte[] getStreamBytes(int index) throws PdfException {
        return getContentStream(index).getBytes();
    }

    /**
     * Calculates and returns next available MCID reference.
     *
     * @return calculated MCID reference.
     * @throws PdfException
     */
    public int getNextMcid() {
        return mcid++;
    }

    public Integer getStructParentIndex() {
        return structParentIndex;
    }

    protected void makeIndirect(PdfDocument pdfDocument) throws PdfException {
        getPdfObject().makeIndirect(pdfDocument);
    }

    private PdfStream newContentStream(boolean before) throws PdfException {
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
        PdfStream contentStream = new PdfStream(getPdfObject().getDocument());
        if (before) {
            array.add(0, contentStream);
        } else {
            array.add(contentStream);
        }
        return contentStream;
    }

}

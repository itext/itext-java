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

    protected PdfResources resources = null;

    protected PdfPage(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
        PdfDictionary resources = pdfObject.getAsDictionary(PdfName.Resources);
        if (resources != null)
            this.resources = new PdfResources(resources);
        else
            this.resources = new PdfResources();
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    protected PdfPage(PdfDocument pdfDocument, PageSize pageSize) throws PdfException {
        super(new PdfDictionary(), pdfDocument);
        PdfStream contentStream = new PdfStream(pdfDocument);
        pdfObject.put(PdfName.Contents, contentStream);
        pdfObject.put(PdfName.Resources, (resources = new PdfResources()).getPdfObject());
        pdfObject.put(PdfName.Type, PdfName.Page);
        pdfObject.put(PdfName.MediaBox, new PdfArray(pageSize));
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    protected PdfPage(PdfDocument pdfDocument) throws PdfException {
        this(pdfDocument, pdfDocument.getDefaultPageSize());
    }

    public PdfStream getContentStream(int index) throws PdfException {
        int count = getContentStreamCount();
        if (index >= count)
            throw new IndexOutOfBoundsException(String.format("Index: %d, Size: %d", index, count));
        PdfObject contents = pdfObject.get(PdfName.Contents);
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
        PdfObject contents = pdfObject.get(PdfName.Contents);
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

    public PdfResources getResources() {
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

    public void setXmpMetadata(final XMPMeta xmpMeta, final SerializeOptions serializeOptions) throws XMPException, IOException {
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    public void setXmpMetadata(final XMPMeta xmpMeta) throws XMPException, IOException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
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
        getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.EndPage, this));
        super.flush();
    }

    public Rectangle getMediaBox() throws PdfException {
        return pdfObject.getAsRectangle(PdfName.MediaBox);
    }

    public Rectangle getCropBox() throws PdfException {
        Rectangle cropBox = pdfObject.getAsRectangle(PdfName.CropBox);
        if (cropBox == null)
            cropBox = getMediaBox();
        return cropBox;
    }

    /**
     * Get decoded bytes for the whole page content.
     *
     * @return
     * @throws PdfException
     */
    public byte[] getContentBytes() throws PdfException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < getContentStreamCount(); i++) {
                baos.write(getStreamBytes(i));
            }
            baos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            return bytes;
        } catch (IOException ioe) {
            throw new PdfException(PdfException.CannotGetContentBytes, ioe, this);
        }
    }

    /**
     * Gets decoded bytes of a certain stream of a page content.
     *
     * @param index
     * @return
     * @throws PdfException
     */
    public byte[] getStreamBytes(int index) throws PdfException {
        PdfStream stream = getContentStream(index);
        return stream.getBytes();
    }

    protected void makeIndirect(PdfDocument pdfDocument) {
        pdfObject.makeIndirect(pdfDocument);
    }

    private PdfStream newContentStream(boolean before) throws PdfException {
        PdfObject contents = pdfObject.get(PdfName.Contents);
        PdfArray a = null;
        if (contents instanceof PdfStream) {
            a = new PdfArray();
            a.add(contents);
        } else if (contents instanceof PdfArray) {
            a = (PdfArray) contents;
        }
        PdfStream contentStream = new PdfStream(pdfObject.getDocument());
        if (before)
            a.add(0, contentStream);
        else
            a.add(contentStream);
        return contentStream;
    }

}

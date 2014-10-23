package com.itextpdf.core.pdf;

import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.core.xmp.XMPMeta;
import com.itextpdf.core.xmp.XMPMetaFactory;
import com.itextpdf.core.xmp.options.SerializeOptions;

import java.io.IOException;
import java.util.ArrayList;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    public final static int FirstPage = 1;
    public final static int LastPage = Integer.MAX_VALUE;

    protected PdfResources resources = null;

    public PdfPage(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
        PdfDictionary resources = pdfObject.getAsDictionary(PdfName.Resources);
        if (resources != null)
            this.resources = new PdfResources(resources);
        else
            this.resources = new PdfResources();
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    public PdfPage(PdfDocument pdfDocument, PageSize pageSize) throws PdfException {
        super(new PdfDictionary(), pdfDocument);
        PdfStream contentStream = new PdfStream(pdfDocument);
        pdfObject.put(PdfName.Contents, contentStream);
        pdfObject.put(PdfName.Resources, (resources = new PdfResources()).getPdfObject());
        pdfObject.put(PdfName.Type, PdfName.Page);
        pdfObject.put(PdfName.MediaBox, new PdfArray(pageSize));
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    public PdfPage(PdfDocument pdfDocument) throws PdfException {
        this(pdfDocument, pdfDocument.getDefaultPageSize());
    }

    public PdfStream getContentStream() throws PdfException {
        PdfObject contents = pdfObject.get(PdfName.Contents);
        if (contents instanceof PdfStream)
            return (PdfStream) contents;
        else if (contents instanceof PdfArray) {
            PdfArray a = (PdfArray) contents;
            return (PdfStream) a.get(a.size() - 1);
        } else
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
    public PdfPage copy(PdfDocument pdfDocument) throws PdfException {
        PdfDictionary dictionary = getPdfObject().copy(pdfDocument, new ArrayList<PdfName>() {{
            add(PdfName.Parent);
        }}, true);
        return new PdfPage(dictionary, pdfDocument);
    }

    /**
     * Copies a page to the same document.
     *
     * @return copied page.
     * @throws PdfException
     */
    public PdfPage copy() throws PdfException {
        return copy(getDocument());
    }

    @Override
    public void flush() throws PdfException {
        getDocument().dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.EndPage, this));
        super.flush();
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

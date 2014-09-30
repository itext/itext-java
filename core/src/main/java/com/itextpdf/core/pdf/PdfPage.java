package com.itextpdf.core.pdf;

import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.geom.PageSize;

import java.io.IOException;

public class PdfPage extends PdfObjectWrapper<PdfDictionary> {

    public final static int FirstPage = 1;
    public final static int LastPage = Integer.MAX_VALUE;

    public PdfPage(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfPage(PdfDocument pdfDocument, PageSize pageSize) {
        super(new PdfDictionary(), pdfDocument);
        PdfStream contentStream = new PdfStream(pdfDocument);
        pdfObject.put(PdfName.Contents, contentStream);
        pdfObject.put(PdfName.Resources, new PdfResources());
        pdfObject.put(PdfName.Type, PdfName.Page);
        pdfObject.put(PdfName.MediaBox, new PdfArray(pageSize));
        pdfDocument.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    public PdfPage(PdfDocument pdfDocument) {
        this(pdfDocument, pdfDocument.getDefaultPageSize());
    }

    public PdfStream getContentStream() {
        PdfObject contents = pdfObject.get(PdfName.Contents);
        if (contents instanceof PdfStream)
            return (PdfStream)contents;
        else if (contents instanceof PdfArray) {
            PdfArray a = (PdfArray)contents;
            return (PdfStream)a.get(a.size() - 1);
        } else
            return null;
    }

    public PdfStream newContentStreamBefore() {
        return newContentStream(true);
    }

    public PdfStream newContentStreamAfter() {
        return newContentStream(false);
    }

    public PdfResources getResources() {
        return (PdfResources) pdfObject.get(PdfName.Resources);
    }

    private PdfStream newContentStream(boolean before) {
        PdfObject contents = pdfObject.get(PdfName.Contents);
        PdfArray a = null;
        if (contents instanceof PdfStream) {
            a = new PdfArray();
            a.add(contents);
        }
        else if (contents instanceof PdfArray) {
            a = (PdfArray)contents;
        }
        PdfStream contentStream = new PdfStream(pdfObject.getDocument());
        if (before)
            a.add(0, contentStream);
        else
            a.add(contentStream);
        return contentStream;
    }

}

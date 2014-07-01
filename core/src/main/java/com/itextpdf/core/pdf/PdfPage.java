package com.itextpdf.core.pdf;

import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.objects.PdfDictionary;
import com.itextpdf.core.pdf.objects.PdfObject;

public class PdfPage extends PdfDictionary {

    public final static int FirstPage = 1;
    public final static int LastPage = Integer.MAX_VALUE;

    protected PageSize pageSize = null;

    public PdfPage(PdfDocument doc) {
        this(doc, doc.getDefaultPageSize());
    }

    public PdfPage(PdfDocument doc, PageSize pageSize) {
        super(doc);
        this.pageSize = pageSize;
        doc.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    public PdfContentStream getContentStream() {
        return new PdfContentStream(pdfDocument);
    }

    @Override
    public PdfObject flush(PdfDocument doc, PdfObjectFlushInfo flushInfo) {
        doc.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.EndPage, this));
        return super.flush(doc, flushInfo);
    }

}

package com.itextpdf.core.pdf;

import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.objects.PdfDictionary;

public class PdfPage extends PdfDictionary {

    public final static int FirstPage = 1;
    public final static int LastPage = Integer.MAX_VALUE;
    public final static int CurrentPage = Integer.MAX_VALUE - 1;

    protected PageSize pageSize = null;

    public PdfPage(PdfDocument doc) {
        this(doc, doc.getDefaultPageSize());
    }

    public PdfPage(PdfDocument doc, PageSize pageSize) {
        super(doc);
        this.pageSize = pageSize;
    }

    public PdfContentStream getContentStream() {
        return new PdfContentStream(pdfDocument);
    }

}

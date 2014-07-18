package com.itextpdf.core.pdf;

import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.objects.PdfArray;
import com.itextpdf.core.pdf.objects.PdfDictionary;
import com.itextpdf.core.pdf.objects.PdfName;

import java.io.IOException;

public class PdfPage extends PdfDictionary {

    public final static int FirstPage = 1;
    public final static int LastPage = Integer.MAX_VALUE;

    protected PageSize pageSize = null;
    protected PdfContentStream contentStream = null;
    private boolean nestedObjectsFlushed = false;

    private PdfPage() {
        super();
    }

    public PdfPage(PdfDocument doc) {
        this(doc, doc.getDefaultPageSize());
    }

    public PdfPage(PdfDocument doc, PageSize pageSize) {
        super(doc);
        contentStream = new PdfContentStream(doc);
        put(PdfName.Type, PdfName.Page);
        put(PdfName.MediaBox, new PdfArray(pageSize));
        put(PdfName.Contents, contentStream);
        put(PdfName.Resources, new PdfDictionary());
        this.pageSize = pageSize;
        doc.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.StartPage, this));
    }

    public PdfContentStream getContentStream() {
        return contentStream;
    }

    @Override
    public boolean flush() throws IOException {
        contentStream.flush();
        if (pdfDocument.isClosing()) {
            return super.flush();
        }
        return false;
    }
}

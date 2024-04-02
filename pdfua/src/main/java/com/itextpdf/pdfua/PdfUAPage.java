package com.itextpdf.pdfua;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;

class PdfUAPage extends PdfPage {
    protected PdfUAPage(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfUAPage(PdfDocument pdfDocument, PageSize pageSize) {
        super(pdfDocument, pageSize);
    }

    @Override
    public void flush(boolean flushResourcesContentStreams) {
        final PdfDocument document = getDocument();
        if (((PdfUADocument) document).isClosing()) {
            super.flush(flushResourcesContentStreams);
            return;
        }
        ((PdfUADocument) document).warnOnPageFlush();
    }
}

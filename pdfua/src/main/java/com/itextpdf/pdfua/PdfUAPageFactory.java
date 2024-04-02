package com.itextpdf.pdfua;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.IPdfPageFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;

class PdfUAPageFactory implements IPdfPageFactory {


    public PdfUAPageFactory() {
        //empty constructor
    }

    /**
     * @param pdfObject the {@link PdfDictionary} object on which the {@link PdfPage} will be based
     *
     * @return The pdf page.
     */
    @Override
    public PdfPage createPdfPage(PdfDictionary pdfObject) {
        return new PdfUAPage(pdfObject);
    }

    /**
     * @param pdfDocument {@link PdfDocument} to add page
     * @param pageSize    {@link PageSize} of the created page
     *
     * @return The Pdf page.
     */
    @Override
    public PdfPage createPdfPage(PdfDocument pdfDocument, PageSize pageSize) {
        return new PdfUAPage(pdfDocument, pageSize);
    }
}

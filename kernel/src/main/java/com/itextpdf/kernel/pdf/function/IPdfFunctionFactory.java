package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.pdf.PdfObject;

/**
 * Interface represents a factory for {@link AbstractPdfFunction} objects.
 */
@FunctionalInterface
public interface IPdfFunctionFactory {
    /**
     * Creates a PDF function instance.
     *
     * @param pdfObject the pdf object which defines a function.
     *
     * @return the PDF function
     */
    IPdfFunction create(PdfObject pdfObject);
}

package com.itextpdf.core.pdf;

/**
 * This interface defines logic which can be used to perform a custom copying
 * operation of a {@link PdfPage}.
 */
public interface IPdfPageExtraCopier {

    /**
     * Copies a page.
     *
     * The new page must already be created before calling this, either in a new
     * {@link PdfDocument} or in the same {@link PdfDocument} as the old page.
     *
     * @param fromPage the source page
     * @param toPage the target page in a target document
     */
    void copy(PdfPage fromPage, PdfPage toPage);
}

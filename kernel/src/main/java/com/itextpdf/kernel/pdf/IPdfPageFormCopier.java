package com.itextpdf.kernel.pdf;

/**
 * This interface extends the logic of the {#link IPdfPageExtraCopier} interface to
 * copy AcroForm fields to a new page.
 */
public interface IPdfPageFormCopier extends IPdfPageExtraCopier {

    /**
     * Create Acroform from its PDF object to process form field objects added to the Acroform during copying.
     *
     * <p>
     * All pages must already be copied to the target document before calling this. So fields with the same names will
     * be merged and target document tag structure will be correct.
     *
     * @param documentTo the target document.
     */
    void recreateAcroformToProcessCopiedFields(PdfDocument documentTo);
}

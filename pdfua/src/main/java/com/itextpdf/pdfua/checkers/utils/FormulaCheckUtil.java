package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagutils.ITagTreeIteratorHandler;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

public final class FormulaCheckUtil {

    /**
     * Creates a new {@link FormulaCheckUtil} instance.
     */
    private FormulaCheckUtil() {
        // Empty constructor
    }

    /**
     * Creates the handler that handles PDF/UA compliance for Formula tags
     * @return {@link ITagTreeIteratorHandler} The formula tag handler.
     */
    public static ITagTreeIteratorHandler createFormulaTagHandler() {
        return new ITagTreeIteratorHandler() {
            @Override
            public void nextElement(IStructureNode elem) {
                final PdfStructElem structElem =TagTreeHandlerUtil.getElementIfRoleMatches(PdfName.Formula, elem);
                if (structElem == null) {
                    return;
                }
                final PdfDictionary pdfObject = structElem.getPdfObject();
                if (hasInvalidValues(pdfObject.getAsString(PdfName.Alt), pdfObject.getAsString(PdfName.ActualText))) {
                    throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.FORMULA_SHALL_HAVE_ALT);
                }
            }
        };
    }


    private static boolean hasInvalidValues(PdfString altText, PdfString actualText) {
        String altTextValue = null;
        if (altText != null) {
            altTextValue = altText.getValue();
        }
        String actualTextValue =null;
        if (actualText != null) {
            actualTextValue = actualText.getValue();
        }
        return !(!(altTextValue == null || altTextValue.isEmpty()) || actualTextValue != null);
    }

}

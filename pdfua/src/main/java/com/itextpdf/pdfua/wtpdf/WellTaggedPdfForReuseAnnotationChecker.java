package com.itextpdf.pdfua.wtpdf;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.ua2.PdfUA2AnnotationChecker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

public class WellTaggedPdfForReuseAnnotationChecker extends PdfUA2AnnotationChecker {
    /**
     * Creates a new instance of the {@link WellTaggedPdfForReuseAnnotationChecker}.
     */
    public WellTaggedPdfForReuseAnnotationChecker() {
        // Empty constructor.
    }

    @Override
    protected void checkRequiredContentsEntry(PdfName subtype, PdfDictionary annotation) {
        if (PdfName.Screen.equals(subtype)) {
            PdfString contents = annotation.getAsString(PdfName.Contents);
            if (contents == null || contents.getValue().isEmpty()) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.ANNOT_CONTENTS_IS_NULL_OR_EMPTY);
            }
        }
    }

    /**
     * Handler for checking annotation elements in the tag tree.
     */
    public static class WellTaggedPdfForReuseAnnotationHandler extends PdfUA2AnnotationHandler {
        /**
         * Creates a new instance of the {@link PdfUA2AnnotationChecker.PdfUA2AnnotationHandler}.
         *
         * @param context the validation context
         */
        public WellTaggedPdfForReuseAnnotationHandler(PdfUAValidationContext context) {
            super(context);
            checker = new WellTaggedPdfForReuseAnnotationChecker();
        }
    }
}

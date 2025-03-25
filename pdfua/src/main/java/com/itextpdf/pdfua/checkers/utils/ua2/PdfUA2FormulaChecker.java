package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class which performs "Formula" tag related checks according to PDF/UA-2 specification.
 */
public final class PdfUA2FormulaChecker {
    private static final String MATH = "math";

    private final PdfUAValidationContext context;

    private PdfUA2FormulaChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks if "math" structure element from "MathML" namespace is enclosed within "Formula" tag.
     *
     * @param elem structure element to check
     *
     * @throws PdfUAConformanceException if document has incorrect tag structure for Formula tag
     */
    public void checkStructElement(IStructureNode elem) {
        final String role = context.resolveToStandardRole(elem);
        if (role == null) {
            return;
        }
        if (MATH.equals(role)) {
            PdfStructElem mathStructElem = context.getElementIfRoleMatches(new PdfName(MATH), elem);
            if (mathStructElem != null) {
                IStructureNode parent = mathStructElem.getParent();
                if (parent != null) {
                    if (!StandardRoles.FORMULA.equals(context.resolveToStandardRole(parent))) {
                        throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MATH_NOT_CHILD_OF_FORMULA);
                    }
                }
            }
        }
    }

    /**
     * Handler class that checks "Formula" tags while traversing the tag tree.
     */
    public static class PdfUA2FormulaTagHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA2FormulaChecker checker;

        /**
         * Creates a new instance of {@link PdfUA2FormulaTagHandler}.
         *
         * @param context the validation context
         */
        public PdfUA2FormulaTagHandler(PdfUAValidationContext context) {
            super(context);
            checker = new PdfUA2FormulaChecker(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            checker.checkStructElement(elem);
        }
    }
}

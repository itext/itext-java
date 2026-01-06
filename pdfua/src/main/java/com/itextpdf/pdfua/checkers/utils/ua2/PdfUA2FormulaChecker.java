/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.pdfua.checkers.utils.ua1;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Class that provides methods for checking PDF/UA-1 compliance of Formula elements.
 */
public final class PdfUA1FormulaChecker {
    private final PdfUAValidationContext context;

    private PdfUA1FormulaChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks "Formula" structure element.
     *
     * @param elem structure element to check
     *
     * @throws PdfUAConformanceException if document has incorrect tag structure for Formula tag
     */
    public void checkStructElement(IStructureNode elem) {
        final PdfStructElem structElem = context.getElementIfRoleMatches(PdfName.Formula, elem);
        if (structElem == null) {
            return;
        }
        final PdfDictionary pdfObject = structElem.getPdfObject();
        if (hasInvalidValues(pdfObject.getAsString(PdfName.Alt), pdfObject.getAsString(PdfName.ActualText))) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.FORMULA_SHALL_HAVE_ALT);
        }
    }

    private static boolean hasInvalidValues(PdfString altText, PdfString actualText) {
        String altTextValue = null;
        if (altText != null) {
            altTextValue = altText.getValue();
        }
        String actualTextValue = null;
        if (actualText != null) {
            actualTextValue = actualText.getValue();
        }
        return !(!(altTextValue == null || altTextValue.isEmpty()) || actualTextValue != null);
    }

    /**
     * Handler for checking Formula elements in the TagTree.
     */
    public static class PdfUA1FormulaTagHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA1FormulaChecker checker;

        /**
         * Creates a new {@link PdfUA1FormulaTagHandler} instance.
         * @param context The validation context.
         */
        public PdfUA1FormulaTagHandler(PdfUAValidationContext context) {
            super(context);
            this.checker = new PdfUA1FormulaChecker(context);
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

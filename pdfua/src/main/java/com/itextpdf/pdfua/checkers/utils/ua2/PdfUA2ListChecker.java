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
package com.itextpdf.pdfua.checkers.utils.ua2;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class which performs lists check according to PDF/UA-2 specification.
 */
public final class PdfUA2ListChecker {
    private final PdfUAValidationContext context;

    /**
     * Creates a new instance of {@link PdfUA2ListChecker}.
     *
     * @param context the validation context
     */
    public PdfUA2ListChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks if list element has correct tag structure according to PDF/UA-2 specification.
     *
     * <p>
     * Conforming files shall tag any real content within LI structure element as either Lbl or LBody. For list items,
     * if Lbl is present, not None ListNumbering attribute shall be specified on the respective L structure element.
     *
     * @param structNode list structure element to check
     *
     * @throws PdfUAConformanceException if document has incorrect tag structure for list
     */
    public void checkStructElement(IStructureNode structNode) {
        PdfStructElem list = context.getElementIfRoleMatches(PdfName.L, structNode);
        if (list == null) {
            return;
        }
        boolean isLblPresent = false;
        for (IStructureNode listItem : list.getKids()) {
            final String listItemRole = context.resolveToStandardRole(listItem);
            if (StandardRoles.LI.equals(listItemRole)) {
                for (IStructureNode kid : listItem.getKids()) {
                    final String kidRole = context.resolveToStandardRole(kid);
                    if (StandardRoles.LBL.equals(kidRole)) {
                        isLblPresent = true;
                    } else if (!StandardRoles.LBODY.equals(kidRole) && !StandardRoles.ARTIFACT.equals(kidRole)) {
                        throw new PdfUAConformanceException(
                                PdfUAExceptionMessageConstants.LIST_ITEM_CONTENT_HAS_INVALID_TAG);
                    }
                }
            }
        }
        if (isLblPresent) {
            boolean isValidListNumbering = false;
            for (PdfStructureAttributes attribute : list.getAttributesList()) {
                String listNumValue = attribute.getAttributeAsEnum(PdfName.ListNumbering.getValue());
                if (listNumValue != null) {
                    if (!PdfName.None.getValue().equals(listNumValue)) {
                        isValidListNumbering = true;
                    }
                    break;
                }
            }
            if (!isValidListNumbering) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.LIST_NUMBERING_IS_NOT_SPECIFIED);
            }
        }
    }

    /**
     * Handler class that checks list tags while traversing the tag tree.
     */
    public static class PdfUA2ListHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA2ListChecker checker;

        /**
         * Creates a new instance of {@link PdfUA2ListHandler}.
         *
         * @param context the validation context
         */
        public PdfUA2ListHandler(PdfUAValidationContext context) {
            super(context);
            checker = new PdfUA2ListChecker(context);
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

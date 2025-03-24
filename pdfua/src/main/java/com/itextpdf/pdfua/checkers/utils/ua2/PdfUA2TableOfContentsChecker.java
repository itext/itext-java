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
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class which performs table of contents check according to PDF/UA-2 specification.
 */
public final class PdfUA2TableOfContentsChecker {
    private final PdfUAValidationContext context;

    /**
     * Creates a new instance of {@link PdfUA2TableOfContentsChecker}.
     *
     * @param context the validation context
     */
    public PdfUA2TableOfContentsChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks that table of contents item identifies the target of the reference according to PDF/UA-2 specification.
     *
     * <p>
     * Each {@code TOCI} in the table of contents shall identify the target of the reference using the {@code Ref}
     * entry, either directly on the {@code TOCI} structure element itself or on a child structure element contained
     * within, such as a {@code Reference} structure element.
     *
     * @param structNode {@code TOCI} structure element to check
     *
     * @throws PdfUAConformanceException if document has incorrect table of contents item
     */
    public void checkRefInTociStructElement(IStructureNode structNode) {
        PdfStructElem toci = context.getElementIfRoleMatches(PdfName.TOCI, structNode);
        if (toci == null) {
            return;
        }
        if (!isRefPresent(toci)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.TOCI_SHALL_IDENTIFY_REF);
        }
    }

    private static boolean isRefPresent(PdfStructElem toci) {
        if (!toci.getRefsList().isEmpty()) {
            return true;
        }
        for (IStructureNode kid : toci.getKids()) {
            if (kid instanceof PdfStructElem && isRefPresent((PdfStructElem) kid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handler class that checks {@code TOCI} tags while traversing the tag tree.
     */
    public static class PdfUA2TableOfContentsHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA2TableOfContentsChecker checker;

        /**
         * Creates a new instance of {@link PdfUA2TableOfContentsHandler}.
         *
         * @param context the validation context
         */
        public PdfUA2TableOfContentsHandler(PdfUAValidationContext context) {
            super(context);
            checker = new PdfUA2TableOfContentsChecker(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            checker.checkRefInTociStructElement(elem);
        }
    }
}

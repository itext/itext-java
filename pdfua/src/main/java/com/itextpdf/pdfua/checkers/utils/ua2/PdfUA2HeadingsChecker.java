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

import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class which performs headings check according to PDF/UA-2 specification.
 */
public final class PdfUA2HeadingsChecker {
    private final PdfUAValidationContext context;

    /**
     * Creates a new instance of {@link PdfUA2HeadingsChecker}.
     *
     * @param context the validation context
     */
    public PdfUA2HeadingsChecker(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks if layout element has correct heading according to PDF/UA-2 specification.
     *
     * <p>
     * Conforming files shall use the explicitly numbered heading structure types (H1-Hn) and
     * shall not use the H structure type.
     *
     * <p>
     * Note, that PDF/UA-2 specification does not include requirements on the use of sequential heading levels. But
     * where a heading’s level is evident, the heading level of the structure element enclosing it shall match that
     * heading level, e.g. a heading with the real content “5.1.6.4 Some header” is evidently at heading level 4.
     * This requirement is not checked.
     *
     * @param renderer layout element to check
     *
     * @throws PdfUAConformanceException if document uses the H structure type
     */
    public void checkLayoutElement(IRenderer renderer) {
        IPropertyContainer element = renderer.getModelElement();
        if (element instanceof IAccessibleElement) {
            IAccessibleElement accessibleElement = (IAccessibleElement) element;
            String role = context.resolveToStandardRole(accessibleElement.getAccessibilityProperties().getRole());

            if (StandardRoles.H.equals(role)) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
            }
        }
    }

    /**
     * Checks if layout element has correct heading according to PDF/UA-2 specification.
     *
     * <p>
     * Conforming files shall use the explicitly numbered heading structure types (H1-Hn) and
     * shall not use the H structure type.
     *
     * <p>
     * Note, that PDF/UA-2 specification does not include requirements on the use of sequential heading levels. But
     * where a heading’s level is evident, the heading level of the structure element enclosing it shall match that
     * heading level, e.g. a heading with the real content “5.1.6.4 Some header” is evidently at heading level 4.
     * This requirement is not checked.
     *
     * @param structNode structure element to check
     *
     * @throws PdfUAConformanceException if document uses the H structure type
     */
    public void checkStructElement(IStructureNode structNode) {
        final String role = context.resolveToStandardRole(structNode);
        if (role == null) {
            return;
        }

        if (StandardRoles.H.equals(role)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_USES_H_TAG);
        }
    }

    /**
     * Handler class that checks heading tags while traversing the tag tree.
     */
    public static class PdfUA2HeadingHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA2HeadingsChecker checker;

        /**
         * Creates a new instance of {@link PdfUA2HeadingsChecker}.
         *
         * @param context the validation context
         */
        public PdfUA2HeadingHandler(PdfUAValidationContext context) {
            super(context);
            checker = new PdfUA2HeadingsChecker(context);
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

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
package com.itextpdf.pdfua.checkers.utils.headings;

import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1HeadingsChecker;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;

/**
 * Utility class which performs headings check according to PDF/UA-1 specification.
 *
 * @deprecated in favor of {@link com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1HeadingsChecker}
 */
@Deprecated
public final class HeadingsChecker {
    private PdfUA1HeadingsChecker headingsChecker;

    /**
     * Creates a new instance of {@link HeadingsChecker}.
     *
     * @param context the validation context
     */
    public HeadingsChecker(PdfUAValidationContext context) {
        this.headingsChecker = new PdfUA1HeadingsChecker(context);
    }

    /**
     * Checks if layout element has correct heading.
     *
     * @param renderer layout element to check
     *
     * @throws PdfUAConformanceException if headings sequence is incorrect
     */
    public void checkLayoutElement(IRenderer renderer) {
        this.headingsChecker.checkLayoutElement(renderer);
    }

    /**
     * Checks if structure element has correct heading.
     *
     * @param structNode structure element to check
     *
     * @throws PdfUAConformanceException if headings sequence is incorrect
     */
    public void checkStructElement(IStructureNode structNode) {
        this.headingsChecker.checkStructElement(structNode);
    }

    /**
     * Handler class that checks heading tags while traversing the tag tree.
     *
     * @deprecated in favor of {@link com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1HeadingsChecker.PdfUA1HeadingHandler}
     */
    @Deprecated
    public static class HeadingHandler extends ContextAwareTagTreeIteratorHandler {
        private final PdfUA1HeadingsChecker checker;

        /**
         * Creates a new instance of {@link PdfUA1HeadingsChecker}.
         *
         * @param context the validation context
         */
        public HeadingHandler(PdfUAValidationContext context) {
            super(context);
            checker = new PdfUA1HeadingsChecker(context);
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

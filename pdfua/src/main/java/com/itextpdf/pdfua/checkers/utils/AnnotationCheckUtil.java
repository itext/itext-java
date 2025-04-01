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
package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1AnnotationChecker;

/**
 * Class that provides methods for checking PDF/UA compliance of annotations.
 *
 * @deprecated in favor of {@link PdfUA1AnnotationChecker}
 */
@Deprecated
public final class AnnotationCheckUtil {
    private AnnotationCheckUtil() {
        // Empty constructor.
    }

    /**
     * Is annotation visible: {@code true} if hidden flag isn't
     * set and annotation intersects CropBox (default value is MediaBox).
     *
     * @param annotDict annotation to check
     * @return {@code true} if annotation should be checked, otherwise {@code false}
     */
    public static boolean isAnnotationVisible(PdfDictionary annotDict) {
        return PdfUA1AnnotationChecker.isAnnotationVisible(annotDict);
    }

    /**
     * Helper class that checks the conformance of annotations while iterating the tag tree structure.
     *
     * @deprecated in favor of {@link PdfUA1AnnotationChecker.PdfUA1AnnotationHandler}
     */
    @Deprecated
    public static class AnnotationHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new instance of the {@link AnnotationCheckUtil.AnnotationHandler}.
         *
         * @param context The validation context.
         */
        public AnnotationHandler(PdfUAValidationContext context) {
            super(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            PdfUA1AnnotationChecker.checkElement(this.context, elem);
        }
    }
}

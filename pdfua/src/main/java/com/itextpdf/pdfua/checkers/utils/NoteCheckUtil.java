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
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

/**
 * Utility class for delegating notes checks to the correct checking logic.
 */
public class NoteCheckUtil {
    /**
     * Handler for checking Note elements in the TagTree.
     */
    public static class NoteTagHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new {@link  NoteCheckUtil.NoteTagHandler} instance.
         * @param context The validation context.
         */
        public NoteTagHandler(PdfUAValidationContext context) {
            super(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            final PdfStructElem structElem = context.getElementIfRoleMatches(PdfName.Note, elem);
            if (structElem == null) {
                return;
            }
            final PdfDictionary pdfObject = structElem.getPdfObject();
            if (pdfObject.get(PdfName.ID) == null) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.NOTE_TAG_SHALL_HAVE_ID_ENTRY);
            }
        }
    }
}

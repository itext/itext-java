/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.checkers.utils.ContextAwareTagTreeIteratorHandler;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;

/**
 * Class that provides methods for checking PDF/UA compliance of table elements.
 */
public final class TableCheckUtil {

    private final PdfUAValidationContext context;

    /**
     * Creates a new {@link TableCheckUtil} instance.
     *
     * @param context the validation context.
     */
    public TableCheckUtil(PdfUAValidationContext context) {
        this.context = context;
    }

    /**
     * Checks if the table is pdf/ua compliant.
     *
     * @param table the table to check.
     */
    public void checkTable(Table table) {
        new CellResultMatrix(table, this.context);
    }

    /**
     * Handler class that checks table tags.
     */
    public static class TableHandler extends ContextAwareTagTreeIteratorHandler {

        /**
         * Creates a new instance of {@link TableHandler}.
         *
         * @param context the validationContext
         */
        public TableHandler(PdfUAValidationContext context) {
            super(context);
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            PdfStructElem table = context.getElementIfRoleMatches(PdfName.Table, elem);
            if (table == null) {
                return;
            }
            new StructTreeResultMatrix((PdfStructElem) elem, context).checkValidTableTagging();
        }
    }

}



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
import com.itextpdf.kernel.pdf.tagutils.ITagTreeIteratorHandler;
import com.itextpdf.layout.element.Table;

/**
 * Class that provides methods for checking PDF/UA compliance of table elements.
 */
public final class TableCheckUtil {

    /**
     * Creates a new {@link TableCheckUtil} instance.
     */
    private TableCheckUtil() {
        // Empty constructor
    }

    /**
     * Checks if the table is pdf/ua compliant.
     *
     * @param table the table to check.
     */
    public static void checkLayoutTable(Table table) {
        new CellResultMatrix(table).checkValidTableTagging();
    }

    /**
     * Creates a {@link ITagTreeIteratorHandler} that handles the PDF/UA1 verification
     * of table elements on closing.
     *
     * @return The created handler.
     */
    public static ITagTreeIteratorHandler createTagTreeHandler() {
        return new ITagTreeIteratorHandler() {
            @Override
            public void nextElement(IStructureNode elem) {
                if (elem == null) {
                    return;
                }
                if (elem instanceof PdfStructElem && PdfName.Table.equals(elem.getRole())) {
                    new StructTreeResultMatrix((PdfStructElem) elem).checkValidTableTagging();
                }
            }
        };
    }


}



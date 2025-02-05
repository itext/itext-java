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
package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that has the result of the algorithm that checks the table for PDF/UA compliance.
 */
final class CellResultMatrix extends AbstractResultMatrix<Cell> {

    /**
     * Creates a new {@link CellResultMatrix} instance.
     *
     * @param table   The table that needs to be checked.
     * @param context The validation context.
     */
    public CellResultMatrix(Table table, PdfUAValidationContext context) {
        super(new TableCellIterator(table, context));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<byte[]> getHeaders(Cell cell) {
        PdfObject headerArray = getAttribute(cell.getAccessibilityProperties(), PdfName.Headers);
        if (headerArray == null) {
            return null;
        }
        //If it's not an array, we return an empty list to trigger failure.
        if (!headerArray.isArray()) {
            return new ArrayList<>();
        }
        PdfArray array = (PdfArray) headerArray;
        List<byte[]> result = new ArrayList<>();
        for (PdfObject pdfObject : array) {
            result.add(((PdfString) pdfObject).getValueBytes());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getScope(Cell cell) {
        PdfName pdfStr = (PdfName) getAttribute(cell.getAccessibilityProperties(), PdfName.Scope);
        return pdfStr == null ? null : pdfStr.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte[] getElementId(Cell cell) {
        return cell.getAccessibilityProperties().getStructureElementId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getRole(Cell cell) {
        return ((TableCellIterator) iterator).context.resolveToStandardRole(
                cell.getAccessibilityProperties().getRole());
    }

    private static PdfObject getAttribute(AccessibilityProperties props, PdfName name) {
        for (PdfStructureAttributes attributes : props.getAttributesList()) {
            PdfObject obj = attributes.getPdfObject().get(name);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }
}

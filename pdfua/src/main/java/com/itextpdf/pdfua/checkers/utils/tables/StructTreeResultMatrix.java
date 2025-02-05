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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The result matrix to validate PDF UA1 tables based on the TagTreeStructure of the document.
 */
class StructTreeResultMatrix extends AbstractResultMatrix<PdfStructElem> {

    /**
     * Creates a new {@link StructTreeResultMatrix} instance.
     *
     * @param elem a table structure element.
     * @param context The validation context.
     */
    public StructTreeResultMatrix(PdfStructElem elem, PdfUAValidationContext context) {
        super(new TableStructElementIterator(elem, context));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<byte[]> getHeaders(PdfStructElem cell) {
        PdfObject object = cell.getAttributes(false);
        PdfArray pdfArr = null;
        if (object instanceof PdfArray) {
            PdfArray array = (PdfArray) object;
            for (PdfObject pdfObject : array) {
                if (pdfObject instanceof PdfDictionary) {
                    pdfArr = ((PdfDictionary) pdfObject).getAsArray(PdfName.Headers);
                }
            }
        } else if (object instanceof PdfDictionary) {
            pdfArr = ((PdfDictionary) object).getAsArray(PdfName.Headers);
        }
        if (pdfArr == null) {
            return null;
        }
        List<byte[]> list = new ArrayList<>();
        for (PdfObject pdfObject : pdfArr) {
            PdfString str = (PdfString) pdfObject;
            list.add(str.getValueBytes());
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getScope(PdfStructElem cell) {
        PdfObject object = cell.getAttributes(false);
        if (object instanceof PdfArray) {
            PdfArray array = (PdfArray) object;
            for (PdfObject pdfObject : array) {
                if (pdfObject instanceof PdfDictionary) {
                    PdfName f = ((PdfDictionary) pdfObject).getAsName(PdfName.Scope);
                    if (f != null) {
                        return f.getValue();
                    }
                }
            }
        } else if (object instanceof PdfDictionary) {
            PdfName f = ((PdfDictionary) object).getAsName(PdfName.Scope);
            if (f != null) {
                return f.getValue();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte[] getElementId(PdfStructElem cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getStructureElementId() == null) {
            return null;
        }
        return cell.getStructureElementId().getValueBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getRole(PdfStructElem cell) {
        return ((TableStructElementIterator)iterator).context.resolveToStandardRole(cell);
    }

}

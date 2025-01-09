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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.GenericNameTree;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.validation.context.DuplicateIdEntryValidationContext;


/**
 * Models the tree of structure element IDs.
 * This is an optional feature of tagged PDF documents.
 */
public class PdfStructIdTree extends GenericNameTree {
    PdfStructIdTree(PdfDocument pdfDoc) {
        super(pdfDoc);
    }

    /**
     * Parse a structure element ID tree into its in-memory representation.
     *
     * @param pdfDoc the associated {@link PdfDocument}
     * @param dict   the {@link PdfDictionary} from which to parse the tree
     * @return the parsed {@link PdfStructIdTree}
     */
    static PdfStructIdTree readFromDictionary(PdfDocument pdfDoc, PdfDictionary dict) {
        PdfStructIdTree structIdTree = new PdfStructIdTree(pdfDoc);
        structIdTree.setItems(GenericNameTree.readTree(dict));
        return structIdTree;
    }

    /**
     * Retrieve a structure element by ID, if it has one.
     *
     * @param id  the ID of the structure element to retrieve
     * @return the structure element with the given ID if one exists, or null otherwise.
     */
    public PdfStructElem getStructElemById(PdfString id) {
        PdfObject rawObj = this.getItems().get(id);
        if(rawObj instanceof PdfIndirectReference) {
            rawObj = ((PdfIndirectReference) rawObj).getRefersTo();
        }
        if(rawObj instanceof PdfDictionary) {
            return new PdfStructElem((PdfDictionary) rawObj);
        }
        return null;
    }

    /**
     * Retrieve a structure element by ID, if it has one.
     *
     * @param id  the ID of the structure element to retrieve
     * @return the structure element with the given ID if one exists, or null otherwise.
     */
    public PdfStructElem getStructElemById(byte[] id) {
        return this.getStructElemById(new PdfString(id));
    }

    @Override
    public void addEntry(PdfString key, PdfObject value) {
        super.addEntry(key, value, pdfDoc -> pdfDoc.checkIsoConformance(new DuplicateIdEntryValidationContext(key)));
    }
}

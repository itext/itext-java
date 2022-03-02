/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.GenericNameTree;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;


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
}

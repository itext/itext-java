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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

public class PdfFixedPrint extends PdfObjectWrapper<PdfDictionary> {


	public PdfFixedPrint() {
        this(new PdfDictionary());
    }

    public PdfFixedPrint(PdfDictionary pdfObject) {
        super(pdfObject);
        pdfObject.put(PdfName.Type, PdfName.FixedPrint);
    }

    public PdfFixedPrint setMatrix(PdfArray matrix){
        getPdfObject().put(PdfName.Matrix, matrix);
        return this;
    }

    public PdfFixedPrint setMatrix(float[] matrix) {
        getPdfObject().put(PdfName.Matrix, new PdfArray(matrix));
        return this;
    }

    public PdfFixedPrint setHorizontalTranslation(float horizontal){
        getPdfObject().put(PdfName.H, new PdfNumber(horizontal));
        return this;
    }

    public PdfFixedPrint setVerticalTranslation(float vertical){
        getPdfObject().put(PdfName.V, new PdfNumber(vertical));
        return this;
    }

    public PdfArray getMatrix() {
        return getPdfObject().getAsArray(PdfName.Matrix);
    }

    public PdfNumber getHorizontalTranslation() {
        return getPdfObject().getAsNumber(PdfName.H);
    }

    public PdfNumber getVerticalTranslation() {
        return getPdfObject().getAsNumber(PdfName.V);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}

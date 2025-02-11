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
package com.itextpdf.signatures;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

/**
 * A dictionary that stores the name of the application that signs the PDF.
 */
public class PdfSignatureApp extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates a new PdfSignatureApp
     */
    public PdfSignatureApp() {
        super(new PdfDictionary());
    }

    /**
     * Creates a new PdfSignatureApp.
     *
     * @param pdfObject PdfDictionary containing initial values
     */
    public PdfSignatureApp(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Sets the signature created property in the Prop_Build dictionary's App
     * dictionary.
     *
     * @param name String name of the application creating the signature
     */
    public void setSignatureCreator(String name) {
        getPdfObject().put(PdfName.Name, new PdfName(name));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}

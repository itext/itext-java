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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * This class is a wrapper around a Windows launch parameter dictionary.
 */
public class PdfWin extends PdfObjectWrapper<PdfDictionary> {


    /**
     * Creates a new wrapper around an existing Windows launch parameter dictionary.
     *
     * @param pdfObject the dictionary to create a wrapper for
     */
    public PdfWin(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a new wrapper around a newly created Windows launch parameter dictionary.
     *
     * @param f the file name of the application that shall be launched or the document that shall be opened or printed,
     *          in standard Windows pathname format. If the name string includes a backslash character (\),
     *          the backslash shall itself be preceded by a backslash.
     */
    public PdfWin(PdfString f) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.F, f);
    }

    /**
     * Creates a new wrapper around a newly created Windows launch parameter dictionary.
     *
     * @param f the file name of the application that shall be launched or the document that shall be opened or printed,
     *          in standard Windows pathname format. If the name string includes a backslash character (\),
     *          the backslash shall itself be preceded by a backslash
     * @param d a bye string specifying the default directory in standard DOS syntax
     * @param o an ASCII string specifying the operation to perform on the file. Shall be one of the following:
     *          "open", "print"
     * @param p a parameter string that shall be passed to the application.
     *          This entry shall be omitted if a document is abound to be opened
     */
    public PdfWin(PdfString f, PdfString d, PdfString o, PdfString p) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.F, f);
        getPdfObject().put(PdfName.D, d);
        getPdfObject().put(PdfName.O, o);
        getPdfObject().put(PdfName.P, p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}

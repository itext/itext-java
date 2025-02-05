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

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;

public class PdfMcrDictionary extends PdfMcr {


    public PdfMcrDictionary(PdfDictionary pdfObject, PdfStructElem parent) {
        super(pdfObject, parent);
    }

    public PdfMcrDictionary(PdfPage page, PdfStructElem parent) {
        super(new PdfDictionary(), parent);
        PdfDictionary dict = (PdfDictionary) getPdfObject();
        dict.put(PdfName.Type, PdfName.MCR);
        // Explicitly using object indirect reference here in order to correctly process released objects.
        dict.put(PdfName.Pg, page.getPdfObject().getIndirectReference());
        dict.put(PdfName.MCID, new PdfNumber(page.getNextMcid()));
    }

    @Override
    public int getMcid() {
        PdfNumber mcidNumber = ((PdfDictionary) getPdfObject()).getAsNumber(PdfName.MCID);
        return mcidNumber != null ? mcidNumber.intValue() : -1;
    }

    @Override
    public PdfDictionary getPageObject() {
        return super.getPageObject();
    }
}

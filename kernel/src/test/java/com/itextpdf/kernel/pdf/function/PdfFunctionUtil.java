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
package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

public final class PdfFunctionUtil {
    private PdfFunctionUtil() {
        // do nothing
    }

    public static PdfDictionary createMinimalPdfType0FunctionDict() {
        PdfDictionary type0Func = new PdfDictionary();
        type0Func.put(PdfName.FunctionType, new PdfNumber(0));

        PdfArray domain = new PdfArray(new int[] {0, 1, 0, 1});
        type0Func.put(PdfName.Domain, domain);

        type0Func.put(PdfName.Size, new PdfArray(new double[] {2, 2}));

        return type0Func;
    }

    public static PdfDictionary createMinimalPdfType2FunctionDict() {
        PdfDictionary type2Func = new PdfDictionary();
        type2Func.put(PdfName.FunctionType, new PdfNumber(2));

        PdfArray domain = new PdfArray(new int[] {0, 1});
        type2Func.put(PdfName.Domain, domain);

        type2Func.put(PdfName.N, new PdfNumber(2));

        return type2Func;
    }
}

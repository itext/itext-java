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
package com.itextpdf.pdfa.checker;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfACheckerTestUtils {
    private PdfACheckerTestUtils() {
    }

    static PdfString getLongString(int length) {
        return new PdfString(getLongPlainString(length));
    }

    static PdfName getLongName(int length) {
        return new PdfName(getLongPlainString(length));
    }

    static PdfArray getLongArray(int length) {
        PdfArray array = new PdfArray();
        for (int i = 0; i < length; i++) {
            array.add(new PdfNumber(i));
        }
        return array;
    }

    static PdfDictionary getLongDictionary(int length) {
        PdfDictionary dict = new PdfDictionary();
        for (int i = 0; i < length; i++) {
            dict.put(new PdfName("value #" + i), new PdfNumber(i));
        }
        return dict;
    }

    static PdfStream getStreamWithLongDictionary(int length) {
        PdfStream stream = new PdfStream("Hello, world!".getBytes());
        for (int i = 0; i < length; i++) {
            stream.put(new PdfName("value #" + i), new PdfNumber(i));
        }
        return stream;
    }

    static String getStreamWithValue(PdfObject object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfOutputStream stream = new PdfOutputStream(baos);
        stream.write(object);
        return "q\n"
                + "BT\n"
                + "/F1 12 Tf\n"
                + "36 787.96 Td\n"
                + new String(baos.toByteArray()) + " Tj\n"
                + "ET\n"
                + "Q";
    }

    private static String getLongPlainString(int length) {
        final char charToFill = 'A';
        char[] array = new char[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = charToFill;
        }
        return new String(array);
    }
}

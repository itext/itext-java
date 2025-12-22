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
package com.itextpdf.io.font;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfEncodingsTest extends ExtendedITextTest {

    @Test
    public void convertToBytesNoEncodingTest() {
        Assertions.assertArrayEquals(new byte[]{(byte) 194}, PdfEncodings.convertToBytes('Â', null));
        Assertions.assertArrayEquals(new byte[]{(byte) 194}, PdfEncodings.convertToBytes('Â', ""));
        Assertions.assertArrayEquals(new byte[]{(byte) 194}, PdfEncodings.convertToBytes('Â', "symboltt"));
    }

    @Test
    public void convertToBytesSymbolTTTest() {
        Assertions.assertArrayEquals(new byte[]{}, PdfEncodings.convertToBytes('原', "symboltt"));
        Assertions.assertArrayEquals(new byte[]{}, PdfEncodings.convertToBytes((char) 21407, "symboltt"));
        Assertions.assertArrayEquals(new byte[]{(byte) 159}, PdfEncodings.convertToBytes((char) 21407, null));
    }

    @Test
    public void convertToBytesExtraEncodingTest() {
        Assertions.assertArrayEquals(new byte[]{}, PdfEncodings.convertToBytes('奆', "symbol"));
        Assertions.assertArrayEquals(new byte[]{}, PdfEncodings.convertToBytes('奆', PdfEncodings.WINANSI));
        PdfEncodings.addExtraEncoding("TestExtra", new IExtraEncoding() {
            @Override
            public byte[] charToByte(String text, String encoding) {
                return null;
            }

            @Override
            public byte[] charToByte(char char1, String encoding) {
                return null;
            }

            @Override
            public String byteToChar(byte[] b, String encoding) {
                return "";
            }
        });
        Assertions.assertArrayEquals(new byte[]{}, PdfEncodings.convertToBytes('奆', "TestExtra"));
        Assertions.assertArrayEquals(new byte[]{}, PdfEncodings.convertToBytes("奆時灈", "TestExtra"));
    }
}

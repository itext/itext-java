/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.io.source;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.nio.charset.StandardCharsets;

@Tag("UnitTest")
public class WriteStringsTest extends ExtendedITextTest {

    @Test
    public void writeStringTest() {
        String str = "SomeString";
        byte[] content = ByteUtils.getIsoBytes(str);
        Assertions.assertArrayEquals(str.getBytes(StandardCharsets.ISO_8859_1), content);
    }

    @Test
    public void writeNameTest() {
        String str = "SomeName";
        byte[] content = ByteUtils.getIsoBytes((byte) '/', str);
        Assertions.assertArrayEquals(("/" + str).getBytes(StandardCharsets.ISO_8859_1), content);
    }

    @Test
    public void writePdfStringTest() {
        String str = "Some PdfString";
        byte[] content = ByteUtils.getIsoBytes((byte) '(', str, (byte) ')');
        Assertions.assertArrayEquals(("(" + str + ")").getBytes(StandardCharsets.ISO_8859_1), content);
    }
}

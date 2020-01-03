/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.io.font.cmap;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CMapByteCidTest extends ExtendedITextTest {

    @Test
    public void addCharAndDecodeByteCodeTest() {
        CMapByteCid cMapByteCid = new CMapByteCid();
        char byteCode = (char)0x94e0;
        int cid = 7779;

        byte[] byteCodeBytes = {(byte) ((byteCode & 0xFF00) >> 8), (byte) (byteCode & 0xFF)};
        char[] charPerByteSequence = {(char) byteCodeBytes[0], (char) byteCodeBytes[1]};

        cMapByteCid.addChar(new String(charPerByteSequence), new CMapObject(CMapObject.NUMBER, cid));

        String actual = cMapByteCid.decodeSequence(byteCodeBytes, 0, 2);
        String expected = new String(new char[]{(char) cid});

        Assert.assertEquals(expected, actual);
    }
}

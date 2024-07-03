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
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.test.ExtendedITextTest;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CMapCidToCodepointTest extends ExtendedITextTest {
    @Test
    public void addCharAndLookupTest() {
        CMapCidToCodepoint cidToCode = new CMapCidToCodepoint();
        Assertions.assertArrayEquals(new byte[0], cidToCode.lookup(14));
        cidToCode.addChar(new String(new byte[] {32, 17}), new CMapObject(CMapObject.NUMBER, 14));
        cidToCode.addChar(new String(new byte[] {32, 19}), new CMapObject(CMapObject.STRING, "some text"));

        Assertions.assertArrayEquals(new byte[] {32, 17}, cidToCode.lookup(14));
        Assertions.assertArrayEquals(new byte[0], cidToCode.lookup(1));
    }

    @Test
    public void getReverseMapTest() {
        CMapCidToCodepoint cidToCode = new CMapCidToCodepoint();
        cidToCode.addChar(new String(new byte[] {32, 17}), new CMapObject(CMapObject.NUMBER, 14));
        cidToCode.addChar(new String(new byte[] {32, 18}), new CMapObject(CMapObject.NUMBER, 15));

        IntHashtable table = cidToCode.getReversMap();
        Assertions.assertEquals(2, table.size());
        Assertions.assertEquals(14, table.get(8209));
        Assertions.assertEquals(15, table.get(8210));
    }

    @Test
    public void addAndGetCodeSpaceRangeTest() {
        CMapCidToCodepoint cidToCode = new CMapCidToCodepoint();
        Assertions.assertTrue(cidToCode.getCodeSpaceRanges().isEmpty());

        cidToCode.addCodeSpaceRange(new byte[] {11}, new byte[] {12, 13});
        cidToCode.addCodeSpaceRange(null, new byte[] {});
        List<byte[]> codeSpaceRanges = cidToCode.getCodeSpaceRanges();
        Assertions.assertEquals(4, codeSpaceRanges.size());
        Assertions.assertArrayEquals(new byte[] {11}, codeSpaceRanges.get(0));
        Assertions.assertArrayEquals(new byte[] {12, 13}, codeSpaceRanges.get(1));
        Assertions.assertNull(codeSpaceRanges.get(2));
        Assertions.assertArrayEquals(new byte[] {}, codeSpaceRanges.get(3));
    }
}

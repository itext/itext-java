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
package com.itextpdf.io.font.cmap;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CMapCodepointToCidTest extends ExtendedITextTest {
    @Test
    public void reverseConstructorTest() {
        CMapCidToCodepoint cidToCode = new CMapCidToCodepoint();
        cidToCode.addChar(new String(new byte[] {32, 17}), new CMapObject(CMapObject.NUMBER, 14));
        cidToCode.addChar(new String(new byte[] {32, 18}), new CMapObject(CMapObject.NUMBER, 15));

        CMapCodepointToCid codeToCid = new CMapCodepointToCid(cidToCode);
        Assertions.assertEquals(14, codeToCid.lookup(8209));
        Assertions.assertEquals(15, codeToCid.lookup(8210));
    }

    @Test
    public void addCharAndLookupTest() {
        CMapCodepointToCid codeToCid = new CMapCodepointToCid();
        Assertions.assertEquals(0, codeToCid.lookup(8209));

        codeToCid.addChar(new String(new byte[] {32, 17}), new CMapObject(CMapObject.NUMBER, 14));
        codeToCid.addChar(new String(new byte[] {32, 19}), new CMapObject(CMapObject.STRING, "some text"));

        Assertions.assertEquals(14, codeToCid.lookup(8209));
        Assertions.assertEquals(0, codeToCid.lookup(1));
    }
}

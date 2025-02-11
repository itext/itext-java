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

import com.itextpdf.io.font.cmap.CMapByteCid;
import com.itextpdf.io.font.cmap.CMapCidToCodepoint;
import com.itextpdf.io.font.cmap.CMapCidUni;
import com.itextpdf.io.font.cmap.CMapCodepointToCid;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class CjkResourceLoaderTest extends ExtendedITextTest {

    @Test
    public void getCompatibleCidFont() {
        String expected = "HeiseiMin-W3";

        String compatibleCidFont = CjkResourceLoader.getCompatibleCidFont("78-RKSJ-V");
        Assertions.assertEquals(expected, compatibleCidFont);
    }

    @Test
    public void getCompatibleCmaps() {
        Set<String> compatibleCmaps = CjkResourceLoader.getCompatibleCmaps("HeiseiKakuGo-W5");
        Assertions.assertEquals(66, compatibleCmaps.size());
        Assertions.assertTrue(compatibleCmaps.contains("78-RKSJ-V"));
    }

    @Test
    public void getRegistryNames() {
        Map<String, Set<String>> registryNames = CjkResourceLoader.getRegistryNames();
        Assertions.assertEquals(9, registryNames.size());
        Assertions.assertTrue(registryNames.containsKey("Adobe_Japan1"));
        Assertions.assertTrue(registryNames.get("Adobe_Japan1").contains("78-RKSJ-V"));
    }

    @Test
    public void getCid2UniCMap() {
        CMapCidUni cid2UniCmap = CjkResourceLoader.getCid2UniCmap("UniJIS-UTF16-H");
        Assertions.assertEquals(0x00b5, cid2UniCmap.lookup(159));
    }

    @Test
    public void getUni2CidCMap() {
        CMapCodepointToCid uni2CidCmap = CjkResourceLoader.getCodepointToCidCmap("UniJIS-UTF16-H");
        Assertions.assertEquals(159, uni2CidCmap.lookup(0x00b5));
    }

    @Test
    public void getByte2CidCMap() {
        CMapByteCid byte2CidCmap = CjkResourceLoader.getByte2CidCmap("78ms-RKSJ-H");
        int byteCode = 0x94e0;
        char cid = (char) 7779;

        byte[] byteCodeBytes = {(byte) ((byteCode & 0xFF00) >> 8), (byte) (byteCode & 0xFF)};
        String actual = byte2CidCmap.decodeSequence(byteCodeBytes, 0, 2);
        String expected = new String(new char[]{cid});

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getCid2ByteCMap() {
        CMapCidToCodepoint cid2Byte = CjkResourceLoader.getCidToCodepointCmap("78ms-RKSJ-H");
        int byteCode = 0x94e0;
        int cid = 7779;

        byte[] actual = cid2Byte.lookup(cid);
        byte[] expected = {(byte) ((byteCode & 0xFF00) >> 8), (byte) (byteCode & 0xFF)};
        Assertions.assertArrayEquals(expected, actual);
    }
}

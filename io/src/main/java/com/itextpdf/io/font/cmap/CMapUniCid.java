/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.io.util.TextUtil;

/**
 * Maps Unicode code points to CIDs.
 */
public class CMapUniCid extends AbstractCMap {

    private IntHashtable map = new IntHashtable(65537);

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            int codePoint;
            String s = toUnicodeString(mark, true);
            if (TextUtil.isSurrogatePair(s, 0)) {
                codePoint = TextUtil.convertToUtf32(s, 0);
            } else {
                codePoint = (int) s.charAt(0);
            }
            map.put(codePoint, (int)code.getValue());
        }
    }

    /**
     * Looks up the CID assigned to a Unicode code point.
     *
     * @param character the Unicode code point
     *
     * @return the mapped CID, or {@code 0} when absent
     */
    public int lookup(int character) {
        return map.get(character);
    }

    /**
     * Exports this mapping as a ToUnicode CMap.
     *
     * @return a newly created CMap containing this map's CID-to-Unicode mappings
     */
    public CMapToUnicode exportToUnicode() {
        CMapToUnicode uni = new CMapToUnicode();
        int[] keys = map.toOrderedKeys();
        for (int key : keys) {
            uni.addChar(map.get(key), TextUtil.convertFromUtf32(key));
        }
        int spaceCid = lookup(32);
        if (spaceCid != 0) {
            uni.addChar(spaceCid, TextUtil.convertFromUtf32(32));
        }
        return uni;
    }

    /**
     * Returns all mapped code points.
     *
     * @return an array containing all mapped code points
     */
    public int[] getCodePoints() {
        return map.getKeys();

    }
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author psoares
 */
public class CMapCidByte extends AbstractCMap {

    private static final long serialVersionUID = 4956059671207068672L;
    private Map<Integer, byte[]> map = new HashMap<>();
    private final byte[] EMPTY = {};
    private List<byte[]> codeSpaceRanges = new ArrayList<>();

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            byte[] ser = decodeStringToByte(mark);
            map.put((int)code.getValue(), ser);
        }
    }

    public byte[] lookup(int cid) {
        byte[] ser = map.get(cid);
        if (ser == null) {
            return EMPTY;
        } else {
            return ser;
        }
    }

    public IntHashtable getReversMap() {
        IntHashtable code2cid = new IntHashtable(map.size());
        for (int cid : map.keySet()) {
            byte[] bytes = map.get(cid);
            int byteCode = 0;
            for (byte b: bytes) {
                byteCode <<= 8;
                byteCode += b & 0xff;
            }
            code2cid.put(byteCode, cid);
        }
        return code2cid;
    }

    /**
     * Returns a list containing sequential pairs of code space beginning and endings:
     * (begincodespacerange1, endcodespacerange1, begincodespacerange2, endcodespacerange1, ...)
     *
     * @return list of {@code byte[]} that contain code space ranges
     */
    public List<byte[]> getCodeSpaceRanges() {
        return codeSpaceRanges;
    }

    @Override
    void addCodeSpaceRange(byte[] low, byte[] high) {
        codeSpaceRanges.add(low);
        codeSpaceRanges.add(high);
    }
}

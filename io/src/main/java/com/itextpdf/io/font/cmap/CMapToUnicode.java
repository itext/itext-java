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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.TextUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CMap file.
 */
public class CMapToUnicode extends AbstractCMap {

    public static final CMapToUnicode EMPTY_CMAP = new CMapToUnicode(true);

    private final Map<Integer, char[]> byteMappings;

    private final List<byte[]> codeSpaceRanges = new ArrayList<>();

    private CMapToUnicode(boolean emptyCMap) {
        byteMappings = Collections.<Integer, char[]>emptyMap();
    }

    /**
     * Creates a new instance of CMap.
     */
    public CMapToUnicode() {
        byteMappings = new HashMap<>();
    }

    public static CMapToUnicode getIdentity() {
        CMapToUnicode uni = new CMapToUnicode();
        for (int i = 0; i < 65537; i++) {
            uni.addChar(i, TextUtil.convertFromUtf32(i));
        }
        uni.addCodeSpaceRange(new byte[] {0, 0}, new byte[] {(byte) 0xff, (byte) 0xff});
        return uni;
    }

    /**
     * This will tell if this cmap has any two byte mappings.
     *
     * @return true If there are any two byte mappings, false otherwise.
     */
    public boolean hasByteMappings() {
        return byteMappings.size() != 0;
    }

    /**
     * This will perform a lookup into the map.
     *
     * @param code   The code used to lookup.
     * @param offset The offset into the byte array.
     * @param length The length of the data we are getting.
     * @return The string that matches the lookup.
     */
    public char[] lookup(byte[] code, int offset, int length) {
        char[] result = null;
        int key;
        if (length == 1) {
            key = code[offset] & 0xff;
            result = byteMappings.get(key);
        } else if (length == 2) {
            int intKey = code[offset] & 0xff;
            intKey <<= 8;
            intKey += code[offset + 1] & 0xff;
            key = intKey;
            result = byteMappings.get(key);
        }
        return result;
    }

    public char[] lookup(byte[] code) {
        return lookup(code, 0, code.length);
    }

    public char[] lookup(int code) {
        return byteMappings.get(code);
    }

    public Set<Integer> getCodes() {
        return byteMappings.keySet();
    }

    public IntHashtable createDirectMapping() {
        IntHashtable result = new IntHashtable();
        for (Map.Entry<Integer, char[]> entry : byteMappings.entrySet()) {
            if (entry.getValue().length == 1) {
                result.put((int) entry.getKey(), convertToInt(entry.getValue()));
            }
        }
        return result;
    }

    public Map<Integer, Integer> createReverseMapping() {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map.Entry<Integer, char[]> entry : byteMappings.entrySet()) {
            if (entry.getValue().length == 1) {
                result.put(convertToInt(entry.getValue()), entry.getKey());
            }
        }
        return result;
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

    private int convertToInt(char[] s) {
        int value = 0;
        for (int i = 0; i < s.length - 1; i++) {
            value += s[i];
            value <<= 8;
        }
        value += s[s.length - 1];
        return value;
    }

    void addChar(int cid, char[] uni) {
        byteMappings.put(cid, uni);
    }

    @Override
    void addChar(String mark, CMapObject code) {
        if (mark.length() == 1) {
            char[] dest = createCharsFromDoubleBytes((byte[]) code.getValue());
            byteMappings.put((int) mark.charAt(0), dest);
        } else if (mark.length() == 2) {
            char[] dest = createCharsFromDoubleBytes((byte[]) code.getValue());
            byteMappings.put((mark.charAt(0) << 8) + mark.charAt(1), dest);
        } else {
            Logger logger = LoggerFactory.getLogger(CMapToUnicode.class);
            logger.warn(IoLogMessageConstant.TOUNICODE_CMAP_MORE_THAN_2_BYTES_NOT_SUPPORTED);
        }
    }

    private char[] createCharsFromSingleBytes(byte[] bytes) {
        if (bytes.length == 1) {
            return new char[]{(char) (bytes[0] & 0xff)};
        } else {
            char[] chars = new char[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                chars[i] = (char) (bytes[i] & 0xff);
            }
            return chars;
        }
    }

    private char[] createCharsFromDoubleBytes(byte[] bytes) {
        char[] chars = new char[bytes.length / 2];
        for (int i = 0; i < bytes.length; i+=2) {
            chars[i/2] = (char)(((bytes[i] & 0xff) << 8) + (bytes[i+1] & 0xff));
        }
        return chars;
    }
}

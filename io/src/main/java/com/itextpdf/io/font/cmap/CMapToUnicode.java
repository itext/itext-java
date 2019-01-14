/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font.cmap;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a CMap file.
 *
 * @author Ben Litchfield (ben@benlitchfield.com)
 */
public class CMapToUnicode extends AbstractCMap {

    private static final long serialVersionUID = 1037675640549795312L;
    public static CMapToUnicode EmptyCMapToUnicodeMap = new CMapToUnicode(true);

    private Map<Integer, char[]> byteMappings;

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

    public Map<Integer, Integer> createReverseMapping() throws java.io.IOException {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map.Entry<Integer, char[]> entry : byteMappings.entrySet()) {
            if (entry.getValue().length == 1) {
                result.put(convertToInt(entry.getValue()), entry.getKey());
            }
        }
        return result;
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
            logger.warn(LogMessageConstant.TOUNICODE_CMAP_MORE_THAN_2_BYTES_NOT_SUPPORTED);
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

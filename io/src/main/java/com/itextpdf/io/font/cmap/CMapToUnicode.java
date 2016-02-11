package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.Utilities;
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

    public static CMapToUnicode EmptyCMapToUnicodeMap = new CMapToUnicode(true);

    private Map<Integer, char[]> byteMappings;

    private CMapToUnicode(boolean emptyCMap) {
        byteMappings = Collections.emptyMap();
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
            uni.addChar(i, Utilities.convertFromUtf32(i));
        }
        return uni;
    }

    /**
     * This will tell if this cmap has any two byte mappings.
     *
     * @return true If there are any two byte mappings, false otherwise.
     */
    public boolean hasByteMappings() {
        return !byteMappings.isEmpty();
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
        Integer key;
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
            if (entry.getValue().length <= 2) {
                result.put(entry.getKey(), convertToInt(entry.getValue()));
            }
        }
        return result;
    }

    public Map<Integer, Integer> createReverseMapping() throws java.io.IOException {
        Map<Integer, Integer> result = new HashMap<>();
        for (Map.Entry<Integer, char[]> entry : byteMappings.entrySet()) {
            if (entry.getValue().length <= 2) {
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
        try {
            if (mark.length() == 1) {
                char[] dest = createCharsFromSingleBytes((byte[]) code.getValue());
                byteMappings.put((int) mark.charAt(0), dest);
            } else if (mark.length() == 2) {
                char[] dest = createCharsFromDoubleBytes((byte[]) code.getValue());
                byteMappings.put((mark.charAt(0) << 8) + mark.charAt(1), dest);
            } else {
                Logger logger = LoggerFactory.getLogger(CMapToUnicode.class);
                logger.warn(LogMessageConstant.TOUNICODE_CMAP_MORE_THAN_2_BYTES_NOT_SUPPORTED);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException();
        }
    }

    private char[] createCharsFromSingleBytes(byte[] bytes) throws java.io.IOException {
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

    private char[] createCharsFromDoubleBytes(byte[] bytes) throws java.io.IOException {
        char[] chars = new char[bytes.length / 2];
        for (int i = 0; i < bytes.length; i+=2) {
            chars[i/2] = (char)(((bytes[i] & 0xff) << 8) + (bytes[i+1] & 0xff));
        }
        return chars;
    }
}

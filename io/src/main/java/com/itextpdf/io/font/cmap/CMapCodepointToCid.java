package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;

/**
 * Class represents real codepoint-CID mapping without any additional manipulation.
 *
 * <p>
 * See {@link CMapCidToCodepoint} for CID-codepoint representation.
 */
public class CMapCodepointToCid extends AbstractCMap {

    private final IntHashtable map;

    public CMapCodepointToCid() {
        map = new IntHashtable();
    }

    public CMapCodepointToCid(CMapCidToCodepoint reverseMap) {
        map = reverseMap.getReversMap();
    }

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            byte[] ser = decodeStringToByte(mark);
            int byteCode = 0;
            for (byte b: ser) {
                byteCode <<= 8;
                byteCode += b & 0xff;
            }
            map.put(byteCode, (int) code.getValue());
        }
    }

    public int lookup(int codepoint) {
        return this.map.get(codepoint);
    }
}
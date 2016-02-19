package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author psoares
 */
public class CMapCidByte extends AbstractCMap {

    private Map<Integer, byte[]> map = new HashMap<>();
    private final byte[] EMPTY = {};

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
        for (Integer cid: map.keySet()) {
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
}

package com.itextpdf.basics.font.cmap;

import java.util.HashMap;

/**
 * @author psoares
 */
public class CMapCidByte extends AbstractCMap {

    private HashMap<Integer, byte[]> map = new HashMap<Integer,byte[]>();
    private final byte[] EMPTY = {};

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            byte[] ser = decodeStringToByte(mark);
            map.put((int)code.getValue(), ser);
        }
    }

    public byte[] lookup(int cid) {
        byte[] ser = map.get(Integer.valueOf(cid));
        if (ser == null) {
            return EMPTY;
        } else {
            return ser;
        }
    }
}

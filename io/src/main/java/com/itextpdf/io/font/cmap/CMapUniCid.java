package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.Utilities;

/**
 * @author psoares
 */
public class CMapUniCid extends AbstractCMap {

    private IntHashtable map = new IntHashtable(65537);

    @Override
    void addChar(String mark, CMapObject code) {
        if (code.isNumber()) {
            int codePoint;
            String s = toUnicodeString(mark, true);
            if (Utilities.isSurrogatePair(s, 0)) {
                codePoint = Utilities.convertToUtf32(s, 0);
            } else {
                codePoint = (int) s.charAt(0);
            }
            map.put(codePoint, (int)code.getValue());
        }
    }

    public int lookup(int character) {
        return map.get(character);
    }

//    public CMapToUnicode exportToUnicode() {
//        CMapToUnicode uni = new CMapToUnicode();
//        int[] keys = map.toOrderedKeys();
//        for (int key : keys) {
//            uni.addChar(map.get(key), Utilities.convertFromUtf32(key));
//        }
//        return uni;
//    }

}

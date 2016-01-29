package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.Utilities;

/**
 * @author psoares
 */
public class CMapCidUni extends AbstractCMap {

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
            map.put((int)code.getValue(), codePoint);
        }
    }

    public int lookup(int character) {
        return map.get(character);
    }
}

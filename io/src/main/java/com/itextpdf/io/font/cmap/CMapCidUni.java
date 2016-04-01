package com.itextpdf.io.font.cmap;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.TextUtil;

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
            if (TextUtil.isSurrogatePair(s, 0)) {
                codePoint = TextUtil.convertToUtf32(s, 0);
            } else {
                codePoint = (int) s.charAt(0);
            }
            map.put((int)code.getValue(), codePoint);
        }
    }

    public int lookup(int character) {
        return map.get(character);
    }

    public int[] getCids(){
        return map.getKeys();
    }
}

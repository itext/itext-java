package com.itextpdf.core.font;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.font.cmap.CMapLocation;
import com.itextpdf.io.font.cmap.CMapLocationFromBytes;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfStream;

class FontUtils {

    static CMapToUnicode processToUnicode(PdfObject toUnicode) {
        CMapToUnicode cMapToUnicode = null;
        if (toUnicode instanceof PdfStream) {
            try {
                byte[] uniBytes = ((PdfStream) toUnicode).getBytes();
                CMapLocation lb = new CMapLocationFromBytes(uniBytes);
                cMapToUnicode = new CMapToUnicode();
                CMapParser.parseCid("", cMapToUnicode, lb);
            } catch (Exception e) {
                cMapToUnicode = CMapToUnicode.EmptyCMapToUnicodeMap;
            }
        } else if (toUnicode instanceof PdfName) {
            if (toUnicode.equals(PdfName.IdentityH)) {
                cMapToUnicode = CMapToUnicode.getIdentity();
            }
        }
        return cMapToUnicode;
    }

    static String createRandomFontName() {
        StringBuilder s = new StringBuilder("");
        for (int k = 0; k < 7; ++k) {
            s.append((char) (Math.random() * 26 + 'A'));
        }
        return s.toString();
    }

    static int[] convertSimpleWidthsArray(PdfArray widthsArray, int first) {
        int[] res = new int[256];
        if (widthsArray == null) {
            return res;
        }

        for (int i = 0; i < widthsArray.size() && first + i < 256; i++) {
            PdfNumber number = widthsArray.getAsNumber(i);
            res[first + i] = number != null ? number.getIntValue() : 0;
        }
        return res;
    }

    static IntHashtable convertCompositeWidthsArray(PdfArray widthsArray) {
        IntHashtable res = new IntHashtable();
        if (widthsArray == null) {
            return res;
        }

        for (int k = 0; k < widthsArray.size(); ++k) {
            int c1 = widthsArray.getAsInt(k);
            PdfObject obj = widthsArray.get(++k);
            if (obj.isArray()) {
                PdfArray subWidths = (PdfArray)obj;
                for (int j = 0; j < subWidths.size(); ++j) {
                    int c2 = subWidths.getAsInt(j);
                    res.put(c1++, c2);
                }
            } else {
                int c2 = ((PdfNumber)obj).getIntValue();
                int w = widthsArray.getAsInt(++k);
                for (; c1 <= c2; ++c1) {
                    res.put(c1, w);
                }
            }
        }
        return res;
    }
}

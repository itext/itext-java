package com.itextpdf.core.font;

import com.itextpdf.basics.font.cmap.CMapLocation;
import com.itextpdf.basics.font.cmap.CMapLocationFromBytes;
import com.itextpdf.basics.font.cmap.CMapParser;
import com.itextpdf.basics.font.cmap.CMapToUnicode;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfStream;

class DocFontUtils {

    static CMapToUnicode processToUnicode(PdfDictionary fontDictionary) {
        CMapToUnicode cMapToUnicode = null;
        PdfObject toUni = fontDictionary.get(PdfName.ToUnicode);
        if (toUni instanceof PdfStream) {
            try {
                byte[] uniBytes = ((PdfStream) toUni).getBytes();
                CMapLocation lb = new CMapLocationFromBytes(uniBytes);
                cMapToUnicode = new CMapToUnicode();
                CMapParser.parseCid("", cMapToUnicode, lb);
            } catch (Exception e) {
                cMapToUnicode = CMapToUnicode.EmptyCMapToUnicodeMap;
            }
        }
        return cMapToUnicode;
    }

    static String createRandomFontName() {
        StringBuilder s = new StringBuilder("");
        for (int k = 0; k < 10; ++k) {
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

}

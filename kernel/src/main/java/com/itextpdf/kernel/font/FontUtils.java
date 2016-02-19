package com.itextpdf.kernel.font;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.FontCache;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.cmap.CMapLocation;
import com.itextpdf.io.font.cmap.CMapLocationFromBytes;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.cmap.CMapUniCid;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

class FontUtils {

    private static final HashMap<String, CMapToUnicode> uniMaps = new HashMap<>();

    static CMapToUnicode processToUnicode(PdfObject toUnicode) {
        CMapToUnicode cMapToUnicode = null;
        if (toUnicode instanceof PdfStream) {
            try {
                byte[] uniBytes = ((PdfStream) toUnicode).getBytes();
                CMapLocation lb = new CMapLocationFromBytes(uniBytes);
                cMapToUnicode = new CMapToUnicode();
                CMapParser.parseCid("", cMapToUnicode, lb);
            } catch (Exception e) {
                Logger logger = LoggerFactory.getLogger(CMapToUnicode.class);
                logger.error(LogMessageConstant.UNKNOWN_ERROR_WHILE_PROCESSING_CMAP);
                cMapToUnicode = CMapToUnicode.EmptyCMapToUnicodeMap;
            }
        } else if (PdfName.IdentityH.equals(toUnicode)) {
            cMapToUnicode = CMapToUnicode.getIdentity();
        }
        return cMapToUnicode;
    }

    static CMapToUnicode getToUnicodeFromUniMap(String uniMap) {
        if (uniMap == null)
            return null;
        synchronized (uniMaps) {
            if (uniMaps.containsKey(uniMap)) {
                return uniMaps.get(uniMap);
            }
            CMapToUnicode toUnicode;
            if (PdfEncodings.IDENTITY_H.equals(uniMap)) {
                toUnicode = CMapToUnicode.getIdentity();
            } else {
                CMapUniCid uni = FontCache.getUni2CidCmap(uniMap);
                if (uni == null) {
                    return null;
                }
                toUnicode = uni.exportToUnicode();
            }
            uniMaps.put(uniMap, toUnicode);
            return toUnicode;
        }
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

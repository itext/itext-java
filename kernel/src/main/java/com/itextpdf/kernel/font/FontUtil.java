/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.font;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.FontCache;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.cmap.CMapLocationFromBytes;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.cmap.CMapUniCid;
import com.itextpdf.io.font.cmap.ICMapLocation;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontUtil {
    private static final SecureRandom NUMBER_GENERATOR = new SecureRandom();

    private static final HashMap<String, CMapToUnicode> uniMaps = new HashMap<>();

    private FontUtil() {}

    public static String addRandomSubsetPrefixForFontName(final String fontName) {
        final StringBuilder newFontName = new StringBuilder(fontName.length() + 7);
        byte[] randomByte = new byte[1];
        for (int k = 0; k < 6; ++k) {
            NUMBER_GENERATOR.nextBytes(randomByte);
            newFontName.append((char) (Math.abs(randomByte[0]%26) + 'A'));
        }
        newFontName.append('+').append(fontName);
        return newFontName.toString();
    }

    static CMapToUnicode processToUnicode(PdfObject toUnicode) {
        CMapToUnicode cMapToUnicode = null;
        if (toUnicode instanceof PdfStream) {
            try {
                byte[] uniBytes = ((PdfStream) toUnicode).getBytes();
                ICMapLocation lb = new CMapLocationFromBytes(uniBytes);
                cMapToUnicode = new CMapToUnicode();
                CMapParser.parseCid("", cMapToUnicode, lb);
            } catch (Exception e) {
                Logger logger = LoggerFactory.getLogger(CMapToUnicode.class);
                logger.error(IoLogMessageConstant.UNKNOWN_ERROR_WHILE_PROCESSING_CMAP);
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

    static int[] convertSimpleWidthsArray(PdfArray widthsArray, int first, int missingWidth) {
        int[] res = new int[256];
        Arrays.fill(res, missingWidth);
        if (widthsArray == null) {
            Logger logger = LoggerFactory.getLogger(FontUtil.class);
            logger.warn(IoLogMessageConstant.FONT_DICTIONARY_WITH_NO_WIDTHS);
            return res;
        }

        for (int i = 0; i < widthsArray.size() && first + i < 256; i++) {
            PdfNumber number = widthsArray.getAsNumber(i);
            res[first + i] = number != null ? number.intValue() : missingWidth;
        }
        return res;
    }

    static IntHashtable convertCompositeWidthsArray(PdfArray widthsArray) {
        IntHashtable res = new IntHashtable();
        if (widthsArray == null) {
            return res;
        }

        for (int k = 0; k < widthsArray.size(); ++k) {
            int c1 = widthsArray.getAsNumber(k).intValue();
            PdfObject obj = widthsArray.get(++k);
            if (obj.isArray()) {
                PdfArray subWidths = (PdfArray)obj;
                for (int j = 0; j < subWidths.size(); ++j) {
                    int c2 = subWidths.getAsNumber(j).intValue();
                    res.put(c1++, c2);
                }
            } else {
                int c2 = ((PdfNumber)obj).intValue();
                int w = widthsArray.getAsNumber(++k).intValue();
                for (; c1 <= c2; ++c1) {
                    res.put(c1, w);
                }
            }
        }
        return res;
    }
}

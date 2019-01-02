/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.font;

import com.itextpdf.io.LogMessageConstant;
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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FontUtil {

    private static final HashMap<String, CMapToUnicode> uniMaps = new HashMap<>();

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

    static int[] convertSimpleWidthsArray(PdfArray widthsArray, int first, int missingWidth) {
        int[] res = new int[256];
        for (int i = 0; i < res.length; i++) {
            res[i] = missingWidth;
        }
        if (widthsArray == null) {
            Logger logger = LoggerFactory.getLogger(FontUtil.class);
            logger.warn(LogMessageConstant.FONT_DICTIONARY_WITH_NO_WIDTHS);
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

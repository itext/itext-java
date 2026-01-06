/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.io.font.CjkResourceLoader;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.cmap.CMapContentParser;
import com.itextpdf.io.font.cmap.CMapLocationFromBytes;
import com.itextpdf.io.font.cmap.CMapLocationResource;
import com.itextpdf.io.font.cmap.CMapParser;
import com.itextpdf.io.font.cmap.CMapToUnicode;
import com.itextpdf.io.font.cmap.CMapUniCid;
import com.itextpdf.io.font.cmap.ICMapLocation;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.HighPrecisionOutputStream;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for font processing.
 */
public class FontUtil {
    private static final SecureRandom NUMBER_GENERATOR = new SecureRandom();

    private static final HashMap<String, CMapToUnicode> uniMaps = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(FontUtil.class);

    private static final String UNIVERSAL_CMAP_DIR = "toUnicode/";

    private static final Set<String> UNIVERSAL_CMAP_ORDERINGS = new HashSet<>(Arrays.asList(
            "CNS1", "GB1", "Japan1", "Korea1", "KR"));

    private FontUtil() {}

    /**
     * Adds random subset prefix (+ and 6 upper case letters) to passed font name.
     *
     * @param fontName the font add prefix to
     *
     * @return the font name with added prefix.
     */
    public static String addRandomSubsetPrefixForFontName(final String fontName) {
        final StringBuilder newFontName = getRandomFontPrefix(6);
        newFontName.append('+').append(fontName);
        return newFontName.toString();
    }

    /**
     * Processes passed {@code ToUnicode} object to {@link CMapToUnicode} instance.
     *
     * @param toUnicode the {@code ToUnicode} object
     *
     * @return parsed {@link CMapToUnicode} instance
     */
    public static CMapToUnicode processToUnicode(PdfObject toUnicode) {
        CMapToUnicode cMapToUnicode = null;
        if (toUnicode instanceof PdfStream) {
            try {
                byte[] uniBytes = ((PdfStream) toUnicode).getBytes();
                ICMapLocation lb = new CMapLocationFromBytes(uniBytes);
                cMapToUnicode = new CMapToUnicode();
                CMapParser.parseCid("", cMapToUnicode, lb);
            } catch (Exception e) {
                LOGGER.error(IoLogMessageConstant.UNKNOWN_ERROR_WHILE_PROCESSING_CMAP, e);
                cMapToUnicode = CMapToUnicode.EMPTY_CMAP;
            }
        } else if (PdfName.IdentityH.equals(toUnicode)) {
            cMapToUnicode = CMapToUnicode.getIdentity();
        }
        return cMapToUnicode;
    }

    /**
     * Converts passed {@code W} array to integer table.
     *
     * @param widthsArray the {@code W} array to convert
     *
     * @return converted {@code W} array as an integer table
     */
    public static IntHashtable convertCompositeWidthsArray(PdfArray widthsArray) {
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

    /**
     * Gets a {@code ToUnicode} {@link PdfStream} from passed glyphs.
     *
     * @param glyphs the glyphs {@code ToUnicode} will be based on
     *
     * @return the created {@code ToUnicode} {@link PdfStream}
     */
    public static PdfStream getToUnicodeStream(Set<Glyph> glyphs) {
        HighPrecisionOutputStream<ByteArrayOutputStream> stream =
                new HighPrecisionOutputStream<>(new ByteArrayOutputStream());
        stream.writeString("/CIDInit /ProcSet findresource begin\n" +
                "12 dict begin\n" +
                "begincmap\n" +
                "/CIDSystemInfo\n" +
                "<< /Registry (Adobe)\n" +
                "/Ordering (UCS)\n" +
                "/Supplement 0\n" +
                ">> def\n" +
                "/CMapName /Adobe-Identity-UCS def\n" +
                "/CMapType 2 def\n" +
                "1 begincodespacerange\n" +
                "<0000><FFFF>\n" +
                "endcodespacerange\n");

        //accumulate long tag into a subset and write it.
        List<Glyph> glyphGroup = new ArrayList<>(100);

        int bfranges = 0;
        for (Glyph glyph : glyphs) {
            if (glyph.getChars() != null) {
                glyphGroup.add(glyph);
                if (glyphGroup.size() == 100) {
                    bfranges += writeBfrange(stream, glyphGroup);
                }
            }
        }
        //flush leftovers
        bfranges += writeBfrange(stream, glyphGroup);

        if (bfranges == 0) {
            return null;
        }

        stream.writeString("endcmap\n" +
                "CMapName currentdict /CMap defineresource pop\n" +
                "end end\n");
        return new PdfStream(((ByteArrayOutputStream)stream.getOutputStream()).toByteArray());
    }

    private static int writeBfrange(HighPrecisionOutputStream<ByteArrayOutputStream> stream, List<Glyph> range) {
        if (range.isEmpty()) {
            return 0;
        }
        stream.writeInteger(range.size());
        stream.writeString(" beginbfrange\n");
        for (Glyph glyph: range) {
            String fromTo = CMapContentParser.toHex(glyph.getCode());
            stream.writeString(fromTo);
            stream.writeString(fromTo);
            stream.writeByte('<');
            for (char ch : glyph.getChars()) {
                stream.writeString(toHex4(ch));
            }
            stream.writeByte('>');
            stream.writeByte('\n');
        }
        stream.writeString("endbfrange\n");
        range.clear();
        return 1;
    }

    private static String toHex4(char ch) {
        String s = "0000" + Integer.toHexString(ch);
        return s.substring(s.length() - 4);
    }

    static CMapToUnicode parseUniversalToUnicodeCMap(String ordering) {
        if (!UNIVERSAL_CMAP_ORDERINGS.contains(ordering)) {
            return null;
        }
        String cmapRelPath = UNIVERSAL_CMAP_DIR + "Adobe-" + ordering + "-UCS2";
        CMapToUnicode cMapToUnicode = new CMapToUnicode();
        try {
            CMapParser.parseCid(cmapRelPath, cMapToUnicode, new CMapLocationResource());
        } catch (Exception e) {
            LOGGER.error(IoLogMessageConstant.UNKNOWN_ERROR_WHILE_PROCESSING_CMAP, e);
            return null;
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
                CMapUniCid uni = CjkResourceLoader.getUni2CidCmap(uniMap);
                toUnicode = uni.exportToUnicode();
            }
            uniMaps.put(uniMap, toUnicode);
            return toUnicode;
        }
    }

    static String createRandomFontName() {
        return getRandomFontPrefix(7).toString();
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

    private static StringBuilder getRandomFontPrefix(int length) {
        final StringBuilder stringBuilder = new StringBuilder();
        final byte[] randomByte = new byte[length];
        NUMBER_GENERATOR.nextBytes(randomByte);
        for (int k = 0; k < length; ++k) {
            stringBuilder.append((char) (Math.abs(randomByte[k] % 26) + 'A'));
        }
        return stringBuilder;
    }
}

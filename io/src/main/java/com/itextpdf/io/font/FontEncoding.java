/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.io.font;

import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.util.TextUtil;

import java.util.Objects;
import java.util.StringTokenizer;

public class FontEncoding {


    private static final byte[] emptyBytes = new byte[0];

    public static final String FONT_SPECIFIC = "FontSpecific";
    /**
     * A not defined character in a custom PDF encoding.
     */
    public static final String NOTDEF = ".notdef";

    /**
     * Base font encoding.
     */
    protected String baseEncoding;
    /**
     * {@code true} if the font must use its built in encoding. In that case
     * the {@code encoding} is only used to map a char to the position inside the font, not to the expected char name.
     */
    protected boolean fontSpecific;

    /**
     * Mapping map from unicode to simple code according to the encoding.
     */
    protected IntHashtable unicodeToCode;

    protected int[] codeToUnicode;

    /**
     * Encoding names.
     */
    protected String[] differences;
    /**
     * Encodings unicode differences
     */
    protected IntHashtable unicodeDifferences;

    protected FontEncoding() {
        unicodeToCode = new IntHashtable(256);
        codeToUnicode = ArrayUtil.fillWithValue(new int[256], -1);
        unicodeDifferences = new IntHashtable(256);
        fontSpecific = false;
    }

    public static FontEncoding createFontEncoding(String baseEncoding) {
        FontEncoding encoding = new FontEncoding();
        encoding.baseEncoding = normalizeEncoding(baseEncoding);
        if (encoding.baseEncoding.startsWith("#")) {
            encoding.fillCustomEncoding();
        } else {
            encoding.fillNamedEncoding();
        }
        return encoding;
    }

    public static FontEncoding createEmptyFontEncoding() {
        FontEncoding encoding = new FontEncoding();
        encoding.baseEncoding = null;
        encoding.fontSpecific = false;
        encoding.differences = new String[256];
        for (int ch = 0; ch < 256; ch++) {
            encoding.unicodeDifferences.put(ch, ch);
        }
        return encoding;
    }

    /**
     * This encoding will base on font encoding (FontSpecific encoding in Type 1 terminology)
     *
     * @return created font specific encoding
     */
    public static FontEncoding createFontSpecificEncoding() {
        FontEncoding encoding = new FontEncoding();
        encoding.fontSpecific = true;
        FontEncoding.fillFontEncoding(encoding);

        return encoding;
    }

    /**
     * Fill {@link FontEncoding} object with default data.
     *
     * @param encoding {@link FontEncoding} to fill.
     */
    public static void fillFontEncoding(FontEncoding encoding) {
        for (int ch = 0; ch < 256; ch++) {
            encoding.unicodeToCode.put(ch, ch);
            encoding.codeToUnicode[ch] = ch;
            encoding.unicodeDifferences.put(ch, ch);
        }
    }

    public String getBaseEncoding() {
        return baseEncoding;
    }

    public boolean isFontSpecific() {
        return fontSpecific;
    }

    public boolean addSymbol(int code, int unicode) {
        if (code < 0 || code > 255) {
            return false;
        }
        String glyphName = AdobeGlyphList.unicodeToName(unicode);
        if (glyphName != null) {
            unicodeToCode.put(unicode, code);
            codeToUnicode[code] = unicode;
            differences[code] = glyphName;
            unicodeDifferences.put(unicode, unicode);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets unicode value for corresponding font's char code.
     *
     * @param index font's char code
     * @return -1, if the char code unsupported or valid unicode.
     */
    public int getUnicode(int index) {
        return codeToUnicode[index];
    }

    public int getUnicodeDifference(int index) {
        return unicodeDifferences.get(index);
    }

    public boolean hasDifferences() {
        return differences != null;
    }

    public String getDifference(int index) {
        return differences != null ? differences[index] : null;
    }

    /**
     * Sets a new value in the differences array.
     * See {@link #differences}.
     *
     * @param index position to replace
     * @param difference new difference value
     */
    public void setDifference(int index, String difference) {
        if (index >= 0 && differences != null && index < differences.length) {
            differences[index] = difference;
        }
    }

    /**
     * Converts a {@code String} to a {@code byte} array according to the encoding.
     * String could contain a unicode symbols or font specific codes.
     *
     * @param text the {@code String} to be converted.
     * @return an array of {@code byte} representing the conversion according to the encoding
     */
    public byte[] convertToBytes(String text) {
        if (text == null || text.length() == 0) {
            return emptyBytes;
        }
        int ptr = 0;
        byte[] bytes = new byte[text.length()];
        for (int i = 0; i < text.length(); i++) {
            if (unicodeToCode.containsKey(text.charAt(i))) {
                bytes[ptr++] = (byte) convertToByte(text.charAt(i));
            }
        }
        return ArrayUtil.shortenArray(bytes, ptr);
    }

    /**
     * Converts a unicode symbol or font specific code
     * to {@code byte} according to the encoding.
     *
     * @param unicode a unicode symbol or FontSpecif code to be converted.
     * @return a {@code byte} representing the conversion according to the encoding
     */
    public int convertToByte(int unicode) {
        return unicodeToCode.get(unicode);
    }

    /**
     * Check whether a unicode symbol or font specific code can be converted
     * to {@code byte} according to the encoding.
     *
     * @param unicode a unicode symbol or font specific code to be checked.
     * @return {@code true} if {@code ch} could be encoded.
     */
    public boolean canEncode(int unicode) {
        return unicodeToCode.containsKey(unicode) || TextUtil.isNonPrintable(unicode) || TextUtil.isNewLine(unicode);
    }

    /**
     * Check whether a {@code byte} code can be converted
     * to unicode symbol according to the encoding.
     *
     * @param code a byte code to be checked.
     * @return {@code true} if {@code code} could be decoded.
     */
    public boolean canDecode(int code) {
        return codeToUnicode[code] > -1;
    }

    /**
     * Checks whether the {@link FontEncoding} was built with corresponding encoding.
     *
     * @param encoding an encoding
     * @return true, if the FontEncoding was built with the encoding. Otherwise false.
     */
    public boolean isBuiltWith(String encoding) {
        return Objects.equals(normalizeEncoding(encoding), baseEncoding);
    }

    protected void fillCustomEncoding() {
        differences = new String[256];
        StringTokenizer tok = new StringTokenizer(baseEncoding.substring(1), " ,\t\n\r\f");
        if (tok.nextToken().equals("full")) {
            while (tok.hasMoreTokens()) {
                String order = tok.nextToken();
                String name = tok.nextToken();
                char uni = (char) Integer.parseInt(tok.nextToken(), 16);
                int uniName = AdobeGlyphList.nameToUnicode(name);
                int orderK;
                if (order.startsWith("'")) {
                    orderK = order.charAt(1);
                } else {
                    orderK = Integer.parseInt(order);
                }
                orderK %= 256;
                unicodeToCode.put(uni, orderK);
                codeToUnicode[orderK] = (int) uni;
                differences[orderK] = name;
                unicodeDifferences.put(uni, uniName);
            }
        } else {
            int k = 0;
            if (tok.hasMoreTokens()) {
                k = Integer.parseInt(tok.nextToken());
            }
            while (tok.hasMoreTokens() && k < 256) {
                String hex = tok.nextToken();
                int uni = Integer.parseInt(hex, 16) % 0x10000;
                String name = AdobeGlyphList.unicodeToName(uni);
                if (name == null) {
                    name = "uni" + hex;
                }
                unicodeToCode.put(uni, k);
                codeToUnicode[k] = uni;
                differences[k] = name;
                unicodeDifferences.put(uni, uni);
                k++;
            }
        }
        for (int k = 0; k < 256; k++) {
            if (differences[k] == null) {
                differences[k] = NOTDEF;
            }
        }
    }

    protected void fillNamedEncoding() {
        // check if the encoding exists
        PdfEncodings.convertToBytes(" ", baseEncoding);
        boolean stdEncoding = PdfEncodings.WINANSI.equals(baseEncoding) || PdfEncodings.MACROMAN.equals(baseEncoding);
        if (!stdEncoding && differences == null) {
            differences = new String[256];
        }

        byte[] b = new byte[256];
        for (int k = 0; k < 256; ++k) {
            b[k] = (byte) k;
        }
        String str = PdfEncodings.convertToString(b, baseEncoding);
        char[] encoded = str.toCharArray();
        for (int ch = 0; ch < 256; ++ch) {
            char uni = encoded[ch];
            String name = AdobeGlyphList.unicodeToName(uni);
            if (name == null) {
                name = NOTDEF;
            } else {
                unicodeToCode.put(uni, ch);
                codeToUnicode[ch] = (int) uni;
                unicodeDifferences.put(uni, uni);
            }
            if (differences != null) {
                differences[ch] = name;
            }
        }
    }

    protected void fillStandardEncoding() {
        int[] encoded = PdfEncodings.standardEncoding;
        for (int ch = 0; ch < 256; ++ch) {
            int uni = encoded[ch];
            String name = AdobeGlyphList.unicodeToName(uni);
            if (name == null) {
                name = NOTDEF;
            } else {
                unicodeToCode.put(uni, ch);
                codeToUnicode[ch] = uni;
                unicodeDifferences.put(uni, uni);
            }
            if (differences != null) {
                differences[ch] = name;
            }
        }
    }

    /**
     * Normalize the encoding names. "winansi" is changed to "Cp1252" and
     * "macroman" is changed to "MacRoman".
     *
     * @param enc the encoding to be normalized
     * @return the normalized encoding
     */
    protected static String normalizeEncoding(String enc) {
        String tmp = enc == null ? "" : enc.toLowerCase();
        switch (tmp) {
            case "":
            case "winansi":
            case "winansiencoding":
                return PdfEncodings.WINANSI;
            case "macroman":
            case "macromanencoding":
                return PdfEncodings.MACROMAN;
            case "zapfdingbatsencoding":
                return PdfEncodings.ZAPFDINGBATS;
            default:
                return enc;
        }
    }
}

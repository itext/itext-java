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
package com.itextpdf.io.util;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class TextUtil {

    private TextUtil() {
    }

    /**
     * Check if the value of a character belongs to a certain interval
     * that indicates it's the higher part of a surrogate pair.
     *
     * @param c the character
     * @return true if the character belongs to the interval
     */
    public static boolean isSurrogateHigh(char c) {
        return c >= '\ud800' && c <= '\udbff';
    }

    /**
     * Check if the value of a character belongs to a certain interval
     * that indicates it's the lower part of a surrogate pair.
     *
     * @param c the character
     * @return true if the character belongs to the interval
     */
    public static boolean isSurrogateLow(char c) {
        return c >= '\udc00' && c <= '\udfff';
    }

    public static char highSurrogate(int codePoint) {
        return (char) ((codePoint >>> 10)
                + ('\uD800' - (0x010000 >>> 10)));
    }

    public static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & 0x3ff) + '\uDC00');
    }

    /**
     * Checks if two subsequent characters in a String are
     * are the higher and the lower character in a surrogate
     * pair (and therefore eligible for conversion to a UTF 32 character).
     *
     * @param text the String with the high and low surrogate characters
     * @param idx  the index of the 'high' character in the pair
     * @return true if the characters are surrogate pairs
     */
    public static boolean isSurrogatePair(String text, int idx) {
        return !(idx < 0 || idx > text.length() - 2)
                && isSurrogateHigh(text.charAt(idx))
                && isSurrogateLow(text.charAt(idx + 1));
    }

    /**
     * Checks if two subsequent characters in a character array are
     * are the higher and the lower character in a surrogate
     * pair (and therefore eligible for conversion to a UTF 32 character).
     *
     * @param text the character array with the high and low surrogate characters
     * @param idx  the index of the 'high' character in the pair
     * @return true if the characters are surrogate pairs
     */
    public static boolean isSurrogatePair(char[] text, int idx) {
        return !(idx < 0 || idx > text.length - 2)
                && isSurrogateHigh(text[idx])
                && isSurrogateLow(text[idx + 1]);
    }

    /**
     * Returns the code point of a UTF32 character corresponding with
     * a high and a low surrogate value.
     *
     * @param highSurrogate the high surrogate value
     * @param lowSurrogate  the low surrogate value
     * @return a code point value
     */
    public static int convertToUtf32(char highSurrogate, char lowSurrogate) {
        return (highSurrogate - 0xd800) * 0x400 + lowSurrogate - 0xdc00 + 0x10000;
    }

    /**
     * Converts a unicode character in a character array to a UTF 32 code point value.
     *
     * @param text a character array that has the unicode character(s)
     * @param idx  the index of the 'high' character
     * @return the code point value
     */
    public static int convertToUtf32(char[] text, int idx) {
        return (text[idx] - 0xd800) * 0x400 + text[idx + 1] - 0xdc00 + 0x10000;
    }

    /**
     * Converts a unicode character in a String to a UTF32 code point value
     *
     * @param text a String that has the unicode character(s)
     * @param idx  the index of the 'high' character
     * @return the codepoint value
     */
    public static int convertToUtf32(String text, int idx) {
        return (text.charAt(idx) - 0xd800) * 0x400 + text.charAt(idx + 1) - 0xdc00 + 0x10000;
    }

    public static int[] convertToUtf32(String text) {
        if (text == null) {
            return null;
        }
        List<Integer> charCodes = new ArrayList<>(text.length());
        int pos = 0;
        while (pos < text.length()) {
            if (isSurrogatePair(text, pos)) {
                charCodes.add(convertToUtf32(text, pos));
                pos += 2;
            } else {
                charCodes.add((int) text.charAt(pos));
                pos++;
            }
        }
        return ArrayUtil.toIntArray(charCodes);
    }

    /**
     * Converts a UTF32 code point value to a String with the corresponding character(s).
     *
     * @param codePoint a Unicode value
     * @return the corresponding characters in a String
     */
    public static char[] convertFromUtf32(int codePoint) {
        if (codePoint < 0x10000) {
            return new char[]{(char) codePoint};
        }
        codePoint -= 0x10000;
        return new char[]{(char) (codePoint / 0x400 + 0xd800), (char) (codePoint % 0x400 + 0xdc00)};
    }

    /**
     * /**
     * Converts a UTF32 code point sequence to a String with the corresponding character(s).
     *
     * @param text     a Unicode text sequence
     * @param startPos start position of text to convert, inclusive
     * @param endPos   end position of txt to convert, exclusive
     * @return the corresponding characters in a String
     */
    public static String convertFromUtf32(int[] text, int startPos, int endPos) {
        StringBuilder sb = new StringBuilder();
        for (int i = startPos; i < endPos; i++) {
            sb.append(convertFromUtf32ToCharArray(text[i]));
        }
        return sb.toString();
    }

    /**
     * Converts a UTF32 code point value to a char array with the corresponding character(s).
     *
     * @param codePoint a Unicode value
     * @return the corresponding characters in a char arrat
     */
    public static char[] convertFromUtf32ToCharArray(int codePoint) {
        if (codePoint < 0x10000) {
            return new char[]{(char) codePoint};
        }
        codePoint -= 0x10000;
        return new char[]{(char) (codePoint / 0x400 + 0xd800), (char) (codePoint % 0x400 + 0xdc00)};
    }

    public static String charToString(char ch) {
        return String.valueOf(ch);
    }

    public static boolean isNewLine(Glyph glyph) {
        int unicode = glyph.getUnicode();
        return unicode == '\n' || unicode == '\r';
    }

    public static boolean isCarriageReturnFollowedByLineFeed(GlyphLine glyphLine, int carriageReturnPosition) {
        return glyphLine.size() > 1
                && carriageReturnPosition <= glyphLine.size() - 2
                && glyphLine.get(carriageReturnPosition).getUnicode() == '\r'
                && glyphLine.get(carriageReturnPosition + 1).getUnicode() == '\n';
    }

    public static boolean isSpaceOrWhitespace(Glyph glyph) {
        //\r, \n, and \t are whitespaces, but not space chars.
        //\u00a0 is SpaceChar, but not whitespace.
        return Character.isSpaceChar((char) glyph.getUnicode()) || Character.isWhitespace((char) glyph.getUnicode());
    }

    public static boolean isWhitespace(Glyph glyph) {
        return Character.isWhitespace(glyph.getUnicode());
    }

    public static boolean isNonBreakingHyphen(Glyph glyph) {
        return '\u2011' == glyph.getUnicode();
    }

    public static boolean isSpace(Glyph glyph) {
        return Character.isSpaceChar((char) glyph.getUnicode());
    }

    public static boolean isUni0020(Glyph g) {
        return g.getUnicode() == ' ';
    }

    public static boolean isNonPrintable(int c) {
        return Character.isIdentifierIgnorable(c) || c == '\u00AD';
    }

    public static boolean isWhitespaceOrNonPrintable(int code) {
        return Character.isWhitespace(code) || isNonPrintable(code);
    }

    public static boolean charsetIsSupported(String charsetName) {
        try {
            return Charset.isSupported(charsetName);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

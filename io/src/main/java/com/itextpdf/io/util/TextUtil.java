package com.itextpdf.io.util;

import java.util.ArrayList;
import java.util.List;

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
        return ArrayUtil.toArray(charCodes);
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
}

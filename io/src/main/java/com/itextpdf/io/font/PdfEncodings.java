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
package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.EncodingUtil;
import com.itextpdf.io.util.IntHashtable;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PdfEncodings {

    //-Encodings--------------------------------------------------------------------------------------------------------

    /** The Unicode encoding with horizontal writing. */
    public static final String IDENTITY_H = "Identity-H";
    /** The Unicode encoding with vertical writing. */
    public static final String IDENTITY_V = "Identity-V";
    /** A possible encoding. */
    public static final String CP1250 = "Cp1250";
    /** A possible encoding. */
    public static final String CP1252 = "Cp1252";
    /** A possible encoding. */
    public static final String CP1253 = "Cp1253";
    /** A possible encoding. */
    public static final String CP1257 = "Cp1257";
    /** A possible encoding. */
    public static final String WINANSI = "Cp1252";
    /** A possible encoding. */
    public static final String MACROMAN = "MacRoman";
    /** A possible encoding. */
    public static final String SYMBOL = "Symbol";
    /** A possible encoding. */
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    /** This is the encoding to be used to output text in Unicode. */
    public static final String UNICODE_BIG = "UnicodeBig";
    /** This is the encoding to be used to output text for Identity-H/V CMaps. */
    public static final String UNICODE_BIG_UNMARKED = "UnicodeBigUnmarked";
    /** This is the default encoding to be used for converting Strings into
     * bytes and vice versa. The default encoding is PDF_DOC_ENCODING. */
    public static final String PDF_DOC_ENCODING = "PDF";
    public static final String UTF8 = "UTF-8";

    private static final String EMPTY_STRING = "";

    private static final char[] winansiByteToChar = {
            (char) 0, (char) 1, (char) 2, (char) 3, (char) 4, (char) 5, (char) 6, (char) 7, (char) 8, (char) 9, (char) 10, (char) 11, (char) 12, (char) 13, (char) 14, (char) 15,
            (char) 16, (char) 17, (char) 18, (char) 19, (char) 20, (char) 21, (char) 22, (char) 23, (char) 24, (char) 25, (char) 26, (char) 27, (char) 28, (char) 29, (char) 30, (char) 31,
            (char) 32, (char) 33, (char) 34, (char) 35, (char) 36, (char) 37, (char) 38, (char) 39, (char) 40, (char) 41, (char) 42, (char) 43, (char) 44, (char) 45, (char) 46, (char) 47,
            (char) 48, (char) 49, (char) 50, (char) 51, (char) 52, (char) 53, (char) 54, (char) 55, (char) 56, (char) 57, (char) 58, (char) 59, (char) 60, (char) 61, (char) 62, (char) 63,
            (char) 64, (char) 65, (char) 66, (char) 67, (char) 68, (char) 69, (char) 70, (char) 71, (char) 72, (char) 73, (char) 74, (char) 75, (char) 76, (char) 77, (char) 78, (char) 79,
            (char) 80, (char) 81, (char) 82, (char) 83, (char) 84, (char) 85, (char) 86, (char) 87, (char) 88, (char) 89, (char) 90, (char) 91, (char) 92, (char) 93, (char) 94, (char) 95,
            (char) 96, (char) 97, (char) 98, (char) 99, (char) 100, (char) 101, (char) 102, (char) 103, (char) 104, (char) 105, (char) 106, (char) 107, (char) 108, (char) 109, (char) 110, (char) 111,
            (char) 112, (char) 113, (char) 114, (char) 115, (char) 116, (char) 117, (char) 118, (char) 119, (char) 120, (char) 121, (char) 122, (char) 123, (char) 124, (char) 125, (char) 126, (char) 127,
            (char) 8364, (char) 65533, (char) 8218, (char) 402, (char) 8222, (char) 8230, (char) 8224, (char) 8225, (char) 710, (char) 8240, (char) 352, (char) 8249, (char) 338, (char) 65533, (char) 381, (char) 65533,
            (char) 65533, (char) 8216, (char) 8217, (char) 8220, (char) 8221, (char) 8226, (char) 8211, (char) 8212, (char) 732, (char) 8482, (char) 353, (char) 8250, (char) 339, (char) 65533, (char) 382, (char) 376,
            (char) 160, (char) 161, (char) 162, (char) 163, (char) 164, (char) 165, (char) 166, (char) 167, (char) 168, (char) 169, (char) 170, (char) 171, (char) 172, (char) 173, (char) 174, (char) 175,
            (char) 176, (char) 177, (char) 178, (char) 179, (char) 180, (char) 181, (char) 182, (char) 183, (char) 184, (char) 185, (char) 186, (char) 187, (char) 188, (char) 189, (char) 190, (char) 191,
            (char) 192, (char) 193, (char) 194, (char) 195, (char) 196, (char) 197, (char) 198, (char) 199, (char) 200, (char) 201, (char) 202, (char) 203, (char) 204, (char) 205, (char) 206, (char) 207,
            (char) 208, (char) 209, (char) 210, (char) 211, (char) 212, (char) 213, (char) 214, (char) 215, (char) 216, (char) 217, (char) 218, (char) 219, (char) 220, (char) 221, (char) 222, (char) 223,
            (char) 224, (char) 225, (char) 226, (char) 227, (char) 228, (char) 229, (char) 230, (char) 231, (char) 232, (char) 233, (char) 234, (char) 235, (char) 236, (char) 237, (char) 238, (char) 239,
            (char) 240, (char) 241, (char) 242, (char) 243, (char) 244, (char) 245, (char) 246, (char) 247, (char) 248, (char) 249, (char) 250, (char) 251, (char) 252, (char) 253, (char) 254, (char) 255
    };

    private static final char[] pdfEncodingByteToChar = {
            (char) 0, (char) 1, (char) 2, (char) 3, (char) 4, (char) 5, (char) 6, (char) 7, (char) 8, (char) 9, (char) 10, (char) 11, (char) 12, (char) 13, (char) 14, (char) 15,
            (char) 16, (char) 17, (char) 18, (char) 19, (char) 20, (char) 21, (char) 22, (char) 23, (char) 24, (char) 25, (char) 26, (char) 27, (char) 28, (char) 29, (char) 30, (char) 31,
            (char) 32, (char) 33, (char) 34, (char) 35, (char) 36, (char) 37, (char) 38, (char) 39, (char) 40, (char) 41, (char) 42, (char) 43, (char) 44, (char) 45, (char) 46, (char) 47,
            (char) 48, (char) 49, (char) 50, (char) 51, (char) 52, (char) 53, (char) 54, (char) 55, (char) 56, (char) 57, (char) 58, (char) 59, (char) 60, (char) 61, (char) 62, (char) 63,
            (char) 64, (char) 65, (char) 66, (char) 67, (char) 68, (char) 69, (char) 70, (char) 71, (char) 72, (char) 73, (char) 74, (char) 75, (char) 76, (char) 77, (char) 78, (char) 79,
            (char) 80, (char) 81, (char) 82, (char) 83, (char) 84, (char) 85, (char) 86, (char) 87, (char) 88, (char) 89, (char) 90, (char) 91, (char) 92, (char) 93, (char) 94, (char) 95,
            (char) 96, (char) 97, (char) 98, (char) 99, (char) 100, (char) 101, (char) 102, (char) 103, (char) 104, (char) 105, (char) 106, (char) 107, (char) 108, (char) 109, (char) 110, (char) 111,
            (char) 112, (char) 113, (char) 114, (char) 115, (char) 116, (char) 117, (char) 118, (char) 119, (char) 120, (char) 121, (char) 122, (char) 123, (char) 124, (char) 125, (char) 126, (char) 127,
            (char) 0x2022, (char) 0x2020, (char) 0x2021, (char) 0x2026, (char) 0x2014, (char) 0x2013, (char) 0x0192, (char) 0x2044, (char) 0x2039, (char) 0x203a, (char) 0x2212, (char) 0x2030, (char) 0x201e, (char) 0x201c, (char) 0x201d, (char) 0x2018,
            (char) 0x2019, (char) 0x201a, (char) 0x2122, (char) 0xfb01, (char) 0xfb02, (char) 0x0141, (char) 0x0152, (char) 0x0160, (char) 0x0178, (char) 0x017d, (char) 0x0131, (char) 0x0142, (char) 0x0153, (char) 0x0161, (char) 0x017e, (char) 65533,
            (char) 0x20ac, (char) 161, (char) 162, (char) 163, (char) 164, (char) 165, (char) 166, (char) 167, (char) 168, (char) 169, (char) 170, (char) 171, (char) 172, (char) 173, (char) 174, (char) 175,
            (char) 176, (char) 177, (char) 178, (char) 179, (char) 180, (char) 181, (char) 182, (char) 183, (char) 184, (char) 185, (char) 186, (char) 187, (char) 188, (char) 189, (char) 190, (char) 191,
            (char) 192, (char) 193, (char) 194, (char) 195, (char) 196, (char) 197, (char) 198, (char) 199, (char) 200, (char) 201, (char) 202, (char) 203, (char) 204, (char) 205, (char) 206, (char) 207,
            (char) 208, (char) 209, (char) 210, (char) 211, (char) 212, (char) 213, (char) 214, (char) 215, (char) 216, (char) 217, (char) 218, (char) 219, (char) 220, (char) 221, (char) 222, (char) 223,
            (char) 224, (char) 225, (char) 226, (char) 227, (char) 228, (char) 229, (char) 230, (char) 231, (char) 232, (char) 233, (char) 234, (char) 235, (char) 236, (char) 237, (char) 238, (char) 239,
            (char) 240, (char) 241, (char) 242, (char) 243, (char) 244, (char) 245, (char) 246, (char) 247, (char) 248, (char) 249, (char) 250, (char) 251, (char) 252, (char) 253, (char) 254, (char) 255 };

    static final int[] standardEncoding = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            32, 33, 34, 35, 36, 37, 38, 8217, 40, 41, 42, 43, 44, 45, 46, 47,
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
            64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
            80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95,
            8216, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 161, 162, 163, 8260, 165, 402, 167, 164, 39, 8220, 171, 8249, 8250, 64257, 64258,
            0, 8211, 8224, 8225, 183, 0, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 0, 191,
            0, 96, 180, 710, 732, 175, 728, 729, 168, 0, 730, 184, 0, 733, 731, 711,
            8212, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 198, 0, 170, 0, 0, 0, 0, 321, 216, 338, 186, 0, 0, 0, 0,
            0, 230, 0, 0, 0, 305, 0, 0, 322, 248, 339, 223, 0, 0, 0, 0
    };

    private static final IntHashtable winansi = new IntHashtable();

    private static final IntHashtable pdfEncoding = new IntHashtable();

    private static final Map<String, IExtraEncoding> extraEncodings = new HashMap<>();

    static {
        for (int k = 128; k < 161; ++k) {
            char c = winansiByteToChar[k];
            if (c != 65533)
                winansi.put((int) c, k);
        }

        for (int k = 128; k < 161; ++k) {
            char c = pdfEncodingByteToChar[k];
            if (c != 65533)
                pdfEncoding.put((int) c, k);
        }


        addExtraEncoding("Wingdings", new WingdingsConversion());
        addExtraEncoding("Symbol", new SymbolConversion(true));
        addExtraEncoding("ZapfDingbats", new SymbolConversion(false));
        addExtraEncoding("SymbolTT", new SymbolTTConversion());
        addExtraEncoding("Cp437", new Cp437Conversion());
    }

    /**
     * Converts a {@code String} to a {@code byte} array according
     * to the font's encoding.
     *
     * @param encoding the encoding
     * @param text     the {@code String} to be converted
     * @return an array of {@code byte} representing the conversion according to the font's encoding
     */
    public static byte[] convertToBytes(String text, String encoding) {
        if (text == null)
            return new byte[0];
        if (encoding == null || encoding.length() == 0) {
            int len = text.length();
            byte[] b = new byte[len];
            for (int k = 0; k < len; ++k) {
                b[k] = (byte) text.charAt(k);
            }
            return b;
        }
        IExtraEncoding extra = extraEncodings.get(encoding.toLowerCase());
        if (extra != null) {
            byte[] b = extra.charToByte(text, encoding);
            if (b != null)
                return b;
        }
        IntHashtable hash = null;
        if (encoding.equals(WINANSI)) {
            hash = winansi;
        } else if (encoding.equals(PDF_DOC_ENCODING)) {
            hash = pdfEncoding;
        }
        if (hash != null) {
            char[] cc = text.toCharArray();
            int len = cc.length;
            int ptr = 0;
            byte[] b = new byte[len];
            int c;
            for (int k = 0; k < len; ++k) {
                char ch = cc[k];
                if (ch < 128 || ch > 160 && ch <= 255) {
                    c = ch;
                } else {
                    c = hash.get((int) ch);
                }
                if (c != 0) {
                    b[ptr++] = (byte) c;
                }
            }
            if (ptr == len)
                return b;
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }
        try {
            return EncodingUtil.convertToBytes(text.toCharArray(), encoding);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CharacterCodeException, e);
        }
    }

    /**
     * Converts a {@code String} to a {@code byte} array according
     * to the font's encoding.
     *
     * @param encoding the encoding
     * @param ch    the {@code char} to be converted
     * @return an array of {@code byte} representing the conversion according to the font's encoding
     */
    public static byte[] convertToBytes(char ch, String encoding) {
        if (encoding == null || encoding.length() == 0)
            return new byte[]{(byte) ch};
        IntHashtable hash = null;
        if (encoding.equals(WINANSI))
            hash = winansi;
        else if (encoding.equals(PDF_DOC_ENCODING))
            hash = pdfEncoding;
        if (hash != null) {
            int c;
            if (ch < 128 || ch > 160 && ch <= 255) {
                c = ch;
            } else {
                c = hash.get((int) ch);
            }
            if (c != 0) {
                return new byte[]{(byte) c};
            } else {
                return new byte[0];
            }
        }
        try {
            return EncodingUtil.convertToBytes(new char[]{ch}, encoding);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CharacterCodeException, e);
        }
    }

    /**
     * Converts a {@code byte} array to a {@code String} according
     * to the some encoding.
     *
     * @param bytes    the bytes to convert
     * @param encoding the encoding
     * @return the converted {@code String}
     */
    public static String convertToString(byte[] bytes, String encoding) {
        if (bytes == null)
            return EMPTY_STRING;
        if (encoding == null || encoding.length() == 0) {
            char[] c = new char[bytes.length];
            for (int k = 0; k < bytes.length; ++k) {
                c[k] = (char) (bytes[k] & 0xff);
            }
            return new String(c);
        }
        IExtraEncoding extra = extraEncodings.get(encoding.toLowerCase());
        if (extra != null) {
            String text = extra.byteToChar(bytes, encoding);
            if (text != null) {
                return text;
            }
        }
        char[] ch = null;
        if (encoding.equals(WINANSI))
            ch = winansiByteToChar;
        else if (encoding.equals(PDF_DOC_ENCODING))
            ch = pdfEncodingByteToChar;
        if (ch != null) {
            int len = bytes.length;
            char[] c = new char[len];
            for (int k = 0; k < len; ++k) {
                c[k] = ch[bytes[k] & 0xff];
            }
            return new String(c);
        }
        try {
            return EncodingUtil.convertToString(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IOException(IOException.UnsupportedEncodingException, e);
        }
    }

    /**
     * Checks is {@code text} only has PDF_DOC_ENCODING characters.
     *
     * @param text the {@code String} to test
     * @return {@code true} if only PDF_DOC_ENCODING characters are present
     */
    public static boolean isPdfDocEncoding(String text) {
        if (text == null)
            return true;
        int len = text.length();
        for (int k = 0; k < len; k++) {
            char ch = text.charAt(k);
            if (ch < 128 || ch > 160 && ch <= 255)
                continue;
            if (!pdfEncoding.containsKey((int) ch))
                return false;
        }
        return true;
    }


    /** Adds an extra encoding.
     * @param name the name of the encoding. The encoding recognition is case insensitive
     * @param enc the conversion class
     */
    @SuppressWarnings("unchecked")
    public static void addExtraEncoding(String name, IExtraEncoding enc) {
        synchronized (extraEncodings) {
            extraEncodings.put(name.toLowerCase(), enc);
        }
    }

    private static class WingdingsConversion implements IExtraEncoding {

        public byte[] charToByte(char char1, String encoding) {
            if (char1 == ' ')
                return new byte[]{(byte)char1};
            else if (char1 >= '\u2701' && char1 <= '\u27BE') {
                byte v = table[char1 - 0x2700];
                if (v != 0)
                    return new byte[]{v};
            }
            return new byte[0];
        }

        public byte[] charToByte(String text, String encoding) {
            char[] cc = text.toCharArray();
            byte[] b = new byte[cc.length];
            int ptr = 0;
            int len = cc.length;
            for (int k = 0; k < len; ++k) {
                char c = cc[k];
                if (c == ' ')
                    b[ptr++] = (byte)c;
                else if (c >= '\u2701' && c <= '\u27BE') {
                    byte v = table[c - 0x2700];
                    if (v != 0)
                        b[ptr++] = v;
                }
            }
            if (ptr == len)
                return b;
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        public String byteToChar(byte[] b, String encoding) {
            return null;
        }

        private static final byte[] table = {
                (byte) 0x00, (byte) 0x23, (byte) 0x22, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x29, (byte) 0x3e, (byte) 0x51, (byte) 0x2a,
                (byte) 0x00, (byte) 0x00, (byte) 0x41, (byte) 0x3f, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xfc,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xfb, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x56, (byte) 0x00, (byte) 0x58, (byte) 0x59, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0xb5, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xb6, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0xad, (byte) 0xaf, (byte) 0xac, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x7c, (byte) 0x7b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x54, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xa6, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x71, (byte) 0x72, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x75, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x7d, (byte) 0x7e, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x8c, (byte) 0x8d,
                (byte) 0x8e, (byte) 0x8f, (byte) 0x90, (byte) 0x91, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x81, (byte) 0x82,
                (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x8c, (byte) 0x8d,
                (byte) 0x8e, (byte) 0x8f, (byte) 0x90, (byte) 0x91, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0xe8, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0xe8, (byte) 0xd8, (byte) 0x00, (byte) 0x00, (byte) 0xc4, (byte) 0xc6, (byte) 0x00, (byte) 0x00, (byte) 0xf0,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xdc,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00
        };
    }

    private static class Cp437Conversion implements IExtraEncoding {
        private static IntHashtable c2b = new IntHashtable();

        public byte[] charToByte(String text, String encoding) {
            char[] cc = text.toCharArray();
            byte[] b = new byte[cc.length];
            int ptr = 0;
            int len = cc.length;
            for (int k = 0; k < len; ++k) {
                char c = cc[k];
                if (c < 128)
                    b[ptr++] = (byte)c;
                else {
                    byte v = (byte)c2b.get(c);
                    if (v != 0)
                        b[ptr++] = v;
                }
            }
            if (ptr == len)
                return b;
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        public byte[] charToByte(char char1, String encoding) {
            if (char1 < 128)
                return new byte[]{(byte)char1};
            else {
                byte v = (byte)c2b.get(char1);
                if (v != 0)
                    return new byte[]{v};
                else
                    return new byte[0];
            }
        }

        public String byteToChar(byte[] b, String encoding) {
            int len = b.length;
            char[] cc = new char[len];
            int ptr = 0;
            for (int k = 0; k < len; ++k) {
                int c = b[k] & 0xff;
                if (c < ' ')
                    continue;
                if (c < 128)
                    cc[ptr++] = (char)c;
                else {
                    char v = table[c - 128];
                    cc[ptr++] = v;
                }
            }
            return new String(cc, 0, ptr);
        }

        private static final char[] table = {
                '\u00C7', '\u00FC', '\u00E9', '\u00E2', '\u00E4', '\u00E0', '\u00E5', '\u00E7', '\u00EA', '\u00EB', '\u00E8', '\u00EF', '\u00EE', '\u00EC', '\u00C4', '\u00C5',
                '\u00C9', '\u00E6', '\u00C6', '\u00F4', '\u00F6', '\u00F2', '\u00FB', '\u00F9', '\u00FF', '\u00D6', '\u00DC', '\u00A2', '\u00A3', '\u00A5', '\u20A7', '\u0192',
                '\u00E1', '\u00ED', '\u00F3', '\u00FA', '\u00F1', '\u00D1', '\u00AA', '\u00BA', '\u00BF', '\u2310', '\u00AC', '\u00BD', '\u00BC', '\u00A1', '\u00AB', '\u00BB',
                '\u2591', '\u2592', '\u2593', '\u2502', '\u2524', '\u2561', '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255D', '\u255C', '\u255B', '\u2510',
                '\u2514', '\u2534', '\u252C', '\u251C', '\u2500', '\u253C', '\u255E', '\u255F', '\u255A', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550', '\u256C', '\u2567',
                '\u2568', '\u2564', '\u2565', '\u2559', '\u2558', '\u2552', '\u2553', '\u256B', '\u256A', '\u2518', '\u250C', '\u2588', '\u2584', '\u258C', '\u2590', '\u2580',
                '\u03B1', '\u00DF', '\u0393', '\u03C0', '\u03A3', '\u03C3', '\u00B5', '\u03C4', '\u03A6', '\u0398', '\u03A9', '\u03B4', '\u221E', '\u03C6', '\u03B5', '\u2229',
                '\u2261', '\u00B1', '\u2265', '\u2264', '\u2320', '\u2321', '\u00F7', '\u2248', '\u00B0', '\u2219', '\u00B7', '\u221A', '\u207F', '\u00B2', '\u25A0', '\u00A0'
        };

        static {
            for (int k = 0; k < table.length; ++k)
                c2b.put(table[k], k + 128);
        }
    }

    private static class SymbolConversion implements IExtraEncoding {

        private static final IntHashtable t1 = new IntHashtable();
        private static final IntHashtable t2 = new IntHashtable();
        private IntHashtable translation;
        private final char[] byteToChar;

        SymbolConversion(boolean symbol) {
            if (symbol) {
                translation = t1;
                byteToChar = table1;
            } else {
                translation = t2;
                byteToChar = table2;
            }
        }

        public byte[] charToByte(String text, String encoding) {
            char[] cc = text.toCharArray();
            byte[] b = new byte[cc.length];
            int ptr = 0;
            int len = cc.length;
            for (int k = 0; k < len; ++k) {
                char c = cc[k];
                byte v = (byte)translation.get(c);
                if (v != 0)
                    b[ptr++] = v;
            }
            if (ptr == len)
                return b;
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        public byte[] charToByte(char char1, String encoding) {
            byte v = (byte)translation.get(char1);
            if (v != 0)
                return new byte[]{v};
            else
                return new byte[0];
        }

        public String byteToChar(byte[] b, String encoding) {
            int len = b.length;
            char[] cc = new char[len];
            int ptr = 0;
            for (int k = 0; k < len; ++k) {
                int c = b[k] & 0xff;
                char v = byteToChar[c];
                cc[ptr++] = v;
            }
            return new String(cc, 0, ptr);
        }

        private static final char[] table1 = {
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                ' ','!','\u2200','#','\u2203','%','&','\u220b','(',')','*','+',',','-','.','/',
                '0','1','2','3','4','5','6','7','8','9',':',';','<','=','>','?',
                '\u2245','\u0391','\u0392','\u03a7','\u0394','\u0395','\u03a6','\u0393','\u0397','\u0399','\u03d1','\u039a','\u039b','\u039c','\u039d','\u039f',
                '\u03a0','\u0398','\u03a1','\u03a3','\u03a4','\u03a5','\u03c2','\u03a9','\u039e','\u03a8','\u0396','[','\u2234',']','\u22a5','_',
                '\u0305','\u03b1','\u03b2','\u03c7','\u03b4','\u03b5','\u03d5','\u03b3','\u03b7','\u03b9','\u03c6','\u03ba','\u03bb','\u03bc','\u03bd','\u03bf',
                '\u03c0','\u03b8','\u03c1','\u03c3','\u03c4','\u03c5','\u03d6','\u03c9','\u03be','\u03c8','\u03b6','{','|','}','~','\0',
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                '\u20ac','\u03d2','\u2032','\u2264','\u2044','\u221e','\u0192','\u2663','\u2666','\u2665','\u2660','\u2194','\u2190','\u2191','\u2192','\u2193',
                '\u00b0','\u00b1','\u2033','\u2265','\u00d7','\u221d','\u2202','\u2022','\u00f7','\u2260','\u2261','\u2248','\u2026','\u2502','\u2500','\u21b5',
                '\u2135','\u2111','\u211c','\u2118','\u2297','\u2295','\u2205','\u2229','\u222a','\u2283','\u2287','\u2284','\u2282','\u2286','\u2208','\u2209',
                '\u2220','\u2207','\u00ae','\u00a9','\u2122','\u220f','\u221a','\u22c5','\u00ac','\u2227','\u2228','\u21d4','\u21d0','\u21d1','\u21d2','\u21d3',
                '\u25ca','\u2329','\0','\0','\0','\u2211','\u239b','\u239c','\u239d','\u23a1','\u23a2','\u23a3','\u23a7','\u23a8','\u23a9','\u23aa',
                '\0','\u232a','\u222b','\u2320','\u23ae','\u2321','\u239e','\u239f','\u23a0','\u23a4','\u23a5','\u23a6','\u23ab','\u23ac','\u23ad','\0'
        };

        private static final char[] table2 = {
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                '\u0020','\u2701','\u2702','\u2703','\u2704','\u260e','\u2706','\u2707','\u2708','\u2709','\u261b','\u261e','\u270C','\u270D','\u270E','\u270F',
                '\u2710','\u2711','\u2712','\u2713','\u2714','\u2715','\u2716','\u2717','\u2718','\u2719','\u271A','\u271B','\u271C','\u271D','\u271E','\u271F',
                '\u2720','\u2721','\u2722','\u2723','\u2724','\u2725','\u2726','\u2727','\u2605','\u2729','\u272A','\u272B','\u272C','\u272D','\u272E','\u272F',
                '\u2730','\u2731','\u2732','\u2733','\u2734','\u2735','\u2736','\u2737','\u2738','\u2739','\u273A','\u273B','\u273C','\u273D','\u273E','\u273F',
                '\u2740','\u2741','\u2742','\u2743','\u2744','\u2745','\u2746','\u2747','\u2748','\u2749','\u274A','\u274B','\u25cf','\u274D','\u25a0','\u274F',
                '\u2750','\u2751','\u2752','\u25b2','\u25bc','\u25c6','\u2756','\u25d7','\u2758','\u2759','\u275A','\u275B','\u275C','\u275D','\u275E','\u0000',
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                '\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0',
                '\u0000','\u2761','\u2762','\u2763','\u2764','\u2765','\u2766','\u2767','\u2663','\u2666','\u2665','\u2660','\u2460','\u2461','\u2462','\u2463',
                '\u2464','\u2465','\u2466','\u2467','\u2468','\u2469','\u2776','\u2777','\u2778','\u2779','\u277A','\u277B','\u277C','\u277D','\u277E','\u277F',
                '\u2780','\u2781','\u2782','\u2783','\u2784','\u2785','\u2786','\u2787','\u2788','\u2789','\u278A','\u278B','\u278C','\u278D','\u278E','\u278F',
                '\u2790','\u2791','\u2792','\u2793','\u2794','\u2192','\u2194','\u2195','\u2798','\u2799','\u279A','\u279B','\u279C','\u279D','\u279E','\u279F',
                '\u27A0','\u27A1','\u27A2','\u27A3','\u27A4','\u27A5','\u27A6','\u27A7','\u27A8','\u27A9','\u27AA','\u27AB','\u27AC','\u27AD','\u27AE','\u27AF',
                '\u0000','\u27B1','\u27B2','\u27B3','\u27B4','\u27B5','\u27B6','\u27B7','\u27B8','\u27B9','\u27BA','\u27BB','\u27BC','\u27BD','\u27BE','\u0000'
        };

        static {
            for (int k = 0; k < 256; ++k) {
                int v = table1[k];
                if (v != 0)
                    t1.put(v, k);
            }
            for (int k = 0; k < 256; ++k) {
                int v = table2[k];
                if (v != 0)
                    t2.put(v, k);
            }
        }
    }

    private static class SymbolTTConversion implements IExtraEncoding {

        public byte[] charToByte(char char1, String encoding) {
            if ((char1 & 0xff00) == 0 || (char1 & 0xff00) == 0xf000)
                return new byte[]{(byte)char1};
            else
                return new byte[0];
        }

        public byte[] charToByte(String text, String encoding) {
            char[] ch = text.toCharArray();
            byte[] b = new byte[ch.length];
            int ptr = 0;
            int len = ch.length;
            for (int k = 0; k < len; ++k) {
                char c = ch[k];
                if ((c & 0xff00) == 0 || (c & 0xff00) == 0xf000)
                    b[ptr++] = (byte)c;
            }
            if (ptr == len)
                return b;
            byte[] b2 = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }

        public String byteToChar(byte[] b, String encoding) {
            return null;
        }
    }
}

package com.itextpdf.basics.font;

import com.itextpdf.basics.PdfException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;
import java.util.Map;

public class PdfEncodings {

    public static final String EmptyString = "";
    /**
     * This is the default encoding to be used for converting Strings into
     * bytes and vice versa. The default encoding is PdfDocEncoding.
     */
    public static final String TEXT_PDFDOCENCODING = "PDF";

    /**
     * This is the encoding to be used to output text in Unicode.
     */
    public static final String TEXT_UNICODE = "UnicodeBig";

    /**
     * WinANSI encoding.
     */
    public static final String WINANSI = "Cp1252";

    static final char winansiByteToChar[] = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
            32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
            64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
            80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95,
            96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127,
            8364, 65533, 8218, 402, 8222, 8230, 8224, 8225, 710, 8240, 352, 8249, 338, 65533, 381, 65533,
            65533, 8216, 8217, 8220, 8221, 8226, 8211, 8212, 732, 8482, 353, 8250, 339, 65533, 382, 376,
            160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175,
            176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191,
            192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207,
            208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223,
            224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239,
            240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255};

    static final char pdfEncodingByteToChar[] = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
            32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
            64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
            80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95,
            96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127,
            0x2022, 0x2020, 0x2021, 0x2026, 0x2014, 0x2013, 0x0192, 0x2044, 0x2039, 0x203a, 0x2212, 0x2030, 0x201e, 0x201c, 0x201d, 0x2018,
            0x2019, 0x201a, 0x2122, 0xfb01, 0xfb02, 0x0141, 0x0152, 0x0160, 0x0178, 0x017d, 0x0131, 0x0142, 0x0153, 0x0161, 0x017e, 65533,
            0x20ac, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175,
            176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191,
            192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207,
            208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223,
            224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239,
            240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255};

    public static final Map<Integer, Integer> winansi = new HashMap<Integer, Integer>();

    public static final Map<Integer, Integer> pdfEncoding = new HashMap<Integer, Integer>();

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
            byte b[] = new byte[len];
            for (int k = 0; k < len; ++k)
                b[k] = (byte) text.charAt(k);
            return b;
        }
        Map<Integer, Integer> hash = null;
        if (encoding.equals(WINANSI))
            hash = winansi;
        else if (encoding.equals(TEXT_PDFDOCENCODING))
            hash = pdfEncoding;
        if (hash != null) {
            char cc[] = text.toCharArray();
            int len = cc.length;
            int ptr = 0;
            byte b[] = new byte[len];
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
            byte b2[] = new byte[ptr];
            System.arraycopy(b, 0, b2, 0, ptr);
            return b2;
        }
        if (encoding.equals(TEXT_UNICODE)) {
            // workaround for jdk 1.2.2 bug
            char cc[] = text.toCharArray();
            int len = cc.length;
            byte b[] = new byte[cc.length * 2 + 2];
            b[0] = -2;
            b[1] = -1;
            int bptr = 2;
            for (int k = 0; k < len; ++k) {
                char c = cc[k];
                b[bptr++] = (byte) (c >> 8);
                b[bptr++] = (byte) (c & 0xff);
            }
            return b;
        }
        try {
            Charset cc = Charset.forName(encoding);
            CharsetEncoder ce = cc.newEncoder();
            ce.onUnmappableCharacter(CodingErrorAction.IGNORE);
            CharBuffer cb = CharBuffer.wrap(text.toCharArray());
            java.nio.ByteBuffer bb = ce.encode(cb);
            bb.rewind();
            int lim = bb.limit();
            byte[] br = new byte[lim];
            bb.get(br);
            return br;
        } catch (IOException e) {
            //TODO change exception type
            throw new RuntimeException();
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
        Map<Integer, Integer> hash = null;
        if (encoding.equals(WINANSI))
            hash = winansi;
        else if (encoding.equals(TEXT_PDFDOCENCODING))
            hash = pdfEncoding;
        if (hash != null) {
            int c;
            if (ch < 128 || ch > 160 && ch <= 255)
                c = ch;
            else
                c = hash.get((int) ch);
            if (c != 0)
                return new byte[]{(byte) c};
            else
                return new byte[0];
        }
        if (encoding.equals(TEXT_UNICODE)) {
            // workaround for jdk 1.2.2 bug
            byte b[] = new byte[4];
            b[0] = -2;
            b[1] = -1;
            b[2] = (byte) (ch >> 8);
            b[3] = (byte) (ch & 0xff);
            return b;
        }
        try {
            Charset cc = Charset.forName(encoding);
            CharsetEncoder ce = cc.newEncoder();
            ce.onUnmappableCharacter(CodingErrorAction.IGNORE);
            CharBuffer cb = CharBuffer.wrap(new char[]{ch});
            java.nio.ByteBuffer bb = ce.encode(cb);
            bb.rewind();
            int lim = bb.limit();
            byte[] br = new byte[lim];
            bb.get(br);
            return br;
        } catch (IOException e) {
            //TODO change exception type
            throw new RuntimeException(e);
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
    public static String convertToString(byte bytes[], String encoding) throws PdfException {
        if (bytes == null)
            return EmptyString;
        if (encoding == null || encoding.length() == 0) {
            char c[] = new char[bytes.length];
            for (int k = 0; k < bytes.length; ++k)
                c[k] = (char) (bytes[k] & 0xff);
            return new String(c);
        }
        // TODO!
//        ExtraEncoding extra = extraEncodings.get(encoding.toLowerCase());
//        if (extra != null) {
//            String text = extra.byteToChar(bytes, encoding);
//            if (text != null)
//                return text;
//        }
        char ch[] = null;
        if (encoding.equals(PdfEncodings.WINANSI))
            ch = winansiByteToChar;
        else if (encoding.equals(PdfEncodings.TEXT_PDFDOCENCODING))
            ch = pdfEncodingByteToChar;
        if (ch != null) {
            int len = bytes.length;
            char c[] = new char[len];
            for (int k = 0; k < len; ++k) {
                c[k] = ch[bytes[k] & 0xff];
            }
            return new String(c);
        }
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new PdfException(PdfException.PdfEncodings, e);
        }
    }

    /**
     * Checks is {@code text} only has PdfDocEncoding characters.
     *
     * @param text the {@code String} to test
     * @return {@code true} if only PdfDocEncoding characters are present
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
}

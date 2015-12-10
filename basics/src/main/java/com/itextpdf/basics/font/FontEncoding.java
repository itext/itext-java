package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.LogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.StringTokenizer;

public class FontEncoding {

    private static final byte[] emptyBytes = new byte[0];
    /**
     * Base font encoding.
     */
    private String baseEncoding;
    /**
     * {@code true} if the font must use its built in encoding. In that case
     * the {@code encoding} is only used to map a char to the position inside the font, not to the expected char name.
     */
    private boolean fontSpecific;

    /**
     * Mapping map from unicode to simple code according to the encoding.
     */
    private IntHashtable unicodeToCode = new IntHashtable(256);

    private Integer[] codeToUnicode = new Integer[256];

    /**
     * Encoding names.
     */
    private String[] differences;
    /**
     * Encodings unicode differences
     */
    private IntHashtable unicodeDifferences = new IntHashtable(256);

    public FontEncoding(String baseEncoding, boolean fontSpecific) {
        this.fontSpecific = fontSpecific;
        if (fontSpecific) {
            for (int ch = 0; ch < 256; ch++) {
                unicodeToCode.put(ch, ch);
                codeToUnicode[ch] = ch;
                unicodeDifferences.put(ch, ch);
            }
        } else {
            this.baseEncoding = normalizeEncoding(baseEncoding);
            if (this.baseEncoding.startsWith("#")) {
                processCustomEncoding();
            } else {
                processEncoding();
            }
        }
    }

    public FontEncoding(String baseEncoding) {
        this(baseEncoding, false);
    }

    public FontEncoding(boolean fontSpecific) {
        this(null, true);
    }

    public String getBaseEncoding() {
        return baseEncoding;
    }

    public boolean isFontSpecific() {
        return fontSpecific;
    }

    public Integer getUnicode(int index) {
        return codeToUnicode[index];
    }

    public int getUnicodeDifference(int index) {
        return unicodeDifferences.get(index);
    }

    public boolean hasDifferences() {
        return differences != null;
    }

    public String getDifferences(int index) {
        return differences != null ? differences[index] : null;
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
                bytes[ptr++] = (byte) unicodeToCode.get(i);
            }
        }

        if (ptr < bytes.length) {
            byte[] b2 = new byte[ptr];
            System.arraycopy(bytes, 0, b2, 0, ptr);
            return b2;
        } else {
            return bytes;
        }
    }

    /**
     * Converts a unicode symbol or font specific code
     * to {@code byte} according to the encoding.
     *
     * @param ch a unicode symbol or FontSpecif code to be converted.
     * @return a {@code byte} representing the conversion according to the encoding
     */
    public byte convertToByte(int ch) {
        return (byte) unicodeToCode.get(ch);
    }

    /**
     * Check whether a unicode symbol or font specific code can be converted
     * to {@code byte} according to the encoding.
     *
     * @param ch a unicode symbol or font specific code to be checked.
     * @return {@code true} if {@code ch}
     */
    public boolean canEncode(int ch) {
        return unicodeToCode.containsKey(ch);
    }

    /**
     * Normalize the encoding names. "winansi" is changed to "Cp1252" and
     * "macroman" is changed to "MacRoman".
     *
     * @param enc the encoding to be normalized
     * @return the normalized encoding
     */
    protected static String normalizeEncoding(String enc) {
        if (enc == null || enc.toLowerCase().equals("winansi") || enc.equals("")) {
            return PdfEncodings.WINANSI;
        } else if (enc.toLowerCase().equals("macroman")) {
            return PdfEncodings.MACROMAN;
        } else {
            return enc;
        }
    }

    protected void processCustomEncoding() {
        differences = new String[256];
        StringTokenizer tok = new StringTokenizer(baseEncoding.substring(1), " ,\t\n\r\f");
        if (tok.nextToken().equals("full")) {
            while (tok.hasMoreTokens()) {
                String order = tok.nextToken();
                String name = tok.nextToken();
                char uni = (char) Integer.parseInt(tok.nextToken(), 16);
                Integer uniName = AdobeGlyphList.nameToUnicode(name);
                if (uniName != null) {
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
                } else {
                    Logger logger = LoggerFactory.getLogger(FontEncoding.class);
                    logger.warn(MessageFormat.format(LogMessageConstant.UnknowGlyphName1EntityWillBeIgnored, name));
                }
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
                if (name != null) {
                    unicodeToCode.put(uni, k);
                    codeToUnicode[k] = uni;
                    differences[k] = name;
                    unicodeDifferences.put(uni, uni);
                    k++;
                }
            }
        }
        for (int k = 0; k < 256; k++) {
            if (differences[k] == null) {
                differences[k] = FontConstants.notdef;
            }
        }
    }

    protected void processEncoding() {
        PdfEncodings.convertToBytes(" ", baseEncoding); // check if the encoding exists
        boolean stdEncoding = PdfEncodings.WINANSI.equals(baseEncoding) || PdfEncodings.MACROMAN.equals(baseEncoding);
        if (!stdEncoding) {
            differences = new String[256];
        }
        byte[] b = new byte[1];
        for (int ch = 0; ch < 256; ++ch) {
            b[0] = (byte) ch;
            String str = PdfEncodings.convertToString(b, baseEncoding);
            if (str.length() > 0) {
                char uni = str.charAt(0);
                String name = AdobeGlyphList.unicodeToName(uni);
                if (name == null) {
                    name = FontConstants.notdef;
                } else {
                    unicodeToCode.put(uni, ch);
                    codeToUnicode[ch] = (int) uni;
                    unicodeDifferences.put(uni, uni);
                }
                if (!stdEncoding) {
                    differences[ch] = name;
                }
            }
        }
    }
}

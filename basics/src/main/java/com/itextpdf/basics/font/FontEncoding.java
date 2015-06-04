package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;

public class FontEncoding {

    /**
     * Converts {@code char} directly to {@code byte} by casting.
     */
    private boolean directTextToByte;
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
     * Special mapping map for custom user-specific encodings. ("# full …" or "# simple …")
     */
    private IntHashtable specialMap = new IntHashtable();
    /**
     * Encoding names.
     */
    private String[] differences = new String[256];
    /**
     * Same as differences but with the unicode codes.
     */
    private char[] unicodeDifferences = new char[256];
    private boolean hasSpecialEncoding = false;
    private boolean fastWinansi = false;

    public FontEncoding(String baseEncoding, boolean fontSpecific) {
        this.baseEncoding = normalizeEncoding(baseEncoding);
        this.fontSpecific = fontSpecific;
        this.fastWinansi = baseEncoding.equals(PdfEncodings.CP1252);
        if (!this.baseEncoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", this.baseEncoding); // check if the encoding exists
        } else {
            hasSpecialEncoding = true;
        }
    }

    public String getBaseEncoding() {
        return baseEncoding;
    }

    public boolean isFontSpecific() {
        return fontSpecific;
    }

    public boolean hasSpecialEncoding() {
        return hasSpecialEncoding;
    }

    public IntHashtable getSpecialMap() {
        return specialMap;
    }

    public String getDifferences(int index) {
        return differences[index];
    }

    public char getUnicodeDifferences(int index) {
        return unicodeDifferences[index];
    }

    public void setDifferences(int index, String name) {
        differences[index] = name;
    }

    public void setUnicodeDifferences(int index, char ch) {
        unicodeDifferences[index] = ch;
    }

    /**
     * Gets the direct conversion of {@code char} to {@code byte}.
     *
     * @return value of property directTextToByte.
     * @see #setDirectTextToByte(boolean directTextToByte)
     */
    public boolean hasDirectTextToByte() {
        return directTextToByte;
    }

    /**
     * Sets the conversion of {@code char} directly to {@code byte} by casting.
     * This is a low level feature to put the bytes directly in the content stream without passing through String.getBytes().
     *
     * @param directTextToByte New value of property directTextToByte.
     */
    public void setDirectTextToByte(boolean directTextToByte) {
        this.directTextToByte = directTextToByte;
    }

    public void fillEmptyDifferences() {
        for (int k = 0; k < 256; ++k) {
            if (differences[k] == null) {
                differences[k] = FontConstants.notdef;
            }
        }
    }

    public int getByte(char ch) {
        return -1;
    }

    /**
     * Converts a <CODE>String</CODE> to a </CODE>byte</CODE> array according
     * to the font's encoding.
     *
     * @param text the <CODE>String</CODE> to be converted
     * @return an array of <CODE>byte</CODE> representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(String text) {
        if (directTextToByte)
            return PdfEncodings.convertToBytes(text, null);
        if (hasSpecialEncoding) {
            byte[] b = new byte[text.length()];
            int ptr = 0;
            int length = text.length();
            for (int k = 0; k < length; ++k) {
                char c = text.charAt(k);
                if (specialMap.containsKey(c))
                    b[ptr++] = (byte) specialMap.get(c);
            }
            if (ptr < length) {
                byte[] b2 = new byte[ptr];
                System.arraycopy(b, 0, b2, 0, ptr);
                return b2;
            } else
                return b;
        }
        return PdfEncodings.convertToBytes(text, baseEncoding);
    }

    /**
     * Converts a {@code char} to a {@code byte} array according
     * to the font's encoding.
     *
     * @param ch the {@code char} to be converted
     * @return an array of {@code byte} representing the conversion according to the font's encoding
     */
    public byte[] convertToBytes(int ch) {
        if (directTextToByte)
            return PdfEncodings.convertToBytes((char) ch, null);
        if (hasSpecialEncoding) {
            if (specialMap.containsKey(ch))
                return new byte[]{(byte) specialMap.get(ch)};
            else
                return new byte[0];
        }
        return PdfEncodings.convertToBytes((char) ch, baseEncoding);
    }

    protected boolean isFastWinansi() {
        return fastWinansi;
    }

    /**
     * Normalize the encoding names. "winansi" is changed to "Cp1252" and
     * "macroman" is changed to "MacRoman".
     *
     * @param enc the encoding to be normalized
     * @return the normalized encoding
     */
    protected static String normalizeEncoding(String enc) {
        if (enc.toLowerCase().equals("winansi") || enc.equals("")) {
            return PdfEncodings.WINANSI;
        } else if (enc.toLowerCase().equals("macroman")) {
            return PdfEncodings.MACROMAN;
        } else {
            return enc;
        }
    }
}

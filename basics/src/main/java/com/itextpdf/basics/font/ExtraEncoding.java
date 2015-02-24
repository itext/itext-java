package com.itextpdf.basics.font;

/**
 * Classes implementing this interface can create custom encodings or
 * replace existing ones. It is used in the context of <code>PdfEncoding</code>.
 * @author Paulo Soares
 */
public interface ExtraEncoding {

    /**
     * Converts an Unicode string to a byte array according to some encoding.
     * @param text the Unicode string
     * @param encoding the requested encoding. It's mainly of use if the same class
     * supports more than one encoding.
     * @return the conversion or <CODE>null</CODE> if no conversion is supported
     */
    public byte[] charToByte(String text, String encoding);

    /**
     * Converts an Unicode char to a byte array according to some encoding.
     * @param char1 the Unicode char
     * @param encoding the requested encoding. It's mainly of use if the same class
     * supports more than one encoding.
     * @return the conversion or <CODE>null</CODE> if no conversion is supported
     */
    public byte[] charToByte(char char1, String encoding);

    /**
     * Converts a byte array to an Unicode string according to some encoding.
     * @param b the input byte array
     * @param encoding the requested encoding. It's mainly of use if the same class
     * supports more than one encoding.
     * @return the conversion or <CODE>null</CODE> if no conversion is supported
     */
    public String byteToChar(byte b[], String encoding);
}

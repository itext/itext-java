package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.io.ByteBuffer;
import com.itextpdf.basics.font.PdfEncodings;

import java.nio.charset.Charset;

/**
 * A {@code PdfString}-class is the PDF-equivalent of a
 * JAVA-{@code String}-object.
 * <p/>
 * A string is a sequence of characters delimited by parenthesis.
 * If a string is too long to be conveniently placed on a single line, it may
 * be split across multiple lines by using the backslash character (\) at the
 * end of a line to indicate that the string continues on the following line.
 * Within a string, the backslash character is used as an escape to specify
 * unbalanced parenthesis, non-printing ASCII characters, and the backslash
 * character itself. Use of the \<i>ddd</i> escape sequence is the preferred
 * way to represent characters outside the printable ASCII character set.<br>
 * This object is described in the 'Portable Document Format Reference Manual
 * version 1.7' section 3.2.3 (page 53-56).
 * <p/>
 * {@link PdfObject}
 */
public class PdfString extends PdfPrimitiveObject {

    private static String defaultCharset = "UTF-8";

    protected String value;
    protected String encoding;
    protected boolean hexWriting = false;

    public PdfString(String value, String encoding) {
        super();
        this.value = value;
        this.encoding = encoding;
    }

    public PdfString(String value) {
        this(value, null);
    }

    public PdfString(byte[] content, boolean hexWriting) {
        super(content);
        this.hexWriting = hexWriting;
    }

    private PdfString() {
        super();
    }

    @Override
    public byte getType() {
        return String;
    }

    public boolean isHexWriting() {
        return hexWriting;
    }

    public PdfString setHexWriting(boolean hexWriting) {
        if (value == null) {
            generateValue();
            content = null;
        }
        this.hexWriting = hexWriting;
        return this;
    }

    public String getValue() {
        if (value == null)
            generateValue();
        return value;
    }

    /**
     * Gets the encoding of this string.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding of this string.
     * NOTE. Byte content will be removed.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        this.content = null;
    }

    /**
     * Returns the Unicode {@code String} value of this
     * {@code PdfString}-object.
     */
    public String toUnicodeString() throws PdfException {
        if (encoding != null && encoding.length() != 0) {
            return getValue();
        }
        if (content == null) {
            generateContent();
        }

        byte[] b = decodeContent();
        if (b.length >= 2 && b[0] == -2 && b[1] == -1) {
            return PdfEncodings.convertToString(b, PdfEncodings.UnicodeBig);
        } else {
            return PdfEncodings.convertToString(b, PdfEncodings.PdfDocEncoding);
        }
    }


    @Override
    public String toString() {
        if (value == null) {
            return new String(content, Charset.forName(defaultCharset));
        } else {
            return getValue();
        }
    }

    /**
     * Gets bytes of String-value considering encoding.
     *
     * @return byte array
     */
    // Analog of com.itextpdf.text.pdf.PdfString.getBytes() method in iText5.
    protected byte[] getValueBytes() {
        if (value == null)
            generateValue();
        if (encoding != null && encoding.equals(PdfEncodings.UnicodeBig) && PdfEncodings.isPdfDocEncoding(value))
            return PdfEncodings.convertToBytes(value, PdfEncodings.PdfDocEncoding);
        else
            return PdfEncodings.convertToBytes(value, encoding);
    }

    /**
     * Gets bytes of String-value without considering encoding.
     *
     * @return byte array
     */
    protected byte[] getIsoBytes() {
        return com.itextpdf.basics.io.OutputStream.getIsoBytes(getValue());
    }

    protected void generateValue() {
        assert content != null : "No byte[] content to generate value";
        value = new String(convertBytesToChars(decodeContent()));
    }

    @Override
    protected void generateContent() {
        content = encodeBytes(getValueBytes());
    }

    /**
     * Decrypt content of an encrypted {@code PdfString}.
     */
    protected PdfString decrypt(PdfEncryption decrypt) throws PdfException {
        if (decrypt != null) {
            assert content != null : "No byte content to decrypt value";
            byte[] decodedContent = decodeContent();
            content = null;
            if (getIndirectReference() != null) {
                decrypt.setHashKey(getIndirectReference().getObjNr(), getIndirectReference().getGenNr());
            } else {
                decrypt.setHashKey(0, 0);
            }
            value = new String(decrypt.decryptByteArray(decodedContent), Charset.forName(defaultCharset));
        }
        return this;
    }


    /**
     * Encrypt content of {@code value} and set as content. {@code generateContent()} won't be called.
     *
     * @param encrypt @see PdfEncryption
     * @return true if value was encrypted, otherwise false.
     */
    protected boolean encrypt(PdfEncryption encrypt) throws PdfException {
        if (encrypt != null && !encrypt.isEmbeddedFilesOnly()) {
            byte[] b = encrypt.encryptByteArray(getValueBytes());
            content = encodeBytes(b);
            return true;
        }
        return false;
    }

    /**
     * Escape special symbols or convert to hexadecimal string.
     * This method don't change either {@code value} or {@code content} ot the {@code PdfString}.
     *
     * @param bytes byte array to manipulate with.
     * @return Hexadecimal string or string with escaped symbols in byte array view.
     */
    protected byte[] encodeBytes(byte[] bytes) {
        if (hexWriting) {
            ByteBuffer buf = new ByteBuffer(bytes.length * 2);
            for (byte b : bytes) {
                buf.appendHex(b);
            }
            return buf.getInternalBuffer();
        } else {
            ByteBuffer buf = Utilities.createBufferedEscapedString(bytes);
            return buf.toByteArray(1, buf.size() - 2);
        }
    }

    /**
     * Resolve escape symbols or hexadecimal symbols.
     * <p/>
     * NOTE Due to PdfReference 1.7 part 3.2.3 String value contain ASCII characters,
     * so we can convert it directly to byte array.
     *
     * @return byte[] for decrypting or for creating {@link java.lang.String}.
     */
    protected byte[] decodeContent() {
        ByteBuffer buffer = new ByteBuffer(content.length);
        if (hexWriting) {       // <6954657874ae...>
            for (int i = 0; i < content.length; ) {
                int v1 = ByteBuffer.getHex(content[i++]);
                if (i == content.length) {
                    buffer.appendAsCharBytes(v1 << 4);
                    break;
                }
                int v2 = content[i++];
                v2 = ByteBuffer.getHex(v2);
                buffer.appendAsCharBytes((v1 << 4) + v2);
            }
        } else {                // ((iText\( some version)...)
            for (int i = 0; i < content.length; ) {
                int ch = content[i++];
                if (ch == '\\') {
                    boolean lineBreak = false;
                    ch = content[i++];
                    switch (ch) {
                        case 'n':
                            ch = '\n';
                            break;
                        case 'r':
                            ch = '\r';
                            break;
                        case 't':
                            ch = '\t';
                            break;
                        case 'b':
                            ch = '\b';
                            break;
                        case 'f':
                            ch = '\f';
                            break;
                        case '(':
                        case ')':
                        case '\\':
                            break;
                        case '\r':
                            lineBreak = true;
                            if (i < content.length && content[i++] != '\n') {
                                i--;
                            }
                            break;
                        case '\n':
                            lineBreak = true;
                            break;
                        default: {
                            if (ch < '0' || ch > '7') {
                                break;
                            }
                            int octal = ch - '0';
                            ch = content[i++];
                            if (ch < '0' || ch > '7') {
                                i--;
                                ch = octal;
                                break;
                            }
                            octal = (octal << 3) + ch - '0';
                            ch = content[i++];
                            if (ch < '0' || ch > '7') {
                                i--;
                                ch = octal;
                                break;
                            }
                            octal = (octal << 3) + ch - '0';
                            ch = octal & 0xff;
                            break;
                        }
                    }
                    if (lineBreak)
                        continue;
                } else if (ch == '\r') {
                    // in this case current char is '\n' and we have to skip next '\n' if it presents.
                    ch = '\n';
                    if (i < content.length && content[i++] != '\n') {
                        i--;
                    }
                }
                buffer.append(ch);
            }
        }
        return buffer.toByteArray();
    }

    @Override
    protected PdfString newInstance() {
        return new PdfString();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        super.copyContent(from, document);
        PdfString string = (PdfString) from;
        value = string.value;
        hexWriting = string.hexWriting;
    }

    private char[] convertBytesToChars(byte[] b) {
        int length = b.length;
        char[] cc = new char[length];
        for (int i = 0; i < length; i++) {
            if (hexWriting) {
                cc[i] = (char) (b[i] & 0xff);
            } else {
                cc[i] = (char) (b[i]);
            }
        }
        return cc;
    }


}

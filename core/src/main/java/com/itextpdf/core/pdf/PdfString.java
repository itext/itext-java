package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.io.ByteBuffer;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.io.OutputStream;

import java.nio.charset.Charset;

/**
 * A {@code PdfString}-class is the PDF-equivalent of a
 * JAVA-{@code String}-object.
 * <p>
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
 *
 * {@link PdfObject}
 */
public class PdfString extends PdfPrimitiveObject {

    private static String defaultCharset = "UTF-8";
    private static final byte[] escR = OutputStream.getIsoBytes("\\r");
    private static final byte[] escN = OutputStream.getIsoBytes("\\n");
    private static final byte[] escT = OutputStream.getIsoBytes("\\t");
    private static final byte[] escB = OutputStream.getIsoBytes("\\b");
    private static final byte[] escF = OutputStream.getIsoBytes("\\f");

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

    protected PdfString(byte[] content, boolean hexWriting) {
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
        if (content.length >= 2 && content[0] == (byte)254 && content[1] == (byte)255) {
            return PdfEncodings.convertToString(content, PdfEncodings.TEXT_UNICODE);
        } else {
            return PdfEncodings.convertToString(content, PdfEncodings.TEXT_PDFDOCENCODING);
        }
    }

    @Override
    public String toString() {
        if (value == null) {
            return new String(content, Charset.forName("UTF8"));
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
        if (encoding != null && encoding.equals(PdfEncodings.TEXT_UNICODE) && PdfEncodings.isPdfDocEncoding(value))
            return PdfEncodings.convertToBytes(value, PdfEncodings.TEXT_PDFDOCENCODING);
        else
            return PdfEncodings.convertToBytes(value, encoding);
    }

    /**
     * Gets bytes of String-value without considering encoding.
     *
     * @return byte array
     */
    protected byte[] getIsoBytes(){
        return com.itextpdf.basics.io.OutputStream.getIsoBytes(getValue());
    }

    protected void generateValue() {
        assert content != null : "No byte[] content to generate value";
        value = new String(decodeContent(), Charset.forName("UTF8"));
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
            value = new String(decrypt.decryptByteArray(decodedContent), Charset.forName("UTF8"));
        }
        return this;
    }


    /**
     * Encrypt content of {@code value} and set as content. {@code generateContent()} won't be called.
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
     * @param bytes byte array to manipulate with.
     * @return Hexadecimal string or string with escaped symbols in byte array view.
     */
    protected byte[] encodeBytes(byte[] bytes) {
        if(hexWriting) {
            ByteBuffer buf = new ByteBuffer(bytes.length * 2);
            for (byte b : bytes) {
                buf.appendHex(b);
            }
            return buf.getInternalBuffer();
        } else {
            ByteBuffer buf =  Utilities.createBufferedEscapedString(bytes);
            return buf.toByteArray(1, buf.size() - 2);
        }
    }

    /**
     * Resolve escape symbols or hexadecimal symbols.
     *
     * NOTE Due to PdfReference 1.7 part 3.2.3 String value contain ASCII characters,
     * so we can convert it directly to byte array.
     * @return byte[] for decrypting or for creating {@link java.lang.String}.
     */
    protected byte[] decodeContent() {
        ByteBuffer buffer = new ByteBuffer(content.length);
        if (hexWriting) {       // <6954657874ae...>
            for (int i = 0; i < content.length;) {
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
            for(int i = 0; i < content.length;) {
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
                            ch = content[i++];
                            if (ch != '\n')
                                i--;
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
                    ch = content[i++];
                    if (ch != '\n') {
                        i--;
                        ch = '\n';
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
        PdfString string = (PdfString)from;
        value = string.value;
        hexWriting = string.hexWriting;
    }
}

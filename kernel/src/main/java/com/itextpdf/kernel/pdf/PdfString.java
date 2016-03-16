package com.itextpdf.kernel.pdf;

import com.itextpdf.io.util.Utilities;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.PdfTokenizer;

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

    private static final long serialVersionUID = 390789504287887010L;

	private static String defaultCharset = "UTF-8";

    protected String value;
    protected String encoding;
    protected boolean hexWriting = false;
    /*
    * using for decryption
    * */
    private int decryptInfoNum = 0;
    /*
    * using for decryption
    * */
    private int decryptInfoGen = 0;

    public PdfString(String value, String encoding) {
        super();
        this.value = value;
        this.encoding = encoding;
    }

    public PdfString(String value) {
        this(value, null);
    }

    public PdfString(byte[] content) {
        super();
        if (content != null && content.length > 0) {
            StringBuilder str = new StringBuilder(content.length);
            for (byte b: content) {
                str.append((char)(b & 0xff));
            }
            this.value = str.toString();
        } else {
            this.value = "";
        }
    }

    /**
     * Only PdfReader can use this method
     */
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
    public String toUnicodeString() {
        if (encoding != null && encoding.length() != 0) {
            return getValue();
        }
        if (content == null) {
            generateContent();
        }

        byte[] b = PdfTokenizer.decodeStringContent(content, hexWriting);
        if (b.length >= 2 && b[0] == -2 && b[1] == -1) {
            return PdfEncodings.convertToString(b, PdfEncodings.UnicodeBig);
        } else {
            return PdfEncodings.convertToString(b, PdfEncodings.PdfDocEncoding);
        }
    }

    /**
     * Gets bytes of String-value considering encoding.
     *
     * @return byte array
     */
    // Analog of com.itextpdf.text.pdf.PdfString.getBytes() method in iText5.
    public byte[] getValueBytes() {
        if (value == null)
            generateValue();
        if (encoding != null && encoding.equals(PdfEncodings.UnicodeBig) && PdfEncodings.isPdfDocEncoding(value))
            return PdfEncodings.convertToBytes(value, PdfEncodings.PdfDocEncoding);
        else
            return PdfEncodings.convertToBytes(value, encoding);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfString makeIndirect(PdfDocument document) {
        return super.makeIndirect(document);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfString makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        return super.makeIndirect(document, reference);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfString copyTo(PdfDocument document) {
        return super.copyTo(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfString copyTo(PdfDocument document, boolean allowDuplicating) {
        return super.copyTo(document, allowDuplicating);
    }

    @Override
    public String toString() {
        if (value == null) {
            return new String(content, Charset.forName(defaultCharset));
        } else {
            return getValue();
        }
    }

    protected void generateValue() {
        assert content != null : "No byte[] content to generate value";
        value = convertBytesToString(PdfTokenizer.decodeStringContent(content, hexWriting));
    }

    @Override
    protected void generateContent() {
        content = encodeBytes(getValueBytes());
    }

    /**
     * Decrypt content of an encrypted {@code PdfString}.
     */
    protected PdfString decrypt(PdfEncryption decrypt) {
        if (decrypt != null) {
            assert content != null : "No byte content to decrypt value";
            byte[] decodedContent = PdfTokenizer.decodeStringContent(content, hexWriting);
            content = null;
            decrypt.setHashKeyForNextObject(decryptInfoNum, decryptInfoGen);
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
    protected boolean encrypt(PdfEncryption encrypt) {
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

    @Override
    protected PdfString newInstance() {
        return new PdfString();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfString string = (PdfString) from;
        value = string.value;
        hexWriting = string.hexWriting;
    }

    private String convertBytesToString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder(bytes.length);
        for (byte b : bytes) {
            buffer.append((char) (b & 0xff));
        }
        return buffer.toString();
    }

    public int getDecryptInfoNum() {
        return decryptInfoNum;
    }

    public void setDecryptInfoNum(int decryptInfoNum) {
        this.decryptInfoNum = decryptInfoNum;
    }

    public int getDecryptInfoGen() {
        return decryptInfoGen;
    }

    public void setDecryptInfoGen(int decryptInfoGen) {
        this.decryptInfoGen = decryptInfoGen;
    }
}

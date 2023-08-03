/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.utils.ICopyFilter;

import java.nio.charset.StandardCharsets;

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
 * @see PdfObject
 */
public class PdfString extends PdfPrimitiveObject {


    protected String value;
    protected String encoding;
    protected boolean hexWriting = false;

    private int decryptInfoNum;
    private int decryptInfoGen;
    // if it's not null: content shall contain encrypted data; value shall be null
    private PdfEncryption decryption;

    public PdfString(String value, String encoding) {
        super();
        assert value != null;
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
            for (byte b : content) {
                str.append((char) (b & 0xff));
            }
            this.value = str.toString();
        } else {
            this.value = "";
        }
    }

    /**
     * Only PdfReader can use this method!
     *
     * @param content    byte content the {@link PdfString} will be created from
     * @param hexWriting boolean indicating if hex writing will be used
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
        return STRING;
    }

    public boolean isHexWriting() {
        return hexWriting;
    }

    public PdfString setHexWriting(boolean hexWriting) {
        if (value == null) {
            generateValue();
        }
        content = null;
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
     *
     * @return the name of the encoding specifying the byte representation of current {@link PdfString} value
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Returns the Unicode {@code String} value of this
     * {@code PdfString}-object.
     *
     * @return Unicode string value created by current {@link PdfString} object
     */
    public String toUnicodeString() {
        if (encoding != null && encoding.length() != 0) {
            return getValue();
        }
        if (content == null) {
            generateContent();
        }
        byte[] b = decodeContent();
        if (b.length >= 2 && b[0] == (byte) 0xFE && b[1] == (byte) 0xFF) {
            return PdfEncodings.convertToString(b, PdfEncodings.UNICODE_BIG);
        } else if (b.length >= 3 && b[0] == (byte)0xEF && b[1] == (byte)0xBB && b[2] == (byte)0xBF) {
            return PdfEncodings.convertToString(b, PdfEncodings.UTF8);
        } else {
            return PdfEncodings.convertToString(b, PdfEncodings.PDF_DOC_ENCODING);
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
        if (encoding != null && PdfEncodings.UNICODE_BIG.equals(encoding) && PdfEncodings.isPdfDocEncoding(value))
            return PdfEncodings.convertToBytes(value, PdfEncodings.PDF_DOC_ENCODING);
        else
            return PdfEncodings.convertToBytes(value, encoding);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PdfString that = (PdfString) o;
        String v1 = getValue();
        String v2 = that.getValue();
        if (v1 != null && v1.equals(v2)) {
            String e1 = getEncoding();
            String e2 = that.getEncoding();
            if ((e1 == null && e2 == null)
                    || (e1 != null && e1.equals(e2))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (value == null) {
            return new String(decodeContent(), StandardCharsets.ISO_8859_1);
        } else {
            return getValue();
        }
    }

    @Override
    public int hashCode() {
        String v = getValue();
        String e = getEncoding();
        int result = v != null ? v.hashCode() : 0;
        return 31 * result + (e != null ? e.hashCode() : 0);
    }

    /**
     * Marks this string object as not encrypted in the encrypted document.
     * <p>
     * If it's marked so, it will be considered as already in plaintext and decryption will not be performed for it.
     * In order to have effect, this method shall be called before {@link #getValue()} and {@link #getValueBytes()} methods.
     * <p>
     * NOTE: this method is only needed in a very specific cases of encrypted documents. E.g. digital signature dictionary
     * /Contents entry shall not be encrypted. Also this method isn't meaningful in non-encrypted documents.
     */
    public void markAsUnencryptedObject() {
        setState(PdfObject.UNENCRYPTED);
    }

    void setDecryption(int decryptInfoNum, int decryptInfoGen, PdfEncryption decryption) {
        this.decryptInfoNum = decryptInfoNum;
        this.decryptInfoGen = decryptInfoGen;
        this.decryption = decryption;
    }

    protected void generateValue() {
        assert content != null : "No byte[] content to generate value";
        value = PdfEncodings.convertToString(decodeContent(), null);
        if (decryption != null) {
            decryption = null;
            content = null;
        }
    }

    @Override
    protected void generateContent() {
        content = encodeBytes(getValueBytes());
    }

    /**
     * Encrypt content of {@code value} and set as content. {@code generateContent()} won't be called.
     *
     * @param encrypt {@link PdfEncryption} instance
     * @return true if value was encrypted, otherwise false.
     */
    protected boolean encrypt(PdfEncryption encrypt) {
        if (checkState(PdfObject.UNENCRYPTED)) {
            return false;
        }
        if (encrypt != decryption) {
            if (decryption != null) {
                generateValue();
            }
            if (encrypt != null && !encrypt.isEmbeddedFilesOnly()) {
                byte[] b = encrypt.encryptByteArray(getValueBytes());
                content = encodeBytes(b);
                return true;
            }
        }
        return false;
    }

    protected byte[] decodeContent() {
        byte[] decodedBytes = PdfTokenizer.decodeStringContent(content, hexWriting);
        if (decryption != null && !checkState(PdfObject.UNENCRYPTED)) {
            decryption.setHashKeyForNextObject(decryptInfoNum, decryptInfoGen);
            decodedBytes = decryption.decryptByteArray(decodedBytes);
        }
        return decodedBytes;
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
            ByteBuffer buf = StreamUtil.createBufferedEscapedString(bytes);
            return buf.toByteArray(1, buf.size() - 2);
        }
    }

    @Override
    protected PdfObject newInstance() {
        return new PdfString();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter copyFilter) {
        super.copyContent(from, document,copyFilter);
        PdfString string = (PdfString) from;
        value = string.value;
        hexWriting = string.hexWriting;
        decryption = string.decryption;
        decryptInfoNum = string.decryptInfoNum;
        decryptInfoGen = string.decryptInfoGen;
        encoding = string.encoding;
    }
}

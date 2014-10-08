package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.ByteBuffer;

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
 * @see PdfObject
 */
public class PdfString extends PdfPrimitiveObject {

    protected String value;
    protected boolean hexWriting = false;

    public PdfString(String value) {
        super();
        this.value = value;
    }

    public PdfString(byte[] content, boolean hexWriting) {
        super(content);
        this.hexWriting = hexWriting;
    }

    public PdfString(byte[] content) {
        this(content, false);
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

    public void setHexWriting(boolean hexWriting) {
        this.hexWriting = hexWriting;
    }

    public String getValue() {
        if (value == null)
            generateValue();
        return value;
    }

    /**
     * Returns the Unicode {@code String} value of this
     * {@code PdfString}-object.
     *
     * @return A {@code String}
     */
    public String toUnicodeString() {
        return getValue();
    }

    @Override
    public String toString() {
        return getValue();
    }

    protected void generateValue() {
        StringBuilder builder = new StringBuilder();
        if (hexWriting) {       // <6954657874ae...>
            for (int i = 1; i < content.length - 1;) {
                int v1 = ByteBuffer.getHex(content[i++]);
                int v2 = content[i++];
                if (v2 == '>') {
                    builder.append((char)(v1 << 4));
                    break;
                }
                v2 = ByteBuffer.getHex(v2);
                builder.append((char)((v1 << 4) + v2));
            }
        } else {                // ((iText\( some version)...)
            for(int i = 1; i < content.length - 1; ) {
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
                builder.append((char)ch);
            }
        }
        value = builder.toString();
    }

    @Override
    protected void generateContent() {
        ByteBuffer buf;
        if(hexWriting) {
            buf = new ByteBuffer(value.length()*2 + 2);
            buf.append('<');
            for (int k = 0; k < value.length(); ++k) {
                buf.appendHex((byte) value.charAt(k));
            }
            buf.append('>');
        } else {
            buf = new ByteBuffer(value.length() + 2);
            buf.append('(');
            for (int k = 0; k < value.length(); ++k) {
                buf.append((byte) value.charAt(k));
            }
            buf.append(')');
        }
        content = buf.getInternalBuffer();
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

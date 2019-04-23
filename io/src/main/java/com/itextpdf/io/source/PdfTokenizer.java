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
package com.itextpdf.io.source;

import com.itextpdf.io.IOException;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Arrays;

public class PdfTokenizer implements Closeable, Serializable {

    private static final long serialVersionUID = -2949864233416670521L;

    public enum TokenType {
        Number,
        String,
        Name,
        Comment,
        StartArray,
        EndArray,
        StartDic,
        EndDic,
        Ref,
        Obj,
        EndObj,
        Other,
        EndOfFile
    }

    public static final boolean[] delims = {
            true, true, false, false, false, false, false, false, false, false,
            true, true, false, true, true, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, true, false, false, false, false, true, false,
            false, true, true, false, false, false, false, false, true, false,
            false, false, false, false, false, false, false, false, false, false,
            false, true, false, true, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, true, false, true, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false};


    public static final byte[] Obj = ByteUtils.getIsoBytes("obj");
    public static final byte[] R = ByteUtils.getIsoBytes("R");
    public static final byte[] Xref = ByteUtils.getIsoBytes("xref");
    public static final byte[] Startxref = ByteUtils.getIsoBytes("startxref");
    public static final byte[] Stream = ByteUtils.getIsoBytes("stream");
    public static final byte[] Trailer = ByteUtils.getIsoBytes("trailer");
    public static final byte[] N = ByteUtils.getIsoBytes("n");
    public static final byte[] F = ByteUtils.getIsoBytes("f");
    public static final byte[] Null = ByteUtils.getIsoBytes("null");
    public static final byte[] True = ByteUtils.getIsoBytes("true");
    public static final byte[] False = ByteUtils.getIsoBytes("false");

    protected TokenType type;
    protected int reference;
    protected int generation;
    protected boolean hexString;
    protected ByteBuffer outBuf;

    private final RandomAccessFileOrArray file;
    /**
     * Streams are closed automatically.
     */
    private boolean closeStream = true;

    /**
     * Creates a PdfTokenizer for the specified {@link RandomAccessFileOrArray}.
     * The beginning of the file is read to determine the location of the header, and the data source is adjusted
     * as necessary to account for any junk that occurs in the byte source before the header
     *
     * @param file the source
     */
    public PdfTokenizer(RandomAccessFileOrArray file) {
        this.file = file;
        this.outBuf = new ByteBuffer();
    }

    public void seek(long pos) throws java.io.IOException {
        file.seek(pos);
    }

    public void readFully(byte[] bytes) throws java.io.IOException {
        file.readFully(bytes);
    }

    public long getPosition() throws java.io.IOException {
        return file.getPosition();
    }

    public void close() throws java.io.IOException {
        if (closeStream)
            file.close();
    }

    public long length() throws java.io.IOException {
        return file.length();
    }

    public int read() throws java.io.IOException {
        return file.read();
    }

    public String readString(int size) throws java.io.IOException {
        StringBuilder buf = new StringBuilder();
        int ch;
        while ((size--) > 0) {
            ch = read();
            if (ch == -1)
                break;
            buf.append((char) ch);
        }
        return buf.toString();
    }

    public TokenType getTokenType() {
        return type;
    }

    public byte[] getByteContent() {
        return outBuf.toByteArray();
    }

    public String getStringValue() {
        return new String(outBuf.getInternalBuffer(), 0, outBuf.size());
    }

    public byte[] getDecodedStringContent() {
        return decodeStringContent(outBuf.getInternalBuffer(), 0, outBuf.size() - 1, isHexString());
    }

    public boolean tokenValueEqualsTo(byte[] cmp) {
        if (cmp == null)
            return false;

        int size = cmp.length;
        if (outBuf.size() != size)
            return false;

        for (int i = 0; i < size; i++)
            if (cmp[i] != outBuf.getInternalBuffer()[i])
                return false;
        return true;
    }

    public int getObjNr() {
        return reference;
    }

    public int getGenNr() {
        return generation;
    }

    public void backOnePosition(int ch) {
        if (ch != -1)
            file.pushBack((byte) ch);
    }

    public int getHeaderOffset() throws java.io.IOException {
        String str = readString(1024);
        int idx = str.indexOf("%PDF-");
        if (idx < 0) {
            idx = str.indexOf("%FDF-");
            if (idx < 0)
                throw new IOException(IOException.PdfHeaderNotFound, this);
        }

        return idx;
    }

    public String checkPdfHeader() throws java.io.IOException {
        file.seek(0);
        String str = readString(1024);
        int idx = str.indexOf("%PDF-");
        if (idx != 0)
            throw new IOException(IOException.PdfHeaderNotFound, this);
        return str.substring(idx + 1, idx + 8);
    }

    public void checkFdfHeader() throws java.io.IOException {
        file.seek(0);
        String str = readString(1024);
        int idx = str.indexOf("%FDF-");
        if (idx != 0)
            throw new IOException(IOException.FdfStartxrefNotFound, this);
    }

    public long getStartxref() throws java.io.IOException {
        int arrLength = 1024;
        long fileLength = file.length();
        long pos = fileLength - arrLength;
        if (pos < 1) pos = 1;
        while (pos > 0) {
            file.seek(pos);
            String str = readString(arrLength);
            int idx = str.lastIndexOf("startxref");
            if (idx >= 0) return pos + idx;
            pos = pos - arrLength + 9;                  // 9 = "startxref".length()
        }
        throw new IOException(IOException.PdfStartxrefNotFound, this);
    }

    public void nextValidToken() throws java.io.IOException {
        int level = 0;
        byte[] n1 = null;
        byte[] n2 = null;
        long ptr = 0;
        while (nextToken()) {
            if (type == TokenType.Comment)
                continue;
            switch (level) {
                case 0: {
                    if (type != TokenType.Number)
                        return;
                    ptr = file.getPosition();
                    n1 = getByteContent();
                    ++level;
                    break;
                }
                case 1: {
                    if (type != TokenType.Number) {
                        file.seek(ptr);
                        type = TokenType.Number;
                        outBuf.reset().append(n1);
                        return;
                    }
                    n2 = getByteContent();
                    ++level;
                    break;
                }
                case 2: {
                    if (type == TokenType.Other) {
                        if (tokenValueEqualsTo(R)) {
                            assert n2 != null;
                            type = TokenType.Ref;
                            try {
                                reference = Integer.parseInt(new String(n1));
                                generation = Integer.parseInt(new String(n2));
                            } catch (Exception ex) {
                                //warn about incorrect reference number
                                //Exception: NumberFormatException for java, FormatException or OverflowException for .NET
                                Logger logger = LoggerFactory.getLogger(PdfTokenizer.class);
                                logger.error(MessageFormatUtil.format(LogMessageConstant.INVALID_INDIRECT_REFERENCE, new String(n1), new String(n2)));
                                reference = -1;
                                generation = 0;
                            }
                            return;
                        } else if (tokenValueEqualsTo(Obj)) {
                            assert n2 != null;
                            type = TokenType.Obj;
                            reference = Integer.parseInt(new String(n1));
                            generation = Integer.parseInt(new String(n2));
                            return;
                        }
                    }
                    file.seek(ptr);
                    type = TokenType.Number;
                    outBuf.reset().append(n1);
                    return;
                }
            }
        }

        if (level == 1) { // if the level 1 check returns EOF, then we are still looking at a number - set the type back to Number
            type = TokenType.Number;
            outBuf.reset().append(n1);
        }
        // if we hit here, the file is either corrupt (stream ended unexpectedly),
        // or the last token ended exactly at the end of a stream.  This last
        // case can occur inside an Object Stream.
    }

    public boolean nextToken() throws java.io.IOException {
        int ch;
        outBuf.reset();
        do {
            ch = file.read();
        } while (ch != -1 && isWhitespace(ch));
        if (ch == -1) {
            type = TokenType.EndOfFile;
            return false;
        }
        switch (ch) {
            case '[': {
                type = TokenType.StartArray;
                break;
            }
            case ']': {
                type = TokenType.EndArray;
                break;
            }
            case '/': {
                type = TokenType.Name;
                while (true) {
                    ch = file.read();
                    if (delims[ch + 1])
                        break;
                    outBuf.append(ch);
                }
                backOnePosition(ch);
                break;
            }
            case '>': {
                ch = file.read();
                if (ch != '>')
                    throwError(IOException.GtNotExpected);
                type = TokenType.EndDic;
                break;
            }
            case '<': {
                int v1 = file.read();
                if (v1 == '<') {
                    type = TokenType.StartDic;
                    break;
                }
                type = TokenType.String;
                hexString = true;
                int v2 = 0;
                while (true) {
                    while (isWhitespace(v1))
                        v1 = file.read();
                    if (v1 == '>')
                        break;
                    outBuf.append(v1);
                    v1 = ByteBuffer.getHex(v1);
                    if (v1 < 0)
                        break;
                    v2 = file.read();
                    while (isWhitespace(v2))
                        v2 = file.read();
                    if (v2 == '>') {
                        break;
                    }
                    outBuf.append(v2);
                    v2 = ByteBuffer.getHex(v2);
                    if (v2 < 0)
                        break;
                    v1 = file.read();
                }
                if (v1 < 0 || v2 < 0)
                    throwError(IOException.ErrorReadingString);
                break;
            }
            case '%': {
                type = TokenType.Comment;
                do {
                    ch = file.read();
                } while (ch != -1 && ch != '\r' && ch != '\n');
                break;
            }
            case '(': {
                type = TokenType.String;
                hexString = false;
                int nesting = 0;
                while (true) {
                    ch = file.read();
                    if (ch == -1)
                        break;
                    if (ch == '(') {
                        ++nesting;
                    } else if (ch == ')') {
                        --nesting;
                        if (nesting == -1)
                            break;
                    } else if (ch == '\\') {
                        outBuf.append('\\');
                        ch = file.read();
                        if (ch < 0)
                            break;
                    }
                    outBuf.append(ch);
                }
                if (ch == -1)
                    throwError(IOException.ErrorReadingString);
                break;
            }
            default: {
                if (ch == '-' || ch == '+' || ch == '.' || (ch >= '0' && ch <= '9')) {
                    type = TokenType.Number;
                    boolean isReal = false;
                    int numberOfMinuses = 0;
                    if (ch == '-') {
                        // Take care of number like "--234". If Acrobat can read them so must we.
                        do {
                            ++numberOfMinuses;
                            ch = file.read();
                        } while (ch == '-');
                        outBuf.append('-');
                    } else {
                        outBuf.append(ch);
                        // We don't need to check if the number is real over here
                        // as we need to know that fact only in case if there are any minuses.
                        ch = file.read();
                    }
                    while (ch >= '0' && ch <= '9') {
                        outBuf.append(ch);
                        ch = file.read();
                    }

                    if ( ch == '.'){
                        isReal = true;
                        outBuf.append(ch);
                        ch = file.read();

                        //verify if there is minus after '.'
                        //In that case just ignore minus chars and everything after as Adobe Reader does
                        int numberOfMinusesAfterDot = 0;
                        if (ch == '-') {
                            numberOfMinusesAfterDot++;
                            ch = file.read();
                        }
                        while (ch >= '0' && ch <= '9') {
                            if (numberOfMinusesAfterDot == 0) {
                                outBuf.append(ch);
                            }
                            ch = file.read();
                        }
                    }

                    if (numberOfMinuses > 1 && !isReal) {
                        // Numbers of integer type and with more than one minus before them
                        // are interpreted by Acrobat as zero.
                        outBuf.reset();
                        outBuf.append('0');
                    }
                } else {
                    type = TokenType.Other;
                    do {
                        outBuf.append(ch);
                        ch = file.read();
                    } while (!delims[ch + 1]);
                }
                if (ch != -1)
                    backOnePosition(ch);
                break;
            }
        }
        return true;
    }

    public long getLongValue() {
        return Long.parseLong(getStringValue());
    }

    public int getIntValue() {
        return Integer.parseInt(getStringValue());
    }

    public boolean isHexString() {
        return this.hexString;
    }

    public boolean isCloseStream() {
        return closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }

    public RandomAccessFileOrArray getSafeFile() {
        return file.createView();
    }

    /**
     * Resolve escape symbols or hexadecimal symbols.
     * <br>
     * NOTE Due to PdfReference 1.7 part 3.2.3 String value contain ASCII characters,
     * so we can convert it directly to byte array.
     *
     * @param content
     * @param from
     * @param to
     * @param hexWriting
     * @return byte[] for decrypting or for creating {@link java.lang.String}.
     */
    protected static byte[] decodeStringContent(byte[] content, int from, int to, boolean hexWriting) {
        ByteBuffer buffer = new ByteBuffer(to - from + 1);
        if (hexWriting) {       // <6954657874ae...>
            for (int i = from; i <= to; ) {
                int v1 = ByteBuffer.getHex(content[i++]);
                if (i > to) {
                    buffer.append(v1 << 4);
                    break;
                }
                int v2 = content[i++];
                v2 = ByteBuffer.getHex(v2);
                buffer.append((v1 << 4) + v2);
            }
        } else {                // ((iText\( some version)...)
            for (int i = from; i <= to; ) {
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
                            if (i <= to && content[i++] != '\n') {
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
                    if (i <= to && content[i++] != '\n') {
                        i--;
                    }
                }
                buffer.append(ch);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Resolve escape symbols or hexadecimal symbols.
     * <br>
     * NOTE Due to PdfReference 1.7 part 3.2.3 String value contain ASCII characters,
     * so we can convert it directly to byte array.
     *
     * @param content
     * @param hexWriting
     * @return byte[] for decrypting or for creating {@link java.lang.String}.
     */
    public static byte[] decodeStringContent(byte[] content, boolean hexWriting) {
        return decodeStringContent(content, 0, content.length - 1, hexWriting);
    }

        /**
         * Is a certain character a whitespace? Currently checks on the following: '0', '9', '10', '12', '13', '32'.
         * <br>
         * The same as calling {@link #isWhitespace(int, boolean) isWhiteSpace(ch, true)}.
         *
         * @param ch int
         * @return boolean
         */
    public static boolean isWhitespace(int ch) {
        return isWhitespace(ch, true);
    }

    /**
     * Checks whether a character is a whitespace. Currently checks on the following: '0', '9', '10', '12', '13', '32'.
     *
     * @param ch           int
     * @param isWhitespace boolean
     * @return boolean
     */
    protected static boolean isWhitespace(int ch, boolean isWhitespace) {
        return ((isWhitespace && ch == 0) || ch == 9 || ch == 10 || ch == 12 || ch == 13 || ch == 32);
    }

    protected static boolean isDelimiter(int ch) {
        return (ch == '(' || ch == ')' || ch == '<' || ch == '>' || ch == '[' || ch == ']' || ch == '/' || ch == '%');
    }

    protected static boolean isDelimiterWhitespace(int ch) {
        return delims[ch + 1];
    }

    /**
     * Helper method to handle content errors. Add file position to {@code PdfRuntimeException}.
     * @param error message.
     * @param messageParams error params.
     * @throws IOException wrap error message into {@code PdfRuntimeException} and add position in file.
     */
    public void throwError(String error, Object... messageParams) {
        try {
            throw new IOException(IOException.ErrorAtFilePointer1, new IOException(error).setMessageParams(messageParams))
                    .setMessageParams(file.getPosition());
        } catch (java.io.IOException e) {
            throw new IOException(IOException.ErrorAtFilePointer1, new IOException(error).setMessageParams(messageParams))
                    .setMessageParams(error, "no position");
        }
    }

    /**
     * Checks whether {@code line} equals to 'trailer'.
     * @param line for check.
     * @return true, if line is equals tio 'trailer', otherwise false.
     */
    public static boolean checkTrailer(ByteBuffer line) {
        if (Trailer.length > line.size())
            return false;
        for (int i = 0; i < Trailer.length; i++) {
            if (Trailer[i] != line.get(i))
                return false;
        }
        return true;
    }

    /**
     * Reads data into the provided byte[]. Checks on leading whitespace.
     * See {@link #isWhitespace(int) isWhiteSpace(int)} or {@link #isWhitespace(int, boolean) isWhiteSpace(int, boolean)}
     * for a list of whitespace characters.
     * <br>
     * The same as calling {@link #readLineSegment(com.itextpdf.io.source.ByteBuffer, boolean) readLineSegment(input, true)}.
     *
     * @param buffer @see ByteBuffer
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean readLineSegment(ByteBuffer buffer) throws java.io.IOException {
        return readLineSegment(buffer, true);
    }

    /**
     * Reads data into the provided byte[]. Checks on leading whitespace.
     * See {@link #isWhitespace(int) isWhiteSpace(int)} or {@link #isWhitespace(int, boolean) isWhiteSpace(int, boolean)}
     * for a list of whitespace characters.
     *
     * @param buffer           @see ByteBuffer
     * @param isNullWhitespace boolean to indicate whether '0' is whitespace or not.
     *                         If in doubt, use true or overloaded method {@link #readLineSegment(com.itextpdf.io.source.ByteBuffer) readLineSegment(input)}
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean readLineSegment(ByteBuffer buffer, boolean isNullWhitespace) throws java.io.IOException {
        int c;
        boolean eol = false;
        // ssteward, pdftk-1.10, 040922:
        // skip initial whitespace; added this because PdfReader.rebuildXref()
        // assumes that line provided by readLineSegment does not have init. whitespace;
        while (isWhitespace((c = read()), isNullWhitespace)) ;

        boolean prevWasWhitespace = false;
        while (!eol) {
            switch (c) {
                case -1:
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    long cur = getPosition();
                    if ((read()) != '\n') {
                        seek(cur);
                    }
                    break;
                case 9: //whitespaces
                case 12:
                case 32:
                    if (prevWasWhitespace)
                        break;
                    prevWasWhitespace = true;
                    buffer.append((byte) c);
                    break;
                default:
                    prevWasWhitespace = false;
                    buffer.append((byte) c);
                    break;
            }
            // break loop? do it before we read() again
            if (eol || buffer.size() == buffer.capacity()) {
                eol = true;
            } else {
                c = read();
            }
        }
        if (buffer.size() == buffer.capacity()) {
            eol = false;
            while (!eol) {
                switch (c = read()) {
                    case -1:
                    case '\n':
                        eol = true;
                        break;
                    case '\r':
                        eol = true;
                        long cur = getPosition();
                        if ((read()) != '\n') {
                            seek(cur);
                        }
                        break;
                }
            }
        }
        return !(c == -1 && buffer.isEmpty());
    }

    /**
     * Check whether line starts with object declaration.
     * @param lineTokenizer tokenizer, built by single line.
     * @return object number and generation if check is successful, otherwise - null.
     */
    public static int[] checkObjectStart(PdfTokenizer lineTokenizer) {
        try {
            lineTokenizer.seek(0);
            if (!lineTokenizer.nextToken() || lineTokenizer.getTokenType() != TokenType.Number)
                return null;
            int num = lineTokenizer.getIntValue();
            if (!lineTokenizer.nextToken() || lineTokenizer.getTokenType() != TokenType.Number)
                return null;
            int gen = lineTokenizer.getIntValue();
            if (!lineTokenizer.nextToken())
                return null;
            if (!Arrays.equals(Obj, lineTokenizer.getByteContent()))
                return null;
            return new int[]{num, gen};
        } catch (Exception ioe) {
            // empty on purpose
        }
        return null;
    }

    protected static class ReusableRandomAccessSource implements IRandomAccessSource {
        private ByteBuffer buffer;

        public ReusableRandomAccessSource(ByteBuffer buffer) {
            if (buffer == null) throw new IllegalArgumentException("Passed byte buffer can not be null.");
            this.buffer = buffer;
        }

        @Override
        public int get(long offset) {
            if (offset >= buffer.size()) return -1;
            return 0xff & buffer.getInternalBuffer()[(int) offset];
        }

        @Override
        public int get(long offset, byte[] bytes, int off, int len) {
            if (buffer == null) throw new IllegalStateException("Already closed");

            if (offset >= buffer.size())
                return -1;

            if (offset + len > buffer.size())
                len = (int) (buffer.size() - offset);

            System.arraycopy(buffer.getInternalBuffer(), (int) offset, bytes, off, len);

            return len;
        }

        @Override
        public long length() {
            return buffer.size();
        }

        @Override
        public void close() throws java.io.IOException {
            buffer = null;
        }
    }
}

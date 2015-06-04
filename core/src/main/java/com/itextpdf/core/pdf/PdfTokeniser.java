package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.io.ByteBuffer;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSource;

import java.io.IOException;
import java.util.Arrays;

public class PdfTokeniser {

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

    public static final boolean delims[] = {
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


    protected static final byte[] Obj = OutputStream.getIsoBytes("obj");
    protected static final byte[] R = OutputStream.getIsoBytes("R");
    protected static final byte[] Xref = OutputStream.getIsoBytes("xref");
    protected static final byte[] Startxref = OutputStream.getIsoBytes("startxref");
    protected static final byte[] Stream = OutputStream.getIsoBytes("stream");
    protected static final byte[] Trailer = OutputStream.getIsoBytes("trailer");
    protected static final byte[] N = OutputStream.getIsoBytes("n");
    protected static final byte[] F = OutputStream.getIsoBytes("f");
    protected static final byte[] Null = OutputStream.getIsoBytes("null");
    protected static final byte[] True = OutputStream.getIsoBytes("true");
    protected static final byte[] False = OutputStream.getIsoBytes("false");

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
     * Creates a PRTokeniser for the specified {@link RandomAccessFileOrArray}.
     * The beginning of the file is read to determine the location of the header, and the data source is adjusted
     * as necessary to account for any junk that occurs in the byte source before the header
     *
     * @param file the source
     */
    public PdfTokeniser(RandomAccessFileOrArray file) {
        this.file = file;
        this.outBuf = new ByteBuffer();
    }

    public void seek(long pos) throws IOException {
        file.seek(pos);
    }

    public void readFully(byte[] bytes) throws IOException {
        file.readFully(bytes);
    }

    public long getPosition() throws IOException {
        return file.getPosition();
    }

    public void close() throws IOException {
        if (closeStream)
            file.close();
    }

    public long length() throws IOException {
        return file.length();
    }

    public int read() throws IOException {
        return file.read();
    }

    public String readString(int size) throws IOException {
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

    public int getHeaderOffset() throws IOException {
        String str = readString(1024);
        int idx = str.indexOf("%PDF-");
        if (idx < 0) {
            idx = str.indexOf("%FDF-");
            if (idx < 0)
                throw new PdfRuntimeException(PdfRuntimeException.PdfHeaderNotFound, this);
        }

        return idx;
    }

    public char checkPdfHeader() throws IOException {
        file.seek(0);
        String str = readString(1024);
        int idx = str.indexOf("%PDF-");
        if (idx != 0)
            throw new PdfRuntimeException(PdfRuntimeException.PdfHeaderNotFound, this);
        return str.charAt(7);
    }

    public void checkFdfHeader() throws IOException {
        file.seek(0);
        String str = readString(1024);
        int idx = str.indexOf("%FDF-");
        if (idx != 0)
            throw new PdfRuntimeException(PdfRuntimeException.FdfStartxrefNotFound, this);
    }

    public long getStartxref() throws IOException {
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
        throw new PdfRuntimeException(PdfRuntimeException.PdfStartxrefNotFound, this);
    }

    public void nextValidToken() throws IOException {
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
                            reference = Integer.parseInt(new String(n1));
                            generation = Integer.parseInt(new String(n2));
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
        }
        // if we hit here, the file is either corrupt (stream ended unexpectedly),
        // or the last token ended exactly at the end of a stream.  This last
        // case can occur inside an Object Stream.
    }

    public boolean nextToken() throws IOException {
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
                    throwError(PdfRuntimeException.GtNotExpected);
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
                    throwError(PdfRuntimeException.ErrorReadingString);
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
                    throwError(PdfRuntimeException.ErrorReadingString);
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
                    while (ch != -1 && ((ch >= '0' && ch <= '9') || ch == '.')) {
                        if (ch == '.')
                            isReal = true;
                        outBuf.append(ch);
                        ch = file.read();
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
                        outBuf.append((char) ch);
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
     * Is a certain character a whitespace? Currently checks on the following: '0', '9', '10', '12', '13', '32'.
     * <br />The same as calling {@link #isWhitespace(int, boolean) isWhiteSpace(ch, true)}.
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

    protected void throwError(String error, Object... messageParams) {
        try {
            throw new PdfRuntimeException(PdfRuntimeException.ErrorAtFilePointer1, new PdfRuntimeException(error).setMessageParams(messageParams))
                    .setMessageParams(file.getPosition());
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.ErrorAtFilePointer1, new PdfRuntimeException(error).setMessageParams(messageParams))
                    .setMessageParams(error, "no position");
        }
    }

    protected static boolean checkTrailer(ByteBuffer line) {
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
     * <br />The same as calling {@link #readLineSegment(com.itextpdf.basics.io.ByteBuffer, boolean) readLineSegment(input, true)}.
     *
     * @param buffer @see ByteBuffer
     * @return boolean
     * @throws IOException
     */
    protected boolean readLineSegment(ByteBuffer buffer) throws IOException {
        return readLineSegment(buffer, true);
    }

    /**
     * Reads data into the provided byte[]. Checks on leading whitespace.
     * See {@link #isWhitespace(int) isWhiteSpace(int)} or {@link #isWhitespace(int, boolean) isWhiteSpace(int, boolean)}
     * for a list of whitespace characters.
     *
     * @param buffer           @see ByteBuffer
     * @param isNullWhitespace boolean to indicate whether '0' is whitespace or not.
     *                         If in doubt, use true or overloaded method {@link #readLineSegment(com.itextpdf.basics.io.ByteBuffer) readLineSegment(input)}
     * @return boolean
     * @throws IOException
     */
    protected boolean readLineSegment(ByteBuffer buffer, boolean isNullWhitespace) throws IOException {
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

    protected static int[] checkObjectStart(PdfTokeniser lineTokeniser) {
        try {
            lineTokeniser.seek(0);
            if (!lineTokeniser.nextToken() || lineTokeniser.getTokenType() != TokenType.Number)
                return null;
            int num = lineTokeniser.getIntValue();
            if (!lineTokeniser.nextToken() || lineTokeniser.getTokenType() != TokenType.Number)
                return null;
            int gen = lineTokeniser.getIntValue();
            if (!lineTokeniser.nextToken())
                return null;
            if (!Arrays.equals(Obj, lineTokeniser.getByteContent()))
                return null;
            return new int[]{num, gen};
        } catch (Exception ioe) {
            // empty on purpose
        }
        return null;
    }

    protected static class ReusableRandomAccessSource implements RandomAccessSource {
        private ByteBuffer buffer;

        public ReusableRandomAccessSource(ByteBuffer buffer) {
            if (buffer == null) throw new NullPointerException();
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
        public void close() throws IOException {
            buffer = null;
        }
    }
}

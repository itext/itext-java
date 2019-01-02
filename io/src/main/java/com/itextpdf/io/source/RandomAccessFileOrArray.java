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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.Serializable;

/**
 * Class that is used to unify reading from random access files and arrays.
 */
public class RandomAccessFileOrArray implements DataInput, Serializable {

    private static final long serialVersionUID = -169314546265954851L;


    /**
     * When true the file access is not done through a memory mapped file. Use it if the file
     * is too big to be mapped in your address space.
     */
    public static boolean plainRandomAccess = false;

    /**
     * The source that backs this object
     */
    private IRandomAccessSource byteSource;

    /**
     * The physical location in the underlying byte source.
     */
    private long byteSourcePosition;

    /**
     * the pushed  back byte, if any
     */
    private byte back;
    /**
     * Whether there is a pushed back byte
     */
    private boolean isBack = false;

    /**
     * Creates a RandomAccessFileOrArray that wraps the specified byte source.  The byte source will be closed when
     * this RandomAccessFileOrArray is closed.
     *
     * @param byteSource the byte source to wrap
     */
    public RandomAccessFileOrArray(IRandomAccessSource byteSource) {
        this.byteSource = byteSource;
    }

    /**
     * Creates an independent view of this object (with it's own file pointer and push back queue).  Closing the new object will not close this object.
     * Closing this object will have adverse effect on the view.
     *
     * @return the new view
     */
    public RandomAccessFileOrArray createView() {
        ensureByteSourceIsThreadSafe();
        return new RandomAccessFileOrArray(new IndependentRandomAccessSource(byteSource));
    }

    /**
     * Creates the view of the byte source of this object. Closing the view won't affect this object.
     * Closing source will have adverse effect on the view.
     *
     * @return the byte source view.
     */
    public IRandomAccessSource createSourceView() {
        ensureByteSourceIsThreadSafe();
        return new IndependentRandomAccessSource(byteSource);
    }

    /**
     * Pushes a byte back.  The next get() will return this byte instead of the value from the underlying data source
     *
     * @param b the byte to push
     */
    public void pushBack(byte b) {
        back = b;
        isBack = true;
    }

    /**
     * Reads a single byte
     *
     * @return the byte, or -1 if EOF is reached
     * @throws java.io.IOException in case of any reading error.
     */
    public int read() throws java.io.IOException {
        if (isBack) {
            isBack = false;
            return back & 0xff;
        }

        return byteSource.get(byteSourcePosition++);
    }

    /**
     * Reads the specified amount of bytes to the buffer applying the offset.
     *
     * @param b   destination buffer
     * @param off offset at which to start storing characters
     * @param len maximum number of characters to read
     * @return the number of bytes actually read or -1 in case of EOF
     * @throws java.io.IOException in case of any I/O error
     */
    public int read(byte[] b, int off, int len) throws java.io.IOException {
        if (len == 0)
            return 0;
        int count = 0;
        if (isBack && len > 0) {
            isBack = false;
            b[off++] = back;
            --len;
            count++;
        }
        if (len > 0) {
            int byteSourceCount = byteSource.get(byteSourcePosition, b, off, len);
            if (byteSourceCount > 0) {
                count += byteSourceCount;
                byteSourcePosition += byteSourceCount;
            }
        }
        if (count == 0)
            return -1;
        return count;
    }

    /**
     * Reads the bytes to the buffer. This method will try to read as many bytes as the buffer can hold.
     *
     * @param b the destination buffer
     * @return the number of bytes actually read
     * @throws java.io.IOException in
     */
    public int read(byte b[]) throws java.io.IOException {
        return read(b, 0, b.length);
    }

    /**
     * {@inheritDoc}
     */
    public void readFully(byte b[]) throws java.io.IOException {
        readFully(b, 0, b.length);
    }

    /**
     * {@inheritDoc}
     */
    public void readFully(byte[] b, int off, int len) throws java.io.IOException {
        int n = 0;
        do {
            int count = read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        } while (n < len);
    }

    /**
     * Make an attempt to skip the specified amount of bytes in source.
     * However it may skip less amount of bytes. Possibly zero.
     *
     * @param n the number of bytes to skip
     * @return the actual number of bytes skipped
     * @throws java.io.IOException in case of any I/O error
     */
    public long skip(long n) throws java.io.IOException {
        if (n <= 0) {
            return 0;
        }
        int adj = 0;
        if (isBack) {
            isBack = false;
            if (n == 1) {
                return 1;
            } else {
                --n;
                adj = 1;
            }
        }
        long pos;
        long len;
        long newpos;

        pos = getPosition();
        len = length();
        newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }
        seek(newpos);

        return newpos - pos + adj;
    }

    /**
     * {@inheritDoc}
     */
    public int skipBytes(int n) throws java.io.IOException {
        return (int) skip(n);
    }

    /**
     * Closes the underlying source.
     *
     * @throws java.io.IOException
     */
    public void close() throws java.io.IOException {
        isBack = false;

        byteSource.close();
    }

    /**
     * Gets the total amount of bytes in the source.
     *
     * @return source's size.
     * @throws java.io.IOException
     */
    public long length() throws java.io.IOException {
        return byteSource.length();
    }

    /**
     * Sets the current position in the source to the specified index.
     *
     * @param pos the position to set
     * @throws java.io.IOException
     */
    public void seek(long pos) throws java.io.IOException {
        byteSourcePosition = pos;
        isBack = false;
    }

    /**
     * Gets the current position of the source considering the pushed byte to the source.
     *
     * @return the index of last read byte in the source in
     * or the index of last read byte in source - 1 in case byte was pushed.
     * @throws java.io.IOException in case of any I/O error.
     */
    public long getPosition() throws java.io.IOException {
        return byteSourcePosition - (isBack ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    public boolean readBoolean() throws java.io.IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }

    /**
     * {@inheritDoc}
     */
    public byte readByte() throws java.io.IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (byte) (ch);
    }

    /**
     * {@inheritDoc}
     */
    public int readUnsignedByte() throws java.io.IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    /**
     * {@inheritDoc}
     */
    public short readShort() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short) ((ch1 << 8) + ch2);
    }

    /**
     * Reads a signed 16-bit number from this stream in little-endian order.
     * The method reads two
     * bytes from this stream, starting at the current stream pointer.
     * If the two bytes read, in order, are
     * {@code b1} and {@code b2}, where each of the two values is
     * between {@code 0} and {@code 255}, inclusive, then the
     * result is equal to:
     * <blockquote><pre>
     *     (short)((b2 &lt;&lt; 8) | b1)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this stream, interpreted as a signed
     * 16-bit number.
     * @throws EOFException        if this stream reaches the end before reading
     *                             two bytes.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public final short readShortLE() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short) ((ch2 << 8) + ch1);
    }

    /**
     * {@inheritDoc}
     */
    public int readUnsignedShort() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + ch2;
    }

    /**
     * Reads an unsigned 16-bit number from this stream in little-endian order.
     * This method reads
     * two bytes from the stream, starting at the current stream pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where {@code 0 <= b1, b2 <= 255},
     * then the result is equal to:
     * <blockquote><pre>
     *     (b2 &lt;&lt; 8) | b1
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this stream, interpreted as an
     * unsigned 16-bit integer.
     * @throws EOFException        if this stream reaches the end before reading
     *                             two bytes.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public final int readUnsignedShortLE() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch2 << 8) + ch1;
    }

    /**
     * {@inheritDoc}
     */
    public char readChar() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char) ((ch1 << 8) + ch2);
    }

    /**
     * Reads a Unicode character from this stream in little-endian order.
     * This method reads two
     * bytes from the stream, starting at the current stream pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where {@code 0 <= b1, b2 <= 255},
     * then the result is equal to:
     * <blockquote><pre>
     *     (char)((b2 &lt;&lt; 8) | b1)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next two bytes of this stream as a Unicode character.
     * @throws EOFException        if this stream reaches the end before reading
     *                             two bytes.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public final char readCharLE() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char) ((ch2 << 8) + ch2);
    }

    /**
     * {@inheritDoc}
     */
    public int readInt() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4);
    }

    /**
     * Reads a signed 32-bit integer from this stream in little-endian order.
     * This method reads 4
     * bytes from the stream, starting at the current stream pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, {@code b3}, and {@code b4}, where {@code 0 <= b1, b2, b3, b4 <= 255},
     * then the result is equal to:
     * <blockquote><pre>
     *     (b4 &lt;&lt; 24) | (b3 &lt;&lt; 16) + (b2 &lt;&lt; 8) + b1
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this stream, interpreted as an {@code int}.
     * @throws EOFException        if this stream reaches the end before reading
     *                             four bytes.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public final int readIntLE() throws java.io.IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1);
    }

    /**
     * Reads an unsigned 32-bit integer from this stream. This method reads 4
     * bytes from the stream, starting at the current stream pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, {@code b3}, and {@code b4}, where {@code 0 <= b1, b2, b3, b4 <= 255},
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return the next four bytes of this stream, interpreted as a {@code long}.
     * @throws EOFException        if this stream reaches the end before reading
     *                             four bytes.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public final long readUnsignedInt() throws java.io.IOException {
        long ch1 = this.read();
        long ch2 = this.read();
        long ch3 = this.read();
        long ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4);
    }

    public final long readUnsignedIntLE() throws java.io.IOException {
        long ch1 = this.read();
        long ch2 = this.read();
        long ch3 = this.read();
        long ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1);
    }

    /**
     * {@inheritDoc}
     */
    public long readLong() throws java.io.IOException {
        return ((long) (readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    public final long readLongLE() throws java.io.IOException {
        int i1 = readIntLE();
        int i2 = readIntLE();
        return ((long) i2 << 32) + (i1 & 0xFFFFFFFFL);
    }

    /**
     * {@inheritDoc}
     */
    public float readFloat() throws java.io.IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final float readFloatLE() throws java.io.IOException {
        return Float.intBitsToFloat(readIntLE());
    }

    /**
     * {@inheritDoc}
     */
    public double readDouble() throws java.io.IOException {
        return Double.longBitsToDouble(readLong());
    }

    public final double readDoubleLE() throws java.io.IOException {
        return Double.longBitsToDouble(readLongLE());
    }

    /**
     * {@inheritDoc}
     */
    public String readLine() throws java.io.IOException {
        StringBuilder input = new StringBuilder();
        int c = -1;
        boolean eol = false;

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
                default:
                    input.append((char) c);
                    break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String readUTF() throws java.io.IOException {
        return DataInputStream.readUTF(this);
    }

    /**
     * Reads a {@code String} from the font file as bytes using the given encoding.
     *
     * @param length   the length of bytes to read
     * @param encoding the given encoding
     * @return the {@code String} read
     * @throws java.io.IOException the font file could not be read
     */
    public String readString(int length, String encoding) throws java.io.IOException {
        byte[] buf = new byte[length];
        readFully(buf);
        return new String(buf, encoding);
    }

    private void ensureByteSourceIsThreadSafe() {
        if (!(byteSource instanceof ThreadSafeRandomAccessSource)) {
            byteSource = new ThreadSafeRandomAccessSource(byteSource);
        }
    }
}

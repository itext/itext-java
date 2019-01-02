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
package com.itextpdf.io.util;

import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.IRandomAccessSource;

import java.io.*;

/**
 * This file is a helper class for internal usage only.
 * Be aware that its API and functionality may be changed in future.
 */
public final class StreamUtil {

    private static final int TRANSFER_SIZE = 64 * 1024;

    private static final byte[] escR = ByteUtils.getIsoBytes("\\r");
    private static final byte[] escN = ByteUtils.getIsoBytes("\\n");
    private static final byte[] escT = ByteUtils.getIsoBytes("\\t");
    private static final byte[] escB = ByteUtils.getIsoBytes("\\b");
    private static final byte[] escF = ByteUtils.getIsoBytes("\\f");

    private StreamUtil() {
    }

    /**
     * This method is an alternative for the {@code InputStream.skip()}
     * -method that doesn't seem to work properly for big values of {@code size
     * }.
     *
     * @param stream   the {@code InputStream}
     * @param size the number of bytes to skip
     * @throws java.io.IOException
     */
    public static void skip(InputStream stream, long size) throws java.io.IOException {
        long n;
        while (size > 0) {
            n = stream.skip(size);
            if (n <= 0) {
                break;
            }
            size -= n;
        }
    }

    /**
     * Escapes a {@code byte} array according to the PDF conventions.
     *
     * @param bytes the {@code byte} array to escape
     * @return an escaped {@code byte} array
     */
    public static byte[] createEscapedString(byte[] bytes) {
        return createBufferedEscapedString(bytes).toByteArray();
    }

    /**
     * Escapes a {@code byte} array according to the PDF conventions.
     *
     * @param outputStream the {@code OutputStream} an escaped {@code byte} array write to.
     * @param bytes the {@code byte} array to escape.
     */
    public static void writeEscapedString(OutputStream outputStream, byte[] bytes) {
        ByteBuffer buf = createBufferedEscapedString(bytes);
        try {
            outputStream.write(buf.getInternalBuffer(), 0, buf.size());
        } catch (java.io.IOException e) {
            throw new com.itextpdf.io.IOException(com.itextpdf.io.IOException.CannotWriteBytes, e);
        }
    }

    public static void writeHexedString(OutputStream outputStream, byte[] bytes) {
        ByteBuffer buf = createBufferedHexedString(bytes);
        try {
            outputStream.write(buf.getInternalBuffer(), 0, buf.size());
        } catch (java.io.IOException e) {
            throw new com.itextpdf.io.IOException(com.itextpdf.io.IOException.CannotWriteBytes, e);
        }
    }

    public static ByteBuffer createBufferedEscapedString(byte[] bytes) {
        ByteBuffer buf = new ByteBuffer(bytes.length * 2 + 2);
        buf.append('(');
        for (byte b : bytes) {
            switch (b) {
                case (byte) '\r':
                    buf.append(escR);
                    break;
                case (byte) '\n':
                    buf.append(escN);
                    break;
                case (byte) '\t':
                    buf.append(escT);
                    break;
                case (byte) '\b':
                    buf.append(escB);
                    break;
                case (byte) '\f':
                    buf.append(escF);
                    break;
                case (byte) '(':
                case (byte) ')':
                case (byte) '\\':
                    buf.append('\\').append(b);
                    break;
                default:
                    if (b < 8 && b >= 0) {
                        buf.append("\\00").append(Integer.toOctalString(b));
                    } else if (b >= 8 && b < 32) {
                        buf.append("\\0").append(Integer.toOctalString(b));
                    } else {
                        buf.append(b);
                    }
            }
        }
        buf.append(')');
        return buf;
    }

    public static ByteBuffer createBufferedHexedString(byte[] bytes) {
        ByteBuffer buf = new ByteBuffer(bytes.length * 2 + 2);
        buf.append('<');
        for (byte b : bytes) {
            buf.appendHex(b);
        }
        buf.append('>');
        return buf;
    }

    public static void transferBytes(InputStream input, java.io.OutputStream output) throws java.io.IOException {
        byte[] buffer = new byte[TRANSFER_SIZE];
        for (; ; ) {
            int len = input.read(buffer, 0, TRANSFER_SIZE);
            if (len > 0) {
                output.write(buffer, 0, len);
            } else {
                break;
            }
        }
    }

    public static void transferBytes(RandomAccessFileOrArray input, java.io.OutputStream output) throws java.io.IOException {
        byte[] buffer = new byte[TRANSFER_SIZE];
        for (; ; ) {
            int len = input.read(buffer, 0, TRANSFER_SIZE);
            if (len > 0) {
                output.write(buffer, 0, len);
            } else {
                break;
            }
        }
    }

    /**
     * Reads the full content of a stream and returns them in a byte array
     *
     * @param stream the stream to read
     * @return a byte array containing all of the bytes from the stream
     * @throws java.io.IOException if there is a problem reading from the input stream
     */
    public static byte[] inputStreamToArray(InputStream stream) throws java.io.IOException {
        byte[] b = new byte[8192];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (true) {
            int read = stream.read(b);
            if (read < 1) {
                break;
            }
            output.write(b, 0, read);
        }
        output.close();
        return output.toByteArray();
    }

    /**
     * Copy bytes from the {@code RandomAccessSource} to {@code OutputStream}.
     *
     * @param source the {@code RandomAccessSource} copy from.
     * @param start  start position of source copy from.
     * @param length length copy to.
     * @param output   the {@code OutputStream} copy to.
     * @throws java.io.IOException on error.
     */
    public static void copyBytes(IRandomAccessSource source, long start, long length, java.io.OutputStream output) throws java.io.IOException {
        if (length <= 0) {
            return;
        }
        long idx = start;
        byte[] buf = new byte[8192];
        while (length > 0) {
            long n = source.get(idx, buf,0, (int) Math.min((long) buf.length, length));
            if (n <= 0) {
                throw new EOFException();
            }
            output.write(buf, 0, (int) n);
            idx += n;
            length -= n;
        }
    }

    /**
     *
     * Reads {@code len}  bytes from an input stream.
     *
     * @param b the buffer into which the data is read.
     * @param off an int specifying the offset into the data.
     * @param len an int specifying the number of bytes to read.
     * @exception EOFException  if this stream reaches the end before reading all the bytes.
     * @exception IOException   if an I/O error occurs.
     */
    public static void readFully(InputStream input, byte[] b, int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = input.read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

}

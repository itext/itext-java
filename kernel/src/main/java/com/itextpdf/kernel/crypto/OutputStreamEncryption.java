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
package com.itextpdf.kernel.crypto;

import java.io.IOException;

public abstract class OutputStreamEncryption extends java.io.OutputStream {

    protected java.io.OutputStream out;
    private byte[] sb = new byte[1];

    /**
     * Creates a new instance of OutputStreamCounter
     */
    protected OutputStreamEncryption(java.io.OutputStream out) {
        this.out = out;
    }

    /**
     * Closes this output stream and releases any system resources
     * associated with this stream. The general contract of {@code close}
     * is that it closes the output stream. A closed stream cannot perform
     * output operations and cannot be reopened.
     * <p>
     * The {@code close} method of {@code OutputStream} does nothing.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        finish();
        out.close();
    }

    /**
     * Flushes this output stream and forces any buffered output bytes
     * to be written out. The general contract of {@code flush} is
     * that calling it is an indication that, if any bytes previously
     * written have been buffered by the implementation of the output
     * stream, such bytes should immediately be written to their
     * intended destination.
     * <p>
     * The {@code flush} method of {@code OutputStream} does nothing.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Writes {@code b.length} bytes from the specified byte array
     * to this output stream. The general contract for {@code write(b)}
     * is that it should have exactly the same effect as the call
     * {@code write(b, 0, b.length)}.
     *
     * @param b the data.
     * @throws IOException if an I/O error occurs.
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * Writes the specified byte to this output stream. The general
     * contract for {@code write} is that one byte is written
     * to the output stream. The byte to be written is the eight
     * low-order bits of the argument {@code b}. The 24
     * high-order bits of {@code b} are ignored.
     * <p>
     * Subclasses of {@code OutputStream} must provide an
     * implementation for this method.
     *
     * @param b the {@code byte}.
     * @throws IOException if an I/O error occurs. In particular, an {@code IOException} may be thrown if the
     *                     output stream has been closed.
     */
    public void write(int b) throws IOException {
        sb[0] = (byte) b;
        write(sb, 0, 1);
    }

    /**
     * Writes {@code len} bytes from the specified byte array
     * starting at offset {@code off} to this output stream.
     * The general contract for {@code write(b, off, len)} is that
     * some of the bytes in the array {@code b} are written to the
     * output stream in order; element {@code b[off]} is the first
     * byte written and {@code b[off+len-1]} is the last byte written
     * by this operation.
     * <p>
     * The {@code write} method of {@code OutputStream} calls
     * the write method of one argument on each of the bytes to be
     * written out. Subclasses are encouraged to override this method and
     * provide a more efficient implementation.
     * <p>
     * If {@code b} is {@code null}, a
     * {@code NullPointerException} is thrown.
     * <p>
     * If {@code off} is negative, or {@code len} is negative, or
     * {@code off+len} is greater than the length of the array
     * {@code b}, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs. In particular,
     *                     an {@code IOException} is thrown if the output
     *                     stream is closed.
     */
    public abstract void write(byte[] b, int off, int len) throws IOException;

    public abstract void finish();
}

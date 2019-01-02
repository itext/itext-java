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

import com.itextpdf.kernel.PdfException;
import java.io.IOException;

public class OutputStreamAesEncryption extends OutputStreamEncryption {
    protected AESCipher cipher;
    private boolean finished;

    /**
     * Creates a new instance of OutputStreamCounter
     */
    public OutputStreamAesEncryption(java.io.OutputStream out, byte[] key, int off, int len) {
        super(out);
        byte[] iv = IVGenerator.getIV();
        byte[] nkey = new byte[len];
        System.arraycopy(key, off, nkey, 0, len);
        cipher = new AESCipher(true, nkey, iv);
        try {
            write(iv);
        } catch (IOException e) {
            throw new PdfException(PdfException.PdfEncryption, e);
        }
    }

    public OutputStreamAesEncryption(java.io.OutputStream out, byte[] key) {
        this(out, key, 0, key.length);
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
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] b2 = cipher.update(b, off, len);
        if (b2 == null || b2.length == 0)
            return;
        out.write(b2, 0, b2.length);
    }

    public void finish() {
        if (!finished) {
            finished = true;

            byte[] b = cipher.doFinal();
            try {
                out.write(b, 0, b.length);
            } catch (IOException e) {
                throw new PdfException(PdfException.PdfEncryption, e);
            }
        }
    }
}

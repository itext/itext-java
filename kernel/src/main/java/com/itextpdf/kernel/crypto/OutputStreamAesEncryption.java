/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.crypto;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import java.io.IOException;

public class OutputStreamAesEncryption extends OutputStreamEncryption {
    protected AESCipher cipher;
    private boolean finished;

    /**
     * Creates a new instance of {@link OutputStreamAesEncryption}
     * @param out the {@link java.io.OutputStream} instance to be used as the destination for the encrypted content
     * @param key the byte array containing the key for encryption
     * @param off offset of the key in the byte array
     * @param len the length of the key in the byte array
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
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }
    }

    /**
     * Creates a new instance of {@link OutputStreamAesEncryption}
     * @param out the {@link java.io.OutputStream} instance to be used as the destination for the encrypted content
     * @param key the byte array which is the key for encryption
     */
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
                throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
            }
        }
    }
}

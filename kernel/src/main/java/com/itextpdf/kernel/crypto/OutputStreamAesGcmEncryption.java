/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * An output stream accepts output bytes and sends them to underlying {@link OutputStreamEncryption} instance.
 */
public class OutputStreamAesGcmEncryption extends OutputStreamEncryption {
    private final AESGCMCipher cipher;
    private boolean finished;
    private static final SecureRandom rng = new SecureRandom();

    /**
     * Creates a new instance of {@link OutputStreamAesGcmEncryption}.
     *
     * @param out the {@link java.io.OutputStream} instance to be used as the destination for the encrypted content
     * @param key the byte array containing the key for encryption
     * @param noncePart a 7 byte nonce
     */
    public OutputStreamAesGcmEncryption(java.io.OutputStream out, byte[] key, byte[] noncePart) {
        super(out);
        byte[] iv = new byte[12];
        byte[] randomPart = new byte[5];
        synchronized (rng) {
            rng.nextBytes(randomPart);
        }
        System.arraycopy(randomPart, 0, iv, 0, 5);
        System.arraycopy(noncePart, 0, iv, 5, 7);
        cipher = new AESGCMCipher(true, key, iv);
        try {
            out.write(iv);
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }
    }

    /**
     * Writes {@code len} bytes from the specified byte array
     * starting at offset {@code off} to this output stream.
     * The general contract for {@code write(b, off, len)} is that
     * some bytes in the array {@code b} are written to the
     * output stream in order; element {@code b[off]} is the first
     * byte written and {@code b[off+len-1]} is the last byte written
     * by this operation.
     * <p>
     * The {@code write} method of {@code OutputStream} calls
     * the write method of one argument on each of the bytes to be
     * written out. Subclasses are encouraged to override this method and
     * provide a more efficient implementation.
     * <p>
     * If {@code off} is negative, or {@code len} is negative, or
     * {@code off+len} is greater than the length of the array
     * {@code b}, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param b   the data
     * @param off the start offset in the data
     * @param len the number of bytes to write
     *
     * @throws IOException if an I/O error occurs. In particular,
     *                     an {@code IOException} is thrown if the output
     *                     stream is closed
     */
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] cipherBuffer = cipher.update(b, off, len);
        if (cipherBuffer.length != 0) {
            out.write(cipherBuffer, 0, cipherBuffer.length);
        }
    }

    /**
     * Finishes and dispose all resources used for writing in encrypted stream.
     * Input data that may have been buffered during a previous update operation is processed,
     * with padding (if requested) being applied and authentication tag is appended.
     */
    public void finish() {
        if (!finished) {
            finished = true;
            byte[] cipherBuffer = cipher.doFinal();
            try {
                out.write(cipherBuffer, 0, cipherBuffer.length);
            } catch (IOException e) {
                throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
            }
        }
    }
}

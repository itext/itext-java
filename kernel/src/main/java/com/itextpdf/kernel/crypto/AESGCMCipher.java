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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.crypto.modes.IGCMBlockCipher;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import java.security.GeneralSecurityException;

/**
 * Creates an Advanced Encryption Standard-Galois/Counter Mode (AES-GCM) Cipher.
 */
public class AESGCMCipher {
    public static final int MAC_SIZE_BITS = 128;

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final IGCMBlockCipher cipher;

    /**
     * Creates a new instance of {@link AESGCMCipher}.
     *
     * @param forEncryption if true the cipher is initialised for
     *                      encryption, if false for decryption
     * @param key the key to be used in the cipher
     * @param iv initialization vector to be used in cipher
     */
    public AESGCMCipher(boolean forEncryption, byte[] key, byte[] iv) {
        try {
            cipher = BOUNCY_CASTLE_FACTORY.createGCMBlockCipher();
            cipher.init(forEncryption, key, MAC_SIZE_BITS, iv);
        } catch (GeneralSecurityException e) {
            throw new PdfException(KernelExceptionMessageConstant.ERROR_WHILE_INITIALIZING_AES_CIPHER, e);
        }
    }

    /**
     * Continues a multiple-part encryption or decryption operation
     * (depending on how this cipher was initialized), processing another data
     * part.
     *
     * <p>
     * The first {@code len} bytes in the {@code b} input buffer, starting at {@code off} offset inclusive,
     * are processed, and the result is stored in a new buffer.
     *
     * @param b the input buffer
     * @param off the offset in {@code b} where the input starts
     * @param len the input length
     *
     * @return the new buffer with the result
     */
    public byte[] update(byte[] b, int off, int len) {
        byte[] cipherBuffer = new byte[cipher.getUpdateOutputSize(len)];
        try {
            cipher.processBytes(b, off, len, cipherBuffer, 0);
        } catch (GeneralSecurityException e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }
        return cipherBuffer;
    }

    /**
     * Finishes a multiple-part encryption or decryption operation, depending on how this cipher was initialized
     * and resets underlying cipher object to the state it was in when previously
     * initialized via a call to init.
     *
     * @return final bytes array
     */
    public byte[] doFinal() {
        byte[] cipherBuffer = new byte[cipher.getOutputSize(0)];
        try {
            cipher.doFinal(cipherBuffer, 0);
            return cipherBuffer;
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }
    }
}

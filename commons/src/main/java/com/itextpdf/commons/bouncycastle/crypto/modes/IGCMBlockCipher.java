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
package com.itextpdf.commons.bouncycastle.crypto.modes;

import java.security.GeneralSecurityException;

/**
 * Interface for aes-gcm cryptographic ciphers.
 */
public interface IGCMBlockCipher {

    /**
     * Initialize this cipher with a key and a set of algorithm parameters.
     *
     * @param forEncryption true to use encrypt mode, false to use decrypt mode
     * @param key the encryption key
     * @param macSizeBits MAC size, MAC sizes from 32 bits to 128 bits (must be a multiple of 8)
     * @param iv the IV source buffer
     *
     * @throws GeneralSecurityException in case any error during encryption or decryption
     */
    void init(boolean forEncryption, byte[] key, int macSizeBits, byte[] iv) throws GeneralSecurityException;

    /**
     * Returns the length in bytes that an output buffer would need to be in order to hold the result of
     * the next update operation, given the input length (in bytes).
     *
     * @param len input length (in bytes)
     *
     * @return output length in bytes
     */
    int getUpdateOutputSize(int len);

    /**
     * Perform a multiple-part encryption or decryption operation (depending on how this cipher was initialized),
     * processing another data part.
     *
     * @param input the input buffer
     * @param inputOffset the offset in input where the input starts
     * @param len the input length
     * @param output the buffer for the result
     * @param outOffset the offset in output where the result is stored
     *
     * @throws GeneralSecurityException in case any error during encryption or decryption
     */
    void processBytes(byte[] input, int inputOffset, int len, byte[] output, int outOffset)
            throws GeneralSecurityException;

    /**
     * Returns the length in bytes that an output buffer would need to be in order to hold the result of
     * the next doFinal operation, given the input length (in bytes).
     *
     * @param len input length (in bytes)
     *
     * @return output length in bytes
     */
    int getOutputSize(int len);

    /**
     * Finishes a multiple-part encryption or decryption operation, depending on how this cipher was initialized.
     * Input data that may have been buffered during a previous update operation is processed, also
     * the authentication tag is appended in the case of encryption, or verified in the case of decryption.
     *
     * @param plainText the buffer for the result
     * @param i the offset in output where the result is stored
     *
     * @throws GeneralSecurityException in case any error during encryption or decryption
     */
    void doFinal(byte[] plainText, int i) throws GeneralSecurityException;
}

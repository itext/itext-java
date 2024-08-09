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

/**
 * Class for decrypting aes-gcm encrypted bytes.
 */
public class AesGcmDecryptor implements IDecryptor {
    private AESGCMCipher cipher;
    private final byte[] key;
    private boolean initiated;
    private final byte[] iv = new byte[12];
    private int ivptr;

    /**
     * Creates a new instance of {@link com.itextpdf.kernel.crypto.AesGcmDecryptor}.
     *
     * @param key the byte array containing the key for decryption
     * @param off offset of the key in the byte array
     * @param len the length of the key in the byte array
     */
    public AesGcmDecryptor(byte[] key, int off, int len) {
        this.key = new byte[len];
        System.arraycopy(key, off, this.key, 0, len);
    }

    /**
     * Continues a multiple-part decryption operation, processing another data part and initializing aes-gcm cipher if
     * this method called for the first time.
     *
     * @param b  the input buffer
     * @param off the offset in input where the input starts
     * @param len the input length
     *
     * @return decrypted bytes array
     */
    public byte[] update(byte[] b, int off, int len) {
        if (!initiated) {
            int left = Math.min(iv.length - ivptr, len);
            System.arraycopy(b, off, iv, ivptr, left);
            off += left;
            len -= left;
            ivptr += left;
            if (ivptr == iv.length) {
                cipher = new AESGCMCipher(false, key, iv);
                initiated = true;
            }
            if (len == 0) {
                return null;
            }
        }
        return cipher.update(b, off, len);
    }

    /**
     * Finishes a multiple-part decryption operation.
     *
     * @return input data that may have been buffered during a previous update operation
     */
    public byte[] finish() {
        if (cipher != null) {
            return cipher.doFinal();
        } else {
            return null;
        }
    }
}

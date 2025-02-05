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

public class AesDecryptor implements IDecryptor {
    private AESCipher cipher;
    private byte[] key;
    private boolean initiated;
    private byte[] iv = new byte[16];
    private int ivptr;

    /**
     * Creates a new instance of {@link AesDecryptor}
     * @param key the byte array containing the key for decryption
     * @param off offset of the key in the byte array
     * @param len the length of the key in the byte array
     */
    public AesDecryptor(byte[] key, int off, int len) {
        this.key = new byte[len];
        System.arraycopy(key, off, this.key, 0, len);
    }

    public byte[] update(byte[] b, int off, int len) {
        if (initiated) {
            return cipher.update(b, off, len);
        } else {
            int left = Math.min(iv.length - ivptr, len);
            System.arraycopy(b, off, iv, ivptr, left);
            off += left;
            len -= left;
            ivptr += left;
            if (ivptr == iv.length) {
                cipher = new AESCipher(false, key, iv);
                initiated = true;
                if (len > 0)
                    return cipher.update(b, off, len);
            }
            return null;
        }
    }

    public byte[] finish() {
        if (cipher != null) {
            return cipher.doFinal();
        } else {
            return null;
        }
    }
}

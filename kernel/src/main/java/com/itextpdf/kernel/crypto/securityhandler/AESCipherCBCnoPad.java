/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.crypto.securityhandler;

/**
 * Creates an AES Cipher with CBC and no padding.
 */
class AESCipherCBCnoPad {

    com.itextpdf.kernel.crypto.AESCipherCBCnoPad aESCipherCBCnoPad;

    /**
     * Creates a new instance of AESCipher with CBC and no padding
     *
     * @param forEncryption if true the cipher is initialised for
     *                      encryption, if false for decryption
     * @param key           the key to be used in the cipher
     */
    AESCipherCBCnoPad(boolean forEncryption, byte[] key) {
        aESCipherCBCnoPad = new com.itextpdf.kernel.crypto.AESCipherCBCnoPad(forEncryption, key);
    }

    /**
     * Creates a new instance of AESCipher with CBC and no padding
     *
     * @param forEncryption if true the cipher is initialised for
     *                      encryption, if false for decryption
     * @param key           the key to be used in the cipher
     * @param initVector    initialization vector to be used in cipher
     */
    AESCipherCBCnoPad(boolean forEncryption, byte[] key, byte[] initVector) {
        aESCipherCBCnoPad = new com.itextpdf.kernel.crypto.AESCipherCBCnoPad(forEncryption, key, initVector);
    }

    byte[] processBlock(byte[] inp, int inpOff, int inpLen) {
        return aESCipherCBCnoPad.processBlock(inp, inpOff, inpLen);
    }
}

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
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * Creates an AES Cipher with CBC and no padding.
 */
public class AESCipherCBCnoPad {
    
    private static final String CIPHER_WITHOUT_PADDING = "AES/CBC/NoPadding";

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final Cipher cipher;

    /**
     * Creates a new instance of AESCipher with CBC and no padding
     *
     * @param forEncryption if true the cipher is initialised for
     *                      encryption, if false for decryption
     * @param key           the key to be used in the cipher
     */
    public AESCipherCBCnoPad(boolean forEncryption, byte[] key) {
        this(forEncryption, key, new byte[16]);
    }

    /**
     * Creates a new instance of AESCipher with CBC and no padding
     *
     * @param forEncryption if true the cipher is initialised for
     *                      encryption, if false for decryption
     * @param key           the key to be used in the cipher
     * @param initVector    initialization vector to be used in cipher
     */
    public AESCipherCBCnoPad(boolean forEncryption, byte[] key, byte[] initVector) {
        try {
            if ("BC".equals(BOUNCY_CASTLE_FACTORY.getProviderName())) {
                // Do not pass bc provider and use default one here not to require bc provider for this functionality
                // Do not use bc provider in kernel
                cipher = Cipher.getInstance(CIPHER_WITHOUT_PADDING);
            } else {
                cipher = Cipher.getInstance(CIPHER_WITHOUT_PADDING, BOUNCY_CASTLE_FACTORY.getProvider());
            }
            cipher.init(forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new IvParameterSpec(initVector));
        } catch (GeneralSecurityException e) {
            throw new PdfException(KernelExceptionMessageConstant.ERROR_WHILE_INITIALIZING_AES_CIPHER, e);
        }
    }

    public byte[] processBlock(byte[] inp, int inpOff, int inpLen) {
        return cipher.update(inp, inpOff, inpLen);
    }
}

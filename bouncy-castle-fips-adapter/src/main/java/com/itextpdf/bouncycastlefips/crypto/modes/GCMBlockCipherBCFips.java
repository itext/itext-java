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
package com.itextpdf.bouncycastlefips.crypto.modes;

import com.itextpdf.commons.bouncycastle.crypto.modes.IGCMBlockCipher;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 * This class provides the functionality of a cryptographic cipher of aes-gcm for encryption and decryption via
 * wrapping and correctly populating {@link Cipher} class.
 */
public class GCMBlockCipherBCFips implements IGCMBlockCipher {
    private final Cipher cipher;
    private boolean forEncryption;
    private int macSize;

    /**
     * Creates new aes-gcm block cipher class.
     *
     * @param cipher crypto instance to populate for aes-gcm encryption
     */
    public GCMBlockCipherBCFips(Cipher cipher) {
        this.cipher = cipher;
    }

    /**
     * Gets actual aes-gcm cipher being wrapped.
     *
     * @return wrapped {@link Cipher}
     */
    public Cipher getCipher() {
        return cipher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(boolean forEncryption, byte[] key, int macSizeBits, byte[] iv) throws GeneralSecurityException {
        cipher.init(forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
                new SecretKeySpec(key, "AES"), new GCMParameterSpec(macSizeBits, iv));
        this.forEncryption = forEncryption;
        this.macSize = macSizeBits / 8;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUpdateOutputSize(int len) {
        int size;
        if (this.forEncryption) {
            size = getOutputSize(len) - this.macSize;
        } else {
            size = getOutputSize(len);
        }

        return size - size % 16;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processBytes(byte[] input, int inputOffset, int len, byte[] output, int outOffset) throws GeneralSecurityException {
        cipher.update(input, inputOffset, len, output, outOffset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOutputSize(int len) {
        return cipher.getOutputSize(len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFinal(byte[] plainText, int i) throws GeneralSecurityException {
        cipher.doFinal(plainText, i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GCMBlockCipherBCFips that = (GCMBlockCipherBCFips) o;
        return Objects.equals(cipher, that.cipher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(cipher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return cipher.toString();
    }
}

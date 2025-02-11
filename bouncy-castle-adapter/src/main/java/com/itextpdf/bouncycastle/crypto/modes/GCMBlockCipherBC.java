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
package com.itextpdf.bouncycastle.crypto.modes;

import com.itextpdf.commons.bouncycastle.crypto.modes.IGCMBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.util.Objects;

/**
 * This class provides the functionality of a cryptographic cipher of aes-gcm for encryption and decryption via
 * wrapping the corresponding {@code GCMBlockCipher} class from bouncy-castle.
 */
public class GCMBlockCipherBC implements IGCMBlockCipher {
    private final GCMBlockCipher cipher;

    /**
     * Creates new wrapper for {@link GCMBlockCipher} aes-gcm block cipher class.
     *
     * @param cipher bouncy-castle class to wrap
     */
    public GCMBlockCipherBC(GCMBlockCipher cipher) {
        this.cipher = cipher;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link GCMBlockCipher}
     */
    public GCMBlockCipher getCipher() {
        return cipher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(boolean forEncryption, byte[] key, int macSizeBits, byte[] iv) {
        cipher.init(forEncryption, new AEADParameters(new KeyParameter(key), macSizeBits, iv));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUpdateOutputSize(int len) {
        return cipher.getUpdateOutputSize(len);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processBytes(byte[] input, int inputOffset, int len, byte[] output, int outOffset) {
        cipher.processBytes(input, inputOffset, len, output, outOffset);
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
    public void doFinal(byte[] plainText, int i) {
        try {
            cipher.doFinal(plainText, i);
        } catch (InvalidCipherTextException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
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
        GCMBlockCipherBC that = (GCMBlockCipherBC) o;
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

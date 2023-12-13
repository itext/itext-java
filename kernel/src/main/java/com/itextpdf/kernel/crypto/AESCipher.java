/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Creates an AES Cipher with CBC and padding PKCS5/7.
 * @author Paulo Soares
 */
public class AESCipher {

    private PaddedBufferedBlockCipher bp;
    
    /**
     * Creates a new instance of AESCipher
     *
     * @param forEncryption if true the cipher is initialised for
     * encryption, if false for decryption
     * @param key the key to be used in the cipher
     * @param iv initialization vector to be used in cipher
     */
    public AESCipher(boolean forEncryption, byte[] key, byte[] iv) {
        BlockCipher aes = new AESFastEngine();
        BlockCipher cbc = new CBCBlockCipher(aes);
        bp = new PaddedBufferedBlockCipher(cbc);
        KeyParameter kp = new KeyParameter(key);
        ParametersWithIV piv = new ParametersWithIV(kp, iv);
        bp.init(forEncryption, piv);
    }
    
    public byte[] update(byte[] inp, int inpOff, int inpLen) {
        int neededLen = bp.getUpdateOutputSize(inpLen);
        byte[] outp;
        if (neededLen > 0) {
            outp = new byte[neededLen];
        } else {
            outp = new byte[0];
        }
        bp.processBytes(inp, inpOff, inpLen, outp, 0);
        return outp;
    }
    
    public byte[] doFinal() {
        int neededLen = bp.getOutputSize(0);
        byte[] outp = new byte[neededLen];
        int n;
        try {
            n = bp.doFinal(outp, 0);
        } catch (Exception ex) {
            return outp;
        }
        if (n != outp.length) {
            byte[] outp2 = new byte[n];
            System.arraycopy(outp, 0, outp2, 0, n);
            return outp2;
        }
        else
            return outp;
    }

}

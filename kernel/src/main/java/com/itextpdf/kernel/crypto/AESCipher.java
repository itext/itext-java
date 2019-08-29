/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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

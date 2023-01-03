/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Creates an AES Cipher with CBC and no padding.
 *
 * @author Paulo Soares
 */
public class AESCipherCBCnoPad {
    
    private static final String CIPHER_WITHOUT_PADDING = "AES/CBC/NoPadding";

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static Cipher cipher;
    
    static {
        try {
            cipher = Cipher.getInstance(CIPHER_WITHOUT_PADDING, BOUNCY_CASTLE_FACTORY.getProvider());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new PdfException(KernelExceptionMessageConstant.ERROR_WHILE_INITIALIZING_AES_CIPHER, e);
        }
    }

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
            cipher.init(forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new IvParameterSpec(initVector));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new PdfException(KernelExceptionMessageConstant.ERROR_WHILE_INITIALIZING_AES_CIPHER, e);
        }
    }

    public byte[] processBlock(byte[] inp, int inpOff, int inpLen) {
        return cipher.update(inp, inpOff, inpLen);
    }
}

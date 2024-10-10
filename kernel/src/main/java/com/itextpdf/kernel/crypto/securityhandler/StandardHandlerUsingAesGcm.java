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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.crypto.AesGcmDecryptor;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamAesGcmEncryption;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfVersion;

import java.io.OutputStream;

/**
 * Standard security handler with Advanced Encryption Standard-Galois/Counter Mode (AES-GCM) encryption algorithm.
 */
public class StandardHandlerUsingAesGcm extends StandardHandlerUsingAes256 {

    protected byte[] noncePart = null;
    protected int inObjectNonceCounter = 0;

    /**
     * Creates new {@link StandardHandlerUsingAesGcm} instance for encryption.
     *
     * @param encryptionDictionary document's encryption dictionary
     * @param userPassword user password
     * @param ownerPassword owner password
     * @param permissions access permissions
     * @param encryptMetadata indicates whether the document-level metadata stream shall be encrypted
     * @param embeddedFilesOnly indicates whether embedded files shall be encrypted in an otherwise unencrypted document
     */
    public StandardHandlerUsingAesGcm(PdfDictionary encryptionDictionary, byte[] userPassword, byte[] ownerPassword,
                                      int permissions, boolean encryptMetadata, boolean embeddedFilesOnly) {
        super(encryptionDictionary, userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, PdfVersion.PDF_2_0);
    }

    /**
     * Creates new {@link StandardHandlerUsingAesGcm} instance for decryption.
     *
     * @param encryptionDictionary document's encryption dictionary
     * @param password owner or user password to decrypt the document
     */
    public StandardHandlerUsingAesGcm(PdfDictionary encryptionDictionary, byte[] password) {
        super(encryptionDictionary, password);
    }

    @Override
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        // Make sure the same IV is never used twice in the same file. We do this by turning the objId/objGen into a
        // 5-byte nonce (with generation restricted to 1 byte instead of 2) plus an in-object 2-byte counter that
        // increments each time a new string is encrypted within the same object. The remaining 5 bytes will be
        // generated randomly using a strong PRNG.
        // This is very different from the situation with AES-CBC, where randomness is paramount. GCM uses a variation
        // of counter mode, so making sure the IV is unique is more important than randomness.
        this.inObjectNonceCounter = 0;
        this.noncePart = new byte[]{
                0, 0,
                (byte) (objGeneration),
                (byte) (objNumber >>> 24),
                (byte) (objNumber >>> 16),
                (byte) (objNumber >>> 8),
                (byte) (objNumber),
        };
    }

    @Override
    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        int ctr = inObjectNonceCounter;
        noncePart[0] = (byte) (ctr >>> 8);
        noncePart[1] = (byte) ctr;
        return new OutputStreamAesGcmEncryption(os, nextObjectKey, noncePart);
    }

    @Override
    public IDecryptor getDecryptor() {
        return new AesGcmDecryptor(nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    void setAES256DicEntries(PdfDictionary encryptionDictionary, byte[] oeKey, byte[] ueKey, byte[] aes256Perms,
                                     boolean encryptMetadata, boolean embeddedFilesOnly) {
        int version = 6;
        int revision = 7;
        PdfName cryptoFilter = PdfName.AESV4;
        setEncryptionDictionaryEntries(encryptionDictionary, oeKey, ueKey, aes256Perms, encryptMetadata, embeddedFilesOnly,
                version, revision, cryptoFilter);
    }

    @Override
    boolean isPdf2(PdfDictionary encryptionDictionary) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMd5MessageDigest() {
        //Do nothing to not initialize md5 message digest, since it's not used by AES-GCM handler
    }
}

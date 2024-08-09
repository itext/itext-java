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
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;

import java.io.OutputStream;
import java.security.Key;
import java.security.cert.Certificate;

/**
 * Public-key security handler with Advanced Encryption Standard-Galois/Counter Mode (AES-GCM) encryption algorithm.
 */
public class PubSecHandlerUsingAesGcm extends PubSecHandlerUsingAes256 {

    protected byte[] noncePart = null;
    protected int inObjectNonceCounter = 0;

    /**
     * Creates new {@link PubSecHandlerUsingAesGcm} instance for encryption.
     *
     * @param encryptionDictionary document's encryption dictionary
     * @param certs recipients' X.509 public key certificates
     * @param permissions access permissions provided to each recipient
     * @param encryptMetadata indicates whether the document-level metadata stream shall be encrypted
     * @param embeddedFilesOnly indicates whether embedded files shall be encrypted in an otherwise unencrypted document
     */
    public PubSecHandlerUsingAesGcm(PdfDictionary encryptionDictionary, Certificate[] certs, int[] permissions,
                                    boolean encryptMetadata, boolean embeddedFilesOnly) {
        super(encryptionDictionary, certs, permissions, encryptMetadata, embeddedFilesOnly);
    }

    /**
     * Creates new {@link PubSecHandlerUsingAesGcm} instance for decryption.
     *
     * @param encryptionDictionary document's encryption dictionary
     * @param certificateKey the recipient private {@link Key} to the certificate
     * @param certificate the recipient {@link Certificate}, serves as recipient identifier
     * @param certificateKeyProvider the certificate key provider id
     *                               for {@link java.security.Security#getProvider(String)}
     * @param externalDecryptionProcess the external decryption process to be used
     * @param encryptMetadata indicates whether the document-level metadata stream shall be encrypted
     */
    public PubSecHandlerUsingAesGcm(PdfDictionary encryptionDictionary, Key certificateKey, Certificate certificate,
                                    String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess,
                                    boolean encryptMetadata) {
        super(encryptionDictionary, certificateKey, certificate, certificateKeyProvider, externalDecryptionProcess,
                encryptMetadata);
    }

    @Override
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
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
    protected void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly) {
        super.setPubSecSpecificHandlerDicEntries(encryptionDictionary, encryptMetadata, embeddedFilesOnly);
        encryptionDictionary.put(PdfName.R, new PdfNumber(6));
        encryptionDictionary.put(PdfName.V, new PdfNumber(7));
    }
}

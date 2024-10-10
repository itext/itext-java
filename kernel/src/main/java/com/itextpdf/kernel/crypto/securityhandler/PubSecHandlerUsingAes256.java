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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;
import java.security.Key;
import java.security.cert.Certificate;

public class PubSecHandlerUsingAes256 extends PubSecHandlerUsingAes128 {

    public PubSecHandlerUsingAes256(PdfDictionary encryptionDictionary, Certificate[] certs, int[] permissions,
                                    boolean encryptMetadata, boolean embeddedFilesOnly) {
        super(encryptionDictionary, certs, permissions, encryptMetadata, embeddedFilesOnly);
    }

    public PubSecHandlerUsingAes256(PdfDictionary encryptionDictionary, Key certificateKey, Certificate certificate,
                                    String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess,
                                    boolean encryptMetadata) {
        super(encryptionDictionary, certificateKey, certificate, certificateKeyProvider, externalDecryptionProcess,
                encryptMetadata);
    }

    @Override
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        // in AES256 we don't recalculate nextObjectKey
    }

    @Override
    protected String getDigestAlgorithm() {
        return "SHA-256";
    }

    protected void initKey(byte[] globalKey, int keyLength) {
        nextObjectKey = globalKey;
        nextObjectKeySize = 32;
    }

    @Override
    protected void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata,
                                                      boolean embeddedFilesOnly) {
        int version = 5;
        PdfName filter = PdfName.AESV3;
        setEncryptionDictEntries(encryptionDictionary, encryptMetadata, embeddedFilesOnly, version, filter);
    }

    void setEncryptionDictEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata,
                                        boolean embeddedFilesOnly, int version, PdfName cryptFilter) {
        encryptionDictionary.put(PdfName.Filter, PdfName.Adobe_PubSec);
        encryptionDictionary.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_s5);

        encryptionDictionary.put(PdfName.V, new PdfNumber(version));

        PdfArray recipients = createRecipientsArray();
        PdfDictionary stdcf = new PdfDictionary();
        stdcf.put(PdfName.Recipients, recipients);
        if (!encryptMetadata) {
            stdcf.put(PdfName.EncryptMetadata, PdfBoolean.FALSE);
        }
        stdcf.put(PdfName.CFM, cryptFilter);
        stdcf.put(PdfName.Length, new PdfNumber(256));
        PdfDictionary cf = new PdfDictionary();
        cf.put(PdfName.DefaultCryptFilter, stdcf);
        encryptionDictionary.put(PdfName.CF, cf);
        if (embeddedFilesOnly) {
            encryptionDictionary.put(PdfName.EFF, PdfName.DefaultCryptFilter);
            encryptionDictionary.put(PdfName.StrF, PdfName.Identity);
            encryptionDictionary.put(PdfName.StmF, PdfName.Identity);
        } else {
            encryptionDictionary.put(PdfName.StrF, PdfName.DefaultCryptFilter);
            encryptionDictionary.put(PdfName.StmF, PdfName.DefaultCryptFilter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMd5MessageDigest() {
        //Do nothing to not initialize md5 message digest, since it's not used by AES256 handler
    }
}

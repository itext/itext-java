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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.crypto.AesDecryptor;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamAesEncryption;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;
import java.io.OutputStream;
import java.security.Key;
import java.security.cert.Certificate;

public class PubSecHandlerUsingAes128 extends PubKeySecurityHandler {
    private static final byte[] salt = {(byte) 0x73, (byte) 0x41, (byte) 0x6c,
            (byte) 0x54};

    public PubSecHandlerUsingAes128(PdfDictionary encryptionDictionary, Certificate[] certs, int[] permissions, boolean encryptMetadata, boolean embeddedFilesOnly) {
        initKeyAndFillDictionary(encryptionDictionary, certs, permissions, encryptMetadata, embeddedFilesOnly);
    }

    public PubSecHandlerUsingAes128(PdfDictionary encryptionDictionary, Key certificateKey, Certificate certificate, String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess, boolean encryptMetadata) {
        initKeyAndReadDictionary(encryptionDictionary, certificateKey, certificate, certificateKeyProvider,
                externalDecryptionProcess, encryptMetadata);
    }

    @Override
    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return new OutputStreamAesEncryption(os, nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    public IDecryptor getDecryptor() {
        return new AesDecryptor(nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        // added by ujihara
        md5.reset();
        extra[0] = (byte) objNumber;
        extra[1] = (byte) (objNumber >> 8);
        extra[2] = (byte) (objNumber >> 16);
        extra[3] = (byte) objGeneration;
        extra[4] = (byte) (objGeneration >> 8);
        md5.update(mkey);
        md5.update(extra);
        md5.update(salt);
        nextObjectKey = md5.digest();
        nextObjectKeySize = mkey.length + 5;
        if (nextObjectKeySize > 16)
            nextObjectKeySize = 16;
    }

    protected String getDigestAlgorithm() {
        return "SHA-1";
    }

    protected void initKey(byte[] globalKey, int keyLength) {
        mkey = new byte[keyLength / 8];
        System.arraycopy(globalKey, 0, mkey, 0, mkey.length);
    }

    @Override
    protected void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly) {
        encryptionDictionary.put(PdfName.Filter, PdfName.Adobe_PubSec);
        encryptionDictionary.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_s5);

        encryptionDictionary.put(PdfName.V, new PdfNumber(4));

        PdfArray recipients = createRecipientsArray();
        PdfDictionary stdcf = new PdfDictionary();
        stdcf.put(PdfName.Recipients, recipients);
        if (!encryptMetadata) {
            stdcf.put(PdfName.EncryptMetadata, PdfBoolean.FALSE);
        }
        stdcf.put(PdfName.CFM, PdfName.AESV2);
        stdcf.put(PdfName.Length, new PdfNumber(128));
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
}

package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.crypto.Decryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.crypto.OutputStreamStandardEncryption;
import com.itextpdf.kernel.crypto.StandardDecryptor;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.security.ExternalDecryptionProcess;
import java.io.OutputStream;
import java.security.Key;
import java.security.cert.Certificate;

public class PubSecHandlerUsingStandard40 extends PubKeySecurityHandler {
    public PubSecHandlerUsingStandard40(PdfDictionary encryptionDictionary, Certificate[] certs, int[] permissions, boolean encryptMetadata, boolean embeddedFilesOnly) {
        initKeyAndFillDictionary(encryptionDictionary, certs, permissions, encryptMetadata, embeddedFilesOnly);
    }

    public PubSecHandlerUsingStandard40(PdfDictionary encryptionDictionary, Key certificateKey, Certificate certificate,
                                        String certificateKeyProvider, ExternalDecryptionProcess externalDecryptionProcess,
                                        boolean encryptMetadata) {
        initKeyAndReadDictionary(encryptionDictionary, certificateKey, certificate, certificateKeyProvider,
                                externalDecryptionProcess, encryptMetadata);
    }

    @Override
    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return new OutputStreamStandardEncryption(os, nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    public Decryptor getDecryptor() {
        return new StandardDecryptor(nextObjectKey, 0, nextObjectKeySize);
    }

    protected String getDigestAlgorithm() {
        return "SHA-1";
    }

    protected void initKey(byte[] globalKey, int keyLength) {
        mkey = new byte[keyLength / 8];
        System.arraycopy(globalKey, 0, mkey, 0, mkey.length);
    }

    protected void setPubSecSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly) {
        encryptionDictionary.put(PdfName.Filter, PdfName.Adobe_PubSec);
        encryptionDictionary.put(PdfName.R, new PdfNumber(2));

        PdfArray recipients = createRecipientsArray();
        encryptionDictionary.put(PdfName.V, new PdfNumber(1));
        encryptionDictionary.put(PdfName.SubFilter, PdfName.Adbe_pkcs7_s4);
        encryptionDictionary.put(PdfName.Recipients, recipients);
    }
}

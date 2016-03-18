package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.kernel.crypto.AesDecryptor;
import com.itextpdf.kernel.crypto.Decryptor;
import com.itextpdf.kernel.crypto.OutputStreamAesEncryption;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import java.io.OutputStream;

public class StandardHandlerUsingAes128 extends StandardHandlerUsingStandard128 {
    private static final byte[] salt = {(byte) 0x73, (byte) 0x41, (byte) 0x6c,
            (byte) 0x54};

    public StandardHandlerUsingAes128(PdfDictionary encryptionDictionary, byte userPassword[], byte ownerPassword[],
                                      int permissions, boolean encryptMetadata, boolean embeddedFilesOnly, byte[] documentId) {
        super(encryptionDictionary, userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, documentId);
    }

    public StandardHandlerUsingAes128(PdfDictionary encryptionDictionary, byte[] password, byte[] documentId, boolean encryptMetadata) {
        super(encryptionDictionary, password, documentId, encryptMetadata);
    }

    @Override
    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return new OutputStreamAesEncryption(os, nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    public Decryptor getDecryptor() {
        return new AesDecryptor(nextObjectKey, 0, nextObjectKeySize);
    }

    @Override
    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        md5.reset(); // added by ujihara
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

    @Override
    protected void setSpecificHandlerDicEntries(PdfDictionary encryptionDictionary, boolean encryptMetadata, boolean embeddedFilesOnly) {
        if (!encryptMetadata) {
            encryptionDictionary.put(PdfName.EncryptMetadata, PdfBoolean.PdfFalse);
        }
        encryptionDictionary.put(PdfName.R, new PdfNumber(4));
        encryptionDictionary.put(PdfName.V, new PdfNumber(4));
        PdfDictionary stdcf = new PdfDictionary();
        stdcf.put(PdfName.Length, new PdfNumber(16));
        if (embeddedFilesOnly) {
            stdcf.put(PdfName.AuthEvent, PdfName.EFOpen);
            encryptionDictionary.put(PdfName.EFF, PdfName.StdCF);
            encryptionDictionary.put(PdfName.StrF, PdfName.Identity);
            encryptionDictionary.put(PdfName.StmF, PdfName.Identity);
        } else {
            stdcf.put(PdfName.AuthEvent, PdfName.DocOpen);
            encryptionDictionary.put(PdfName.StrF, PdfName.StdCF);
            encryptionDictionary.put(PdfName.StmF, PdfName.StdCF);
        }
        stdcf.put(PdfName.CFM, PdfName.AESV2);
        PdfDictionary cf = new PdfDictionary();
        cf.put(PdfName.StdCF, stdcf);
        encryptionDictionary.put(PdfName.CF, cf);
    }
}

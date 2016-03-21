package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.Decryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.crypto.securityhandler.PubKeySecurityHandler;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingAes128;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingAes256;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingStandard128;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingStandard40;
import com.itextpdf.kernel.crypto.securityhandler.SecurityHandler;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAes128;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAes256;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingStandard128;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingStandard40;
import com.itextpdf.kernel.crypto.securityhandler.StandardSecurityHandler;
import com.itextpdf.kernel.security.ExternalDecryptionProcess;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.cert.Certificate;

/**
 * @author Paulo Soares
 * @author Kazuya Ujihara
 */
public class PdfEncryption extends PdfObjectWrapper<PdfDictionary> {
    private static final int StandardEncryption40 = 2;
    private static final int StandardEncryption128 = 3;
    private static final int Aes128 = 4;
    private static final int Aes256 = 5;

    private static long seq = System.currentTimeMillis();

    private int cryptoMode;

    private Long permissions;
    private boolean encryptMetadata;
    private boolean embeddedFilesOnly;

    private byte[] documentId;

    private SecurityHandler securityHandler;

    /**
     * Creates the encryption. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     * AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     * The permissions can be combined by ORing them.
     *
     * @param userPassword   the user password. Can be null or empty
     * @param ownerPassword  the owner password. Can be null or empty
     * @param permissions    the user permissions
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128 or ENCRYPTION_AES128.
     *                       Optionally DO_NOT_ENCRYPT_METADATA can be ored to output the metadata in cleartext
     * @throws PdfException if the document is already open
     */
    public PdfEncryption(byte userPassword[], byte ownerPassword[], int permissions, int encryptionType, byte[] documentId) {
        super(new PdfDictionary());
        this.documentId = documentId;

        int revision = setCryptoMode(encryptionType);
        switch (revision) {
            case StandardEncryption40:
                StandardHandlerUsingStandard40 handlerStd40 = new StandardHandlerUsingStandard40(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerStd40.getPermissions();
                securityHandler = handlerStd40;
                break;
            case StandardEncryption128:
                StandardHandlerUsingStandard128 handlerStd128 = new StandardHandlerUsingStandard128(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerStd128.getPermissions();
                securityHandler = handlerStd128;
                break;
            case Aes128:
                StandardHandlerUsingAes128 handlerAes128 = new StandardHandlerUsingAes128(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerAes128.getPermissions();
                securityHandler = handlerAes128;
                break;
            case Aes256:
                StandardHandlerUsingAes256 handlerAes256 = new StandardHandlerUsingAes256(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly);
                this.permissions = handlerAes256.getPermissions();
                securityHandler = handlerAes256;
                break;
        }
    }

    /**
     * Creates the certificate encryption. An array of one or more public certificates
     * must be provided together with an array of the same size for the permissions for each certificate.
     * The open permissions for the document can be
     * AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     * AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     * The permissions can be combined by ORing them.
     * Optionally DO_NOT_ENCRYPT_METADATA can be ored to output the metadata in cleartext
     *
     * @param certs          the public certificates to be used for the encryption
     * @param permissions    the user permissions for each of the certificates
     * @param encryptionType the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128 or ENCRYPTION_AES128.
     * @throws PdfException if the document is already open
     */
    public PdfEncryption(final Certificate[] certs, final int[] permissions, final int encryptionType) {
        super(new PdfDictionary());
        int revision = setCryptoMode(encryptionType);
        switch (revision) {
            case StandardEncryption40:
                securityHandler = new PubSecHandlerUsingStandard40(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
            case StandardEncryption128:
                securityHandler = new PubSecHandlerUsingStandard128(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
            case Aes128:
                securityHandler = new PubSecHandlerUsingAes128(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
            case Aes256:
                securityHandler = new PubSecHandlerUsingAes256(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
        }
    }

    public PdfEncryption(PdfDictionary pdfDict, byte[] password, byte[] documentId) {
        super(pdfDict);
        setForbidRelease();
        this.documentId = documentId;

        int revision = readAndSetCryptoModeForStdHandler(pdfDict);
        switch (revision) {
            case StandardEncryption40:
                StandardHandlerUsingStandard40 handlerStd40 = new StandardHandlerUsingStandard40(this.getPdfObject(), password, documentId, encryptMetadata);
                permissions = handlerStd40.getPermissions();
                securityHandler = handlerStd40;
                break;
            case StandardEncryption128:
                StandardHandlerUsingStandard128 handlerStd128 = new StandardHandlerUsingStandard128(this.getPdfObject(), password, documentId, encryptMetadata);
                permissions = handlerStd128.getPermissions();
                securityHandler = handlerStd128;
                break;
            case Aes128:
                StandardHandlerUsingAes128 handlerAes128 = new StandardHandlerUsingAes128(this.getPdfObject(), password, documentId, encryptMetadata);
                permissions = handlerAes128.getPermissions();
                securityHandler = handlerAes128;
                break;
            case Aes256:
                StandardHandlerUsingAes256 aes256Handler =  new StandardHandlerUsingAes256(this.getPdfObject(), password);
                permissions = aes256Handler.getPermissions();
                encryptMetadata = aes256Handler.isEncryptMetadata();
                securityHandler = aes256Handler;
                break;
        }
    }

    public PdfEncryption(PdfDictionary pdfDict, Key certificateKey, Certificate certificate,
                         String certificateKeyProvider, ExternalDecryptionProcess externalDecryptionProcess) {
        super(pdfDict);
        setForbidRelease();
        int revision = readAndSetCryptoModeForPubSecHandler(pdfDict);
        switch (revision) {
            case StandardEncryption40:
                securityHandler = new PubSecHandlerUsingStandard40(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
            case StandardEncryption128:
                securityHandler = new PubSecHandlerUsingStandard128(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
            case Aes128:
                securityHandler = new PubSecHandlerUsingAes128(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
            case Aes256:
                securityHandler = new PubSecHandlerUsingAes256(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
        }
    }

    public static byte[] generateNewDocumentId() {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new PdfException(PdfException.PdfEncryption, e);
        }
        long time = System.currentTimeMillis();
        long mem = Runtime.getRuntime().freeMemory();
        String s = time + "+" + mem + "+" + (seq++);

        return md5.digest(s.getBytes());
    }

    public static PdfObject createInfoId(byte id[], boolean modified) {
        com.itextpdf.io.source.ByteBuffer buf = new com.itextpdf.io.source.ByteBuffer(90);
        buf.append('[').append('<');
        if (id.length != 16)
            id = generateNewDocumentId();
        for (int k = 0; k < 16; ++k)
            buf.appendHex(id[k]);
        buf.append('>').append('<');
        if (modified)
            id = generateNewDocumentId();
        for (int k = 0; k < 16; ++k)
            buf.appendHex(id[k]);
        buf.append('>').append(']');
        return new PdfLiteral(buf.toByteArray());
    }

    public Long getPermissions() {
        return permissions;
    }

    public int getCryptoMode() {
        return cryptoMode;
    }

    public boolean isMetadataEncrypted() {
        return encryptMetadata;
    }

    public boolean isEmbeddedFilesOnly() {
        return embeddedFilesOnly;
    }

    /**
     * @return document id which was used for encryption. Could be null, if encryption doesn't rely on document id.
     */
    public byte[] getDocumentId() {
        return documentId;
    }

    public void setHashKeyForNextObject(int objNumber, int objGeneration) {
        securityHandler.setHashKeyForNextObject(objNumber, objGeneration);
    }


    public OutputStreamEncryption getEncryptionStream(OutputStream os) {
        return securityHandler.getEncryptionStream(os);
    }

    public byte[] encryptByteArray(byte[] b) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        OutputStreamEncryption ose = getEncryptionStream(ba);
        try {
            ose.write(b);
        } catch (IOException e) {
            throw new PdfException(PdfException.PdfEncryption, e);
        }
        ose.finish();
        return ba.toByteArray();
    }

    public byte[] decryptByteArray(byte[] b) {
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            Decryptor dec = securityHandler.getDecryptor();
            byte[] b2 = dec.update(b, 0, b.length);
            if (b2 != null)
                ba.write(b2);
            b2 = dec.finish();
            if (b2 != null)
                ba.write(b2);
            return ba.toByteArray();
        } catch (IOException e) {
            throw new PdfException(PdfException.PdfEncryption, e);
        }
    }

    public boolean isOpenedWithFullPermission() {
        if (securityHandler instanceof PubKeySecurityHandler) {
            return true;
        } else if (securityHandler instanceof StandardSecurityHandler) {
            return ((StandardSecurityHandler) securityHandler).isUsedOwnerPassword();
        }
        return true;
    }

    /**
     * Computes user password if standard encryption handler is used with Standard40, Standard128 or AES128 algorithm.
     * @param ownerPassword owner password of the encrypted document.
     * @return user password, or null if not a standard encryption handler was used.
     */
    public byte[] computeUserPassword(byte[] ownerPassword) {
        byte[] userPassword = null;
        if (securityHandler instanceof StandardHandlerUsingStandard40) {
            userPassword = ((StandardHandlerUsingStandard40) securityHandler).computeUserPassword(ownerPassword, getPdfObject());
        }
        return userPassword;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private void setKeyLength(int keyLength) {
        // 40 - is default value;
        if (keyLength != 40) {
            getPdfObject().put(PdfName.Length, new PdfNumber(keyLength));
        }
    }

    private int setCryptoMode(int mode) {
        return setCryptoMode(mode, 0);
    }

    private int setCryptoMode(int mode, int length) {
        int revision;
        cryptoMode = mode;
        encryptMetadata = (mode & PdfWriter.DO_NOT_ENCRYPT_METADATA) != PdfWriter.DO_NOT_ENCRYPT_METADATA;
        embeddedFilesOnly = (mode & PdfWriter.EMBEDDED_FILES_ONLY) == PdfWriter.EMBEDDED_FILES_ONLY;
        mode &= PdfWriter.ENCRYPTION_MASK;
        switch (mode) {
            case PdfWriter.STANDARD_ENCRYPTION_40:
                encryptMetadata = true;
                embeddedFilesOnly = false;
                setKeyLength(40);
                revision = StandardEncryption40;
                break;
            case PdfWriter.STANDARD_ENCRYPTION_128:
                embeddedFilesOnly = false;
                if (length > 0) {
                    setKeyLength(length);
                } else {
                    setKeyLength(128);
                }
                revision = StandardEncryption128;
                break;
            case PdfWriter.ENCRYPTION_AES_128:
                setKeyLength(128);
                revision = Aes128;
                break;
            case PdfWriter.ENCRYPTION_AES_256:
                setKeyLength(256);
                revision = Aes256;
                break;
            default:
                throw new PdfException(PdfException.NoValidEncryptionMode);
        }
        return revision;
    }

    private int readAndSetCryptoModeForStdHandler(PdfDictionary encDict) {
        int cryptoMode;
        int length = 0;

        PdfNumber rValue = encDict.getAsNumber(PdfName.R);
        if (rValue == null)
            throw new PdfException(PdfException.IllegalRValue);
        int revision  = rValue.getIntValue();
        switch (revision) {
            case 2:
                cryptoMode = PdfWriter.STANDARD_ENCRYPTION_40;
                break;
            case 3:
                PdfNumber lengthValue = encDict.getAsNumber(PdfName.Length);
                if (lengthValue == null)
                    throw new PdfException(PdfException.IllegalLengthValue);
                length = lengthValue.getIntValue();
                if (length > 128 || length < 40 || length % 8 != 0)
                    throw new PdfException(PdfException.IllegalLengthValue);
                cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                break;
            case 4:
                PdfDictionary dic = (PdfDictionary) encDict.get(PdfName.CF);
                if (dic == null)
                    throw new PdfException(PdfException.CfNotFoundEncryption);
                dic = (PdfDictionary) dic.get(PdfName.StdCF);
                if (dic == null)
                    throw new PdfException(PdfException.StdcfNotFoundEncryption);
                if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = PdfWriter.ENCRYPTION_AES_128;
                } else {
                    throw new PdfException(PdfException.NoCompatibleEncryptionFound);
                }
                PdfBoolean em = encDict.getAsBoolean(PdfName.EncryptMetadata);
                if (em != null && !em.getValue()) {
                    cryptoMode |= PdfWriter.DO_NOT_ENCRYPT_METADATA;
                }
                break;
            case 5:
                cryptoMode = PdfWriter.ENCRYPTION_AES_256;
                PdfBoolean em5 = encDict.getAsBoolean(PdfName.EncryptMetadata);
                if (em5 != null && !em5.getValue()) {
                    cryptoMode |= PdfWriter.DO_NOT_ENCRYPT_METADATA;
                }
                break;
            default:
                throw new PdfException(PdfException.UnknownEncryptionTypeREq1).setMessageParams(rValue);
        }

        revision = setCryptoMode(cryptoMode, length);
        return revision;
    }

    private int readAndSetCryptoModeForPubSecHandler(PdfDictionary encDict) {
        int cryptoMode;
        int length = 0;

        PdfNumber vValue = encDict.getAsNumber(PdfName.V);
        if (vValue == null)
            throw new PdfException(PdfException.IllegalVValue);
        int v = vValue.getIntValue();
        switch (v) {
            case 1:
                cryptoMode = PdfWriter.STANDARD_ENCRYPTION_40;
                length = 40;
                break;
            case 2:
                PdfNumber lengthValue = encDict.getAsNumber(PdfName.Length);
                if (lengthValue == null)
                    throw new PdfException(PdfException.IllegalLengthValue);
                length = lengthValue.getIntValue();
                if (length > 128 || length < 40 || length % 8 != 0)
                    throw new PdfException(PdfException.IllegalLengthValue);
                cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                break;
            case 4:
            case 5:
                PdfDictionary dic = encDict.getAsDictionary(PdfName.CF);
                if (dic == null)
                    throw new PdfException(PdfException.CfNotFoundEncryption);
                dic = (PdfDictionary) dic.get(PdfName.DefaultCryptFilter);
                if (dic == null)
                    throw new PdfException(PdfException.DefaultcryptfilterNotFoundEncryption);
                if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = PdfWriter.STANDARD_ENCRYPTION_128;
                    length = 128;
                } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = PdfWriter.ENCRYPTION_AES_128;
                    length = 128;
                } else if (PdfName.AESV3.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = PdfWriter.ENCRYPTION_AES_256;
                    length = 256;
                } else {
                    throw new PdfException(PdfException.NoCompatibleEncryptionFound);
                }
                PdfBoolean em = dic.getAsBoolean(PdfName.EncryptMetadata);
                if (em != null && !em.getValue()) {
                    cryptoMode |= PdfWriter.DO_NOT_ENCRYPT_METADATA;
                }
                break;
            default:
                throw new PdfException(PdfException.UnknownEncryptionTypeVEq1, vValue);
        }

        int revision = setCryptoMode(cryptoMode, length);
        return revision;
    }
}

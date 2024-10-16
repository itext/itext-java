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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.io.source.ByteBuffer;
import com.itextpdf.kernel.crypto.IDecryptor;
import com.itextpdf.kernel.crypto.OutputStreamEncryption;
import com.itextpdf.kernel.crypto.securityhandler.PubKeySecurityHandler;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingAes128;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingAes256;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingAesGcm;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingStandard128;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingStandard40;
import com.itextpdf.kernel.crypto.securityhandler.SecurityHandler;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAes128;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAes256;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAesGcm;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingStandard128;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingStandard40;
import com.itextpdf.kernel.crypto.securityhandler.StandardSecurityHandler;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.mac.IMacContainerLocator;
import com.itextpdf.kernel.mac.MacValidationException;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;
import com.itextpdf.kernel.mac.AbstractMacIntegrityProtector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class PdfEncryption extends PdfObjectWrapper<PdfDictionary> {
    private static final int STANDARD_ENCRYPTION_40 = 2;
    private static final int STANDARD_ENCRYPTION_128 = 3;
    private static final int AES_128 = 4;
    private static final int AES_256 = 5;
    private static final int AES_GCM = 6;
    private static final int DEFAULT_KEY_LENGTH = 40;
    private static final int MAC_ENABLED = ~(1 << 12);
    private static final int MAC_DISABLED = 1 << 12;

    private static long seq = SystemUtil.getTimeBasedSeed();

    private int cryptoMode;

    private Integer permissions;
    private boolean encryptMetadata;
    private boolean embeddedFilesOnly;
    private byte[] documentId;
    private SecurityHandler securityHandler;
    private AbstractMacIntegrityProtector macContainer;

    /**
     * Creates the encryption.
     *
     * @param userPassword   the user password. Can be null or of zero length, which is equal to
     *                       omitting the user password
     * @param ownerPassword  the owner password. If it's null or empty, iText will generate
     *                       a random string to be used as the owner password
     * @param permissions    the user permissions
     *                       The open permissions for the document can be
     *                       {@link EncryptionConstants#ALLOW_PRINTING},
     *                       {@link EncryptionConstants#ALLOW_MODIFY_CONTENTS},
     *                       {@link EncryptionConstants#ALLOW_COPY},
     *                       {@link EncryptionConstants#ALLOW_MODIFY_ANNOTATIONS},
     *                       {@link EncryptionConstants#ALLOW_FILL_IN},
     *                       {@link EncryptionConstants#ALLOW_SCREENREADERS},
     *                       {@link EncryptionConstants#ALLOW_ASSEMBLY} and
     *                       {@link EncryptionConstants#ALLOW_DEGRADED_PRINTING}.
     *                       The permissions can be combined by ORing them
     * @param encryptionType the type of encryption. It can be one of
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_40},
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_128},
     *                       {@link EncryptionConstants#ENCRYPTION_AES_128}
     *                       or {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                       Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be
     *                       ORed to output the metadata in cleartext.
     *                       {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                       Please be aware that the passed encryption types may override permissions:
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                       {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                       {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                       {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     * @param documentId     document id which will be used for encryption
     * @param version        the {@link PdfVersion} of the target document for encryption
     * @param macContainer   {@link AbstractMacIntegrityProtector} class for MAC integrity protection
     */
    public PdfEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType,
            byte[] documentId, PdfVersion version, AbstractMacIntegrityProtector macContainer) {
        super(new PdfDictionary());
        this.macContainer = macContainer;
        this.documentId = documentId;
        if (version != null && version.compareTo(PdfVersion.PDF_2_0) >= 0) {
            permissions = fixAccessibilityPermissionPdf20(permissions);
        }
        permissions = configureAccessibilityPermissionsForMac(permissions);
        int revision = setCryptoMode(encryptionType);
        switch (revision) {
            case STANDARD_ENCRYPTION_40:
                StandardHandlerUsingStandard40 handlerStd40 = new StandardHandlerUsingStandard40(this.getPdfObject(),
                        userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerStd40.getPermissions();
                securityHandler = handlerStd40;
                break;
            case STANDARD_ENCRYPTION_128:
                StandardHandlerUsingStandard128 handlerStd128 = new StandardHandlerUsingStandard128(this.getPdfObject(),
                        userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerStd128.getPermissions();
                securityHandler = handlerStd128;
                break;
            case AES_128:
                StandardHandlerUsingAes128 handlerAes128 = new StandardHandlerUsingAes128(this.getPdfObject(),
                        userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerAes128.getPermissions();
                securityHandler = handlerAes128;
                break;
            case AES_256:
                StandardHandlerUsingAes256 handlerAes256 = new StandardHandlerUsingAes256(this.getPdfObject(),
                        userPassword, ownerPassword, permissions, encryptMetadata, embeddedFilesOnly, version);
                this.permissions = handlerAes256.getPermissions();
                securityHandler = handlerAes256;
                break;
            case AES_GCM:
                StandardHandlerUsingAesGcm handlerAesGcm = new StandardHandlerUsingAesGcm(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly);
                this.permissions = handlerAesGcm.getPermissions();
                securityHandler = handlerAesGcm;
                break;
        }
    }

    /**
     * Creates the certificate encryption.
     * <p>
     * An array of one or more public certificates must be provided together with
     * an array of the same size for the permissions for each certificate.
     *
     * @param certs          the public certificates to be used for the encryption
     * @param permissions    the user permissions for each of the certificates
     *                       The open permissions for the document can be
     *                       {@link EncryptionConstants#ALLOW_PRINTING},
     *                       {@link EncryptionConstants#ALLOW_MODIFY_CONTENTS},
     *                       {@link EncryptionConstants#ALLOW_COPY},
     *                       {@link EncryptionConstants#ALLOW_MODIFY_ANNOTATIONS},
     *                       {@link EncryptionConstants#ALLOW_FILL_IN},
     *                       {@link EncryptionConstants#ALLOW_SCREENREADERS},
     *                       {@link EncryptionConstants#ALLOW_ASSEMBLY} and
     *                       {@link EncryptionConstants#ALLOW_DEGRADED_PRINTING}.
     *                       The permissions can be combined by ORing them
     * @param encryptionType the type of encryption. It can be one of
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_40},
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_128},
     *                       {@link EncryptionConstants#ENCRYPTION_AES_128}
     *                       or {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                       Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be ORed
     *                       to output the metadata in cleartext.
     *                       {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                       Please be aware that the passed encryption types may override permissions:
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                       {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                       {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                       {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                       {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *
     * @param version        the {@link PdfVersion} of the target document for encryption
     * @param macContainer   {@link AbstractMacIntegrityProtector} class for MAC integrity protection
     */
    public PdfEncryption(Certificate[] certs, int[] permissions, int encryptionType, PdfVersion version,
            AbstractMacIntegrityProtector macContainer) {
        super(new PdfDictionary());
        this.macContainer = macContainer;
        for (int i = 0; i < permissions.length; i++) {
            if (version != null && version.compareTo(PdfVersion.PDF_2_0) >= 0) {
                permissions[i] = fixAccessibilityPermissionPdf20(permissions[i]);
            }
            permissions[i] = configureAccessibilityPermissionsForMac(permissions[i]);
        }
        int revision = setCryptoMode(encryptionType);
        switch (revision) {
            case STANDARD_ENCRYPTION_40:
                securityHandler = new PubSecHandlerUsingStandard40(this.getPdfObject(), certs, permissions,
                        encryptMetadata, embeddedFilesOnly);
                break;
            case STANDARD_ENCRYPTION_128:
                securityHandler = new PubSecHandlerUsingStandard128(this.getPdfObject(), certs, permissions,
                        encryptMetadata, embeddedFilesOnly);
                break;
            case AES_128:
                securityHandler = new PubSecHandlerUsingAes128(this.getPdfObject(), certs, permissions,
                        encryptMetadata, embeddedFilesOnly);
                break;
            case AES_256:
                securityHandler = new PubSecHandlerUsingAes256(this.getPdfObject(), certs, permissions,
                        encryptMetadata, embeddedFilesOnly);
                break;
            case AES_GCM:
                securityHandler = new PubSecHandlerUsingAesGcm(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
        }
    }

    /**
     * Creates {@link PdfEncryption} instance based on already existing standard encryption dictionary.
     *
     * @param pdfDict {@link PdfDictionary}, which represents encryption dictionary
     * @param password {@code byte[]}, which represents encryption password
     * @param documentId original file ID, the first element in {@link PdfName#ID} key of trailer
     */
    public PdfEncryption(PdfDictionary pdfDict, byte[] password, byte[] documentId) {
        super(pdfDict);
        setForbidRelease();
        this.documentId = documentId;

        int revision = readAndSetCryptoModeForStdHandler(pdfDict);
        switch (revision) {
            case STANDARD_ENCRYPTION_40:
                StandardHandlerUsingStandard40 handlerStd40 = new StandardHandlerUsingStandard40(this.getPdfObject(),
                        password, documentId, encryptMetadata);
                permissions = handlerStd40.getPermissions();
                securityHandler = handlerStd40;
                break;
            case STANDARD_ENCRYPTION_128:
                StandardHandlerUsingStandard128 handlerStd128 = new StandardHandlerUsingStandard128(this.getPdfObject(),
                        password, documentId, encryptMetadata);
                permissions = handlerStd128.getPermissions();
                securityHandler = handlerStd128;
                break;
            case AES_128:
                StandardHandlerUsingAes128 handlerAes128 = new StandardHandlerUsingAes128(this.getPdfObject(), password,
                        documentId, encryptMetadata);
                permissions = handlerAes128.getPermissions();
                securityHandler = handlerAes128;
                break;
            case AES_256:
                StandardHandlerUsingAes256 aes256Handler = new StandardHandlerUsingAes256(this.getPdfObject(),
                        password);
                permissions = aes256Handler.getPermissions();
                encryptMetadata = aes256Handler.isEncryptMetadata();
                securityHandler = aes256Handler;
                break;
            case AES_GCM:
                StandardHandlerUsingAesGcm aesGcmHandler = new StandardHandlerUsingAesGcm(this.getPdfObject(), password);
                permissions = aesGcmHandler.getPermissions();
                encryptMetadata = aesGcmHandler.isEncryptMetadata();
                securityHandler = aesGcmHandler;
                break;
        }
    }

    /**
     * Creates {@link PdfEncryption} instance based on already existing public encryption dictionary.
     *
     * @param pdfDict {@link PdfDictionary}, which represents encryption dictionary
     * @param certificateKey the recipient private {@link Key} to the certificate
     * @param certificate the recipient {@link Certificate}, which serves as recipient identifier
     * @param certificateKeyProvider the certificate key provider id for {@link java.security.Security#getProvider}
     * @param externalDecryptionProcess {@link IExternalDecryptionProcess} the external decryption process to be used
     */
    public PdfEncryption(PdfDictionary pdfDict, Key certificateKey, Certificate certificate,
            String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess) {
        super(pdfDict);
        setForbidRelease();
        int revision = readAndSetCryptoModeForPubSecHandler(pdfDict);
        switch (revision) {
            case STANDARD_ENCRYPTION_40:
                securityHandler = new PubSecHandlerUsingStandard40(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
            case STANDARD_ENCRYPTION_128:
                securityHandler = new PubSecHandlerUsingStandard128(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
            case AES_128:
                securityHandler = new PubSecHandlerUsingAes128(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
            case AES_256:
                securityHandler = new PubSecHandlerUsingAes256(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
            case AES_GCM:
                securityHandler = new PubSecHandlerUsingAesGcm(this.getPdfObject(), certificateKey, certificate,
                        certificateKeyProvider, externalDecryptionProcess, encryptMetadata);
                break;
        }
    }

    public static byte[] generateNewDocumentId() {
        MessageDigest sha512;
        try {
            sha512 = MessageDigest.getInstance("SHA-512");
        } catch (Exception e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }
        long time = SystemUtil.getTimeBasedSeed();
        long mem = SystemUtil.getFreeMemory();
        String s = time + "+" + mem + "+" + (seq++);

        return sha512.digest(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Creates a PdfLiteral that contains an array of two id entries. These entries are both hexadecimal
     * strings containing 16 hex characters. The first entry is the original id, the second entry
     * should be different from the first one if the document has changed.
     *
     * @param id the first id
     * @param modified whether the document has been changed or not
     *
     * @return PdfObject containing the two entries
     */
    public static PdfObject createInfoId(byte[] id, boolean modified) {
        if (modified) {
            return createInfoId(id, generateNewDocumentId(), false);
        } else {
            return createInfoId(id, id, false);
        }
    }

    /**
     * Creates a PdfLiteral that contains an array of two id entries. These entries are both hexadecimal
     * strings containing up to 16 hex characters. The first entry is the original id, the second entry
     * should be different from the first one if the document has changed.
     *
     * @param firstId the first id
     * @param secondId the second id
     * @param preserveEncryption the encryption preserve
     *
     * @return PdfObject containing the two entries.
     */
    public static PdfObject createInfoId(byte[] firstId, byte[] secondId, boolean preserveEncryption) {
        if (!preserveEncryption) {
            if (firstId.length < 16) {
                firstId = padByteArrayTo16(firstId);
            }

            if (secondId.length < 16) {
                secondId = padByteArrayTo16(secondId);
            }
        }

        ByteBuffer buf = new ByteBuffer(90);
        buf.append('[').append('<');
        for (byte value : firstId) {
            buf.appendHex(value);
        }
        buf.append('>').append('<');
        for (byte b : secondId) {
            buf.appendHex(b);
        }
        buf.append('>').append(']');

        return new PdfLiteral(buf.toByteArray());
    }

    private static byte[] padByteArrayTo16(byte[] documentId) {
        byte[] paddingBytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        System.arraycopy(documentId, 0, paddingBytes, 0, documentId.length);

        return paddingBytes;
    }

    /**
     * Gets the encryption permissions. It can be used directly in
     * {@link WriterProperties#setStandardEncryption(byte[], byte[], int, int)}.
     * See ISO 32000-1, Table 22 for more details.
     *
     * @return the encryption permissions, an unsigned 32-bit quantity.
     */
    public Integer getPermissions() {
        return permissions;
    }

    /**
     * Gets encryption algorithm and access permissions.
     *
     * @return the crypto mode value
     * @see EncryptionConstants
     */
    public int getCryptoMode() {
        return cryptoMode;
    }

    /**
     * Gets encryption algorithm.
     *
     * @return the encryption algorithm
     * @see EncryptionConstants
     */
    public int getEncryptionAlgorithm() {
        return cryptoMode & EncryptionConstants.ENCRYPTION_MASK;
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
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
        }
        ose.finish();
        return ba.toByteArray();
    }

    public byte[] decryptByteArray(byte[] b) {
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            IDecryptor dec = securityHandler.getDecryptor();
            byte[] b2 = dec.update(b, 0, b.length);
            if (b2 != null)
                ba.write(b2);
            b2 = dec.finish();
            if (b2 != null)
                ba.write(b2);
            return ba.toByteArray();
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_ENCRYPTION, e);
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

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private void setKeyLength(int keyLength) {
        if (keyLength != DEFAULT_KEY_LENGTH) {
            getPdfObject().put(PdfName.Length, new PdfNumber(keyLength));
        }
    }

    private int setCryptoMode(int mode) {
        return setCryptoMode(mode, 0);
    }

    private int setCryptoMode(int mode, int length) {
        int revision;
        cryptoMode = mode;
        encryptMetadata =
                (mode & EncryptionConstants.DO_NOT_ENCRYPT_METADATA) != EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
        embeddedFilesOnly = (mode & EncryptionConstants.EMBEDDED_FILES_ONLY) == EncryptionConstants.EMBEDDED_FILES_ONLY;
        mode &= EncryptionConstants.ENCRYPTION_MASK;
        switch (mode) {
            case EncryptionConstants.STANDARD_ENCRYPTION_40:
                encryptMetadata = true;
                embeddedFilesOnly = false;
                setKeyLength(40);
                revision = STANDARD_ENCRYPTION_40;
                break;
            case EncryptionConstants.STANDARD_ENCRYPTION_128:
                if (length > 0) {
                    setKeyLength(length);
                } else {
                    setKeyLength(128);
                }
                revision = STANDARD_ENCRYPTION_128;
                break;
            case EncryptionConstants.ENCRYPTION_AES_128:
                setKeyLength(128);
                revision = AES_128;
                break;
            case EncryptionConstants.ENCRYPTION_AES_256:
                setKeyLength(256);
                revision = AES_256;
                break;
            case EncryptionConstants.ENCRYPTION_AES_GCM:
                setKeyLength(256);
                revision = AES_GCM;
                break;
            default:
                throw new PdfException(KernelExceptionMessageConstant.NO_VALID_ENCRYPTION_MODE);
        }
        return revision;
    }

    private int readAndSetCryptoModeForStdHandler(PdfDictionary encDict) {
        int cryptoMode;
        int length = 0;

        PdfNumber rValue = encDict.getAsNumber(PdfName.R);
        if (rValue == null)
            throw new PdfException(KernelExceptionMessageConstant.ILLEGAL_R_VALUE);
        int revision  = rValue.intValue();
        boolean embeddedFilesOnlyMode = readEmbeddedFilesOnlyFromEncryptDictionary(encDict);
        switch (revision) {
            case 2:
                cryptoMode = EncryptionConstants.STANDARD_ENCRYPTION_40;
                break;
            case 3:
                PdfNumber lengthValue = encDict.getAsNumber(PdfName.Length);
                length = lengthValue == null ? DEFAULT_KEY_LENGTH : lengthValue.intValue();
                if (length > 128 || length < 40 || length % 8 != 0)
                    throw new PdfException(KernelExceptionMessageConstant.ILLEGAL_LENGTH_VALUE);
                cryptoMode = EncryptionConstants.STANDARD_ENCRYPTION_128;
                break;
            case 4:
                PdfDictionary dic = (PdfDictionary) encDict.get(PdfName.CF);
                if (dic == null)
                    throw new PdfException(KernelExceptionMessageConstant.CF_NOT_FOUND_ENCRYPTION);
                dic = (PdfDictionary) dic.get(PdfName.StdCF);
                if (dic == null)
                    throw new PdfException(KernelExceptionMessageConstant.STDCF_NOT_FOUND_ENCRYPTION);
                if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = EncryptionConstants.STANDARD_ENCRYPTION_128;
                } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = EncryptionConstants.ENCRYPTION_AES_128;
                } else {
                    throw new PdfException(KernelExceptionMessageConstant.NO_COMPATIBLE_ENCRYPTION_FOUND);
                }
                PdfBoolean em = encDict.getAsBoolean(PdfName.EncryptMetadata);
                if (em != null && !em.getValue()) {
                    cryptoMode |= EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
                }
                if (embeddedFilesOnlyMode) {
                    cryptoMode |= EncryptionConstants.EMBEDDED_FILES_ONLY;
                }
                break;
            case 5:
            case 6:
                cryptoMode = EncryptionConstants.ENCRYPTION_AES_256;
                PdfBoolean em5 = encDict.getAsBoolean(PdfName.EncryptMetadata);
                if (em5 != null && !em5.getValue()) {
                    cryptoMode |= EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
                }
                if (embeddedFilesOnlyMode) {
                    cryptoMode |= EncryptionConstants.EMBEDDED_FILES_ONLY;
                }
                break;
            case 7:
                // (ISO/TS 32003) The security handler defines the use of encryption
                // and decryption in the same way as when the value of R is 6, and declares at least
                // one crypt filter using the AESV4 method.
                PdfDictionary cfDic = encDict.getAsDictionary(PdfName.CF);
                if (cfDic == null) {
                    throw new PdfException(KernelExceptionMessageConstant.CF_NOT_FOUND_ENCRYPTION);
                }
                cfDic = (PdfDictionary) cfDic.get(PdfName.StdCF);
                if (cfDic == null) {
                    throw new PdfException(KernelExceptionMessageConstant.STDCF_NOT_FOUND_ENCRYPTION);
                }
                if (PdfName.AESV4.equals(cfDic.get(PdfName.CFM))) {
                    cryptoMode = EncryptionConstants.ENCRYPTION_AES_GCM;
                    length = 256;
                } else {
                    throw new PdfException(KernelExceptionMessageConstant.NO_COMPATIBLE_ENCRYPTION_FOUND);
                }
                PdfBoolean em7 = encDict.getAsBoolean(PdfName.EncryptMetadata);
                if (em7 != null && !em7.getValue()) {
                    cryptoMode |= EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
                }
                if (embeddedFilesOnlyMode) {
                    cryptoMode |= EncryptionConstants.EMBEDDED_FILES_ONLY;
                }
                break;
            default:
                throw new PdfException(KernelExceptionMessageConstant.UNKNOWN_ENCRYPTION_TYPE_R)
                        .setMessageParams(rValue);
        }

        revision = setCryptoMode(cryptoMode, length);
        return revision;
    }

    private int readAndSetCryptoModeForPubSecHandler(PdfDictionary encDict) {
        int cryptoMode;
        int length;

        PdfNumber vValue = encDict.getAsNumber(PdfName.V);
        if (vValue == null)
            throw new PdfException(KernelExceptionMessageConstant.ILLEGAL_V_VALUE);
        int v = vValue.intValue();
        boolean embeddedFilesOnlyMode = readEmbeddedFilesOnlyFromEncryptDictionary(encDict);
        switch (v) {
            case 1:
                cryptoMode = EncryptionConstants.STANDARD_ENCRYPTION_40;
                length = 40;
                break;
            case 2:
                PdfNumber lengthValue = encDict.getAsNumber(PdfName.Length);
                length = lengthValue == null ? DEFAULT_KEY_LENGTH : lengthValue.intValue();
                if (length > 128 || length < 40 || length % 8 != 0)
                    throw new PdfException(KernelExceptionMessageConstant.ILLEGAL_LENGTH_VALUE);
                cryptoMode = EncryptionConstants.STANDARD_ENCRYPTION_128;
                break;
            case 4:
            case 5:
                PdfDictionary dic = encDict.getAsDictionary(PdfName.CF);
                if (dic == null)
                    throw new PdfException(KernelExceptionMessageConstant.CF_NOT_FOUND_ENCRYPTION);
                dic = (PdfDictionary) dic.get(PdfName.DefaultCryptFilter);
                if (dic == null)
                    throw new PdfException(KernelExceptionMessageConstant.DEFAULT_CRYPT_FILTER_NOT_FOUND_ENCRYPTION);
                if (PdfName.V2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = EncryptionConstants.STANDARD_ENCRYPTION_128;
                    length = 128;
                } else if (PdfName.AESV2.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = EncryptionConstants.ENCRYPTION_AES_128;
                    length = 128;
                } else if (PdfName.AESV3.equals(dic.get(PdfName.CFM))) {
                    cryptoMode = EncryptionConstants.ENCRYPTION_AES_256;
                    length = 256;
                } else {
                    throw new PdfException(KernelExceptionMessageConstant.NO_COMPATIBLE_ENCRYPTION_FOUND);
                }
                PdfBoolean em = dic.getAsBoolean(PdfName.EncryptMetadata);
                if (em != null && !em.getValue()) {
                    cryptoMode |= EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
                }
                if (embeddedFilesOnlyMode) {
                    cryptoMode |= EncryptionConstants.EMBEDDED_FILES_ONLY;
                }
                break;
            case 6:
                // (ISO/TS 32003) The security handler defines the use of encryption
                // and decryption in the same way as when the value of V is 5, and declares at least
                // one crypt filter using the AESV4 method.
                PdfDictionary cfDic = encDict.getAsDictionary(PdfName.CF);
                if (cfDic == null) {
                    throw new PdfException(KernelExceptionMessageConstant.CF_NOT_FOUND_ENCRYPTION);
                }
                cfDic = (PdfDictionary) cfDic.get(PdfName.DefaultCryptFilter);
                if (cfDic == null) {
                    throw new PdfException(KernelExceptionMessageConstant.DEFAULT_CRYPT_FILTER_NOT_FOUND_ENCRYPTION);
                }
                if (PdfName.AESV4.equals(cfDic.get(PdfName.CFM))) {
                    cryptoMode = EncryptionConstants.ENCRYPTION_AES_GCM;
                    length = 256;
                } else {
                    throw new PdfException(KernelExceptionMessageConstant.NO_COMPATIBLE_ENCRYPTION_FOUND);
                }
                PdfBoolean encrM = cfDic.getAsBoolean(PdfName.EncryptMetadata);
                if (encrM != null && !encrM.getValue()) {
                    cryptoMode |= EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
                }
                if (embeddedFilesOnlyMode) {
                    cryptoMode |= EncryptionConstants.EMBEDDED_FILES_ONLY;
                }
                break;
            default:
                throw new PdfException(KernelExceptionMessageConstant.UNKNOWN_ENCRYPTION_TYPE_V, vValue);
        }
        return setCryptoMode(cryptoMode, length);
    }

    private int configureAccessibilityPermissionsForMac(int permissions) {
        if (macContainer == null) {
            return permissions | MAC_DISABLED;
        } else {
            return permissions & MAC_ENABLED;
        }
    }

    static boolean readEmbeddedFilesOnlyFromEncryptDictionary(PdfDictionary encDict) {
        PdfName embeddedFilesFilter = encDict.getAsName(PdfName.EFF);
        boolean encryptEmbeddedFiles = !PdfName.Identity.equals(embeddedFilesFilter) && embeddedFilesFilter != null;
        boolean encryptStreams = !PdfName.Identity.equals(encDict.getAsName(PdfName.StmF));
        boolean encryptStrings = !PdfName.Identity.equals(encDict.getAsName(PdfName.StrF));
        if (encryptStreams || encryptStrings || !encryptEmbeddedFiles) {
            return false;
        }

        PdfDictionary cfDictionary = encDict.getAsDictionary(PdfName.CF);
        if (cfDictionary != null) {
            // Here we check if the crypt filter for embedded files and the filter in the CF dictionary are the same
            return cfDictionary.getAsDictionary(embeddedFilesFilter) != null;
        }
        return false;
    }

    private static int fixAccessibilityPermissionPdf20(int permissions) {
        // This bit was previously used to determine whether
        // content could be extracted for the purposes of accessibility,
        // however, that restriction has been deprecated in PDF 2.0. PDF
        // readers shall ignore this bit and PDF writers shall always set this
        // bit to 1 to ensure compatibility with PDF readers following
        // earlier specifications.
        return permissions | EncryptionConstants.ALLOW_SCREENREADERS;
    }

    void checkEncryptionRequirements(PdfDocument document) {
        if (macContainer != null) {
            if (document.getPdfVersion() == null || document.getPdfVersion().compareTo(PdfVersion.PDF_2_0) < 0) {
                throw new PdfException(KernelExceptionMessageConstant.MAC_FOR_PDF_2);
            }
            if (this.getPdfObject().getAsNumber(PdfName.V) != null &&
                    this.getPdfObject().getAsNumber(PdfName.V).intValue() < 5) {
                throw new PdfException(KernelExceptionMessageConstant.MAC_FOR_ENCRYPTION_5);
            }
        }

        final int encryption = getEncryptionAlgorithm();
        if (encryption < EncryptionConstants.ENCRYPTION_AES_256) {
            VersionConforming.validatePdfVersionForDeprecatedFeatureLogWarn(document, PdfVersion.PDF_2_0,
                    VersionConforming.DEPRECATED_ENCRYPTION_ALGORITHMS);
        } else if (encryption == EncryptionConstants.ENCRYPTION_AES_256) {
            PdfNumber r = getPdfObject().getAsNumber(PdfName.R);
            if (r != null && r.intValue() == 5) {
                VersionConforming.validatePdfVersionForDeprecatedFeatureLogWarn(document, PdfVersion.PDF_2_0,
                        VersionConforming.DEPRECATED_AES256_REVISION);
            }
        } else if (encryption == EncryptionConstants.ENCRYPTION_AES_GCM) {
            VersionConforming.validatePdfVersionForNotSupportedFeatureLogError(document, PdfVersion.PDF_2_0,
                    VersionConforming.NOT_SUPPORTED_AES_GCM);
        }
    }

    void configureEncryptionParametersFromWriter(PdfDocument document) {
        if (macContainer != null) {
            macContainer.setFileEncryptionKey(securityHandler.getMkey().length == 0 ?
                    securityHandler.getNextObjectKey() : securityHandler.getMkey());

            document.getDiContainer().getInstance(IMacContainerLocator.class).locateMacContainer(macContainer);
            document.getCatalog().addDeveloperExtension(PdfDeveloperExtension.ISO_32004);
            PdfString kdfSalt = getPdfObject().getAsString(PdfName.KDFSalt);
            if (kdfSalt == null) {
                getPdfObject().put(PdfName.KDFSalt, new PdfString(macContainer.getKdfSalt()).setHexWriting(true));
                getPdfObject().setModified();
            }
        } else {
            document.getCatalog().removeDeveloperExtension(PdfDeveloperExtension.ISO_32004);
        }

        if (getEncryptionAlgorithm() == EncryptionConstants.ENCRYPTION_AES_GCM) {
            document.getCatalog().addDeveloperExtension(PdfDeveloperExtension.ISO_32003);
        } else {
            document.getCatalog().removeDeveloperExtension(PdfDeveloperExtension.ISO_32003);
        }
    }

    AbstractMacIntegrityProtector getMacContainer() {
        return macContainer;
    }

    void configureEncryptionParametersFromReader(PdfDocument document, PdfDictionary trailer) {
        PdfVersion sourceVersion = document.getReader().headerPdfVersion;
        PdfVersion destVersion = sourceVersion;
        if (document.getWriter() != null && document.getWriter().getProperties().pdfVersion != null) {
            destVersion = document.getWriter().getProperties().pdfVersion;
        }
        try {
            if (trailer.getAsDictionary(PdfName.AuthCode) != null) {
                macContainer = document.getDiContainer().getInstance(IMacContainerLocator.class)
                        .createMacIntegrityProtector(document, trailer.getAsDictionary(PdfName.AuthCode));
                macContainer.setFileEncryptionKey(securityHandler.getMkey().length == 0 ?
                        securityHandler.getNextObjectKey() : securityHandler.getMkey());
                PdfString kdfSalt = getPdfObject().getAsString(PdfName.KDFSalt);
                if (kdfSalt != null) {
                    macContainer.setKdfSalt(kdfSalt.getValueBytes());
                }
                macContainer.validateMacToken();

                // Disable MAC for writing if explicitly requested. In append mode we cannot disable it because it will
                // remove MAC protection from all previous revisions also for knowledgeable attackers
                // TODO DEVSIX-8635 - Verify MAC permission and embed MAC in stamping mode for public key encryption
                if (document.properties.disableMac && !document.properties.appendMode &&
                        securityHandler instanceof StandardSecurityHandler) {
                    macContainer = null;
                    updateMacPermission();
                }
            } else if (PdfVersion.PDF_2_0.compareTo(destVersion) <= 0 &&
                    permissions != null && (permissions & MAC_DISABLED) == 0) {
                // TODO DEVSIX-8635 - Verify MAC permission and embed MAC in stamping mode for public key encryption
                throw new MacValidationException(KernelExceptionMessageConstant.MAC_PERMS_WITHOUT_MAC);
            } else if (!document.properties.disableMac && !document.properties.appendMode &&
                    securityHandler instanceof StandardSecurityHandler) {
                // TODO DEVSIX-8635 - Verify MAC permission and embed MAC in stamping mode for public key encryption

                // This is the branch responsible for embedding MAC into the documents without MAC
                // Do not embed MAC in append mode as it does not add extra security

                PdfNumber vValue = getPdfObject().getAsNumber(PdfName.V);
                if (vValue == null) {
                    throw new PdfException(KernelExceptionMessageConstant.ILLEGAL_V_VALUE);
                }
                final int v = vValue.intValue();
                // We do not support MAC for increasing PDF version to 2.0 (old encryption do not support it)
                // and decreasing from 2.0 (not supported by the spec)
                // v >= 5 stands for supported encryption algorithms for MAC being used
                if (PdfVersion.PDF_2_0.compareTo(destVersion) <= 0 && PdfVersion.PDF_2_0.compareTo(sourceVersion) <= 0
                        && v >= 5) {
                    macContainer = document.getDiContainer().getInstance(IMacContainerLocator.class)
                            .createMacIntegrityProtector(document, EncryptionProperties.DEFAULT_MAC_PROPERTIES);

                    updateMacPermission();
                }
            }
        } catch (MacValidationException exception) {
            document.getDiContainer().getInstance(IMacContainerLocator.class).handleMacValidationError(exception);
        }
    }

    private void updateMacPermission() {
        // We don't parse permissions on reading for PubSec currently
        if (permissions != null) {
            permissions = configureAccessibilityPermissionsForMac(permissions.intValue());
            if (securityHandler instanceof StandardSecurityHandler) {
                ((StandardSecurityHandler) securityHandler).setPermissions(permissions.intValue(), this.getPdfObject());
            }
        }
    }
}

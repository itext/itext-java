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
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingStandard128;
import com.itextpdf.kernel.crypto.securityhandler.PubSecHandlerUsingStandard40;
import com.itextpdf.kernel.crypto.securityhandler.SecurityHandler;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAes128;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAes256;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingStandard128;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingStandard40;
import com.itextpdf.kernel.crypto.securityhandler.StandardSecurityHandler;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.security.IExternalDecryptionProcess;

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
    private static final int DEFAULT_KEY_LENGTH = 40;

    private static long seq = SystemUtil.getTimeBasedSeed();

    private int cryptoMode;

    private Long permissions;
    private boolean encryptMetadata;
    private boolean embeddedFilesOnly;

    private byte[] documentId;

    private SecurityHandler securityHandler;

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
     */
    public PdfEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType, byte[] documentId, PdfVersion version) {
        super(new PdfDictionary());
        this.documentId = documentId;
        if (version != null && version.compareTo(PdfVersion.PDF_2_0) >= 0) {
            permissions = fixAccessibilityPermissionPdf20(permissions);
        }
        int revision = setCryptoMode(encryptionType);
        switch (revision) {
            case STANDARD_ENCRYPTION_40:
                StandardHandlerUsingStandard40 handlerStd40 = new StandardHandlerUsingStandard40(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerStd40.getPermissions();
                securityHandler = handlerStd40;
                break;
            case STANDARD_ENCRYPTION_128:
                StandardHandlerUsingStandard128 handlerStd128 = new StandardHandlerUsingStandard128(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerStd128.getPermissions();
                securityHandler = handlerStd128;
                break;
            case AES_128:
                StandardHandlerUsingAes128 handlerAes128 = new StandardHandlerUsingAes128(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly, documentId);
                this.permissions = handlerAes128.getPermissions();
                securityHandler = handlerAes128;
                break;
            case AES_256:
                StandardHandlerUsingAes256 handlerAes256 = new StandardHandlerUsingAes256(this.getPdfObject(), userPassword, ownerPassword,
                        permissions, encryptMetadata, embeddedFilesOnly, version);
                this.permissions = handlerAes256.getPermissions();
                securityHandler = handlerAes256;
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
     */
    public PdfEncryption(Certificate[] certs, int[] permissions, int encryptionType, PdfVersion version) {
        super(new PdfDictionary());
        if (version != null && version.compareTo(PdfVersion.PDF_2_0) >= 0) {
            for (int i = 0; i < permissions.length; i++) {
                permissions[i] = fixAccessibilityPermissionPdf20(permissions[i]);
            }
        }
        int revision = setCryptoMode(encryptionType);
        switch (revision) {
            case STANDARD_ENCRYPTION_40:
                securityHandler = new PubSecHandlerUsingStandard40(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
            case STANDARD_ENCRYPTION_128:
                securityHandler = new PubSecHandlerUsingStandard128(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
            case AES_128:
                securityHandler = new PubSecHandlerUsingAes128(this.getPdfObject(), certs, permissions, encryptMetadata, embeddedFilesOnly);
                break;
            case AES_256:
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
            case STANDARD_ENCRYPTION_40:
                StandardHandlerUsingStandard40 handlerStd40 = new StandardHandlerUsingStandard40(this.getPdfObject(), password, documentId, encryptMetadata);
                permissions = handlerStd40.getPermissions();
                securityHandler = handlerStd40;
                break;
            case STANDARD_ENCRYPTION_128:
                StandardHandlerUsingStandard128 handlerStd128 = new StandardHandlerUsingStandard128(this.getPdfObject(), password, documentId, encryptMetadata);
                permissions = handlerStd128.getPermissions();
                securityHandler = handlerStd128;
                break;
            case AES_128:
                StandardHandlerUsingAes128 handlerAes128 = new StandardHandlerUsingAes128(this.getPdfObject(), password, documentId, encryptMetadata);
                permissions = handlerAes128.getPermissions();
                securityHandler = handlerAes128;
                break;
            case AES_256:
                StandardHandlerUsingAes256 aes256Handler =  new StandardHandlerUsingAes256(this.getPdfObject(), password);
                permissions = aes256Handler.getPermissions();
                encryptMetadata = aes256Handler.isEncryptMetadata();
                securityHandler = aes256Handler;
                break;
        }
    }

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
        for (int k = 0; k < firstId.length; ++k)
            buf.appendHex(firstId[k]);
        buf.append('>').append('<');
        for (int k = 0; k < secondId.length; ++k)
            buf.appendHex(secondId[k]);
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
    public Long getPermissions() {
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
        encryptMetadata = (mode & EncryptionConstants.DO_NOT_ENCRYPT_METADATA) != EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
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
            default:
                throw new PdfException(KernelExceptionMessageConstant.UNKNOWN_ENCRYPTION_TYPE_R)
                        .setMessageParams(rValue);
        }

        revision = setCryptoMode(cryptoMode, length);
        return revision;
    }

    private int readAndSetCryptoModeForPubSecHandler(PdfDictionary encDict) {
        int cryptoMode;
        int length = 0;

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
            default:
                throw new PdfException(KernelExceptionMessageConstant.UNKNOWN_ENCRYPTION_TYPE_V, vValue);
        }
        return setCryptoMode(cryptoMode, length);
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

    private int fixAccessibilityPermissionPdf20(int permissions) {
        // This bit was previously used to determine whether
        // content could be extracted for the purposes of accessibility,
        // however, that restriction has been deprecated in PDF 2.0. PDF
        // readers shall ignore this bit and PDF writers shall always set this
        // bit to 1 to ensure compatibility with PDF readers following
        // earlier specifications.
        return permissions | EncryptionConstants.ALLOW_SCREENREADERS;
    }

}

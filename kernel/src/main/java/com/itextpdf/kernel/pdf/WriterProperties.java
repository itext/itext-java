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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.kernel.mac.MacProperties;

import java.security.cert.Certificate;

public class WriterProperties {

    protected int compressionLevel;

    /**
     * Indicates if to use full compression (using object streams).
     */
    protected Boolean isFullCompression;

    /**
     * Indicates if the writer copy objects in a smart mode. If so PdfDictionary and PdfStream will be hashed
     * and reused if there's an object with the same content later.
     */
    protected boolean smartMode;
    protected boolean addXmpMetadata;
    protected boolean addUAXmpMetadata;
    protected PdfVersion pdfVersion;
    protected EncryptionProperties encryptionProperties;
    /**
     * The ID entry that represents the initial identifier.
     */
    protected PdfString initialDocumentId;

    /**
     * The ID entry that represents a change in a document.
     */
    protected PdfString modifiedDocumentId;

    public WriterProperties() {
        smartMode = false;
        addUAXmpMetadata = false;
        compressionLevel = CompressionConstants.DEFAULT_COMPRESSION;
        isFullCompression = null;
        encryptionProperties = new EncryptionProperties();
    }

    /**
     * Defines pdf version for the created document. Default value is PDF_1_7.
     *
     * @param version version for the document.
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setPdfVersion(PdfVersion version) {
        this.pdfVersion = version;
        return this;
    }

    /**
     * Enables smart mode.
     * <br>
     * In smart mode when resources (such as fonts, images,...) are
     * encountered, a reference to these resources is saved
     * in a cache, so that they can be reused.
     * This requires more memory, but reduces the file size
     * of the resulting PDF document.
     *
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties useSmartMode() {
        this.smartMode = true;
        return this;
    }

    /**
     * If true, default XMPMetadata based on {@link PdfDocumentInfo} will be added.
     * For PDF 2.0 documents, metadata will be added in any case.
     *
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties addXmpMetadata() {
        this.addXmpMetadata = true;
        return this;
    }

    /**
     * Defines the level of compression for the document.
     * See {@link CompressionConstants}
     *
     * @param compressionLevel {@link CompressionConstants} value.
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
        return this;
    }

    /**
     * Defines if full compression mode is enabled. If enabled, not only the content of the pdf document will be
     * compressed, but also the pdf document inner structure.
     *
     * @param fullCompressionMode true - to enable full compression mode, false to disable it
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setFullCompressionMode(boolean fullCompressionMode) {
        this.isFullCompression = fullCompressionMode;
        return this;
    }

    /**
     * Sets the encryption options for the document.
     *
     * @param userPassword        the user password. Can be null or of zero length, which is equal to
     *                            omitting the user password
     * @param ownerPassword       the owner password. If it's null or empty, iText will generate
     *                            a random string to be used as the owner password
     * @param permissions         the user permissions
     *                            The open permissions for the document can be
     *                            {@link EncryptionConstants#ALLOW_PRINTING},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_CONTENTS},
     *                            {@link EncryptionConstants#ALLOW_COPY},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_ANNOTATIONS},
     *                            {@link EncryptionConstants#ALLOW_FILL_IN},
     *                            {@link EncryptionConstants#ALLOW_SCREENREADERS},
     *                            {@link EncryptionConstants#ALLOW_ASSEMBLY} and
     *                            {@link EncryptionConstants#ALLOW_DEGRADED_PRINTING}.
     *                            The permissions can be combined by ORing them
     * @param encryptionAlgorithm the type of encryption. It can be one of
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40},
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128},
     *                            {@link EncryptionConstants#ENCRYPTION_AES_128}
     *                            or {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                            Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be ORed
     *                            to output the metadata in cleartext.
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                            Please be aware that the passed encryption types may override permissions:
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                            {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setStandardEncryption(byte[] userPassword, byte[] ownerPassword, int permissions,
            int encryptionAlgorithm) {
        return setStandardEncryption(userPassword, ownerPassword, permissions, encryptionAlgorithm,
                EncryptionProperties.DEFAULT_MAC_PROPERTIES);
    }

    /**
     * Sets the encryption options for the document.
     *
     * @param userPassword        the user password. Can be null or of zero length, which is equal to
     *                            omitting the user password
     * @param ownerPassword       the owner password. If it's null or empty, iText will generate
     *                            a random string to be used as the owner password
     * @param permissions         the user permissions
     *                            The open permissions for the document can be
     *                            {@link EncryptionConstants#ALLOW_PRINTING},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_CONTENTS},
     *                            {@link EncryptionConstants#ALLOW_COPY},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_ANNOTATIONS},
     *                            {@link EncryptionConstants#ALLOW_FILL_IN},
     *                            {@link EncryptionConstants#ALLOW_SCREENREADERS},
     *                            {@link EncryptionConstants#ALLOW_ASSEMBLY} and
     *                            {@link EncryptionConstants#ALLOW_DEGRADED_PRINTING}.
     *                            The permissions can be combined by ORing them
     * @param encryptionAlgorithm the type of encryption. It can be one of
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40},
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128},
     *                            {@link EncryptionConstants#ENCRYPTION_AES_128}
     *                            or {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                            Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be ORed
     *                            to output the metadata in cleartext.
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                            Please be aware that the passed encryption types may override permissions:
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                            {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     * @param macProperties {@link MacProperties} class to configure MAC integrity protection properties.
     *                                          Pass {@code null} if you want to disable MAC protection for any reason
     *
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setStandardEncryption(byte[] userPassword, byte[] ownerPassword, int permissions,
            int encryptionAlgorithm, MacProperties macProperties) {
        encryptionProperties.setStandardEncryption(
                userPassword, ownerPassword, permissions, encryptionAlgorithm, macProperties);
        return this;
    }

    /**
     * Sets the certificate encryption options for the document. An array of one or more public certificates
     * must be provided together with an array of the same size for the permissions for each certificate.
     *
     * @param certs               the public certificates to be used for the encryption
     * @param permissions         the user permissions for each of the certificates
     *                            The open permissions for the document can be
     *                            {@link EncryptionConstants#ALLOW_PRINTING},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_CONTENTS},
     *                            {@link EncryptionConstants#ALLOW_COPY},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_ANNOTATIONS},
     *                            {@link EncryptionConstants#ALLOW_FILL_IN},
     *                            {@link EncryptionConstants#ALLOW_SCREENREADERS},
     *                            {@link EncryptionConstants#ALLOW_ASSEMBLY} and
     *                            {@link EncryptionConstants#ALLOW_DEGRADED_PRINTING}.
     *                            The permissions can be combined by ORing them
     * @param encryptionAlgorithm the type of encryption. It can be one of
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40},
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128},
     *                            {@link EncryptionConstants#ENCRYPTION_AES_128}
     *                            or {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                            Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be ORed
     *                            to output the metadata in cleartext.
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                            Please be aware that the passed encryption types may override permissions:
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                            {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setPublicKeyEncryption(Certificate[] certs, int[] permissions, int encryptionAlgorithm) {
        return setPublicKeyEncryption(certs, permissions, encryptionAlgorithm,
                EncryptionProperties.DEFAULT_MAC_PROPERTIES);
    }

    /**
     * Sets the certificate encryption options for the document. An array of one or more public certificates
     * must be provided together with an array of the same size for the permissions for each certificate.
     *
     * @param certs               the public certificates to be used for the encryption
     * @param permissions         the user permissions for each of the certificates
     *                            The open permissions for the document can be
     *                            {@link EncryptionConstants#ALLOW_PRINTING},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_CONTENTS},
     *                            {@link EncryptionConstants#ALLOW_COPY},
     *                            {@link EncryptionConstants#ALLOW_MODIFY_ANNOTATIONS},
     *                            {@link EncryptionConstants#ALLOW_FILL_IN},
     *                            {@link EncryptionConstants#ALLOW_SCREENREADERS},
     *                            {@link EncryptionConstants#ALLOW_ASSEMBLY} and
     *                            {@link EncryptionConstants#ALLOW_DEGRADED_PRINTING}.
     *                            The permissions can be combined by ORing them
     * @param encryptionAlgorithm the type of encryption. It can be one of
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40},
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128},
     *                            {@link EncryptionConstants#ENCRYPTION_AES_128}
     *                            or {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                            Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be ORed
     *                            to output the metadata in cleartext.
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                            Please be aware that the passed encryption types may override permissions:
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                            {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     * @param macProperties       {@link MacProperties} class to configure MAC integrity protection properties.
     *                                           Pass {@code null} if you want to disable MAC protection for any reason
     *
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setPublicKeyEncryption(Certificate[] certs, int[] permissions, int encryptionAlgorithm,
            MacProperties macProperties) {
        BouncyCastleFactoryCreator.getFactory().isEncryptionFeatureSupported(encryptionAlgorithm, true);
        encryptionProperties.setPublicKeyEncryption(certs, permissions, encryptionAlgorithm, macProperties);
        return this;
    }

    /**
     * The /ID entry of a document contains an array with two entries.
     * The first one (initial id) represents the initial document id.
     * It's a permanent identifier based on the contents of the file at the time it was originally created
     * and does not change when the file is incrementally updated.
     * To help ensure the uniqueness of file identifiers,
     * it is recommended to be computed by means of a message digest algorithm such as MD5.
     *
     * iText will by default keep the existing initial id.
     * But if you'd like you can set this id yourself using this setter.
     *
     * @param initialDocumentId the new initial document id
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setInitialDocumentId(PdfString initialDocumentId) {
        this.initialDocumentId = initialDocumentId;
        return this;
    }

    /**
     * The /ID entry of a document contains an array with two entries.
     * The second one (modified id) should be the same entry, unless the document has been modified. iText will by default generate
     * a modified id. But if you'd like you can set this id yourself using this setter.
     *
     * @param modifiedDocumentId the new modified document id
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties setModifiedDocumentId(PdfString modifiedDocumentId) {
        this.modifiedDocumentId = modifiedDocumentId;
        return this;
    }

    /**
     * This method marks the document as PDF/UA and sets related flags is XMPMetaData.
     * This method calls {@link #addXmpMetadata()} implicitly.
     * NOTE: iText does not validate PDF/UA, which means we don't check if created PDF meets all PDF/UA requirements.
     * Don't use this method if you are not familiar with PDF/UA specification in order to avoid creation of non-conformant PDF/UA file.
     *
     * @return this {@link WriterProperties} instance
     */
    public WriterProperties addUAXmpMetadata() {
        this.addUAXmpMetadata = true;
        return addXmpMetadata();
    }

    boolean isStandardEncryptionUsed() {
        return encryptionProperties.isStandardEncryptionUsed();
    }

    boolean isPublicKeyEncryptionUsed() {
        return encryptionProperties.isPublicKeyEncryptionUsed();
    }
}

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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.mac.MacProperties;
import com.itextpdf.kernel.mac.MacProperties.MacDigestAlgorithm;

import java.security.SecureRandom;
import java.security.cert.Certificate;

/**
 * Allows configuration of output PDF encryption.
 */
public class EncryptionProperties {


    protected int encryptionAlgorithm;

    // StandardEncryption properties
    protected byte[] userPassword;
    protected byte[] ownerPassword;
    protected int standardEncryptPermissions;

    // PublicKeyEncryption properties
    protected Certificate[] publicCertificates;
    protected int[] publicKeyEncryptPermissions;

    /**
     * {@link MacProperties} class to configure MAC integrity protection properties.
     */
    protected MacProperties macProperties;

    static final MacProperties DEFAULT_MAC_PROPERTIES = new MacProperties(MacDigestAlgorithm.SHA3_512);

    /**
     * Sets the encryption options for the document.
     *
     * @param userPassword        the user password. Can be null or of zero length, which is equal to
     *                            omitting the user password
     * @param ownerPassword       the owner password. If it's null or empty, iText will generate
     *                            a random string to be used as the owner password
     * @param permissions         the user permissions. The open permissions for the document can be
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
     *                            {@link EncryptionConstants#ENCRYPTION_AES_128} or
     *                            {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                            Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be OEed
     *                            to output the metadata in cleartext.
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                            Please be aware that the passed encryption types may override permissions:
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                            {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *
     * @return this {@link EncryptionProperties}
     */
    public EncryptionProperties setStandardEncryption(byte[] userPassword, byte[] ownerPassword, int permissions,
            int encryptionAlgorithm) {
        return setStandardEncryption(userPassword, ownerPassword, permissions, encryptionAlgorithm,
                DEFAULT_MAC_PROPERTIES);
    }

    /**
     * Sets the encryption options for the document.
     *
     * @param userPassword        the user password. Can be null or of zero length, which is equal to
     *                            omitting the user password
     * @param ownerPassword       the owner password. If it's null or empty, iText will generate
     *                            a random string to be used as the owner password
     * @param permissions         the user permissions. The open permissions for the document can be
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
     *                            {@link EncryptionConstants#ENCRYPTION_AES_128} or
     *                            {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                            Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} can be OEed
     *                            to output the metadata in cleartext.
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                            Please be aware that the passed encryption types may override permissions:
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                            {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     * @param macProperties {@link MacProperties} class to configure MAC integrity protection properties.
     *                                           Pass {@code null} if you want to disable MAC protection for any reason
     *
     * @return this {@link EncryptionProperties}
     */
    public EncryptionProperties setStandardEncryption(byte[] userPassword, byte[] ownerPassword, int permissions,
            int encryptionAlgorithm, MacProperties macProperties) {
        clearEncryption();
        this.userPassword = userPassword;
        if (ownerPassword != null) {
            this.ownerPassword = ownerPassword;
        } else {
            this.ownerPassword = new byte[16];
            randomBytes(this.ownerPassword);
        }
        this.standardEncryptPermissions = permissions;
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.macProperties = macProperties;

        return this;
    }

    /**
     * Sets the certificate encryption options for the document.
     * <p>
     * An array of one or more public certificates must be provided together with an array of the same size
     * for the permissions for each certificate.
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
     *                            {@link EncryptionConstants#ENCRYPTION_AES_128} or
     *                            {@link EncryptionConstants#ENCRYPTION_AES_256}.
     *                            Optionally {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA}
     *                            can be ORed to output the metadata in cleartext.
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} can be ORed as well.
     *                            Please be aware that the passed encryption types may override permissions:
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_40} implicitly sets
     *                            {@link EncryptionConstants#DO_NOT_ENCRYPT_METADATA} and
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     *                            {@link EncryptionConstants#STANDARD_ENCRYPTION_128} implicitly sets
     *                            {@link EncryptionConstants#EMBEDDED_FILES_ONLY} as false;
     * @param macProperties {@link MacProperties} class to configure MAC integrity protection properties.
     *                                           Pass {@code null} if you want to disable MAC protection for any reason
     *
     * @return this {@link EncryptionProperties}
     */
    public EncryptionProperties setPublicKeyEncryption(Certificate[] certs, int[] permissions,
            int encryptionAlgorithm, MacProperties macProperties) {
        clearEncryption();
        this.publicCertificates = certs;
        this.publicKeyEncryptPermissions = permissions;
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.macProperties = macProperties;

        return this;
    }

    boolean isStandardEncryptionUsed() {
        return ownerPassword != null;
    }

    boolean isPublicKeyEncryptionUsed() {
        return publicCertificates != null;
    }

    private void clearEncryption() {
        this.publicCertificates = null;
        this.publicKeyEncryptPermissions = null;
        this.userPassword = null;
        this.ownerPassword = null;
        this.macProperties = null;
    }

    private static void randomBytes(byte[] bytes) {
        new SecureRandom().nextBytes(bytes);
    }
}

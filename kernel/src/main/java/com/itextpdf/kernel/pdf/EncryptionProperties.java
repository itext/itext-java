package com.itextpdf.kernel.pdf;

import java.io.Serializable;
import java.security.cert.Certificate;

public class EncryptionProperties implements Serializable {

    private static final long serialVersionUID = 3926570647944137843L;

    protected int encryptionAlgorithm;
    /**
     * StandardEncryption properties
     */
    protected byte[] userPassword;
    protected byte[] ownerPassword;
    protected int standardEncryptPermissions;
    /** PublicKeyEncryption properties */
    protected Certificate[] publicCertificates;
    protected int[] publicKeyEncryptPermissions;

    /**
     * Sets the encryption options for the document. The userPassword and the
     * ownerPassword can be null or have zero length. In this case the ownerPassword
     * is replaced by a random string. The open permissions for the document can be
     * ALLOW_PRINTING, ALLOW_MODIFY_CONTENTS, ALLOW_COPY, ALLOW_MODIFY_ANNOTATIONS,
     * ALLOW_FILL_IN, ALLOW_SCREENREADERS, ALLOW_ASSEMBLY and ALLOW_DEGRADED_PRINTING.
     * The permissions can be combined by ORing them.
     *
     * See {@link EncryptionConstants}.
     *
     * @param userPassword   the user password. Can be null or empty
     * @param ownerPassword  the owner password. Can be null or empty
     * @param permissions    the user permissions
     * @param encryptionAlgorithm the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128,
     *                       ENCRYPTION_AES128 or ENCRYPTION_AES256
     *                       Optionally DO_NOT_ENCRYPT_METADATA can be ored to output the metadata in cleartext
     */
    public EncryptionProperties setStandardEncryption(byte userPassword[], byte ownerPassword[], int permissions, int encryptionAlgorithm) {
        clearEncryption();
        this.userPassword = userPassword;
        this.ownerPassword = ownerPassword;
        this.standardEncryptPermissions = permissions;
        this.encryptionAlgorithm = encryptionAlgorithm;

        return this;
    }

    /**
     * Sets the certificate encryption options for the document. An array of one or more public certificates
     * must be provided together with an array of the same size for the permissions for each certificate.
     * The open permissions for the document can be
     * AllowPrinting, AllowModifyContents, AllowCopy, AllowModifyAnnotations,
     * AllowFillIn, AllowScreenReaders, AllowAssembly and AllowDegradedPrinting.
     * The permissions can be combined by ORing them.
     * Optionally DO_NOT_ENCRYPT_METADATA can be ORed to output the metadata in cleartext
     *
     * See {@link EncryptionConstants}.
     *
     * @param certs          the public certificates to be used for the encryption
     * @param permissions    the user permissions for each of the certificates
     * @param encryptionAlgorithm the type of encryption. It can be one of STANDARD_ENCRYPTION_40, STANDARD_ENCRYPTION_128,
     *                       ENCRYPTION_AES128 or ENCRYPTION_AES256.
     */
    public EncryptionProperties setPublicKeyEncryption(Certificate[] certs, int[] permissions, int encryptionAlgorithm) {
        clearEncryption();
        this.publicCertificates = certs;
        this.publicKeyEncryptPermissions = permissions;
        this.encryptionAlgorithm = encryptionAlgorithm;

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
    }
}

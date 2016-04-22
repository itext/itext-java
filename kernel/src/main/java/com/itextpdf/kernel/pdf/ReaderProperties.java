package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.security.ExternalDecryptionProcess;
import java.io.Serializable;
import java.security.Key;
import java.security.cert.Certificate;

public class ReaderProperties implements Serializable {

    private static final long serialVersionUID = 5569118801793215916L;

    protected byte[] password; //added by ujihara for decryption

    protected Key certificateKey; //added by Aiken Sam for certificate decryption
    protected Certificate certificate; //added by Aiken Sam for certificate decryption
    protected String certificateKeyProvider; //added by Aiken Sam for certificate decryption
    protected ExternalDecryptionProcess externalDecryptionProcess;

    /**
     * Defines the password which will be used if the document is encrypted with standard encryption.
     * This could be either user or owner password.
     * @param password the password to use in order to open the document.
     */
    public ReaderProperties setPassword(byte[] password) {
        clearEncryptionParams();
        this.password = password;
        return this;
    }

    /**
     * Defines the certificate which will be used if the document is encrypted with public key encryption.
     */
    public ReaderProperties setPublicKeySecurityParams(Certificate certificate, Key certificateKey,
                                                       String certificateKeyProvider, ExternalDecryptionProcess externalDecryptionProcess) {
        clearEncryptionParams();
        this.certificate = certificate;
        this.certificateKey = certificateKey;
        this.certificateKeyProvider = certificateKeyProvider;
        this.externalDecryptionProcess = externalDecryptionProcess;

        return this;
    }

    /**
     * Defines the certificate which will be used if the document is encrypted with public key encryption.
     */
    public ReaderProperties setPublicKeySecurityParams(Certificate certificate, ExternalDecryptionProcess externalDecryptionProcess) {
        clearEncryptionParams();
        this.certificate = certificate;
        this.externalDecryptionProcess = externalDecryptionProcess;
        return this;
    }

    private void clearEncryptionParams() {
        this.password = null;
        this.certificate = null;
        this.certificateKey = null;
        this.certificateKeyProvider = null;
        this.externalDecryptionProcess = null;
    }
}

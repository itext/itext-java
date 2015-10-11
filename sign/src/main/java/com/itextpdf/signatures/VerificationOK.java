package com.itextpdf.signatures;

import java.security.cert.X509Certificate;

/**
 * Class that informs you that the verification of a Certificate
 * succeeded using a specific CertificateVerifier and for a specific
 * reason.
 */
public class VerificationOK {

    /** The certificate that was verified successfully. */
    protected X509Certificate certificate;
    /** The CertificateVerifier that was used for verifying. */
    protected Class<? extends CertificateVerifier> verifierClass;
    /** The reason why the certificate verified successfully. */
    protected String message;

    /**
     * Creates a VerificationOK object
     * @param certificate	the certificate that was successfully verified
     * @param verifierClass	the class that was used for verification
     * @param message		the reason why the certificate could be verified
     */
    public VerificationOK(X509Certificate certificate,
                          Class<? extends CertificateVerifier> verifierClass, String message) {
        this.certificate = certificate;
        this.verifierClass = verifierClass;
        this.message = message;
    }

    /**
     * A single String explaining which certificate was verified, how and why.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (certificate != null) {
            sb.append(certificate.getSubjectDN().getName());
            sb.append(" verified with ");
        }
        sb.append(verifierClass.getName());
        sb.append(": ");
        sb.append(message);
        return sb.toString();
    }
}

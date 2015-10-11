package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * An exception that is thrown when something is wrong with a certificate.
 */
public class VerificationException extends GeneralSecurityException {

    /** A Serial Version UID */
    private static final long serialVersionUID = 2978604513926438256L;

    /**
     * Creates a VerificationException
     */
    public VerificationException(Certificate cert, String message) {
        super(String.format("Certificate %s failed: %s", cert == null ? "Unknown" : ((X509Certificate)cert).getSubjectDN().getName(), message));
    }
}

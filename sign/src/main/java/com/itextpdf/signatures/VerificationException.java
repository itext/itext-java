package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

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
        super(MessageFormat.format("Certificate {0} failed: {1}", cert == null ? "Unknown" : ((X509Certificate) cert).getSubjectDN().getName(), message));
    }
}

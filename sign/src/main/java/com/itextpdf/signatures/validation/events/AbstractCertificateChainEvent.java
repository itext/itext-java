package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * A parent for all events issued during certificate chain validation
 */
public abstract class AbstractCertificateChainEvent implements IValidationEvent {
    private final X509Certificate certificate;

    /**
     * Create a new instance.
     *
     * @param certificate the certificate that is being validated
     */
    protected AbstractCertificateChainEvent(X509Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * Returns the certificate for which the event was fired.
     *
     * @return the certificate for which the event was fired
     */
    public X509Certificate getCertificate() {
        return certificate;
    }
}

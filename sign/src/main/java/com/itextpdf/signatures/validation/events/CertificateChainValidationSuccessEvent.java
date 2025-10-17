package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
  * This event is triggered after certificate chain validation success for the current signature.
  */
public class CertificateChainValidationSuccessEvent implements IValidationEvent {
    private final X509Certificate certificate;

    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate that was tested
     */
    public CertificateChainValidationSuccessEvent(X509Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * returns the validated certificate.
     *
     * @return the validated certificate
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.CERTIFICATE_CHAIN_SUCCESS;
    }
}

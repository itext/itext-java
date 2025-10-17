package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event is triggered when a certificates chain validation fails.
 */
public class CertificateChainValidationFailureEvent implements IValidationEvent {
    private final X509Certificate certificate;
    private final boolean isInconclusive;
    private final String reason;

    /**
     * Creates a new event instance.
     *
     * @param certificate    the validated certificate
     * @param isInconclusive whether the validation result was conclusive
     * @param reason         the reason the validation failed
     */
    public CertificateChainValidationFailureEvent(X509Certificate certificate, boolean isInconclusive, String reason) {
        this.certificate = certificate;
        this.isInconclusive = isInconclusive;
        this.reason = reason;
    }

    /**
     * Returns the validated certificate.
     *
     * @return the validated certificate
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    /**
     * Returns whether the validation result was conclusive.
     *
     * @return whether the validation result was conclusive
     */
    public boolean isInconclusive() {
        return isInconclusive;
    }

    /**
     * Returns the reason the validation failed.
     *
     * @return the reason the validation failed
     */
    public String getReason() {
        return reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.CERTIFICATE_CHAIN_FAILURE;
    }
}

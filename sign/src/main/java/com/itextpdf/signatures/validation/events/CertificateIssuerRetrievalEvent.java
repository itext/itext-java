package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event is triggered when a certificate issues was retrieved from the internet.
 */
public class CertificateIssuerRetrievalEvent extends AbstractCertificateChainEvent {
    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate for which the issuer was retrieved externally
     */
    public CertificateIssuerRetrievalEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.CERTIFICATE_ISSUER_EXTERNAL_RETRIEVAL;
    }
}

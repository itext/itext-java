package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event is triggered when a certificate issuer is retrieved from a document resource outside
 * of the last DSS.
 */
public class CertificateIssuerRetrievedOutsideDSSEvent extends AbstractCertificateChainEvent {

    /**
     * Creates a new event instance,
     *
     * @param certificate the certificate for which the issuer was retrieved externally
     */
    public CertificateIssuerRetrievedOutsideDSSEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.CERTIFICATE_ISSUER_OTHER_INTERNAL_SOURCE_USED;
    }
}

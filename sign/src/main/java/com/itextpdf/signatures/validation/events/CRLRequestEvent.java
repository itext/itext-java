package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event is triggered when a CRL request is executed instead of using a local resource.
 */
public class CRLRequestEvent  extends AbstractCertificateChainEvent {

    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate for which the CRL request was performed
     */
    public CRLRequestEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.CRL_REQUEST;
    }
}

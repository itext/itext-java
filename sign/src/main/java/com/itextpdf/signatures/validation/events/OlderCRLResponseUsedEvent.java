package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event triggers for every CRL response from the document that was not in the most recent DSS.
 */
public class OlderCRLResponseUsedEvent extends AbstractCertificateChainEvent{

    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate the request CRL response is for
     */
    public OlderCRLResponseUsedEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.CRL_OTHER_INTERNAL_SOURCE_USED;
    }
}

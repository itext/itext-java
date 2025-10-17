package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event is triggered when an OCSP request is made instead of using a local resource.
 */
public class OCSPResponseRetrievalEvent extends AbstractCertificateChainEvent {

    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate for which the OCSP request was performed
     */
    public OCSPResponseRetrievalEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.OCSP_REQUEST;
    }
}

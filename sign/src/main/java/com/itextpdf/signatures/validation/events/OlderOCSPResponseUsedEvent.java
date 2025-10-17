package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event is triggered when an OCSP response is used from another
 * document source than the latest DSS.
 */
public class OlderOCSPResponseUsedEvent extends AbstractCertificateChainEvent {

    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate for which the OCSP response was used
     */
    public OlderOCSPResponseUsedEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.OCSP_OTHER_INTERNAL_SOURCE_USED;
    }
}

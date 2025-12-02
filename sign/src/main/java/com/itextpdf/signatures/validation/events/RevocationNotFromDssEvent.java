package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event is triggered when revocation data is retrieved from anywhere except for latest DSS entry.
 */
public class RevocationNotFromDssEvent extends AbstractCertificateChainEvent {
    /**
     * Creates a new event instance,
     *
     * @param certificate the certificate for which there is not revocation data in the DSS
     */
    public RevocationNotFromDssEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.REVOCATION_NOT_FROM_DSS;
    }
}

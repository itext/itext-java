package com.itextpdf.signatures.validation.events;

import java.security.cert.X509Certificate;

/**
 * This event triggered when revocation data from a timestamped DSS is not enough to perform signature validation.
 */
public class DssNotTimestampedEvent extends AbstractCertificateChainEvent {
    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate for which there is no timestamped DSS
     */
    public DssNotTimestampedEvent(X509Certificate certificate) {
        super(certificate);
    }

    /**
     * {@inheritDoc}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.DSS_NOT_TIMESTAMPED;
    }
}

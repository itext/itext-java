/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

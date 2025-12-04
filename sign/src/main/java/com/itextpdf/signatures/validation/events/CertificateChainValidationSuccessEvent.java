/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
  * This event is triggered after certificate chain validation success for the current signature.
  */
public class CertificateChainValidationSuccessEvent implements IValidationEvent {
    private final X509Certificate certificate;

    /**
     * Creates a new event instance.
     *
     * @param certificate the certificate that was tested
     */
    public CertificateChainValidationSuccessEvent(X509Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * returns the validated certificate.
     *
     * @return the validated certificate
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.CERTIFICATE_CHAIN_SUCCESS;
    }
}

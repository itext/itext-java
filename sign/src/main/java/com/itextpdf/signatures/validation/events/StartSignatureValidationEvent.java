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

import com.itextpdf.signatures.PdfSignature;

import java.util.Date;

/**
 * This event is triggered at the start of a signature validation,
 * after successfully parsing the signature.
 */
public class StartSignatureValidationEvent implements IValidationEvent {
    private final PdfSignature sig;
    private final String signatureName;
    private final Date signingDate;

    /**
     * Creates a new event instance.
     *
     * @param sig the PdfSignature containing the signature
     * @param signatureName signature name
     * @param signingDate the signing date
     */
    public StartSignatureValidationEvent(PdfSignature sig, String signatureName, Date signingDate) {
        this.sig = sig;
        this.signatureName = signatureName;
        this.signingDate = signingDate;
    }

    /**
     * Returns the PdfSignature containing the signature.
     *
     * @return the PdfSignature containing the signature
     */
    public PdfSignature getPdfSignature() {
        return sig;
    }

    /**
     * Returns the signature name.
     * 
     * @return the signature name
     */
    public String getSignatureName() {
        return signatureName;
    }

    /**
     * Returns the claimed signing date.
     *
     * @return the claimed signing date
     */
    public Date getSigningDate() {
        return signingDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.SIGNATURE_VALIDATION_STARTED;
    }
}

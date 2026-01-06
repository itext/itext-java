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

/**
 * This event is triggered when a timestamp signature is encountered.
 */
public class ProofOfExistenceFoundEvent implements IValidationEvent {
    private final byte[] timeStampSignature;
    private final PdfSignature sig;
    private final boolean document;

    /**
     * Creates a new event instance for a document timestamp.
     *
     * @param sig the PdfSignature containing the timestamp signature,
     *            only applicable for document signatures
     * @param signatureName signature name, only applicable for document signatures
     */
    public ProofOfExistenceFoundEvent(PdfSignature sig, String signatureName) {
        this.sig = sig;
        this.timeStampSignature = sig.getContents().getValueBytes();
        this.document = true;
    }

    /**
     * Creates a new event instance for a signature timestamp.
     *
     * @param timeStampSignature timestamp container as a byte[]
     */
    public ProofOfExistenceFoundEvent(byte[] timeStampSignature) {
        this.timeStampSignature = timeStampSignature;
        this.sig = null;
        this.document = false;
    }

    /**
     * Returns the encoded timestamp signature.
     *
     * @return the encoded timestamp signature
     */
    public byte[] getTimeStampSignature() {
        return timeStampSignature;
    }

    /**
     * Returns whether this is a document timestamp.
     *
     * @return whether this is a document timestamp
     */
    public boolean isDocumentTimestamp() {
        return document;
    }

    /**
     * Returns the PdfSignature containing the timestamp signature.
     *
     * @return the PdfSignature containing the timestamp signature
     */
    public PdfSignature getPdfSignature() {
        return sig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.PROOF_OF_EXISTENCE_FOUND;
    }
}

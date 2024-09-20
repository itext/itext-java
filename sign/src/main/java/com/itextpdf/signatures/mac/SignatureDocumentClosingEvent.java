/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.mac;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.pdf.PdfIndirectReference;

/**
 * Represents an event firing before embedding the signature into the document.
 */
public class SignatureDocumentClosingEvent extends Event {
    public static final String START_SIGNATURE_PRE_CLOSE = "StartSignaturePreClose";

    private final PdfIndirectReference signatureReference;

    /**
     * Creates an event firing before embedding the signature into the document.
     * It contains the reference to the signature object.
     *
     * @param signatureReference {@link PdfIndirectReference} to the signature object
     */
    public SignatureDocumentClosingEvent(PdfIndirectReference signatureReference) {
        super(START_SIGNATURE_PRE_CLOSE);
        this.signatureReference = signatureReference;
    }

    /**
     * Gets {@link PdfIndirectReference} to the signature object.
     *
     * @return {@link PdfIndirectReference} to the signature object
     */
    public PdfIndirectReference getSignatureReference() {
        return signatureReference;
    }
}

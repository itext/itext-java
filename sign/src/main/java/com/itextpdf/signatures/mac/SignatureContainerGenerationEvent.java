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

import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;

import java.io.InputStream;

/**
 * Represents an event firing before creating signature container.
 */
public class SignatureContainerGenerationEvent extends AbstractPdfDocumentEvent {
    public static final String START_SIGNATURE_CONTAINER_GENERATION = "StartSignatureContainerGeneration";

    private final IASN1EncodableVector unsignedAttributes;
    private final byte[] signature;
    private final InputStream documentInputStream;

    /**
     * Creates an event firing before creating the signature container.
     *
     * @param unsignedAttributes {@link IASN1EncodableVector} unsigned signature attributes
     * @param signature {@code byte[]} signature value
     * @param documentInputStream {@link InputStream} containing document bytes considering byte range
     */
    public SignatureContainerGenerationEvent(IASN1EncodableVector unsignedAttributes, byte[] signature,
            InputStream documentInputStream) {
        super(START_SIGNATURE_CONTAINER_GENERATION);
        this.unsignedAttributes = unsignedAttributes;
        this.signature = signature;
        this.documentInputStream = documentInputStream;
    }

    /**
     * Gets {@link IASN1EncodableVector} unsigned signature attributes.
     *
     * @return {@link IASN1EncodableVector} unsigned signature attributes
     */
    public IASN1EncodableVector getUnsignedAttributes() {
        return unsignedAttributes;
    }

    /**
     * Gets {@code byte[]} signature value.
     *
     * @return {@code byte[]} signature value
     */
    public byte[] getSignature() {
        return signature;
    }

    /**
     * Gets {@link InputStream} containing document bytes considering byte range.
     *
     * @return {@link InputStream} containing document bytes considering byte range
     */
    public InputStream getDocumentInputStream() {
        return documentInputStream;
    }
}

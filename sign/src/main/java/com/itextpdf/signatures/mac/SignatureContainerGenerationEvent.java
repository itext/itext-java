package com.itextpdf.signatures.mac;

import com.itextpdf.commons.bouncycastle.asn1.IASN1EncodableVector;
import com.itextpdf.kernel.events.Event;

import java.io.InputStream;

/**
 * Represents an event firing before creating signature container.
 */
public class SignatureContainerGenerationEvent extends Event {
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

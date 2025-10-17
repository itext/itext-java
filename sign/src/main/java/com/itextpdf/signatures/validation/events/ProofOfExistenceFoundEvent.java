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

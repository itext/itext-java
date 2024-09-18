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

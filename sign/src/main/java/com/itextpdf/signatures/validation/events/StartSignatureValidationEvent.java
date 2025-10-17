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

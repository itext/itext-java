package com.itextpdf.signatures.validation.events;

/**
 * This event is triggered after a successful validation of the current signature.
 */
public class SignatureValidationSuccessEvent implements IValidationEvent {

    public SignatureValidationSuccessEvent() {
        // empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.SIGNATURE_VALIDATION_SUCCESS;
    }
}

package com.itextpdf.signatures.validation.events;

/**
 * This event is triggered after signature validation failed for the current signature.
 */
public class SignatureValidationFailureEvent implements IValidationEvent {
    private final boolean isInconclusive;
    private final String reason;

    /**
     * Create a new event instance.
     *
     * @param isInconclusive {@code true} when validation is neither valid nor invalid,
     *                       {@code false} when it is invalid
     * @param reason         the failure reason
     */
    public SignatureValidationFailureEvent(boolean isInconclusive, String reason) {
        this.isInconclusive = isInconclusive;
        this.reason = reason;
    }

    /**
     * Returns whether the result was inconclusive.
     *
     * @return whether the result was inconclusive
     */
    public boolean isInconclusive() {
        return isInconclusive;
    }

    /**
     * Returns the reason of the failure.
     *
     * @return  the reason of the failure
     */
    public String getReason() {
        return reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventType getEventType() {
        return EventType.SIGNATURE_VALIDATION_FAILURE;
    }
}

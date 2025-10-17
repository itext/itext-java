package com.itextpdf.signatures.validation.events;

import com.itextpdf.commons.actions.IEvent;

/**
 * This interface represents events registered during signature validation.
 */
public interface IValidationEvent extends IEvent {
    /**
     * Returns the event type of the event, this fields is used to avoid instanceof usage.
     *
     * @return the event type of the event
     */
    EventType getEventType();
}

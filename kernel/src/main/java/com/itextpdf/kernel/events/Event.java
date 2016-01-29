package com.itextpdf.kernel.events;

/**
 * Describes abstract event.
 */
public class Event {

    /**
     * A type of event.
     */
    protected String type;

    /**
     * Creates an event of the specified type.
     *
     * @param type type of event
     */
    public Event(String type) {
        this.type = type;
    }

    /**
     * Returns the type of this event.
     *
     * @return type of this event
     */
    public String getType() {
        return type;
    }
}

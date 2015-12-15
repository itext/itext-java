package com.itextpdf.core.events;

/**
 * Describes abstract event.
 */
public class Event {

    /**
     * A type of event.
     */
    protected String type;

    public Event(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

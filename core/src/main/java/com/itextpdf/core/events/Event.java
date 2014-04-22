package com.itextpdf.core.events;

public class Event {

    protected String type;

    public Event(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

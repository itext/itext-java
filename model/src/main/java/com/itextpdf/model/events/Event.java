package com.itextpdf.model.events;

public class Event {

    protected String type;

    public Event(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}

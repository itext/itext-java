package com.itextpdf.signatures.validation.events;

import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;

import java.util.List;
import java.util.ArrayList;


public class MockEventListener implements IEventHandler {

    private List<IEvent> events = new ArrayList<IEvent>();

    @Override
    public void onEvent(IEvent event) {
        events.add(event);
    }

    public List<IEvent> getEvents() {
        return events;
    }
}

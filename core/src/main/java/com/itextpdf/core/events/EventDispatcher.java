package com.itextpdf.core.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * IEventDispatcher implementation that forwards Events to registered {@link com.itextpdf.core.events.IEventHandler}
 * implementations.
 */
public class EventDispatcher implements IEventDispatcher {

    protected Map<String, ArrayList<IEventHandler>> eventHandlers = new HashMap<>();

    @Override
    public void addEventHandler(String type, IEventHandler handler) {
        removeEventHandler(type, handler);
        ArrayList<IEventHandler> handlers = eventHandlers.get(type);
        if (handlers == null) {
            handlers = new ArrayList<>();
            eventHandlers.put(type, handlers);
        }
        handlers.add(handler);
    }

    @Override
    public void dispatchEvent(Event event) {
        dispatchEvent(event, false);
    }

    @Override
    public void dispatchEvent(Event event, boolean delayed) {
        ArrayList<IEventHandler> handlers = eventHandlers.get(event.getType());
        if (handlers != null) {
            for (IEventHandler handler : handlers) {
                handler.handleEvent(event);
            }
        }
    }

    @Override
    public boolean hasEventHandler(String type) {
        return eventHandlers.containsKey(type);
    }

    @Override
    public void removeEventHandler(String type, IEventHandler handler) {
        ArrayList<IEventHandler> handlers = eventHandlers.get(type);
        if (handlers == null)
            return;
        handlers.remove(handler);
        if (handlers.size() == 0)
            eventHandlers.remove(type);
    }

    @Override
    public void removeAllHandlers() {
        eventHandlers.clear();
    }


}

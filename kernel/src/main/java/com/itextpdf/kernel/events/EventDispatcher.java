package com.itextpdf.kernel.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * IEventDispatcher implementation that forwards Events to registered {@link com.itextpdf.kernel.events.IEventHandler}
 * implementations.
 */
public class EventDispatcher implements IEventDispatcher, Serializable {

    private static final long serialVersionUID = -6701670578690210618L;


    protected Map<String, List<IEventHandler>> eventHandlers = new HashMap<>();

    @Override
    public void addEventHandler(String type, IEventHandler handler) {
        removeEventHandler(type, handler);
        List<IEventHandler> handlers = eventHandlers.get(type);
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
        List<IEventHandler> handlers = eventHandlers.get(event.getType());
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
        List<IEventHandler> handlers = eventHandlers.get(type);
        if (handlers == null)
            return;
        handlers.remove(handler);
        if (handlers.isEmpty())
            eventHandlers.remove(type);
    }

    @Override
    public void removeAllHandlers() {
        eventHandlers.clear();
    }


}

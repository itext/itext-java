package com.itextpdf.core.events;

public interface IEventDispatcher {

    public void addEventHandler(String type, IEventHandler handler);

    public void dispatchEvent(Event event);

    public void dispatchEvent(Event event, boolean delayed);

    public boolean hasEventHandler(String type);

    public void removeEventHandler(String type, IEventHandler handler);

    public void removeAllHandlers();

}

package com.itextpdf.model.events;

public interface IEventDispatcher {

    public void setEventHandler(String eventType, IEventHandler handler);

    public IEventHandler getEventHandler(String eventType);

    public void removeEventHandler(String eventType);

    public void dispatchEvent(Event event);

}

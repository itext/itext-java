package com.itextpdf.kernel.events;

/**
 * Event dispatcher interface.
 */
public interface IEventDispatcher {

    /**
     * Adds new event handler.
     *
     * @param type    a type of event to be handled.
     * @param handler event handler.
     */
    public void addEventHandler(String type, IEventHandler handler);

    /**
     * Dispatches an event.
     *
     * @param event
     */
    public void dispatchEvent(Event event);

    /**
     * Dispatches a delayed event.
     * Sometimes event cannot be handled immediately because event handler has not been set yet.
     * In this case event is placed into event ques of dispatcher and is waiting until handler is assigned.
     *
     * @param event
     * @param delayed
     */
    public void dispatchEvent(Event event, boolean delayed);

    /**
     * Checks if event dispatcher as an event handler assigned for a certain event type.
     *
     * @param type
     * @return
     */
    public boolean hasEventHandler(String type);

    /**
     * Removes event handler.
     *
     * @param type
     * @param handler
     */
    public void removeEventHandler(String type, IEventHandler handler);

    /**
     * Remove all event handlers.
     */
    public void removeAllHandlers();

}

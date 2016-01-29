package com.itextpdf.kernel.events;

/**
 * Interface for handling events. EventHandlers are added to the {@link EventDispatcher}.
 */
public interface IEventHandler {

    /**
     * Hook for handling events. Implementations can access the PdfDocument instance
     * associated to the specified Event or, if available, the PdfPage instance.
     *
     * @param event the Event that needs to be processed
     */
    public void handleEvent(Event event);

}

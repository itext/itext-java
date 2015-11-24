package com.itextpdf.core.parser;

import java.util.Set;

/**
 * A callback interface that receives notifications from the {@link PdfContentStreamProcessor}
 * as various events occur (see {@link EventType}).
 */
public interface EventListener {

    /**
     * Called when some event occurs during parsing a content stream.
     * @param data Combines the data required for processing corresponding event type.
     * @param type Event type.
     */
    void eventOccurred(EventData data, EventType type);

    /**
     * Provides the set of event types this listener supports.
     * @return Set of event types supported by this listener.
     */
    Set<EventType> getSupportedEvents();
}

package com.itextpdf.kernel.parser;

/**
 * This is an interface which helps to filter events.
 */
public interface EventFilter {

    /**
     * This method checks an event and decides whether it should be processed further (corresponds to {@code true}
     * return value, or filtered out (corresponds to {@code false} return value.
     * @param data event data
     * @param type event type
     * @return true to process event further, false to filter event out
     */
    boolean accept(EventData data, EventType type);

}

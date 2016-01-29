package com.itextpdf.kernel.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An event listener which filters events on the fly before passing them on to the delegate.
 */
public class FilteredEventListener implements EventListener {

    protected final List<EventListener> delegates;
    protected final List<EventFilter[]> filters;

    /**
     * Constructs a {@link FilteredEventListener} empty instance.
     * Use {@link #attachEventListener(EventListener, EventFilter...)} to add an event listener along with its filters.
     */
    public FilteredEventListener() {
        this.delegates = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    /**
     * Constructs a {@link FilteredEventListener} instance with one delegate.
     * Use {@link #attachEventListener(EventListener, EventFilter...)} to add more {@link EventListener} delegates
     * along with their filters.
     * @param delegate a delegate that fill be called when all the corresponding filters for an event pass
     * @param filterSet filters attached to the delegate that will be tested before passing an event on to the delegate
     */
    public FilteredEventListener(EventListener delegate, EventFilter... filterSet) {
        this();
        attachEventListener(delegate, filterSet);
    }

    /**
     * Attaches another {@link EventListener} delegate with its filters.
     * When all the filters attached to the delegate for an event accept the event, the event will be passed on to
     * the delegate.
     * You can attach multiple delegates to this {@link FilteredEventListener} instance. The content stream will
     * be parsed just once, so it is better for performance than creating multiple {@link FilteredEventListener}
     * instances and parsing the content stream multiple times. This is useful, for instance, when you want
     * to extract content from multiple regions of a page.
     * @param delegate a delegate that fill be called when all the corresponding filters for an event pass
     * @param filterSet filters attached to the delegate that will be tested before passing an event on to the delegate
     * @return delegate that has been passed to the method, used for convenient call chaining
     */
    public <T extends EventListener> T attachEventListener(T delegate, EventFilter... filterSet) {
        delegates.add(delegate);
        filters.add(filterSet);

        return delegate;
    }

    @Override
    public void eventOccurred(EventData data, EventType type) {
        for (int i = 0; i < delegates.size(); i++) {
            EventListener delegate = delegates.get(i);
            boolean filtersPassed = delegate.getSupportedEvents() == null || delegate.getSupportedEvents().contains(type);
            for (EventFilter filter : filters.get(i)) {
                if (!filter.accept(data, type)) {
                    filtersPassed = false;
                    break;
                }
            }
            if (filtersPassed) {
                delegate.eventOccurred(data, type);
            }
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return null;
    }
}

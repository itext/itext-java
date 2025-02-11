/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.filter.IEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An event listener which filters events on the fly before passing them on to the delegate.
 */
public class FilteredEventListener implements IEventListener {

    protected final List<IEventListener> delegates;
    protected final List<IEventFilter[]> filters;

    /**
     * Constructs a {@link FilteredEventListener} empty instance.
     * Use {@link #attachEventListener(IEventListener, IEventFilter...)} to add an event listener along with its filters.
     */
    public FilteredEventListener() {
        this.delegates = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    /**
     * Constructs a {@link FilteredEventListener} instance with one delegate.
     * Use {@link #attachEventListener(IEventListener, IEventFilter...)} to add more {@link IEventListener} delegates
     * along with their filters.
     * @param delegate a delegate that will be called when all the corresponding filters for an event pass
     * @param filterSet filters attached to the delegate that will be tested before passing an event on to the delegate
     */
    public FilteredEventListener(IEventListener delegate, IEventFilter... filterSet) {
        this();
        attachEventListener(delegate, filterSet);
    }

    /**
     * Attaches another {@link IEventListener} delegate with its filters.
     * When all the filters attached to the delegate for an event accept the event, the event will be passed on to
     * the delegate.
     * You can attach multiple delegates to this {@link FilteredEventListener} instance. The content stream will
     * be parsed just once, so it is better for performance than creating multiple {@link FilteredEventListener}
     * instances and parsing the content stream multiple times. This is useful, for instance, when you want
     * to extract content from multiple regions of a page.
     * @param <T> the type of the delegate
     * @param delegate a delegate that will be called when all the corresponding filters for an event pass
     * @param filterSet filters attached to the delegate that will be tested before passing an event on to the delegate
     * @return delegate that has been passed to the method, used for convenient call chaining
     */
    public <T extends IEventListener> T attachEventListener(T delegate, IEventFilter... filterSet) {
        delegates.add(delegate);
        filters.add(filterSet);

        return delegate;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        for (int i = 0; i < delegates.size(); i++) {
            IEventListener delegate = delegates.get(i);
            boolean filtersPassed = delegate.getSupportedEvents() == null || delegate.getSupportedEvents().contains(type);
            for (IEventFilter filter : filters.get(i)) {
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

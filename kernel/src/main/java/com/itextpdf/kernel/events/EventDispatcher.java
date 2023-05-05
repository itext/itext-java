/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.events;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * IEventDispatcher implementation that forwards Events to registered {@link com.itextpdf.kernel.events.IEventHandler}
 * implementations.
 */
public class EventDispatcher implements IEventDispatcher {


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
        if (handlers.size() == 0)
            eventHandlers.remove(type);
    }

    @Override
    public void removeAllHandlers() {
        eventHandlers.clear();
    }
}

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
package com.itextpdf.kernel.counter;

import com.itextpdf.kernel.counter.context.IContext;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager that works with {@link IEventCounterFactory}. Create {@link EventCounter} for each registered {@link IEventCounterFactory}
 * and send corresponding events when calling {@link #onEvent(IEvent, IMetaInfo, Class)} method.
 * <p>
 * You can implement your own {@link IEventCounterFactory} and register them with {@link EventCounterHandler#register(IEventCounterFactory)}
 * Or implement {@link EventCounter} and register it with {@link SimpleEventCounterFactory} like this:
 * <code>EventCounterHandler.getInstance().register(new SimpleEventCounterFactory(new SystemOutEventCounter());</code>
 * {@link SystemOutEventCounter} is just an example of a {@link EventCounter} implementation.
 * <p>
 * This functionality can be used to create metrics in a SaaS context.
 */
public class EventCounterHandler {

    /**
     * The singleton instance.
     */
    private static final EventCounterHandler instance = new EventCounterHandler();

    /**
     * All registered factories.
     */
    private Map<IEventCounterFactory, Boolean> factories = new ConcurrentHashMap<>();

    private EventCounterHandler() {
        register(new SimpleEventCounterFactory(new DefaultEventCounter()));
    }

    /**
     * @return the singleton instance of the factory.
     */
    public static EventCounterHandler getInstance() {
        return instance;
    }

    /**
     * Triggers all registered {@link IEventCounterFactory} to produce {@link EventCounter} instance
     * and count the event.
     *
     * @param event {@link IEvent} to be counted
     * @param metaInfo {@link IMetaInfo} object that can holds information about instance that throws the event
     * @param caller the class that throws the event
     */
    public void onEvent(IEvent event, IMetaInfo metaInfo, Class<?> caller) {
        IContext context = null;
        boolean contextInitialized = false;
        for (IEventCounterFactory factory : factories.keySet()) {
            EventCounter counter = factory.getCounter(caller);
            if (counter != null) {
                if (!contextInitialized) {
                    if (metaInfo != null) {
                        context = ContextManager.getInstance().getContext(metaInfo.getClass());
                    }
                    if (context == null) {
                        context = ContextManager.getInstance().getContext(caller);
                    }
                    if (context == null) {
                        context = ContextManager.getInstance().getContext(event.getClass());
                    }
                    contextInitialized = true;
                }
                if ((context != null && context.allow(event)) || (context == null && counter.fallback.allow(event))) {
                    counter.onEvent(event, metaInfo);
                }
            }
        }
    }

    /**
     * Register new {@link IEventCounterFactory}. Does nothing if same factory was already registered.
     *
     * @param factory {@link IEventCounterFactory} to be registered
     */
    public void register(IEventCounterFactory factory) {
        if (factory != null) {
            factories.put(factory, true);
        }
    }

    /**
     * Checks whether the specified {@link IEventCounterFactory} is registered.
     * @param factory {@link IEventCounterFactory} to be checked
     * @return {@code true} if the specified factory is registered
     */
    public boolean isRegistered(IEventCounterFactory factory) {
        if (factory != null) {
            return factories.containsKey(factory);
        }
        return false;
    }

    /**
     * Unregister specified {@link IEventCounterFactory}. Does nothing if this factory wasn't registered first.
     *
     * @param factory {@link IEventCounterFactory} to be unregistered
     * @return {@code true} if specified factory was registered first
     */
    public boolean unregister(IEventCounterFactory factory) {
        if (factory != null) {
            return factories.remove(factory) != null;
        }
        return false;
    }
}

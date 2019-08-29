/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
 * <code>EventCounterManager.getInstance().register(new SimpleEventCounterFactory(new SystemOutEventCounter());</code>
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

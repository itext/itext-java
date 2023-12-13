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
package com.itextpdf.kernel.log;

import com.itextpdf.kernel.counter.EventCounterHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manager that works with {@link ICounterFactory}. Create {@link ICounter} for each registered {@link ICounterFactory}
 * and send corresponding events on document read and write.
 * <p>
 * You can implement your own {@link ICounterFactory} and register them with {@link CounterManager#register(ICounterFactory)}
 * Or implement {@link ICounter} and register it with {@link SimpleCounterFactory} like this:
 * <code>CounterManager.getInstance().register(new SimpleCounterFactory(new SystemOutCounter());</code>
 * {@link SystemOutCounter} is just an example of a {@link ICounter} implementation.
 * <p>
 * This functionality can be used to create metrics in a SaaS context.
 * @deprecated will be removed in next major release, please use {@link EventCounterHandler} instead.
 */
@Deprecated
public class CounterManager {

    /**
     * The singleton instance.
     */
    private static CounterManager instance = new CounterManager();

    /**
     * All registered factories.
     */
    private Set<ICounterFactory> factories = new HashSet<>();

    private CounterManager() {
    }

    /**
     * Returns the singleton instance of the factory.
     * @return the {@link CounterManager} instance.
     */
    public static CounterManager getInstance() {
        return instance;
    }

    /**
     * Returns a list of registered counters for specific class.
     * @param cls the class for which registered counters are fetched.
     * @return list of registered {@link ICounter}.
     */
    public List<ICounter> getCounters(Class<?> cls) {
        ArrayList<ICounter> result = new ArrayList<>();
        for (ICounterFactory factory : factories) {
            ICounter counter = factory.getCounter(cls);
            if (counter != null) {
                result.add(counter);
            }
        }
        return result;
    }

    /**
     * Register new {@link ICounterFactory}. Does nothing if same factory was already registered.
     *
     * @param factory {@link ICounterFactory} to be registered
     */
    public void register(ICounterFactory factory) {
        if (factory != null) {
            factories.add(factory);
        }
    }

    /**
     * Unregister specified {@link ICounterFactory}. Does nothing if this factory wasn't registered first.
     *
     * @param factory {@link ICounterFactory} to be unregistered
     * @return {@code true} if specified factory was registered first
     */
    public boolean unregister(ICounterFactory factory) {
        if (factory != null) {
            return factories.remove(factory);
        }
        return false;
    }
}

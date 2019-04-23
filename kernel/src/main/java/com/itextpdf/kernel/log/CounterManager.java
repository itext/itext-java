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
     */
    public static CounterManager getInstance() {
        return instance;
    }

    /**
     * Returns a list of registered counters for specific class.
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

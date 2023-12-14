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
package com.itextpdf.kernel.counter.data;

import com.itextpdf.io.util.SystemUtil;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is intended for some heavy concurrent event operations
 * (like writing to database or file).
 *
 * On registration of new {@link IEvent} the instance of {@link EventData} is created with
 * the {@link IEventDataFactory} that can add some additional information like system info
 * or version.
 * This data instance is immediately cached with {@link IEventDataCache} that can for example
 * merge data with the same signature by summing there count.
 * If the previous processing operation is finished and the wait time is passed then the next event is retrieved from cache
 * (it may be for example based on some comparator like the biggest count, if {@link EventDataCacheComparatorBased}
 * is used, or just queue based if the {@link EventDataCacheQueueBased} is used or any other order determined by
 * custom cache) and the processing is started asynchronously
 *
 * This class can be considered thread-safe if the cache and factory instances aren't used anywhere else in the code.
 *
 * @param <T> data signature type
 * @param <V> data type
 */
public abstract class EventDataHandler<T, V extends EventData<T>> {
    private final Object createLock = new Object();
    private final Object processLock = new Object();

    private final IEventDataCache<T, V> cache;
    private final IEventDataFactory<T, V> factory;
    private final AtomicLong lastProcessedTime = new AtomicLong();

    private volatile WaitTime waitTime;

    public EventDataHandler(IEventDataCache<T, V> cache, IEventDataFactory<T, V> factory, long initialWaitTimeMillis, long maxWaitTimeMillis) {
        this.cache = cache;
        this.factory = factory;
        this.waitTime = new WaitTime(initialWaitTimeMillis, maxWaitTimeMillis);
    }

    public List<V> clear() {
        List<V> all;
        synchronized (cache) {
            all = cache.clear();
        }
        lastProcessedTime.set(0);
        resetWaitTime();
        return all != null ? all : Collections.<V>emptyList();
    }

    public void register(IEvent event, IMetaInfo metaInfo) {
        V data;
        synchronized (createLock) {
            data = factory.create(event, metaInfo);
        }
        if (data != null) {
            synchronized (cache) {
                cache.put(data);
            }
            tryProcessNextAsync();
        }
    }

    public void tryProcessNext() {
        long currentTime = SystemUtil.getRelativeTimeMillis();
        if (currentTime - lastProcessedTime.get() > waitTime.getTime()) {
            lastProcessedTime.set(SystemUtil.getRelativeTimeMillis());
            V data;
            synchronized (cache) {
                data = cache.retrieveNext();
            }
            if (data != null) {
                boolean successful;
                synchronized (processLock) {
                    successful = tryProcess(data);
                }
                if (successful) {
                    onSuccess(data);
                } else {
                    synchronized (cache) {
                        cache.put(data);
                    }
                    onFailure(data);
                }
            }
        }
    }

    public void tryProcessNextAsync() {
        tryProcessNextAsync(null);
    }

    public void tryProcessNextAsync(Boolean daemon) {
        long currentTime = SystemUtil.getRelativeTimeMillis();
        if (currentTime - lastProcessedTime.get() > waitTime.getTime()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    tryProcessNext();
                }
            };
            if (daemon != null) {
                thread.setDaemon((boolean) daemon);
            }
            thread.start();
        }
    }

    /**
     * Method that will try to immediately process all cashed data, ignoring the usual error fallback procedures.
     */
    public void tryProcessRest() {
        List<V> unprocessedEvents = clear();
        if (!unprocessedEvents.isEmpty()) {
            try {
                synchronized (processLock) {
                    for (V data : unprocessedEvents) {
                        process(data);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void resetWaitTime() {
        WaitTime local = waitTime;
        waitTime = new WaitTime(local.getInitial(), local.getMaximum());
    }

    public void increaseWaitTime() {
        WaitTime local = waitTime;
        waitTime = new WaitTime(local.getInitial(), local.getMaximum(), Math.min(local.getTime() * 2, local.getMaximum()));
    }

    public void setNoWaitTime() {
        WaitTime local = waitTime;
        waitTime = new WaitTime(local.getInitial(), local.getMaximum(), 0);
    }

    public WaitTime getWaitTime() {
        return waitTime;
    }

    protected void onSuccess(V data) {
        resetWaitTime();
    }

    protected void onFailure(V data) {
        increaseWaitTime();
    }

    /**
     * Is called when exception is thrown in process.
     *
     * @param exception caught exception
     * @return whether processing is treated as success
     */
    protected boolean onProcessException(Exception exception) {
        return false;
    }

    protected abstract boolean process(V data);

    private boolean tryProcess(V data) {
        try {
            return process(data);
        } catch (Exception any) {
            return onProcessException(any);
        }
    }
}

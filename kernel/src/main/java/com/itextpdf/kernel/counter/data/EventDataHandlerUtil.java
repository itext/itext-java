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

import com.itextpdf.io.LogMessageConstant;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * The util class with service methods for {@link EventDataHandler} and comparator class,
 * that can be used in {@link EventDataCacheComparatorBased}.
 */
public final class EventDataHandlerUtil {

    private static final ConcurrentHashMap<Object, Thread> shutdownHooks = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Object, Thread> scheduledTasks = new ConcurrentHashMap<>();

    private EventDataHandlerUtil() {
    }

    /**
     * Registers shutdown hook for {@link EventDataHandler} that will try to process all the events that are left.
     * It isn't guarantied that all events would be processed.
     *
     * @param dataHandler the {@link EventDataHandler} for which the hook will be registered
     * @param <T> the data signature type
     * @param <V> the data type
     */
    public static <T, V extends EventData<T>> void registerProcessAllShutdownHook(final EventDataHandler<T, V> dataHandler) {
        if (shutdownHooks.containsKey(dataHandler)) {
            // hook already registered
            return;
        }
        Thread shutdownHook = new Thread(dataHandler::tryProcessRest);
        shutdownHooks.put(dataHandler, shutdownHook);

        try {
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        } catch (SecurityException security) {
            LoggerFactory.getLogger(EventDataHandlerUtil.class)
                    .error(LogMessageConstant.UNABLE_TO_REGISTER_EVENT_DATA_HANDLER_SHUTDOWN_HOOK);
            shutdownHooks.remove(dataHandler);
        } catch (Exception ignored) {
            //The other exceptions are indicating that ether hook is already registered or hooks are running,
            //so there is nothing to do here
        }
    }

    /**
     * Unregister shutdown hook for {@link EventDataHandler} registered with
     * {@link EventDataHandlerUtil#registerProcessAllShutdownHook(EventDataHandler)}.
     *
     * @param dataHandler the {@link EventDataHandler} for which the hook will be unregistered
     * @param <T> the data signature type
     * @param <V> the data type
     */
    public static <T, V extends EventData<T>> void disableShutdownHooks(final EventDataHandler<T, V> dataHandler) {
        Thread toDisable = shutdownHooks.remove(dataHandler);
        if (toDisable != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(toDisable);
            } catch (SecurityException security) {
                LoggerFactory.getLogger(EventDataHandlerUtil.class)
                        .error(LogMessageConstant.UNABLE_TO_UNREGISTER_EVENT_DATA_HANDLER_SHUTDOWN_HOOK);
            } catch (Exception ignored) {
                //The other exceptions are indicating that hooks are running,
                //so there is nothing to do here
            }
        }
    }

    /**
     * Creates thread that will try to trigger event processing with time interval from
     * specified {@link EventDataHandler}.
     *
     * @param dataHandler the {@link EventDataHandler} for which the thread will be registered
     * @param <T> the data signature type
     * @param <V> the data type
     */
    public static <T, V extends EventData<T>> void registerTimedProcessing(final EventDataHandler<T, V> dataHandler) {
        if (scheduledTasks.containsKey(dataHandler)) {
            // task already registered
            return;
        }
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(dataHandler.getWaitTime().getTime());
                    dataHandler.tryProcessNextAsync(false);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception any) {
                    LoggerFactory.getLogger(EventDataHandlerUtil.class)
                            .error(LogMessageConstant.UNEXPECTED_EVENT_HANDLER_SERVICE_THREAD_EXCEPTION, any);
                    break;
                }
            }
        });
        scheduledTasks.put(dataHandler, thread);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Stop the timed processing thread registered with
     * {@link EventDataHandlerUtil#registerTimedProcessing(EventDataHandler)}.
     *
     * @param dataHandler the {@link EventDataHandler} for which the thread will be registered
     * @param <T> the data signature type
     * @param <V> the data type
     */
    public static <T, V extends EventData<T>> void disableTimedProcessing(final EventDataHandler<T, V> dataHandler) {
        Thread toDisable = scheduledTasks.remove(dataHandler);
        if (toDisable != null) {
            try {
                toDisable.interrupt();
            } catch (SecurityException security) {
                LoggerFactory.getLogger(EventDataHandlerUtil.class)
                        .error(LogMessageConstant.UNABLE_TO_INTERRUPT_THREAD);
            }
        }
    }

    /**
     * Comparator class that can be used in {@link EventDataCacheComparatorBased}.
     * If so, the cache will return {@link EventData} with bigger count first.
     *
     * @param <T> the data signature type
     * @param <V> the data type
     */
    public static class BiggerCountComparator<T, V extends EventData<T>> implements Comparator<V> {
        @Override
        public int compare(V o1, V o2) {
            return Long.compare(o2.getCount(), o1.getCount());
        }
    }
}

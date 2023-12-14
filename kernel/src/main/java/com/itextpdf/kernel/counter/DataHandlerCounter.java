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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.counter.context.IContext;
import com.itextpdf.kernel.counter.context.UnknownContext;
import com.itextpdf.kernel.counter.data.EventDataHandler;
import com.itextpdf.kernel.counter.data.EventDataHandlerUtil;
import com.itextpdf.kernel.counter.data.EventData;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;

import java.io.Closeable;

/**
 * Counter based on {@link EventDataHandler}.
 * Registers shutdown hook and thread for triggering event processing after wait time.
 *
 * @param <T> The data signature class
 * @param <V> The event data class
 */
public class DataHandlerCounter<T, V extends EventData<T>> extends EventCounter implements Closeable {

    private volatile boolean closed = false;

    private final EventDataHandler<T, V> dataHandler;

    /**
     * Create an instance with provided data handler and {@link UnknownContext#PERMISSIVE}
     * fallback context.
     *
     * @param dataHandler the {@link EventDataHandler} for events handling
     */
    public DataHandlerCounter(EventDataHandler<T, V> dataHandler) {
        this(dataHandler, UnknownContext.PERMISSIVE);
    }

    /**
     * Create an instance with provided data handler and fallback context.
     *
     * @param dataHandler the {@link EventDataHandler} for events handling
     * @param fallback the fallback {@link IContext context}
     */
    public DataHandlerCounter(EventDataHandler<T, V> dataHandler, IContext fallback) {
        super(fallback);
        this.dataHandler = dataHandler;
        EventDataHandlerUtil.<T, V>registerProcessAllShutdownHook(this.dataHandler);
        EventDataHandlerUtil.<T, V>registerTimedProcessing(this.dataHandler);
    }

    /**
     * Process the event.
     *
     * @param event {@link IEvent} to count
     * @param metaInfo the {@link IMetaInfo} that can hold information about event origin
     *
     * @throws IllegalStateException if the current instance has been disabled.
     * See {@link DataHandlerCounter#close()}
     */
    @Override
    protected void onEvent(IEvent event, IMetaInfo metaInfo) {
        if (this.closed) {
            throw new IllegalStateException(PdfException.DataHandlerCounterHasBeenDisabled);
        }
        this.dataHandler.register(event, metaInfo);
    }

    /**
     * Disable all registered hooks and process the left data. Note that after this method
     * invocation the {@link DataHandlerCounter#onEvent(IEvent, IMetaInfo)} method would throw
     * an exception.
     */
    @Override
    public void close() {
        this.closed = true;
        try {
            EventDataHandlerUtil.<T, V>disableShutdownHooks(this.dataHandler);
            EventDataHandlerUtil.<T, V>disableTimedProcessing(this.dataHandler);
        } finally {
            this.dataHandler.tryProcessRest();
        }
    }
}

/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

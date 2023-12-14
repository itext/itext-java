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
import com.itextpdf.kernel.counter.context.UnknownContext;
import com.itextpdf.kernel.counter.event.IEvent;
import com.itextpdf.kernel.counter.event.IMetaInfo;

/**
 * Class that can be extended if you want to count iText events, for example the number of documents
 * that are being processed by iText.
 * <p>
 * Implementers may use this method to record actual system usage for licensing purposes
 * (e.g. count the number of documents or the volume in bytes in the context of a SaaS license).
 */
public abstract class EventCounter {

    final IContext fallback;

    /**
     * Creates instance of this class that allows all events from unknown {@link IContext}.
     */
    public EventCounter() {
        this(UnknownContext.PERMISSIVE);
    }

    /**
     * Creates instance of this class with custom fallback {@link IContext}.
     * @param fallback the {@link IContext} that will be used in case the event context is unknown
     */
    public EventCounter(IContext fallback) {
        if (fallback == null) {
            throw new IllegalArgumentException("The fallback context in EventCounter constructor cannot be null");
        }
        this.fallback = fallback;
    }

    /**
     * The method that should be overridden for actual event processing
     *
     * @param event {@link IEvent} to count
     * @param metaInfo the {@link IMetaInfo} that can hold information about event origin
     */
    protected abstract void onEvent(IEvent event, IMetaInfo metaInfo);
}

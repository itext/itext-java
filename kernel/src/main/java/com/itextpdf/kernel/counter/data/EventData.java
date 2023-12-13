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

/**
 * The data that class that contains event signature (in simple cases it can be just event type)
 * and count.
 * Is used in {@link EventDataHandler} for adding some additional information to the event before processing
 * and merging same events by increasing count.
 *
 * @param <T> the signature type
 */
public class EventData<T> {

    private final T signature;
    private long count;

    public EventData(T signature) {
        this(signature, 1);
    }

    public EventData(T signature, long count) {
        this.signature = signature;
        this.count = count;
    }

    /**
     * The signature that identifies this data.
     *
     * @return data signature
     */
    public final T getSignature() {
        return signature;
    }

    /**
     * Number of data instances with the same signature that where merged.
     *
     * @return data count
     */
    public final long getCount() {
        return count;
    }

    protected void mergeWith(EventData<T> data) {
        this.count += data.getCount();
    }
}

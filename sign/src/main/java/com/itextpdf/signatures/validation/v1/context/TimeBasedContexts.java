/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.validation.v1.context;

import java.util.EnumSet;

/**
 * Container class, which contains set of single {@link TimeBasedContext} values.
 */
public final class TimeBasedContexts {
    private final EnumSet<TimeBasedContext> set;

    private TimeBasedContexts(EnumSet<TimeBasedContext> set) {
        this.set = set;
    }

    /**
     * Creates {@link TimeBasedContexts} container from several {@link TimeBasedContext} values.
     *
     * @param first an element that the set is to contain initially
     * @param rest the remaining elements the set is to contain
     *
     * @return {@link TimeBasedContexts} container, containing provided elements
     */
    public static TimeBasedContexts of(TimeBasedContext first,  TimeBasedContext ... rest) {
        return new TimeBasedContexts(EnumSet.<TimeBasedContext>of(first, rest));
    }

    /**
     * Creates {@link TimeBasedContexts} containing all {@link TimeBasedContext} values.
     *
     * @return {@link TimeBasedContexts} container containing all {@link TimeBasedContext} values
     */
    public static TimeBasedContexts all() {
        return new TimeBasedContexts(EnumSet.<TimeBasedContext>allOf(TimeBasedContext.class));
    }

    /**
     * Creates {@link TimeBasedContexts} containing all the elements of this type
     * that are not contained in the specified set.
     *
     * @param other another {@link TimeBasedContexts} from whose complement to initialize this container
     *
     * @return the complement of the specified {@link TimeBasedContexts}.
     */
    public static TimeBasedContexts complementOf(TimeBasedContexts other) {
        EnumSet<TimeBasedContext> result = EnumSet.<TimeBasedContext>complementOf(other.set);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("TimeBasedContexts.all has no valid complement.");
        }
        return new TimeBasedContexts(result);
    }

    /**
     * Gets encapsulated {@link EnumSet} containing {@link TimeBasedContext} elements.
     *
     * @return encapsulated {@link EnumSet} containing {@link TimeBasedContext} elements
     */
    public EnumSet<TimeBasedContext> getSet() {
        return set;
    }
}

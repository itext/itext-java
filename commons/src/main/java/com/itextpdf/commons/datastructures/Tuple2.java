/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.commons.datastructures;

/**
 * Simple tuple container that holds two elements.
 *
 * @param <T1> type of the first element
 * @param <T2> type of the second element
 */
public class Tuple2<T1, T2> {
    private final T1 first;
    private final T2 second;

    /**
     * Creates a new instance of {@link Tuple2} with given elements.
     *
     * @param first  the first element
     * @param second the second element
     */
    public Tuple2(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Get the first element.
     *
     * @return the first element
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Get the second element.
     *
     * @return the second element
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Tuple2{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}

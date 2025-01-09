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
package com.itextpdf.layout.font;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder of {@link Range}.
 */
public class RangeBuilder {

    private static final Range fullRangeSingleton = new Range.FullRange();

    private List<Range.SubRange> ranges = new ArrayList<>();

    /**
     * Default Range instance.
     *
     * @return Range that contains any integer
     */
    static Range getFullRange() {
        return fullRangeSingleton;
    }


    /**
     * Default constructor with empty range.
     */
    public RangeBuilder() {
    }

    /**
     * Constructor with a single range.
     *
     * @param low  low boundary of the range
     * @param high high boundary of the range
     */
    public RangeBuilder(int low, int high) {
        this.addRange(low, high);
    }

    /**
     * Constructor with a single number.
     *
     * @param n a single number
     */
    public RangeBuilder(int n) {
        this(n, n);
    }

    /**
     * Constructor with a single range.
     *
     * @param low  low boundary of the range
     * @param high high boundary of the range
     */
    public RangeBuilder(char low, char high) {
        this((int) low, (int) high);
    }

    /**
     * Constructor with a single char.
     *
     * @param ch a single char
     */
    public RangeBuilder(char ch) {
        this((int) ch);
    }

    /**
     * Add one more range.
     *
     * @param low  low boundary of the range
     * @param high high boundary of the range
     * @return this RangeBuilder
     */
    public RangeBuilder addRange(int low, int high) {
        if (high < low) {
            throw new IllegalArgumentException("'from' shall be less than 'to'");
        }
        ranges.add(new Range.SubRange(low, high));
        return this;
    }

    /**
     * Add one more range.
     *
     * @param low  low boundary of the range
     * @param high high boundary of the range
     * @return this RangeBuilder
     */
    public RangeBuilder addRange(char low, char high) {
        return addRange((int) low, (int) high);
    }

    /**
     * Add range with a single number.
     *
     * @param n a single number
     * @return this RangeBuilder
     */
    public RangeBuilder addRange(int n) {
        return addRange(n, n);
    }

    /**
     * Add range with a single char.
     *
     * @param ch a single char
     * @return this RangeBuilder
     */
    public RangeBuilder addRange(char ch) {
        return addRange((int) ch);
    }


    /**
     * Creates a {@link Range} instance based on added ranges.
     *
     * @return the {@link Range} instance based on added ranges
     */
    public Range create() {
        return new Range(ranges);
    }
}

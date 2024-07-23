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
package com.itextpdf.layout.properties.grid;

/**
 * Represents minmax function template value.
 */
public class MinMaxValue extends FunctionValue {
    private final BreadthValue min;
    private final BreadthValue max;

    /**
     * Create a minmax function with a given values.
     *
     * @param min min value of a track
     * @param max max value of a track
     */
    public MinMaxValue(BreadthValue min, BreadthValue max) {
        super(ValueType.MINMAX);
        this.min = min;
        this.max = max;
    }

    /**
     * Gets min template value
     *
     * @return {@link BreadthValue} instance
     */
    public BreadthValue getMin() {
        return min;
    }

    /**
     * Gets max template value
     *
     * @return {@link BreadthValue} instance
     */
    public BreadthValue getMax() {
        return max;
    }
}

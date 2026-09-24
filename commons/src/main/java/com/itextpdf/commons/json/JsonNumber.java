/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.commons.json;

import java.util.Objects;

/**
 * Class representing JSON number value.
 */
public final class JsonNumber extends JsonValue {
    private final Double doubleValue;
    private final Long longValue;

    /**
     * Creates a new {@link JsonNumber} representing a provided value.
     *
     * @param value to wrap into this {@link JsonNumber}
     */
    public JsonNumber(double value) {
        super();
        this.doubleValue = value;
        this.longValue = null;
    }

    /**
     * Creates a new {@link JsonNumber} representing a provided value.
     *
     * @param value to wrap into this {@link JsonNumber}
     */
    public JsonNumber(long value) {
        super();
        this.doubleValue = null;
        this.longValue = value;
    }

    /**
     * Gets a {@code double} value wrapped into this {@link JsonNumber}.
     *
     * @return a {@code double} value
     */
    public double getValue() {
        return getDoubleValue();
    }

    /**
     * Gets a {@code double} value wrapped into this {@link JsonNumber}.
     *
     * @return a {@code double} value
     */
    public double getDoubleValue() {
        return isDouble() ? doubleValue.doubleValue() : (double) longValue.doubleValue();
    }

    /**
     * Gets a {@code long} value wrapped into this {@link JsonNumber}.
     *
     * @return a {@code long} value
     */
    public long getLongValue() {
        return isDouble() ? (long) doubleValue.longValue() : longValue.longValue();
    }

    /**
     * Checks if this {@link JsonNumber} represents {@code double} value
     *
     * @return {@code true} if this {@link JsonNumber} represents {@code double} value, {@code false} if it represents
     * {@code long} value
     */
    public boolean isDouble() {
        return doubleValue != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        JsonNumber that = (JsonNumber) obj;
        return Objects.equals(this.doubleValue, that.doubleValue) && Objects.equals(this.longValue, that.longValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(doubleValue, longValue);
    }
}

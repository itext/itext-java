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
package com.itextpdf.layout.properties;

/**
 * A specialized class that holds a sizing value and the type it is measured in.
 *
 * <p>
 * For more information see https://www.w3.org/TR/css-sizing-3/#sizing-values.
 */
public class SizingValue {
    /**
     * The type of the value.
     */
    private SizingValueType type;
    /**
     * The unit value.
     */
    private UnitValue unitValue;

    /**
     * Creates a new empty instance of {@link SizingValue} class.
     */
    private SizingValue() {
        // do nothing
    }

    /**
     * Creates an instance of {@link SizingValue} with {@link UnitValue} value.
     *
     * @param unitValue the unit value
     *
     * @return the sizing value instance
     */
    public static SizingValue createUnitValue(UnitValue unitValue) {
        SizingValue result = new SizingValue();
        result.type = SizingValueType.UNIT;
        result.unitValue = unitValue;
        return result;
    }

    /**
     * Creates an instance of {@link SizingValue} with min-content value.
     *
     * @return the sizing value instance
     */
    public static SizingValue createMinContentValue() {
        SizingValue result = new SizingValue();
        result.type = SizingValueType.MIN_CONTENT;
        return result;
    }

    /**
     * Creates an instance of {@link SizingValue} with max-content value.
     *
     * @return the sizing value instance
     */
    public static SizingValue createMaxContentValue() {
        SizingValue result = new SizingValue();
        result.type = SizingValueType.MAX_CONTENT;
        return result;
    }

    /**
     * Creates an instance of {@link SizingValue} with auto value.
     *
     * @return the sizing value instance
     */
    public static SizingValue createAutoValue() {
        SizingValue result = new SizingValue();
        result.type = SizingValueType.AUTO;
        return result;
    }

    /**
     * Checks whether the value is absolute.
     *
     * @return {@code true} if absolute, {@code false} otherwise
     */
    public boolean isAbsoluteValue() {
        return type == SizingValueType.UNIT && unitValue.isPointValue();
    }

    /**
     * Gets absolute value, if exists.
     *
     * @return absolute value, or {@code null} if value is relative
     */
    public Float getAbsoluteValue() {
        if (isAbsoluteValue()) {
            return unitValue.getValue();
        }
        return null;
    }

    /**
     * Gets the type of the value.
     *
     * @return the type of the value
     */
    public SizingValueType getType() {
        return type;
    }

    /**
     * Gets unit value.
     *
     * @return the {@link UnitValue} or {@code null} if another value type is stored
     */
    public UnitValue getUnitValue() {
        return unitValue;
    }

    /**
     * Enum of sizing value types.
     */
    public enum SizingValueType {
        /**
         * Type which presents {@link UnitValue} value, can be both relative (percentage) and absolute (points).
         */
        UNIT,
        /**
         * Type which presents relative auto value.
         */
        AUTO,
        /**
         * Type which presents relative min content value.
         */
        MIN_CONTENT,
        /**
         * Type which presents relative max content value.
         */
        MAX_CONTENT,
        /**
         * Type which presents relative fit content function value.
         */
        FIT_CONTENT
    }
}

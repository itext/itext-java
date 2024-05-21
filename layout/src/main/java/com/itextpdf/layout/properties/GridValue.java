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
 * A specialized class that holds a value for grid-template-columns/rows and
 * grid-auto-columns/rows properties and the type it is measured in.
 */
public class GridValue {
    /**
     * The type of the value.
     */
    private GridValueType type;
    /**
     * The flexible value.
     */
    private Float flex;
    /**
     * The sizing value.
     */
    private SizingValue sizingValue;

    /**
     * Creates a new empty instance of {@link GridValue} class.
     */
    private GridValue() {
        // do nothing
    }

    /**
     * Creates an instance of {@link GridValue} with {@link SizingValue} value.
     *
     * @param sizingValue the sizing value
     *
     * @return the grid value instance
     */
    public static GridValue createSizeValue(SizingValue sizingValue) {
        GridValue result = new GridValue();
        result.sizingValue = sizingValue;
        result.type = GridValueType.SIZING;
        return result;
    }

    /**
     * Creates an instance of {@link GridValue} with {@link UnitValue} inside of {@link SizingValue} value.
     *
     * @param unitValue the unit value
     *
     * @return the grid value instance
     */
    public static GridValue createUnitValue(UnitValue unitValue) {
        GridValue result = new GridValue();
        result.sizingValue = SizingValue.createUnitValue(unitValue);
        result.type = GridValueType.SIZING;
        return result;
    }

    /**
     * Creates an instance of {@link GridValue} with flex value.
     *
     * @param flex the flex value
     *
     * @return the grid value instance
     */
    public static GridValue createFlexValue(float flex) {
        GridValue result = new GridValue();
        result.flex = flex;
        result.type = GridValueType.FLEX;
        return result;
    }

    /**
     * Checks whether the value is  absolute.
     *
     * @return {@code true} if absolute, {@code false} otherwise
     */
    public boolean isAbsoluteValue() {
        return type == GridValueType.SIZING && sizingValue.isAbsoluteValue();
    }

    /**
     * Gets absolute value, if exists.
     *
     * @return absolute value, or {@code null} if value is relative
     */
    public Float getAbsoluteValue() {
        if (isAbsoluteValue()) {
            return sizingValue.getAbsoluteValue();
        }
        return null;
    }

    /**
     * Gets type of value.
     *
     * @return the type of the value
     */
    public GridValueType getType() {
        return type;
    }

    /**
     * Gets the flex value.
     *
     * @return the flex value of {@code null} if another value type is stored
     */
    public Float getFlexValue() {
        return flex;
    }

    /**
     * Gets the sizing value.
     *
     * @return the instance of {@link SizingValue} or {@code null} if another value type is stored
     */
    public SizingValue getSizingValue() {
        return sizingValue;
    }

    /**
     * Enum of grid value types.
     */
    public enum GridValueType {
        /**
         * Type which presents {@link SizingValue} value.
         */
        SIZING,
        /**
         * Type which presents relative flexible value.
         */
        FLEX
    }
}

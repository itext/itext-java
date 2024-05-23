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
    private static final GridValue MIN_CONTENT_VALUE = new GridValue(SizingValueType.MIN_CONTENT);
    private static final GridValue MAX_CONTENT_VALUE = new GridValue(SizingValueType.MAX_CONTENT);
    private static final GridValue AUTO_VALUE = new GridValue(SizingValueType.AUTO);

    private SizingValueType type;
    private Float value;

    private GridValue() {
       // Do nothing
    }

    private GridValue(SizingValueType type) {
        this.type = type;
    }

    /**
     * Creates an instance of {@link GridValue} with point value.
     *
     * @param value the point value
     *
     * @return the grid value instance
     */
    public static GridValue createPointValue(float value) {
        GridValue result = new GridValue();
        result.type = SizingValueType.POINT;
        result.value = value;
        return result;
    }

    /**
     * Creates an instance of {@link GridValue} with percent value.
     *
     * @param value the percent value
     *
     * @return the grid value instance
     */
    public static GridValue createPercentValue(float value) {
        GridValue result = new GridValue();
        result.type = SizingValueType.PERCENT;
        result.value = value;
        return result;
    }

    /**
     * Creates an instance of {@link GridValue} with min-content value.
     *
     * @return the grid value instance
     */
    public static GridValue createMinContentValue() {
        return MIN_CONTENT_VALUE;
    }

    /**
     * Creates an instance of {@link GridValue} with max-content value.
     *
     * @return the grid value instance
     */
    public static GridValue createMaxContentValue() {
        return MAX_CONTENT_VALUE;
    }

    /**
     * Creates an instance of {@link GridValue} with auto value.
     *
     * @return the grid value instance
     */
    public static GridValue createAutoValue() {
        return AUTO_VALUE;
    }

    /**
     * Creates an instance of {@link GridValue} with flexible value.
     *
     * @param value the flexible value
     *
     * @return the grid value instance
     */
    public static GridValue createFlexValue(float value) {
        GridValue result = new GridValue();
        result.type = SizingValueType.FLEX;
        result.value = value;
        return result;
    }


    /**
     * Checks whether the value is absolute.
     *
     * @return {@code true} if absolute, {@code false} otherwise
     */
    public boolean isPointValue() {
        return type == SizingValueType.POINT;
    }

    /**
     * Checks whether the value is percent.
     *
     * @return {@code true} if percent, {@code false} otherwise
     */
    public boolean isPercentValue() {
        return type == SizingValueType.PERCENT;
    }

    /**
     * Checks whether the value is auto.
     *
     * @return {@code true} if auto, {@code false} otherwise
     */
    public boolean isAutoValue() {
        return type == SizingValueType.AUTO;
    }

    /**
     * Checks whether the value is min-content.
     *
     * @return {@code true} if min-content, {@code false} otherwise
     */
    public boolean isMinContentValue() {
        return type == SizingValueType.MIN_CONTENT;
    }

    /**
     * Checks whether the value is max-content.
     *
     * @return {@code true} if max-content, {@code false} otherwise
     */
    public boolean isMaxContentValue() {
        return type == SizingValueType.MAX_CONTENT;
    }

    /**
     * Checks whether the value is flexible.
     *
     * @return {@code true} if flexible, {@code false} otherwise
     */
    public boolean isFlexibleValue() {
        return type == SizingValueType.FLEX;
    }

    /**
     * Gets value, if exists.
     *
     * @return the value, or {@code null} if there is no value
     */
    public Float getValue() {
        return value;
    }

    /**
     * Enum of sizing value types.
     */
    private enum SizingValueType {
        /**
         * Type which presents absolute point value.
         */
        POINT,
        /**
         * Type which presents relative percent value.
         */
        PERCENT,
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
        FIT_CONTENT,
        /**
         * Type which presents relative flexible value.
         */
        FLEX
    }
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.property;

/**
 * A property corresponding to the css line-height property and used to
 * set the height of a line box in the HTML mode. On block-level elements,
 * it specifies the minimum height of line boxes within the element.
 * On non-replaced inline elements, it specifies the height that is used to calculate line box height.
 */
public class LineHeight {
    private static final int FIXED = 1;
    private static final int MULTIPLIED = 2;
    private static final int NORMAL = 4;

    private int type;
    private float value;

    private LineHeight(int type, float value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Returns the line height value.
     * The meaning of the returned value depends on the type of line height.
     *
     * @return the {@link LineHeight} value.
     */
    public float getValue() {
        return value;
    }

    /**
     * Creates a {@link LineHeight} with a fixed value.
     *
     * @param value value to set
     * @return created {@link LineHeight} object
     */
    public static LineHeight createFixedValue(float value) {
        return new LineHeight(FIXED, value);
    }

    /**
     * Creates a {@link LineHeight} with multiplied value.
     * This value must be multiplied by the element's font size.
     *
     * @param value value to set
     * @return created {@link LineHeight} object
     */
    public static LineHeight createMultipliedValue(float value) {
        return new LineHeight(MULTIPLIED, value);
    }

    /**
     * Creates a normal {@link LineHeight}.
     *
     * @return created {@link LineHeight} object
     */
    public static LineHeight createNormalValue() {
        return new LineHeight(NORMAL, 0);
    }

    /**
     * Check if the {@link LineHeight} contains fixed value.
     *
     * @return true if {@link LineHeight} contains fixed value.
     */
    public boolean isFixedValue() {
        return type == FIXED;
    }

    /**
     * Check if the {@link LineHeight} contains multiplied value.
     *
     * @return true if {@link LineHeight} contains multiplied value.
     */
    public boolean isMultipliedValue() {
        return type == MULTIPLIED;
    }

    /**
     * Check if the {@link LineHeight} contains normal value.
     *
     * @return true if {@link LineHeight} is normal.
     */
    public boolean isNormalValue() {
        return type == NORMAL;
    }
}

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
package com.itextpdf.layout.properties;

import com.itextpdf.layout.borders.Border;

/**
 * Represents a border radius.
 */
public class BorderRadius {
    /** The horizontal semi-major axis of the ellipse to use for the border in that corner. */
    private UnitValue horizontalRadius;
    /** The vertical semi-major axis of the ellipse to use for the border in that corner. */
    private UnitValue verticalRadius;

    /**
     * Creates a {@link BorderRadius border radius} with given value.
     *
     * @param radius the radius
     */
    public BorderRadius(UnitValue radius) {
        this.horizontalRadius = radius;
        this.verticalRadius = radius;
    }

    /**
     * Creates a {@link BorderRadius border radius} with a given point value.
     *
     * @param radius the radius
     */
    public BorderRadius(float radius) {
        this.horizontalRadius = UnitValue.createPointValue(radius);
        this.verticalRadius = this.horizontalRadius;
    }

    /**
     * Creates a {@link BorderRadius border radius} with given horizontal and vertical values.
     *
     * @param horizontalRadius the horizontal radius of the corner
     * @param verticalRadius the vertical radius of the corner
     */
    public BorderRadius(UnitValue horizontalRadius, UnitValue verticalRadius) {
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
    }

    /**
     * Creates a {@link BorderRadius border radius} with given horizontal and vertical point values.
     *
     * @param horizontalRadius the horizontal radius of the corner
     * @param verticalRadius the vertical radius of the corner
     */
    public BorderRadius(float horizontalRadius, float verticalRadius) {
        this.horizontalRadius = UnitValue.createPointValue(horizontalRadius);
        this.verticalRadius = UnitValue.createPointValue(verticalRadius);
    }

    /**
     * Gets the horizontal radius of the {@link Border border's} corner.
     *
     * @return the horizontal radius
     */
    public UnitValue getHorizontalRadius() {
        return horizontalRadius;
    }

    /**
     * Gets the vertical radius of the {@link Border border's} corner.
     *
     * @return the vertical radius
     */
    public UnitValue getVerticalRadius() {
        return verticalRadius;
    }
}

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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * A rectangle adapted for working with text elements.
 */
public class TextRectangle extends Rectangle {


    /**
     * The y coordinate of the line on which the text is located.
     */
    private float textBaseLineYCoordinate;

    /**
     * Create new instance of text rectangle.
     *
     * @param x      the x coordinate of lower left point
     * @param y      the y coordinate of lower left point
     * @param width  the width value
     * @param height the height value
     * @param textBaseLineYCoordinate the y coordinate of the line on which the text is located.
     */
    public TextRectangle(float x, float y, float width, float height, float textBaseLineYCoordinate) {
        super(x, y, width, height);
        this.textBaseLineYCoordinate = textBaseLineYCoordinate;
    }

    /**
     * Return the far right point of the rectangle with y on the baseline.
     *
     * @return the far right baseline point
     */
    public Point getTextBaseLineRightPoint() {
        return new Point(getRight(), textBaseLineYCoordinate);
    }
}

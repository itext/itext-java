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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;

/***
 * Implements lineTo(H) attribute of SVG's path element
 * */
public class HorizontalLineTo extends LineTo {

    static final int ARGUMENT_SIZE = 1;
    /**
     * Creates an absolute Horizontal LineTo.
     */
    public HorizontalLineTo() {
        this(false);
    }

    /**
     * Creates a Horizontal LineTo. Set argument to true to create a relative HorizontalLineTo.
     *
     * @param relative whether this is a relative HorizontalLineTo or not
     */
    public HorizontalLineTo(boolean relative) {
        super(relative);
    }

    @Override
    public void setCoordinates(String[] inputCoordinates, Point startPoint) {
        String[] normalizedCoords = new String[LineTo.ARGUMENT_SIZE];
        // An H or h command is equivalent to an L or l command with 0 specified for the y coordinate.
        normalizedCoords[0] = inputCoordinates[0];
        normalizedCoords[1] = isRelative() ? "0" : Double.toString(startPoint.getY());
        super.setCoordinates(normalizedCoords, startPoint);
    }
}

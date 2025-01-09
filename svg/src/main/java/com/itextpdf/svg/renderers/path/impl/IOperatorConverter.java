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

import com.itextpdf.svg.utils.SvgCoordinateUtils;

/**
 * A locally used strategy for converting relative coordinates to absolute coordinates (in the current SVG coordinate
 * space). Its implementation differs between Smooth (Shorthand) Bézier curves and all other path commands.
 */
public interface IOperatorConverter {
    /**
     * Convert an array of relative coordinates to an array with the same size containing absolute coordinates.
     *
     * @param relativeCoordinates the initial set of coordinates
     * @param initialPoint        an array representing the point relative to which the relativeCoordinates are defined
     * @return a String array of absolute coordinates, with the same length as the input array
     */
    String[] makeCoordinatesAbsolute(String[] relativeCoordinates, double[] initialPoint);
}

/**
 * Implementation of {@link IOperatorConverter} specifically for smooth curves. It will convert all operators from
 * relative to absolute coordinates except the first coordinate pair.
 * This implementation is used by the Smooth (Shorthand) Bézier curve commands, because the conversion of the first
 * coordinate pair is calculated in {@link com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer#getShapeCoordinates}.
 */
class SmoothOperatorConverter implements IOperatorConverter {
    @Override
    public String[] makeCoordinatesAbsolute(String[] relativeCoordinates, double[] initialPoint) {
        String[] result = new String[relativeCoordinates.length];
        System.arraycopy(relativeCoordinates, 0, result, 0, 2);
        // convert all relative operators to absolute operators ...
        relativeCoordinates = SvgCoordinateUtils.makeRelativeOperatorCoordinatesAbsolute(relativeCoordinates, initialPoint);
        // ... but don't store the first coordinate pair
        System.arraycopy(relativeCoordinates, 2, result, 2, relativeCoordinates.length - 2);
        return result;
    }
}

/**
 * Default implementation of {@link IOperatorConverter} used by the regular (not-smooth) curves and other path commands.
 * It will convert all operators from relative to absolute coordinates.
 */
class DefaultOperatorConverter implements IOperatorConverter {
    @Override
    public String[] makeCoordinatesAbsolute(String[] relativeCoordinates, double[] initialPoint) {
        return SvgCoordinateUtils.makeRelativeOperatorCoordinatesAbsolute(relativeCoordinates, initialPoint);
    }
}

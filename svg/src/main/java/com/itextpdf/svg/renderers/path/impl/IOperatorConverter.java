/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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

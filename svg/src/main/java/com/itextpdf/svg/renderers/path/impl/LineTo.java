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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;

import java.util.Arrays;

/***
 * Implements lineTo(L) attribute of SVG's path element.
 * */
public class LineTo extends AbstractPathShape {

    static final int ARGUMENT_SIZE = 2;

    /**
     * Creates new {@link LineTo} instance.
     */
    public LineTo() {
        this(false);
    }

    /**
     * Creates new {@link LineTo} instance.
     *
     * @param relative {@code true} in case it is a relative operator, {@code false} if it is an absolute operator
     */
    public LineTo(boolean relative) {
        super(relative);
    }

    @Override
    public void draw() {
        double x = parseHorizontalLength(coordinates[0]);
        double y = parseVerticalLength(coordinates[1]);
        double[] points = new double[]{x, y};
        applyTransform(points);
        context.getCurrentCanvas().lineTo(points[0], points[1]);
    }

    @Override
    public void setCoordinates(String[] inputCoordinates, Point startPoint) {
        if (inputCoordinates.length != ARGUMENT_SIZE) {
            throw new IllegalArgumentException(MessageFormatUtil.format(SvgExceptionMessageConstant.LINE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0, Arrays.toString(inputCoordinates)));
        }
        this.coordinates = new String[] {inputCoordinates[0], inputCoordinates[1]};
        if (isRelative()) {
            this.coordinates = copier.makeCoordinatesAbsolute(coordinates, new double[]{startPoint.getX(), startPoint.getY()});
        }
    }

}

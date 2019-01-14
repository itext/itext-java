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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.utils.SvgCoordinateUtils;

import java.util.Arrays;

/***
 * Implements curveTo(L) attribute of SVG's path element
 * */
public class CurveTo extends AbstractPathShape {

    // Original coordinates from path instruction, according to the (x1 y1 x2 y2 x y)+ spec
    private String[][] coordinates;

    public CurveTo() {
        this(false);
    }

    public CurveTo(boolean relative) {
        this.relative = relative;
    }

    @Override
    public void draw(PdfCanvas canvas) {
        for (int i = 0; i < coordinates.length; i++) {
            float x1 = CssUtils.parseAbsoluteLength(coordinates[i][0]);
            float y1 = CssUtils.parseAbsoluteLength(coordinates[i][1]);
            float x2 = CssUtils.parseAbsoluteLength(coordinates[i][2]);
            float y2 = CssUtils.parseAbsoluteLength(coordinates[i][3]);
            float x = CssUtils.parseAbsoluteLength(coordinates[i][4]);
            float y = CssUtils.parseAbsoluteLength(coordinates[i][5]);
            canvas.curveTo(x1, y1, x2, y2, x, y);
        }
    }

    @Override
    public void setCoordinates(String[] coordinates, Point startPoint) {
        if (coordinates.length == 0 || coordinates.length % 6 != 0) {
            throw new IllegalArgumentException(MessageFormatUtil.format(SvgExceptionMessageConstant.CURVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0, Arrays.toString(coordinates)));
        }
        this.coordinates = new String[coordinates.length / 6][];
        double[] initialPoint = new double[] {startPoint.getX(), startPoint.getY()};
        for (int i = 0; i < coordinates.length; i += 6) {
            String[] curCoordinates = new String[]{coordinates[i], coordinates[i + 1], coordinates[i + 2],
                    coordinates[i + 3], coordinates[i + 4], coordinates[i + 5]};
            if (isRelative()) {
                curCoordinates = SvgCoordinateUtils.makeRelativeOperatorCoordinatesAbsolute(curCoordinates, initialPoint);
                initialPoint[0] = (float)CssUtils.parseFloat(curCoordinates[4]);
                initialPoint[1] = (float)CssUtils.parseFloat(curCoordinates[5]);
            }
            this.coordinates[i / 6] = curCoordinates;
        }
    }

    /**
     * Returns coordinates of the last control point (the one closer to the ending point)
     * in the series of Bezier curves (possibly, one curve), in SVG space coordinates
     * @return coordinates of the last control points in SVG space coordinates
     */
    public Point getLastControlPoint() {
        return createPoint(coordinates[coordinates.length - 1][2], coordinates[coordinates.length - 1][3]);
    }

    @Override
    public Point getEndingPoint() {
        return createPoint(coordinates[coordinates.length - 1][4], coordinates[coordinates.length - 1][5]);
    }

}

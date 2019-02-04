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
 * Implements moveTo(M) attribute of SVG's path element
 * */
public class MoveTo extends AbstractPathShape {

    private String[] coordinates;
    private LineTo additionalLines;

    public MoveTo() {
        this(false);
    }

    public MoveTo(boolean relative) {
        this.relative = relative;
    }

    @Override
    public void draw(PdfCanvas canvas) {
        float x = CssUtils.parseAbsoluteLength(coordinates[0]);
        float y = CssUtils.parseAbsoluteLength(coordinates[1]);
        canvas.moveTo(x, y);
        if (additionalLines != null) {
            additionalLines.draw(canvas);
        }
    }

    @Override
    public void setCoordinates(String[] coordinates, Point startPoint) {
        if (coordinates.length == 0 || coordinates.length % 2 != 0) {
            throw new IllegalArgumentException(MessageFormatUtil.format(SvgExceptionMessageConstant.MOVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0, Arrays.toString(coordinates)));
        }

        this.coordinates = new String[] {coordinates[0], coordinates[1]};
        if (isRelative()) {
            this.coordinates = SvgCoordinateUtils.makeRelativeOperatorCoordinatesAbsolute(coordinates, new double[]{startPoint.x, startPoint.y});
        }

        // If a moveto is followed by multiple pairs of coordinates, the subsequent pairs are treated as implicit lineto commands
        if (coordinates.length > 2) {
            additionalLines = new LineTo(isRelative());
            additionalLines.setCoordinates(Arrays.copyOfRange(coordinates, 2, coordinates.length), getEndingPoint());
        }
    }

    @Override
    public Point getEndingPoint() {
        return createPoint(coordinates[0], coordinates[1]);
    }
}

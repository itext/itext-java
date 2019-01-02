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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;



import java.util.ArrayList;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;polyline&gt; tag.
 */
public class PolylineSvgNodeRenderer extends AbstractSvgNodeRenderer {

    /**
     * A List of {@link Point} objects representing the path to be drawn by the polyline tag
     */
    protected List<Point> points = new ArrayList<>();

    protected List<Point> getPoints() {
        return this.points;
    }

    /**
     * Parses a string of space separated x,y pairs into individual {@link Point} objects and appends them to{@link PolylineSvgNodeRenderer#points}.
     * Throws an {@link SvgProcessingException} if pointsAttribute does not have a valid list of numerical x,y pairs.
     *
     * @param pointsAttribute A string of space separated x,y value pairs
     */
    protected void setPoints(String pointsAttribute) {
        if (pointsAttribute == null) {
            return;
        }

        List<String> points = SvgCssUtils.splitValueList(pointsAttribute);
        if (points.size() % 2 != 0) {
            throw new SvgProcessingException(SvgLogMessageConstant.POINTS_ATTRIBUTE_INVALID_LIST).setMessageParams(pointsAttribute);
        }

        float x, y;
        for (int i = 0; i < points.size(); i = i + 2) {
            x = CssUtils.parseAbsoluteLength(points.get(i));
            y = CssUtils.parseAbsoluteLength(points.get(i + 1));
            this.points.add(new Point(x, y));

        }

    }

    /**
     * Draws this element to a canvas-like object maintained in the context.
     * @param context the object that knows the place to draw this element and maintains its state
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        String pointsAttribute = attributesAndStyles.containsKey(SvgConstants.Attributes.POINTS) ? attributesAndStyles.get(SvgConstants.Attributes.POINTS) : null;
        setPoints(pointsAttribute);

        PdfCanvas canvas = context.getCurrentCanvas();
        canvas.writeLiteral("% polyline\n");
        if (points.size() > 1) {
            Point currentPoint = points.get(0);
            canvas.moveTo(currentPoint.getX(), currentPoint.getY());
            for (int x = 1; x < points.size(); x++) {
                currentPoint = points.get(x);
                canvas.lineTo(currentPoint.getX(), currentPoint.getY());
            }
        }


    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        PolylineSvgNodeRenderer copy = new PolylineSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }
}

/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.borders;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Draws a solid border around the element it's set to.
 */
public class SolidBorder extends Border {

    /**
     * Creates a SolidBorder with the specified width and sets the color to black.
     *
     * @param width width of the border
     */
    public SolidBorder(float width) {
        super(width);
    }

    /**
     * Creates a SolidBorder with the specified width and the specified color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public SolidBorder(Color color, float width) {
        super(color, width);
    }

    /**
     * Creates a SolidBorder with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity width of the border
     */
    public SolidBorder(Color color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return SOLID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2, defaultSide);
        switch (borderSide) {
            case TOP:
                x3 = x2 + borderWidthAfter;
                y3 = y2 + width;
                x4 = x1 - borderWidthBefore;
                y4 = y1 + width;
                break;
            case RIGHT:
                x3 = x2 + width;
                y3 = y2 - borderWidthAfter;
                x4 = x1 + width;
                y4 = y1 + borderWidthBefore;
                break;
            case BOTTOM:
                x3 = x2 - borderWidthAfter;
                y3 = y2 - width;
                x4 = x1 + borderWidthBefore;
                y4 = y1 - width;
                break;
            case LEFT:
                x3 = x2 - width;
                y3 = y2 + borderWidthAfter;
                x4 = x1 - width;
                y4 = y1 - borderWidthBefore;
                break;
        }

        canvas.saveState()
                .setFillColor(transparentColor.getColor());
        transparentColor.applyFillTransparency(canvas);
        canvas
                .moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill()
                .restoreState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float horizontalRadius1, float verticalRadius1, float horizontalRadius2, float verticalRadius2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;

        float innerRadiusBefore,
                innerRadiusFirst,
                innerRadiusSecond,
                innerRadiusAfter;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2, defaultSide);
        switch (borderSide) {
            case TOP:
                x3 = x2 + borderWidthAfter;
                y3 = y2 + width;
                x4 = x1 - borderWidthBefore;
                y4 = y1 + width;

                innerRadiusBefore = Math.max(0, horizontalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, verticalRadius1 - width);
                innerRadiusSecond = Math.max(0, verticalRadius2 - width);
                innerRadiusAfter = Math.max(0, horizontalRadius2 - borderWidthAfter);

                if (innerRadiusBefore > innerRadiusFirst) {
                    x1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x4, y1 - innerRadiusFirst), new Point(x1 + innerRadiusBefore, y1 - innerRadiusFirst)).getX();
                    y1 -= innerRadiusFirst;
                } else if (0 != innerRadiusBefore && 0 != innerRadiusFirst){
                    y1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x1 + innerRadiusBefore, y1), new Point(x1 + innerRadiusBefore, y1 - innerRadiusFirst)).getY();
                    x1 += innerRadiusBefore;
                }
                if (innerRadiusAfter > innerRadiusSecond) {
                    x2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2, y2 - innerRadiusSecond), new Point(x2 - innerRadiusAfter, y2 - innerRadiusSecond)).getX();
                    y2 -= innerRadiusSecond;
                } else if (0 != innerRadiusAfter && 0 != innerRadiusSecond){
                    y2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2 - innerRadiusAfter, y2), new Point(x2 - innerRadiusAfter, y2 - innerRadiusSecond)).getY();
                    x2 -= innerRadiusAfter;
                }

                break;
            case RIGHT:
                x3 = x2 + width;
                y3 = y2 - borderWidthAfter;
                x4 = x1 + width;
                y4 = y1 + borderWidthBefore;

                innerRadiusBefore = Math.max(0, verticalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, horizontalRadius1 - width);
                innerRadiusSecond = Math.max(0, horizontalRadius2 - width);
                innerRadiusAfter = Math.max(0, verticalRadius2 - borderWidthAfter);

                if (innerRadiusFirst > innerRadiusBefore) {
                    x1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x1, y1 - innerRadiusBefore), new Point(x1 - innerRadiusFirst, y1 - innerRadiusBefore)).getX();
                    y1 -= innerRadiusBefore;
                } else if (0 != innerRadiusBefore && 0 != innerRadiusFirst){
                    y1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x1 - innerRadiusFirst, y1), new Point(x1 - innerRadiusFirst, y1 - innerRadiusBefore)).getY();
                    x1 -= innerRadiusFirst;
                }

                if (innerRadiusAfter > innerRadiusSecond) {
                    y2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2 - innerRadiusSecond, y2), new Point(x2 - innerRadiusSecond, y2 + innerRadiusAfter)).getY();
                    x2 -= innerRadiusSecond;
                } else if (0 != innerRadiusAfter && 0 != innerRadiusSecond){
                    x2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2, y2 + innerRadiusAfter), new Point(x2 - innerRadiusSecond, y2 + innerRadiusAfter)).getX();
                    y2 += innerRadiusAfter;
                }

                break;
            case BOTTOM:
                x3 = x2 - borderWidthAfter;
                y3 = y2 - width;
                x4 = x1 + borderWidthBefore;
                y4 = y1 - width;

                innerRadiusBefore = Math.max(0, horizontalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, verticalRadius1 - width);
                innerRadiusSecond = Math.max(0, verticalRadius2 - width);
                innerRadiusAfter = Math.max(0, horizontalRadius2 - borderWidthAfter);

                if (innerRadiusFirst > innerRadiusBefore) {
                    y1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x1 - innerRadiusBefore, y1), new Point(x1 - innerRadiusBefore, y1 + innerRadiusFirst)).getY();
                    x1 -= innerRadiusBefore;
                } else if (0 != innerRadiusBefore && 0 != innerRadiusFirst){
                    x1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x1, y1 + innerRadiusFirst), new Point(x1 - innerRadiusBefore, y1 + innerRadiusFirst)).getX();
                    y1 += innerRadiusFirst;
                }

                if (innerRadiusAfter > innerRadiusSecond) {
                    x2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2, y2 + innerRadiusSecond), new Point(x2 + innerRadiusAfter, y2 + innerRadiusSecond)).getX();
                    y2 += innerRadiusSecond;
                } else if (0 != innerRadiusAfter && 0 != innerRadiusSecond){
                    y2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2 + innerRadiusAfter, y2), new Point(x2 + innerRadiusAfter, y2 + innerRadiusSecond)).getY();
                    x2 += innerRadiusAfter;
                }
                break;
            case LEFT:
                x3 = x2 - width;
                y3 = y2 + borderWidthAfter;
                x4 = x1 - width;
                y4 = y1 - borderWidthBefore;

                innerRadiusBefore = Math.max(0, verticalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, horizontalRadius1 - width);
                innerRadiusSecond = Math.max(0, horizontalRadius2 - width);
                innerRadiusAfter = Math.max(0, verticalRadius2 - borderWidthAfter);

                if (innerRadiusFirst > innerRadiusBefore) {
                    x1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x1, y1 + innerRadiusBefore), new Point(x1 + innerRadiusFirst, y1 + innerRadiusBefore)).getX();
                    y1 += innerRadiusBefore;
                } else if (0 != innerRadiusBefore && 0 != innerRadiusFirst){
                    y1 = (float) getIntersectionPoint(new Point(x1, y1), new Point(x4, y4), new Point(x1 + innerRadiusFirst, y1), new Point(x1 + innerRadiusFirst, y1 + innerRadiusBefore)).getY();
                    x1 += innerRadiusFirst;
                }

                if (innerRadiusAfter > innerRadiusSecond) {
                    y2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2 + innerRadiusSecond, y2), new Point(x2 + innerRadiusSecond, y2 - innerRadiusAfter)).getY();
                    x2 += innerRadiusSecond;
                } else if (0 != innerRadiusAfter && 0 != innerRadiusSecond){
                    x2 = (float) getIntersectionPoint(new Point(x2, y2), new Point(x3, y3), new Point(x2, y2 - innerRadiusAfter), new Point(x2 + innerRadiusSecond, y2 - innerRadiusAfter)).getX();
                    y2 -= innerRadiusAfter;
                }
                break;
        }

        canvas.saveState()
                .setFillColor(transparentColor.getColor());
        transparentColor.applyFillTransparency(canvas);
        canvas
                .moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill()
                .restoreState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        canvas.
                saveState().
                setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.
                setLineWidth(width).
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }
}

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
 * Draws a dotted border around the element it has been set to. Do note that this border draw square dots,
 * if you want to draw round dots, see {@link RoundDotsBorder}.
 */
public class DottedBorder extends Border {

    /**
     * The modifier to be applied on the width to have the initial gap size
     */
    private static final float GAP_MODIFIER = 1.5f;

    /**
     * Creates a DottedBorder instance with the specified width. The color is set to the default: black.
     *
     * @param width width of the border
     */
    public DottedBorder(float width) {
        super(width);
    }

    /**
     * Creates a DottedBorder instance with the specified width and color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public DottedBorder(Color color, float width) {
        super(color, width);
    }

    /**
     * Creates a DottedBorder with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity width of the border
     */
    public DottedBorder(Color color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return Border.DOTTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * GAP_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = getDotsGap(borderLength, initialGap + width);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        float widthHalf = width / 2;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2, defaultSide);
        switch (borderSide) {
            case TOP:
                y1 += widthHalf;
                y2 += widthHalf;
                break;
            case RIGHT:
                x1 += widthHalf;
                x2 += widthHalf;
                break;
            case BOTTOM:
                y1 -= widthHalf;
                y2 -= widthHalf;
                break;
            case LEFT:
                x1 -= widthHalf;
                x2 -= widthHalf;
                break;
        }

        canvas
                .saveState()
                .setLineWidth(width)
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas
                .setLineDash(width, adjustedGap, width + adjustedGap / 2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke()
                .restoreState();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float horizontalRadius1, float verticalRadius1, float horizontalRadius2, float verticalRadius2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float curv = 0.447f;
        float initialGap = width * GAP_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);
        float adjustedGap = getDotsGap(borderLength, initialGap);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        // Points (x0, y0) and (x3, y3) are used to produce Bezier curve
        float x0 = x1, y0 = y1,
                x3 = x2, y3 = y2;
        float innerRadiusBefore,
                innerRadiusFirst,
                innerRadiusSecond,
                innerRadiusAfter;

        float widthHalf = width / 2;

        canvas
                .saveState()
                .setLineWidth(width)
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.setLineDash(width, adjustedGap, width + adjustedGap / 2);

        Point clipPoint1, clipPoint2, clipPoint;
        Border.Side borderSide = getBorderSide(x1, y1, x2, y2, defaultSide);
        switch (borderSide) {
            case TOP:

                innerRadiusBefore = Math.max(0, horizontalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, verticalRadius1 - width);
                innerRadiusSecond = Math.max(0, verticalRadius2 - width);
                innerRadiusAfter = Math.max(0, horizontalRadius2 - borderWidthAfter);

                x0 -= borderWidthBefore / 2;
                y0 -= innerRadiusFirst;

                x3 += borderWidthAfter / 2;
                y3 -= innerRadiusSecond;

                clipPoint1 = getIntersectionPoint(new Point(x1 - borderWidthBefore, y1 + width), new Point(x1, y1), new Point(x0, y0), new Point(x0 + 10, y0));
                clipPoint2 = getIntersectionPoint(new Point(x2 + borderWidthAfter, y2 + width), new Point(x2, y2), new Point(x3, y3), new Point(x3 - 10, y3));
                if (clipPoint1.x > clipPoint2.x) {
                    clipPoint = getIntersectionPoint(new Point(x1 - borderWidthBefore, y1 + width), clipPoint1, clipPoint2, new Point(x2 + borderWidthAfter, y2 + width));
                    canvas.moveTo(x1 - borderWidthBefore, y1 + width).lineTo(clipPoint.x, clipPoint.y).lineTo(x2 + borderWidthAfter, y2 + width).lineTo(x1 - borderWidthBefore, y1 + width);
                } else {
                    canvas.moveTo(x1 - borderWidthBefore, y1 + width).lineTo(clipPoint1.x, clipPoint1.y).lineTo(clipPoint2.x, clipPoint2.y).lineTo(x2 + borderWidthAfter, y2 + width).lineTo(x1 - borderWidthBefore, y1 + width);
                }
                canvas.clip().newPath();

                x1 += innerRadiusBefore;
                y1 += widthHalf;

                x2 -= innerRadiusAfter;
                y2 += widthHalf;

                canvas
                        .moveTo(x0, y0).curveTo(x0, y0 + innerRadiusFirst * curv, x1 - innerRadiusBefore * curv, y1, x1, y1)
                        .lineTo(x2, y2)
                        .curveTo(x2 + innerRadiusAfter * curv, y2, x3, y3 + innerRadiusSecond * curv, x3, y3);
                break;
            case RIGHT:
                innerRadiusBefore = Math.max(0, verticalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, horizontalRadius1 - width);
                innerRadiusSecond = Math.max(0, horizontalRadius2 - width);
                innerRadiusAfter = Math.max(0, verticalRadius2 - borderWidthAfter);
                x0 -= innerRadiusFirst;
                y0 += borderWidthBefore / 2;

                x3 -= innerRadiusSecond;
                y3 -= borderWidthAfter / 2;

                clipPoint1 = getIntersectionPoint(new Point(x1 + width, y1 + borderWidthBefore), new Point(x1, y1), new Point(x0, y0), new Point(x0, y0 - 10));
                clipPoint2 = getIntersectionPoint(new Point(x2 + width, y2 - borderWidthAfter), new Point(x2, y2), new Point(x3, y3), new Point(x3, y3 - 10));
                if (clipPoint1.y < clipPoint2.y) {
                    clipPoint = getIntersectionPoint(new Point(x1 + width, y1 + borderWidthBefore), clipPoint1, clipPoint2, new Point(x2 + width, y2 - borderWidthAfter));
                    canvas.moveTo(x1 + width, y1 + borderWidthBefore).lineTo(clipPoint.x, clipPoint.y).lineTo(x2 + width, y2 - borderWidthAfter).lineTo(x1 + width, y1 + borderWidthBefore).clip().newPath();
                } else {
                    canvas.moveTo(x1 + width, y1 + borderWidthBefore).lineTo(clipPoint1.x, clipPoint1.y).lineTo(clipPoint2.x, clipPoint2.y).lineTo(x2 + width, y2 - borderWidthAfter).lineTo(x1 + width, y1 + borderWidthBefore).clip().newPath();
                }
                canvas.clip().newPath();

                x1 += widthHalf;
                y1 -= innerRadiusBefore;

                x2 += widthHalf;
                y2 += innerRadiusAfter;

                canvas
                        .moveTo(x0, y0).curveTo(x0 + innerRadiusFirst * curv, y0, x1, y1 + innerRadiusBefore * curv, x1, y1)
                        .lineTo(x2, y2)
                        .curveTo(x2, y2 - innerRadiusAfter * curv, x3 + innerRadiusSecond * curv, y3, x3, y3);

                break;
            case BOTTOM:
                innerRadiusBefore = Math.max(0, horizontalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, verticalRadius1 - width);
                innerRadiusSecond = Math.max(0, verticalRadius2 - width);
                innerRadiusAfter = Math.max(0, horizontalRadius2 - borderWidthAfter);
                x0 += borderWidthBefore / 2;
                y0 += innerRadiusFirst;

                x3 -= borderWidthAfter / 2;
                y3 += innerRadiusSecond;

                clipPoint1 = getIntersectionPoint(new Point(x1 + borderWidthBefore, y1 - width), new Point(x1, y1), new Point(x0, y0), new Point(x0 - 10, y0));
                clipPoint2 = getIntersectionPoint(new Point(x2 - borderWidthAfter, y2 - width), new Point(x2, y2), new Point(x3, y3), new Point(x3 + 10, y3));
                if (clipPoint1.x < clipPoint2.x) {
                    clipPoint = getIntersectionPoint(new Point(x1 + borderWidthBefore, y1 - width), clipPoint1, clipPoint2, new Point(x2 - borderWidthAfter, y2 - width));
                    canvas.moveTo(x1 + borderWidthBefore, y1 - width).lineTo(clipPoint.x, clipPoint.y).lineTo(x2 - borderWidthAfter, y2 - width).lineTo(x1 + borderWidthBefore, y1 - width);
                } else {
                    canvas.moveTo(x1 + borderWidthBefore, y1 - width).lineTo(clipPoint1.x, clipPoint1.y).lineTo(clipPoint2.x, clipPoint2.y).lineTo(x2 - borderWidthAfter, y2 - width).lineTo(x1 + borderWidthBefore, y1 - width);
                }
                canvas.clip().newPath();

                x1 -= innerRadiusBefore;
                y1 -= widthHalf;

                x2 += innerRadiusAfter;
                y2 -= widthHalf;

                canvas
                        .moveTo(x0, y0).curveTo(x0, y0 - innerRadiusFirst * curv, x1 + innerRadiusBefore * curv, y1, x1, y1)
                        .lineTo(x2, y2)
                        .curveTo(x2 - innerRadiusAfter * curv, y2, x3, y3 - innerRadiusSecond * curv, x3, y3);

                break;
            case LEFT:
                innerRadiusBefore = Math.max(0, verticalRadius1 - borderWidthBefore);
                innerRadiusFirst = Math.max(0, horizontalRadius1 - width);
                innerRadiusSecond = Math.max(0, horizontalRadius2 - width);
                innerRadiusAfter = Math.max(0, verticalRadius2 - borderWidthAfter);
                x0 += innerRadiusFirst;
                y0 -= borderWidthBefore / 2;

                x3 += innerRadiusSecond;
                y3 += borderWidthAfter / 2;

                clipPoint1 = getIntersectionPoint(new Point(x1 - width, y1 - borderWidthBefore), new Point(x1, y1), new Point(x0, y0), new Point(x0, y0 + 10));
                clipPoint2 = getIntersectionPoint(new Point(x2 - width, y2 + borderWidthAfter), new Point(x2, y2), new Point(x3, y3), new Point(x3, y3 + 10));
                if (clipPoint1.y > clipPoint2.y) {
                    clipPoint = getIntersectionPoint(new Point(x1 - width, y1 - borderWidthBefore), clipPoint1, clipPoint2, new Point(x2 - width, y2 + borderWidthAfter));
                    canvas.moveTo(x1 - width, y1 - borderWidthBefore).lineTo(clipPoint.x, clipPoint.y).lineTo(x2 - width, y2 + borderWidthAfter).lineTo(x1 - width, y1 - borderWidthBefore);
                } else {
                    canvas.moveTo(x1 - width, y1 - borderWidthBefore).lineTo(clipPoint1.x, clipPoint1.y).lineTo(clipPoint2.x, clipPoint2.y).lineTo(x2 - width, y2 + borderWidthAfter).lineTo(x1 - width, y1 - borderWidthBefore);
                }
                canvas.clip().newPath();

                x1 -= widthHalf;
                y1 += innerRadiusBefore;

                x2 -= widthHalf;
                y2 -= innerRadiusAfter;

                canvas
                        .moveTo(x0, y0).curveTo(x0 - innerRadiusFirst * curv, y0, x1, y1 - innerRadiusBefore * curv, x1, y1)
                        .lineTo(x2, y2)
                        .curveTo(x2, y2 + innerRadiusAfter * curv, x3 - innerRadiusSecond * curv, y3, x3, y3);
                break;
        }
        canvas
                .stroke()
                .restoreState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        float initialGap = width * GAP_MODIFIER;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = getDotsGap(borderLength, initialGap + width);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        canvas
                .saveState()
                .setLineWidth(width)
                .setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas
                .setLineDash(width, adjustedGap, width + adjustedGap / 2)
                .moveTo(x1, y1)
                .lineTo(x2, y2)
                .stroke()
                .restoreState();
    }

    /**
     * Adjusts the size of the gap between dots
     *
     * @param distance   the {@link Border border} length
     * @param initialGap the initial size of the gap
     * @return the adjusted size of the gap
     */
    protected float getDotsGap(double distance, float initialGap) {
        double gapsNum = Math.ceil(distance / initialGap);
        if (gapsNum == 0) {
            return initialGap;
        }
        return (float) (distance / gapsNum);
    }

}

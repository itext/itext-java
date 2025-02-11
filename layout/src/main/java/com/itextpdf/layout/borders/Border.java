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
package com.itextpdf.layout.borders;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.properties.TransparentColor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a border.
 */
public abstract class Border {

    /**
     * The null Border, i.e. the presence of such border is equivalent to the absence of the border
     */
    public static final Border NO_BORDER = null;

    /**
     * The solid border.
     *
     * @see SolidBorder
     */
    public static final int SOLID = 0;
    /**
     * The dashed border.
     *
     * @see DashedBorder
     */
    public static final int DASHED = 1;
    /**
     * The dotted border.
     *
     * @see DottedBorder
     */
    public static final int DOTTED = 2;
    /**
     * The double border.
     *
     * @see DoubleBorder
     */
    public static final int DOUBLE = 3;
    /**
     * The round-dots border.
     *
     * @see RoundDotsBorder
     */
    public static final int ROUND_DOTS = 4;
    /**
     * The 3D groove border.
     *
     * @see GrooveBorder
     */
    public static final int _3D_GROOVE = 5;
    /**
     * The 3D inset border.
     *
     * @see InsetBorder
     */
    public static final int _3D_INSET = 6;
    /**
     * The 3D outset border.
     *
     * @see OutsetBorder
     */
    public static final int _3D_OUTSET = 7;
    /**
     * The 3D ridge border.
     *
     * @see RidgeBorder
     */
    public static final int _3D_RIDGE = 8;
    /**
     * The fixed dashed border.
     *
     * @see FixedDashedBorder
     */
    public static final int DASHED_FIXED = 9;

    private static final int ARC_RIGHT_DEGREE = 0;
    private static final int ARC_TOP_DEGREE = 90;
    private static final int ARC_LEFT_DEGREE = 180;
    private static final int ARC_BOTTOM_DEGREE = 270;

    private static final int ARC_QUARTER_CLOCKWISE_EXTENT = -90;

    /**
     * The color of the border.
     *
     * @see TransparentColor
     */
    protected TransparentColor transparentColor;

    /**
     * The width of the border.
     */
    protected float width;
    /**
     * The type of the border.
     */
    protected int type;
    /**
     * The hash value for the border.
     */
    private int hash;

    /**
     * Creates a {@link Border border} with the given width.
     * The {@link Color color} to be set by default is black
     *
     * @param width the width which the border should have
     */
    protected Border(float width) {
        this(ColorConstants.BLACK, width);
    }

    /**
     * Creates a {@link Border border} with given width and {@link Color color}.
     *
     * @param color the color which the border should have
     * @param width the width which the border should have
     */
    protected Border(Color color, float width) {
        this.transparentColor = new TransparentColor(color);
        this.width = width;
    }

    /**
     * Creates a {@link Border border} with given width, {@link Color color} and opacity.
     *
     * @param color   the color which the border should have
     * @param width   the width which the border should have
     * @param opacity the opacity which border should have; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     */
    protected Border(Color color, float width, float opacity) {
        this.transparentColor = new TransparentColor(color, opacity);
        this.width = width;
    }

    /**
     * All borders are supposed to be drawn in such way, that inner content of the element is on the right from the
     * drawing direction. Borders are drawn in this order: top, right, bottom, left.
     * <p>
     * Given points specify the line which lies on the border of the content area,
     * therefore the border itself should be drawn to the left from the drawing direction.
     * <p>
     * <code>borderWidthBefore</code> and <code>borderWidthAfter</code> parameters are used to
     * define the widths of the borders that are before and after the current border, e.g. for
     * the bottom border, <code>borderWidthBefore</code> specifies width of the right border and
     * <code>borderWidthAfter</code> - width of the left border. Those width are used to handle areas
     * of border joins.
     *
     * @param canvas            PdfCanvas to be written to
     * @param x1                x coordinate of the beginning point of the element side, that should be bordered
     * @param y1                y coordinate of the beginning point of the element side, that should be bordered
     * @param x2                x coordinate of the ending point of the element side, that should be bordered
     * @param y2                y coordinate of the ending point of the element side, that should be bordered
     * @param defaultSide       the {@link Border.Side}, that we will fallback to, if it cannot be determined by border coordinates
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter  defines width of the border that is after the current one
     */
    public abstract void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide, float borderWidthBefore, float borderWidthAfter);

     /**
     * Draw borders around the target rectangle.
     *
     * @param canvas    PdfCanvas to be written to
     * @param rectangle border positions rectangle
     */
    public void draw(PdfCanvas canvas, Rectangle rectangle) {
        float left = rectangle.getX();
        float bottom = rectangle.getY();
        float right = rectangle.getX() + rectangle.getWidth();
        float top = rectangle.getY() + rectangle.getHeight();
        draw(canvas, left, top, right, top, Side.TOP, width, width);
        draw(canvas, right, top, right, bottom, Side.RIGHT, width, width);
        draw(canvas, right, bottom, left, bottom, Side.BOTTOM, width, width);
        draw(canvas, left, bottom, left, top, Side.LEFT, width, width);
    }

    /**
     * All borders are supposed to be drawn in such way, that inner content of the element is on the right from the
     * drawing direction. Borders are drawn in this order: top, right, bottom, left.
     * <p>
     * Given points specify the line which lies on the border of the content area,
     * therefore the border itself should be drawn to the left from the drawing direction.
     * <p>
     * <code>borderWidthBefore</code> and <code>borderWidthAfter</code> parameters are used to
     * define the widths of the borders that are before and after the current border, e.g. for
     * the bottom border, <code>borderWidthBefore</code> specifies width of the right border and
     * <code>borderWidthAfter</code> - width of the left border. Those width are used to handle areas
     * of border joins.
     * <p>
     * <code>borderRadius</code> is used to draw rounded borders.
     *
     * @param canvas            PdfCanvas to be written to
     * @param x1                x coordinate of the beginning point of the element side, that should be bordered
     * @param y1                y coordinate of the beginning point of the element side, that should be bordered
     * @param x2                x coordinate of the ending point of the element side, that should be bordered
     * @param y2                y coordinate of the ending point of the element side, that should be bordered
     * @param borderRadius      defines the radius of the element's corners
     * @param defaultSide       the {@link Border.Side}, that we will fallback to, if it cannot be determined by border coordinates
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter  defines width of the border that is after the current one
     */
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderRadius, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        draw(canvas, x1, y1, x2, y2, borderRadius, borderRadius, borderRadius, borderRadius, defaultSide, borderWidthBefore, borderWidthAfter);
    }

    /**
     * All borders are supposed to be drawn in such way, that inner content of the element is on the right from the
     * drawing direction. Borders are drawn in this order: top, right, bottom, left.
     * <p>
     * Given points specify the line which lies on the border of the content area,
     * therefore the border itself should be drawn to the left from the drawing direction.
     * <p>
     * <code>borderWidthBefore</code> and <code>borderWidthAfter</code> parameters are used to
     * define the widths of the borders that are before and after the current border, e.g. for
     * the bottom border, <code>borderWidthBefore</code> specifies width of the right border and
     * <code>borderWidthAfter</code> - width of the left border. Those width are used to handle areas
     * of border joins.
     * <p>
     * <code>horizontalRadius1</code>, <code>verticalRadius1</code>, <code>horizontalRadius2</code>
     * and <code>verticalRadius2</code> are used to draw rounded borders.
     *
     * @param canvas            PdfCanvas to be written to
     * @param x1                x coordinate of the beginning point of the element side, that should be bordered
     * @param y1                y coordinate of the beginning point of the element side, that should be bordered
     * @param x2                x coordinate of the ending point of the element side, that should be bordered
     * @param y2                y coordinate of the ending point of the element side, that should be bordered
     * @param horizontalRadius1 defines the horizontal radius of the border's first corner
     * @param verticalRadius1   defines the vertical radius of the border's first corner
     * @param horizontalRadius2 defines the horizontal radius of the border's second corner
     * @param verticalRadius2   defines the vertical radius of the border's second corner
     * @param defaultSide       the {@link Border.Side}, that we will fallback to, if it cannot be determined by border coordinates
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter  defines width of the border that is after the current one
     */
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float horizontalRadius1, float verticalRadius1, float horizontalRadius2, float verticalRadius2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        Logger logger = LoggerFactory.getLogger(Border.class);
        logger.warn(MessageFormatUtil.format(
                IoLogMessageConstant.METHOD_IS_NOT_IMPLEMENTED_BY_DEFAULT_OTHER_METHOD_WILL_BE_USED,
                "Border#draw(PdfCanvas, float, float, float, float, float, float, float, float, Side, float, float",
                "Border#draw(PdfCanvas, float, float, float, float, Side, float, float)"));
        draw(canvas, x1, y1, x2, y2, defaultSide, borderWidthBefore, borderWidthAfter);
    }

    /**
     * Draws the border of a cell.
     *
     * @param canvas      PdfCanvas to be written to
     * @param x1          x coordinate of the beginning point of the element side, that should be bordered
     * @param y1          y coordinate of the beginning point of the element side, that should be bordered
     * @param x2          x coordinate of the ending point of the element side, that should be bordered
     * @param y2          y coordinate of the ending point of the element side, that should be bordered
     * @param defaultSide the {@link Border.Side}, that we will fallback to, if it cannot be determined by border coordinates
     */
    public abstract void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide);

    /**
     * Returns the type of the {@link Border border}
     *
     * @return the type of border.
     */
    public abstract int getType();

    /**
     * Gets the {@link Color color} of the {@link Border border}
     *
     * @return the {@link Color color}
     */
    public Color getColor() {
        return transparentColor.getColor();
    }

    /**
     * Gets the opacity of the {@link Border border}
     *
     * @return the border opacity; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     */
    public float getOpacity() {
        return transparentColor.getOpacity();
    }

    /**
     * Gets the width of the {@link Border border}
     *
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the {@link Color color} of the {@link Border border}
     *
     * @param color The color
     */
    public void setColor(Color color) {
        this.transparentColor = new TransparentColor(color, this.transparentColor.getOpacity());
    }

    /**
     * Sets the width of the {@link Border border}
     *
     * @param width The width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Indicates whether the border is equal to the given border.
     * The border type, width and color are considered during the comparison.
     */
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Border) {
            Border anotherBorder = (Border) anObject;
            if (anotherBorder.getType() != getType()
                    || !anotherBorder.getColor().equals(getColor())
                    || anotherBorder.getWidth() != getWidth()
                    || anotherBorder.transparentColor.getOpacity() != transparentColor.getOpacity()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int h = hash;

        if (h == 0) {
            h = (int) getWidth() * 31 + getColor().hashCode();
            h = h * 31 + (int) transparentColor.getOpacity();
            hash = h;
        }

        return h;
    }

    /**
     * Returns the {@link Side side} corresponded to the line between two points.
     * Notice that we consider the rectangle traversal to be clockwise.
     * In case side couldn't be detected we will fallback to default side
     *
     * @param x1          the abscissa of the left-bottom point
     * @param y1          the ordinate of the left-bottom point
     * @param x2          the abscissa of the right-top point
     * @param y2          the ordinate of the right-top point
     * @param defaultSide the default side of border
     * @return the corresponded {@link Side side}
     */
    protected Side getBorderSide(float x1, float y1, float x2, float y2, Side defaultSide) {
        boolean isLeft = false;
        boolean isRight = false;
        if (Math.abs(y2 - y1) > 0.0005f) {
            isLeft = y2 - y1 > 0;
            isRight = y2 - y1 < 0;
        }

        boolean isTop = false;
        boolean isBottom = false;
        if (Math.abs(x2 - x1) > 0.0005f) {
            isTop = x2 - x1 > 0;
            isBottom = x2 - x1 < 0;
        }

        if (isTop) {
            return isLeft ? Side.LEFT : Side.TOP;
        } else if (isRight) {
            return Side.RIGHT;
        } else if (isBottom) {
            return Side.BOTTOM;
        } else if (isLeft) {
            return Side.LEFT;
        }

        return defaultSide;
    }

    /**
     * Enumerates the different sides of the rectangle.
     * The rectangle sides are expected to be parallel to corresponding page sides
     * Otherwise the result is Side.NONE
     */
    public enum Side {
        NONE, TOP, RIGHT, BOTTOM, LEFT
    }

    /**
     * Gets a {@link Point} in which two lines intersect.
     *
     * @param lineBeg a {@link Point} which defines some point on the first line
     * @param lineEnd a {@link Point} which defines another point on the first line
     * @param clipLineBeg a {@link Point} which defines some point on the second line
     * @param clipLineEnd a {@link Point} which defines another point on the second line
     * @return the intersection {@link Point}
     */
    protected Point getIntersectionPoint(Point lineBeg, Point lineEnd, Point clipLineBeg, Point clipLineEnd) {
        double A1 = lineBeg.getY() - lineEnd.getY(), A2 = clipLineBeg.getY() - clipLineEnd.getY();
        double B1 = lineEnd.getX() - lineBeg.getX(), B2 = clipLineEnd.getX() - clipLineBeg.getX();
        double C1 = lineBeg.getX() * lineEnd.getY() - lineBeg.getY() * lineEnd.getX();
        double C2 = clipLineBeg.getX() * clipLineEnd.getY() - clipLineBeg.getY() * clipLineEnd.getX();

        double M = B1 * A2 - B2 * A1;

        return new Point((B2 * C1 - B1 * C2) / M, (C2 * A1 - C1 * A2) / M);
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

    /**
     * Perform drawing operations to draw discontinuous borders. Used by {@link DashedBorder}, {@link DottedBorder} and {@link RoundDotsBorder}.
     *
     * @param canvas            canvas to draw on
     * @param defaultSide       the {@link Border.Side}, that we will fallback to, if it cannot be determined by border coordinates
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter  defines width of the border that is after the current one
     */
    /**
     * Perform drawing operations to draw discontinuous borders. Used by {@link DashedBorder}, {@link DottedBorder} and {@link RoundDotsBorder}.
     *
     * @param canvas            canvas to draw on
     * @param boundingRectangle rectangle representing the bounding box of the drawing operations
     * @param horizontalRadii   the horizontal radius of the border's two corners
     * @param verticalRadii     the vertical radius of the border's two corners
     * @param defaultSide       the {@link Border.Side}, that we will fallback to, if it cannot be determined by border coordinates
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter  defines width of the border that is after the current one
     */
    protected void drawDiscontinuousBorders(PdfCanvas canvas, Rectangle boundingRectangle, float[] horizontalRadii, float[] verticalRadii, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        double x1 = boundingRectangle.getX();
        double y1 = boundingRectangle.getY();
        double x2 = boundingRectangle.getRight();
        double y2 = boundingRectangle.getTop();

        final double horizontalRadius1 = horizontalRadii[0];
        final double horizontalRadius2 = horizontalRadii[1];

        final double verticalRadius1 = verticalRadii[0];
        final double verticalRadius2 = verticalRadii[1];

        // Points (x0, y0) and (x3, y3) are used to produce Bezier curve
        double x0 = boundingRectangle.getX();
        double y0 = boundingRectangle.getY();
        double x3 = boundingRectangle.getRight();
        double y3 = boundingRectangle.getTop();

        double innerRadiusBefore;
        double innerRadiusFirst;
        double innerRadiusSecond;
        double innerRadiusAfter;

        final double widthHalf = width / 2.0;

        Point clipPoint1;
        Point clipPoint2;
        Point clipPoint;
        final Border.Side borderSide = getBorderSide((float) x1, (float) y1, (float) x2, (float) y2, defaultSide);
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
                if (clipPoint1.getX() > clipPoint2.getX()) {
                    clipPoint = getIntersectionPoint(new Point(x1 - borderWidthBefore, y1 + width), clipPoint1, clipPoint2, new Point(x2 + borderWidthAfter, y2 + width));
                    canvas.moveTo(x1 - borderWidthBefore, y1 + width)
                            .lineTo(clipPoint.getX(), clipPoint.getY())
                            .lineTo(x2 + borderWidthAfter, y2 + width)
                            .lineTo(x1 - borderWidthBefore, y1 + width);
                } else {
                    canvas.moveTo(x1 - borderWidthBefore, y1 + width)
                            .lineTo(clipPoint1.getX(), clipPoint1.getY())
                            .lineTo(clipPoint2.getX(), clipPoint2.getY())
                            .lineTo(x2 + borderWidthAfter, y2 + width)
                            .lineTo(x1 - borderWidthBefore, y1 + width);
                }
                canvas.clip().endPath();

                x1 += innerRadiusBefore;
                y1 += widthHalf;

                x2 -= innerRadiusAfter;
                y2 += widthHalf;

                canvas
                        .arc(x0, y0 - innerRadiusFirst,
                                x1 + innerRadiusBefore, y1,
                                ARC_LEFT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                        .arcContinuous(x2 - innerRadiusAfter, y2,
                                x3, y3 - innerRadiusSecond,
                                ARC_TOP_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT);
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
                if (clipPoint1.getY() < clipPoint2.getY()) {
                    clipPoint = getIntersectionPoint(new Point(x1 + width, y1 + borderWidthBefore), clipPoint1, clipPoint2, new Point(x2 + width, y2 - borderWidthAfter));
                    canvas.moveTo(x1 + width, y1 + borderWidthBefore)
                            .lineTo(clipPoint.getX(), clipPoint.getY())
                            .lineTo(x2 + width, y2 - borderWidthAfter)
                            .lineTo(x1 + width, y1 + borderWidthBefore)
                            .clip()
                            .endPath();
                } else {
                    canvas.moveTo(x1 + width, y1 + borderWidthBefore)
                            .lineTo(clipPoint1.getX(), clipPoint1.getY())
                            .lineTo(clipPoint2.getX(), clipPoint2.getY())
                            .lineTo(x2 + width, y2 - borderWidthAfter)
                            .lineTo(x1 + width, y1 + borderWidthBefore)
                            .clip()
                            .endPath();
                }
                canvas.clip().endPath();

                x1 += widthHalf;
                y1 -= innerRadiusBefore;

                x2 += widthHalf;
                y2 += innerRadiusAfter;

                canvas
                        .arc(x0 - innerRadiusFirst, y0,
                                x1, y1 - innerRadiusBefore,
                                ARC_TOP_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                        .arcContinuous(x2, y2 + innerRadiusAfter,
                                x3 - innerRadiusSecond, y3,
                                ARC_RIGHT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT);
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
                if (clipPoint1.getX() < clipPoint2.getX()) {
                    clipPoint = getIntersectionPoint(new Point(x1 + borderWidthBefore, y1 - width), clipPoint1, clipPoint2, new Point(x2 - borderWidthAfter, y2 - width));
                    canvas.moveTo(x1 + borderWidthBefore, y1 - width)
                            .lineTo(clipPoint.getX(), clipPoint.getY())
                            .lineTo(x2 - borderWidthAfter, y2 - width)
                            .lineTo(x1 + borderWidthBefore, y1 - width);
                } else {
                    canvas.moveTo(x1 + borderWidthBefore, y1 - width)
                            .lineTo(clipPoint1.getX(), clipPoint1.getY())
                            .lineTo(clipPoint2.getX(), clipPoint2.getY())
                            .lineTo(x2 - borderWidthAfter, y2 - width)
                            .lineTo(x1 + borderWidthBefore, y1 - width);
                }
                canvas.clip().endPath();

                x1 -= innerRadiusBefore;
                y1 -= widthHalf;

                x2 += innerRadiusAfter;
                y2 -= widthHalf;

                canvas
                        .arc(x0, y0 + innerRadiusFirst,
                                x1 - innerRadiusBefore, y1,
                                ARC_RIGHT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                        .arcContinuous(x2 + innerRadiusAfter, y2,
                                x3, y3 + innerRadiusSecond,
                                ARC_BOTTOM_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT);
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
                if (clipPoint1.getY() > clipPoint2.getY()) {
                    clipPoint = getIntersectionPoint(new Point(x1 - width, y1 - borderWidthBefore), clipPoint1, clipPoint2, new Point(x2 - width, y2 + borderWidthAfter));
                    canvas.moveTo(x1 - width, y1 - borderWidthBefore)
                            .lineTo(clipPoint.getX(), clipPoint.getY())
                            .lineTo(x2 - width, y2 + borderWidthAfter)
                            .lineTo(x1 - width, y1 - borderWidthBefore);
                } else {
                    canvas.moveTo(x1 - width, y1 - borderWidthBefore)
                            .lineTo(clipPoint1.getX(), clipPoint1.getY())
                            .lineTo(clipPoint2.getX(), clipPoint2.getY())
                            .lineTo(x2 - width, y2 + borderWidthAfter)
                            .lineTo(x1 - width, y1 - borderWidthBefore);
                }
                canvas.clip().endPath();

                x1 -= widthHalf;
                y1 += innerRadiusBefore;

                x2 -= widthHalf;
                y2 -= innerRadiusAfter;

                canvas
                        .arc(x0 + innerRadiusFirst, y0,
                                x1, y1 + innerRadiusBefore,
                                ARC_BOTTOM_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT)
                        .arcContinuous(x2, y2 - innerRadiusAfter,
                                x3 + innerRadiusSecond, y3,
                                ARC_LEFT_DEGREE, ARC_QUARTER_CLOCKWISE_EXTENT);
                break;
            default:
                break;
        }
        canvas
                .stroke()
                .restoreState();
    }

    /**
     * Calculate adjusted starting points for discontinuous borders, given two opposing points (A and B) that define the bounding rectangle
     *
     * @param x1          x-coordinate of point A
     * @param y1          y-ordinate of point A
     * @param x2          x-coordinate of point B
     * @param y2          y-ordinate of point B
     * @param defaultSide default side of the border used to determine the side given by points A and B
     * @return float[] containing the adjusted starting points in the form {x1,y1,x2,y2}
     */
    protected float[] getStartingPointsForBorderSide(float x1, float y1, float x2, float y2, Side defaultSide) {
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
            default:
                break;
        }
        return new float[]{x1, y1, x2, y2};
    }
}

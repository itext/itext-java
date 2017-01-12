/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.layout.border;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.property.TransparentColor;

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
     * @see SolidBorder
     */
    public static final int SOLID = 0;
    /**
     * The dashed border.
     * @see DashedBorder
     */
    public static final int DASHED = 1;
    /**
     * The dotted border.
     * @see DottedBorder
     */
    public static final int DOTTED = 2;
    /**
     * The double border.
     * @see DoubleBorder
     */
    public static final int DOUBLE = 3;
    /**
     * The round-dots border.
     * @see RoundDotsBorder
     */
    public static final int ROUND_DOTS = 4;
    /**
     * The 3D groove border.
     * @see GrooveBorder
     */
    public static final int _3D_GROOVE = 5;
    /**
     * The 3D inset border.
     * @see InsetBorder
     */
    public static final int _3D_INSET = 6;
    /**
     * The 3D outset border.
     * @see OutsetBorder
     */
    public static final int _3D_OUTSET = 7;
    /**
     * The 3D ridge border.
     * @see RidgeBorder
     */
    public static final int _3D_RIDGE = 8;

    /**
     * The color of the border.
     * @see Color
     * @deprecated use {@link Border#transparentColor} instead
     */
    @Deprecated
    protected Color color;

    /**
     * The color of the border.
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

    private Side tmpSide = Side.NONE;

    /**
     * Creates a {@link Border border} with the given width.
     * The {@link Color color} to be set by default is black
     *
     * @param width the width which the border should have
     */
    protected Border(float width) {
        this(Color.BLACK, width);
    }

    /**
     * Creates a {@link Border border} with given width and {@link Color color}.
     *
     * @param color the color which the border should have
     * @param width the width which the border should have
     */
    protected Border(Color color, float width) {
        this.color = color;
        this.transparentColor = new TransparentColor(color);
        this.width = width;
    }

    /**
     * Creates a {@link Border border} with given width, {@link Color color} and opacity.
     *
     * @param color the color which the border should have
     * @param width the width which the border should have
     * @param opacity the opacity which border should have; a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     */
    protected Border(Color color, float width, float opacity) {
        this.color = color;
        this.transparentColor = new TransparentColor(color, opacity);
        this.width = width;
    }
    
    /**
     * <p>
     * All borders are supposed to be drawn in such way, that inner content of the element is on the right from the
     * drawing direction. Borders are drawn in this order: top, right, bottom, left.
     * </p>
     * <p>
     * Given points specify the line which lies on the border of the content area,
     * therefore the border itself should be drawn to the left from the drawing direction.
     * </p>
     * <p>
     * <code>borderWidthBefore</code> and <code>borderWidthAfter</code> parameters are used to
     * define the widths of the borders that are before and after the current border, e.g. for
     * the bottom border, <code>borderWidthBefore</code> specifies width of the right border and
     * <code>borderWidthAfter</code> - width of the left border. Those width are used to handle areas
     * of border joins.
     * </p>
     * @param canvas PdfCanvas to be written to
     * @param x1 x coordinate of the beginning point of the element side, that should be bordered
     * @param y1 y coordinate of the beginning point of the element side, that should be bordered
     * @param x2 x coordinate of the ending point of the element side, that should be bordered
     * @param y2 y coordinate of the ending point of the element side, that should be bordered
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter defines width of the border that is after the current one
     *
     * @deprecated Will be removed in 7.1.0. use {@link Border#draw(PdfCanvas, float, float, float, float, Side, float, float)} instead
     */
    @Deprecated
    public abstract void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter);

    /**
     * <p>
     * All borders are supposed to be drawn in such way, that inner content of the element is on the right from the
     * drawing direction. Borders are drawn in this order: top, right, bottom, left.
     * </p>
     * <p>
     * Given points specify the line which lies on the border of the content area,
     * therefore the border itself should be drawn to the left from the drawing direction.
     * </p>
     * <p>
     * <code>borderWidthBefore</code> and <code>borderWidthAfter</code> parameters are used to
     * define the widths of the borders that are before and after the current border, e.g. for
     * the bottom border, <code>borderWidthBefore</code> specifies width of the right border and
     * <code>borderWidthAfter</code> - width of the left border. Those width are used to handle areas
     * of border joins.
     * </p>
     * @param canvas PdfCanvas to be written to
     * @param x1 x coordinate of the beginning point of the element side, that should be bordered
     * @param y1 y coordinate of the beginning point of the element side, that should be bordered
     * @param x2 x coordinate of the ending point of the element side, that should be bordered
     * @param y2 y coordinate of the ending point of the element side, that should be bordered
     * @param side the {@link Border.Side}, that represents element side, that should be bordered
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter defines width of the border that is after the current one
     */
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side side, float borderWidthBefore, float borderWidthAfter) {
        tmpSide = side;
        draw(canvas, x1, y1, x2, y2, borderWidthBefore, borderWidthAfter);
        tmpSide = Side.NONE;
    }

    /**
     * Draws the border of a cell.
     *
     * @param canvas PdfCanvas to be written to
     * @param x1 x coordinate of the beginning point of the element side, that should be bordered
     * @param y1 y coordinate of the beginning point of the element side, that should be bordered
     * @param x2 x coordinate of the ending point of the element side, that should be bordered
     * @param y2 y coordinate of the ending point of the element side, that should be bordered
     */
    public abstract void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2);

    /**
     * Returns the type of the {@link Border border}
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
     */
    public void setColor(Color color) {
        this.color = color;
        this.transparentColor = new TransparentColor(color, this.transparentColor.getOpacity());
    }

    /**
     * Sets the width of the {@link Border border}
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
     * If the rectangle sides are not parallel to the corresponding page sides
     * the result is Side.NONE
     *
     * @param x1 the abscissa of the left-bottom point
     * @param y1 the ordinate of the left-bottom point
     * @param x2 the abscissa of the right-top point
     * @param y1 the ordinate of the right-top point
     * @return the corresponded {@link Side side}
     */
    protected Side getBorderSide(float x1, float y1, float x2, float y2) {
        boolean isLeft = false;
        boolean isRight = false;
        if (Math.abs(y2 - y1) > 0.0005f) {
            isLeft = y2 - y1 > 0;
            isRight = y2 - y1 < 0;
        }

        boolean isTop = false;
        boolean isBottom = false;
        if (Math.abs(x2-x1) > 0.0005f) {
            isTop = x2 - x1 > 0;
            isBottom = x2 - x1 < 0;
        }

        if (isTop) {
            return Side.TOP;
        } else if (isRight) {
            return Side.RIGHT;
        } else if (isBottom) {
            return Side.BOTTOM;
        } else if (isLeft) {
            return Side.LEFT;
        }

        return tmpSide;
    }

    /**
     * Enumerates the different sides of the rectangle.
     * The rectangle sides are expected to be parallel to corresponding page sides
     * Otherwise the result is Side.NONE
     */
    public enum Side {NONE, TOP, RIGHT, BOTTOM, LEFT}
}

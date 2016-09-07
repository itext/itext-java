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
package com.itextpdf.kernel.geom;

import java.io.Serializable;

public class Rectangle implements Serializable {

    private static final long serialVersionUID = 8025677415569233446L;

    private static float EPS = 1e-4f;

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle(float width, float height) {
        this(0, 0, width, height);
    }

    public Rectangle(Rectangle rect) {
        this(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    /**
     * Calculates the common rectangle which includes all the input rectangles.
     *
     * @param rectangles list of input rectangles.
     * @return common rectangle.
     */
    public static Rectangle getCommonRectangle(Rectangle... rectangles) {
        float ury = -Float.MAX_VALUE;
        float llx = Float.MAX_VALUE;
        float lly = Float.MAX_VALUE;
        float urx = -Float.MAX_VALUE;
        for (Rectangle rectangle : rectangles) {
            if (rectangle == null)
                continue;
            Rectangle rec = rectangle.clone();
            if (rec.getY() < lly)
                lly = rec.getY();
            if (rec.getX() < llx)
                llx = rec.getX();
            if (rec.getY() + rec.getHeight() > ury)
                ury = rec.getY() + rec.getHeight();
            if (rec.getX() + rec.getWidth() > urx)
                urx = rec.getX() + rec.getWidth();
        }

        return new Rectangle(llx, lly, urx-llx, ury-lly);
    }

    /**
     * Sets the rectangle by the coordinates, specifying its lower left and upper right points. May be used in chain.
     * <br/>
     * <br/>
     * Note: this method will normalize coordinates, so the rectangle will have non negative width and height,
     * and its x and y coordinates specified lower left point.
     *
     * @param llx the X coordinate of lower left point
     * @param lly the Y coordinate of lower left point
     * @param urx the X coordinate of upper right point
     * @param ury the Y coordinate of upper right point
     * @return this {@link Rectangle} instance.
     */
    public Rectangle setBbox(float llx, float lly, float urx, float ury) {
        // If llx is greater than urx, swap them (normalize)
        if (llx > urx) {
            float temp = llx;
            llx = urx;
            urx = temp;
        }
        // If lly is greater than ury, swap them (normalize)
        if (lly > ury) {
            float temp = lly;
            lly = ury;
            ury = temp;
        }
        x = llx;
        y = lly;
        width = urx - llx;
        height = ury - lly;
        return this;
    }

    /**
     * Gets the X coordinate of lower left point.
     *
     * @return the X coordinate of lower left point.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the X coordinate of lower left point. May be used in chain.
     *
     * @param x the X coordinate of lower left point to be set.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * Gets the Y coordinate of lower left point.
     *
     * @return the Y coordinate of lower left point.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the Y coordinate of lower left point. May be used in chain.
     *
     * @param y the Y coordinate of lower left point to be set.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * Gets the width of rectangle.
     *
     * @return the width of rectangle.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of rectangle. May be used in chain.
     *
     * @param width the the width of rectangle to be set.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * Gets the height of rectangle.
     *
     * @return the height of rectangle.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of rectangle. May be used in chain.
     *
     * @param height the the width of rectangle to be set.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * Increases the height of rectangle by the given value. May be used in chain.
     *
     * @param extra the value of the extra height to be added.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle increaseHeight(float extra) {
        this.height += extra;
        return this;
    }

    /**
     * Decreases the height of rectangle by the given value. May be used in chain.
     *
     * @param extra the value of the extra height to be subtracted.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle decreaseHeight(float extra) {
        this.height -= extra;
        return this;
    }

    /**
     *  Gets the X coordinate of the left edge of the rectangle. Same as: {@code getX()}.
     *
     *  @return the X coordinate of the left edge of the rectangle.
     */
    public float getLeft() {
        return x;
    }

    /**
     * Gets the X coordinate of the right edge of the rectangle. Same as: {@code getX() + getWidth()}.
     *
     * @return the X coordinate of the right edge of the rectangle.
     */
    public float getRight() {
        return x + width;
    }

    /**
     * Gets the Y coordinate of the upper edge of the rectangle. Same as: {@code getY() + getHeight()}.
     *
     * @return the Y coordinate of the upper edge of the rectangle.
     */
    public float getTop() {
        return y + height;
    }

    /**
     * Gets the Y coordinate of the lower edge of the rectangle. Same as: {@code getY()}.
     *
     * @return the Y coordinate of the lower edge of the rectangle.
     */
    public float getBottom() {
        return y;
    }

    public Rectangle moveDown(float move) {
        y -= move;
        return this;
    }

    public Rectangle moveUp(float move) {
        y += move;
        return this;
    }

    public Rectangle moveRight(float move) {
        x += move;
        return this;
    }

    public Rectangle moveLeft(float move) {
        x -= move;
        return this;
    }

    public <T extends Rectangle> T applyMargins(float topIndent, float rightIndent, float bottomIndent, float leftIndent, boolean reverse) {
        x += leftIndent * (reverse ? -1 : 1);
        width -= (leftIndent + rightIndent) * (reverse ? -1 : 1);
        y += bottomIndent * (reverse ? -1 : 1);
        height -= (topIndent + bottomIndent) * (reverse ? -1 : 1);
        return (T) this;
    }

    public boolean intersectsLine(float x1, float y1, float x2, float y2) {
        double rx1 = getX();
        double ry1 = getY();
        double rx2 = rx1 + getWidth();
        double ry2 = ry1 + getHeight();
        return
                (rx1 <= x1 && x1 <= rx2 && ry1 <= y1 && y1 <= ry2) ||
                        (rx1 <= x2 && x2 <= rx2 && ry1 <= y2 && y2 <= ry2) ||
                        linesIntersect(rx1, ry1, rx2, ry2, x1, y1, x2, y2) ||
                        linesIntersect(rx2, ry1, rx1, ry2, x1, y1, x2, y2);
    }

    @Override
    public String toString() {
        return "Rectangle: " + getWidth() +
                'x' +
                getHeight();
    }

    /**
     * Gets the copy of this rectangle.
     *
     * @return the copied rectangle.
     */
    public Rectangle clone() {
        return new Rectangle(x, y, width, height);
    }

    public boolean equalsWithEpsilon(Rectangle that) {
        return equalsWithEpsilon(that, EPS);
    }

    public boolean equalsWithEpsilon(Rectangle that, float eps) {
        float dx = Math.abs(x - that.x);
        float dy = Math.abs(y - that.y);
        float dw = Math.abs(width - that.width);
        float dh = Math.abs(height - that.height);
        return dx < eps && dy < eps && dw < eps && dh < eps;
    }

    private static boolean linesIntersect(double x1, double y1, double x2,
                                          double y2, double x3, double y3, double x4, double y4)
    {
        /*
         * A = (x2-x1, y2-y1) B = (x3-x1, y3-y1) C = (x4-x1, y4-y1) D = (x4-x3,
         * y4-y3) = C-B E = (x1-x3, y1-y3) = -B F = (x2-x3, y2-y3) = A-B
         *
         * Result is ((AxB) * (AxC) <=0) and ((DxE) * (DxF) <= 0)
         *
         * DxE = (C-B)x(-B) = BxB-CxB = BxC DxF = (C-B)x(A-B) = CxA-CxB-BxA+BxB =
         * AxB+BxC-AxC
         */

        x2 -= x1; // A
        y2 -= y1;
        x3 -= x1; // B
        y3 -= y1;
        x4 -= x1; // C
        y4 -= y1;

        double AvB = x2 * y3 - x3 * y2;
        double AvC = x2 * y4 - x4 * y2;

        // Online
        if (AvB == 0.0 && AvC == 0.0) {
            if (x2 != 0.0) {
                return
                        (x4 * x3 <= 0.0) ||
                                ((x3 * x2 >= 0.0) &&
                                        (x2 > 0.0 ? x3 <= x2 || x4 <= x2 : x3 >= x2 || x4 >= x2));
            }
            if (y2 != 0.0) {
                return
                        (y4 * y3 <= 0.0) ||
                                ((y3 * y2 >= 0.0) &&
                                        (y2 > 0.0 ? y3 <= y2 || y4 <= y2 : y3 >= y2 || y4 >= y2));
            }
            return false;
        }

        double BvC = x3 * y4 - x4 * y3;

        return (AvB * AvC <= 0.0) && (BvC * (AvB + BvC - AvC) <= 0.0);
    }
}
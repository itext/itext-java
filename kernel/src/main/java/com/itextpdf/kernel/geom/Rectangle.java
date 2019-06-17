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
package com.itextpdf.kernel.geom;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that represent rectangle object.
 */
public class Rectangle implements Serializable {

    private static final long serialVersionUID = 8025677415569233446L;

    private static float EPS = 1e-4f;

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    /**
     * Creates new instance.
     *
     * @param x      the x coordinate of lower left point
     * @param y      the y coordinate of lower left point
     * @param width  the width value
     * @param height the height value
     */
    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Creates new instance of rectangle with (0, 0) as the lower left point.
     *
     * @param width  the width value
     * @param height the height value
     */
    public Rectangle(float width, float height) {
        this(0, 0, width, height);
    }

    /**
     * Creates the copy of given {@link Rectangle}
     *
     * @param rect the copied {@link Rectangle}
     */
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

        return new Rectangle(llx, lly, urx - llx, ury - lly);
    }

    /**
     * Gets the rectangle as it looks on the rotated page
     * and returns the rectangle in coordinates relevant to the true page origin.
     * This rectangle can be used to add annotations, fields, and other objects
     * to the rotated page.
     *
     * @param rect the rectangle as it looks on the rotated page.
     * @param page the page on which one want to process the rectangle.
     * @return the newly created rectangle with translated coordinates.
     */
    public static Rectangle getRectangleOnRotatedPage(Rectangle rect, PdfPage page) {
        Rectangle resultRect = rect;
        int rotation = page.getRotation();
        if (0 != rotation) {
            Rectangle pageSize = page.getPageSize();
            switch ((rotation / 90) % 4) {
                case 1: // 90 degrees
                    resultRect = new Rectangle(pageSize.getWidth() - resultRect.getTop(), resultRect.getLeft(), resultRect.getHeight(), resultRect.getWidth());
                    break;
                case 2: // 180 degrees
                    resultRect = new Rectangle(pageSize.getWidth() - resultRect.getRight(), pageSize.getHeight() - resultRect.getTop(), resultRect.getWidth(), resultRect.getHeight());
                    break;
                case 3: // 270 degrees
                    resultRect = new Rectangle(resultRect.getLeft(), pageSize.getHeight() - resultRect.getRight(), resultRect.getHeight(), resultRect.getWidth());
                    break;
                case 4: // 0 degrees
                default:
                    break;
            }
        }
        return resultRect;
    }

    /**
     * Calculates the bounding box of passed points.
     *
     * @param points the points which appear inside the area
     *
     * @return the bounding box of passed points.
     */
    public static Rectangle calculateBBox(List<Point> points) {
        List<Double> xs = new ArrayList<>();
        List<Double> ys = new ArrayList<>();
        for (Point point : points) {
            xs.add(point.getX());
            ys.add(point.getY());
        }

        double left = Collections.min(xs);
        double bottom = Collections.min(ys);
        double right = Collections.max(xs);
        double top = Collections.max(ys);

        return new Rectangle((float) left, (float) bottom, (float) (right - left), (float) (top - bottom));
    }

    /**
     * Get the rectangle representation of the intersection between this rectangle and the passed rectangle
     *
     * @param rect the rectangle to find the intersection with
     * @return the intersection rectangle if the passed rectangles intersects with this rectangle,
     * a rectangle representing a line if the intersection is along an edge or
     * a rectangle representing a point if the intersection is a single point,
     * null otherwise
     */
    public Rectangle getIntersection(Rectangle rect) {
        Rectangle result = null;

        //Calculate possible lower-left corner and upper-right corner
        float llx = Math.max(x, rect.x);
        float lly = Math.max(y, rect.y);
        float urx = Math.min(getRight(), rect.getRight());
        float ury = Math.min(getTop(), rect.getTop());

        //If width or height is non-negative, there is overlap and we can construct the intersection rectangle
        float width = urx - llx;
        float height = ury - lly;

        if (Float.compare(width, 0) >= 0
                && Float.compare(height, 0) >= 0) {
            if (Float.compare(width, 0) < 0) width = 0;
            if (Float.compare(height, 0) < 0) height = 0;
            result = new Rectangle(llx, lly, width, height);
        }

        return result;
    }

    /**
     * Check if this rectangle contains the passed rectangle.
     * A rectangle will envelop itself, meaning that for any rectangle {@code rect}
     * the expression {@code rect.contains(rect)} always returns true.
     *
     * @param rect a rectangle which is to be checked if it is fully contained inside this rectangle.
     * @return true if this rectangle contains the passed rectangle, false otherwise.
     */
    public boolean contains(Rectangle rect) {
        float llx = this.getX();
        float lly = this.getY();
        float urx = llx + this.getWidth();
        float ury = lly + this.getHeight();

        float rllx = rect.getX();
        float rlly = rect.getY();
        float rurx = rllx + rect.getWidth();
        float rury = rlly + rect.getHeight();

        return llx - EPS <= rllx && lly - EPS <= rlly
                && rurx <= urx + EPS && rury <= ury + EPS;
    }

    /**
     * Check if this rectangle and the passed rectangle overlap
     *
     * @param rect
     * @return true if there is overlap of some kind
     */
    public boolean overlaps(Rectangle rect) {
        // Two rectangles do not overlap if any of the following holds
        return !(this.getX() + this.getWidth() < rect.getX()        //1. the lower left corner of the second rectangle is to the right of the upper-right corner of the first.
                || this.getY() + this.getHeight() < rect.getY()     //2. the lower left corner of the second rectangle is above the upper right corner of the first.
                || this.getX() > rect.getX() + rect.getWidth()      //3. the upper right corner of the second rectangle is to the left of the lower-left corner of the first.
                || this.getY() > rect.getY() + rect.getHeight()     //4. the upper right corner of the second rectangle is below the lower left corner of the first.
        );

    }

    /**
     * Sets the rectangle by the coordinates, specifying its lower left and upper right points. May be used in chain.
     * <br>
     * <br>
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
     * Gets the X coordinate of the left edge of the rectangle. Same as: {@code getX()}.
     *
     * @return the X coordinate of the left edge of the rectangle.
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

    /**
     * Decreases the y coordinate.
     *
     * @param move the value on which the position will be changed.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle moveDown(float move) {
        y -= move;
        return this;
    }


    /**
     * Increases the y coordinate.
     *
     * @param move the value on which the position will be changed.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle moveUp(float move) {
        y += move;
        return this;
    }

    /**
     * Increases the x coordinate.
     *
     * @param move the value on which the position will be changed.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle moveRight(float move) {
        x += move;
        return this;
    }

    /**
     * Decreases the x coordinate.
     *
     * @param move the value on which the position will be changed.
     * @return this {@link Rectangle} instance.
     */
    public Rectangle moveLeft(float move) {
        x -= move;
        return this;
    }

    /**
     * Change the rectangle according the specified margins.
     *
     * @param topIndent    the value on which the top y coordinate will change.
     * @param rightIndent  the value on which the right x coordinate will change.
     * @param bottomIndent the value on which the bottom y coordinate will change.
     * @param leftIndent the value on which the left x coordinate will change.
     * @param reverse if {@code true} the rectangle will expand, otherwise it will shrink
     * @return the  rectanglewith applied margins
     */
    public Rectangle applyMargins(float topIndent, float rightIndent, float bottomIndent, float leftIndent, boolean reverse) {
        x += leftIndent * (reverse ? -1 : 1);
        width -= (leftIndent + rightIndent) * (reverse ? -1 : 1);
        y += bottomIndent * (reverse ? -1 : 1);
        height -= (topIndent + bottomIndent) * (reverse ? -1 : 1);
        return this;
    }

    /**
     * Checks if rectangle have common points with line, specified by two points.
     *
     * @param x1 the x coordinate of first line's point.
     * @param y1 the y coordinate of first line's point.
     * @param x2 the x coordinate of second line's point.
     * @param y2 the y coordinate of second line's point.
     * @return {@code true} if rectangle have common points with line and {@code false} otherwise.
     */
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

    /**
     * Gets the string representation of rectangle.
     *
     * @return the string representation of rectangle.
     */
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

    /**
     * Compares instance of this rectangle with given deviation equals to 0.0001
     *
     * @param that the {@link Rectangle} to compare with.
     * @return {@code true} if the difference between corresponding rectangle values is less than deviation and {@code false} otherwise.
     */
    public boolean equalsWithEpsilon(Rectangle that) {
        return equalsWithEpsilon(that, EPS);
    }

    /**
     * Compares instance of this rectangle with given deviation.
     *
     * @param that the {@link Rectangle} to compare with.
     * @param eps  the deviation value.
     * @return {@code true} if the difference between corresponding rectangle values is less than deviation and {@code false} otherwise.
     */
    public boolean equalsWithEpsilon(Rectangle that, float eps) {
        float dx = Math.abs(x - that.x);
        float dy = Math.abs(y - that.y);
        float dw = Math.abs(width - that.width);
        float dh = Math.abs(height - that.height);
        return dx < eps && dy < eps && dw < eps && dh < eps;
    }

    private static boolean linesIntersect(double x1, double y1, double x2,
                                          double y2, double x3, double y3, double x4, double y4) {
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

    /**
     * Create a list of bounding rectangles from an 8 x n array of Quadpoints.
     * @param quadPoints 8xn array of numbers representing 4 points
     * @return a list of bounding rectangles for the passed quadpoints
     * @throws PdfException if the passed array's size is not a multiple of 8.
     */
    public static List<Rectangle> createBoundingRectanglesFromQuadPoint(PdfArray quadPoints) throws PdfException {
        List<Rectangle> boundingRectangles = new ArrayList<>();
        if (quadPoints.size() % 8 != 0) {
            throw new PdfException(PdfException.QuadPointArrayLengthIsNotAMultipleOfEight);
        }
        for (int i = 0; i < quadPoints.size(); i += 8) {
            float[] quadPointEntry = Arrays.copyOfRange(quadPoints.toFloatArray(),i,i+8);
            PdfArray quadPointEntryFA = new PdfArray(quadPointEntry);
            boundingRectangles.add(createBoundingRectangleFromQuadPoint(quadPointEntryFA));
        }
        return boundingRectangles;
    }

    /**
     * Create the bounding rectangle for the given array of quadpoints.
     * @param quadPoints an array containing 8 numbers that correspond to 4 points.
     * @return The smallest orthogonal rectangle containing the quadpoints.
     * @throws PdfException if the passed array's size is not a multiple of 8.
     */
    public static Rectangle createBoundingRectangleFromQuadPoint(PdfArray quadPoints) throws PdfException {
        //Check if array length is a multiple of 8
        if (quadPoints.size() % 8 != 0) {
            throw new PdfException(PdfException.QuadPointArrayLengthIsNotAMultipleOfEight);
        }
        float llx = Float.MAX_VALUE;
        float lly = Float.MAX_VALUE;
        float urx = -Float.MAX_VALUE;
        float ury = -Float.MAX_VALUE;
        for (int j = 0; j < 8; j += 2) {
            float x = quadPoints.getAsNumber(j).floatValue();
            float y = quadPoints.getAsNumber(j + 1).floatValue();

            if (x < llx) llx = x;
            if (x > urx) urx = x;
            if (y < lly) lly = y;
            if (y > ury) ury = y;
        }// QuadPoints in redact annotations have "Z" order, in spec they're specified
        return (new Rectangle(llx,
                lly,
                urx - llx,
                ury - lly));
    }


}

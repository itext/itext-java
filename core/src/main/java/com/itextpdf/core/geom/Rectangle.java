package com.itextpdf.core.geom;

public class Rectangle implements Cloneable {

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
     * @param rectangles list of input rectangles.
     * @return common rectangle.
     */
    public static Rectangle getCommonRectangle(Rectangle... rectangles) {
        Float ury = -Float.MAX_VALUE;
        Float llx = Float.MAX_VALUE;
        Float lly = Float.MAX_VALUE;
        Float urx = -Float.MAX_VALUE;
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

    public float getX() {
        return x;
    }

    public Rectangle setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Rectangle setY(float y) {
        this.y = y;
        return this;
    }

    public float getWidth() {
        return width;
    }

    public Rectangle setWidth(float width) {
        this.width = width;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public Rectangle setHeight(float height) {
        this.height = height;
        return this;
    }

    public Rectangle increaseHeight(float extra) {
        this.height += extra;
        return this;
    }

    public Rectangle decreaseHeight(float extra) {
        this.height -= extra;
        return this;
    }

    /**
     *  Gets llx, the same: {@code getX()}.
     */
    public float getLeft() {
        return x;
    }

    /**
     * Gets urx, the same to {@code getX() + getWidth()}.
     */
    public float getRight() {
        return x + width;
    }

    /**
     * Gets ury, the same to {@code getY() + getHeight()}.
     */
    public float getTop() {
        return y + height;
    }

    /**
     * Gets lly, the same to {@code getY()}.
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
    public Rectangle clone() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Rectangle))
            return false;
        Rectangle that = (Rectangle) obj;
        return x == that.x && y == that.y && width == that.width && height == that.height;
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
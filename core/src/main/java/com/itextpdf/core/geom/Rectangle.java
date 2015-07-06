package com.itextpdf.core.geom;

import com.itextpdf.core.pdf.PdfArray;

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

    /**
     * Calculates the common rectangle which includes all the input rectangles.
     * @param rectangles list of input rectangles.
     * @return common rectangle.
     */
    static public Rectangle getCommonRectangle(Rectangle... rectangles) {
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

    public Rectangle applyMargins(float topIndent, float rightIndent, float bottomIndent, float leftIndent, boolean reverse) {
        x += leftIndent * (reverse ? -1 : 1);
        width -= (leftIndent + rightIndent) * (reverse ? -1 : 1);
        y += bottomIndent * (reverse ? -1 : 1);
        height -= (topIndent + bottomIndent) * (reverse ? -1 : 1);
        return this;
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

    public PdfArray toPdfArray(){
        return new PdfArray(this);
    }
}

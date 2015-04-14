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
            Rectangle rec = (Rectangle) rectangle.clone();
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

    public Rectangle(float width, float height) {
        this(0, 0, width, height);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
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

    @Override
    public Rectangle clone() {
        return new Rectangle(x, y, width, height);
    }
}

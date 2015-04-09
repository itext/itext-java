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

    public void setHeight(float height) {
        this.height = height;
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

package com.itextpdf.model.layout;

public class LayoutRect {

    protected Float x;
    protected Float y;
    protected Float width;
    protected Float height;

    public LayoutRect(Float x, Float y, Float width, Float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public LayoutRect(Float width, Float height) {
        this((float) 0, (float)0, width, height);
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

}

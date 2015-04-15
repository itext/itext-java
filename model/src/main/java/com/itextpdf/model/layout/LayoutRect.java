package com.itextpdf.model.layout;


public class LayoutRect implements Cloneable {

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

    /**
     * Calculates the common rectangle which includes all the input rectangles.
     * @param rectangles list of input rectangles.
     * @return common rectangle.
     */
    static public LayoutRect getCommonRectangle(LayoutRect... rectangles) {
        Float ury = -Float.MAX_VALUE;
        Float llx = Float.MAX_VALUE;
        Float lly = Float.MAX_VALUE;
        Float urx = -Float.MAX_VALUE;
        for (LayoutRect rectangle : rectangles) {
            LayoutRect rec = (LayoutRect) rectangle.clone();
            if (rec.getHeight() == null)
                rec.setHeight(0f);
            if (rec.getWidth() == null)
                rec.setWidth(0f);
            if (rec.getY() < lly)
                lly = rec.getY();
            if (rec.getX() < llx)
                llx = rec.getX();
            if (rec.getY() + rec.getHeight() > ury)
                ury = rec.getY() + rec.getHeight();
            if (rec.getX() + rec.getWidth() > urx)
                urx = rec.getX() + rec.getWidth();
        }

        return new LayoutRect(llx, lly, urx-llx, ury-lly);
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

    @Override
    protected Object clone() {
        return new LayoutRect(x, y, width, height);
    }



}

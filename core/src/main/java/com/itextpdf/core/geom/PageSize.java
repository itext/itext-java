package com.itextpdf.core.geom;

public class PageSize extends Rectangle {

    static public PageSize A4 = new PageSize(612, 792);
    static public PageSize Default = A4;

    protected float leftMargin = 36;
    protected float rightMargin = 36;
    protected float topMargin = 36;
    protected float bottomMargin = 36;

    public PageSize(float width, float height) {
        this(width, height, 36, 36, 36, 36);
    }

    public PageSize(float width, float height, float leftMargin, float rightMargin, float topMargin, float bottomMargin) {
        super(0, 0, width, height);
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }


    public float getLeftMargin() {
        return leftMargin;
    }

    public PageSize setLeftMargin(float leftMargin) {
        this.leftMargin = leftMargin;
        return this;
    }

    public float getRightMargin() {
        return rightMargin;
    }

    public PageSize setRightMargin(float rightMargin) {
        this.rightMargin = rightMargin;
        return this;
    }

    public float getTopMargin() {
        return topMargin;
    }

    public PageSize setTopMargin(float topMargin) {
        this.topMargin = topMargin;
        return this;
    }

    public float getBottomMargin() {
        return bottomMargin;
    }

    public PageSize setBottomMargin(float bottomMargin) {
        this.bottomMargin = bottomMargin;
        return this;
    }
}

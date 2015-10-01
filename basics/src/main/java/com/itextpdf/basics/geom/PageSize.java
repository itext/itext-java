package com.itextpdf.basics.geom;

public class PageSize extends Rectangle {

    static public PageSize A0 = new PageSize(2384, 3370);
    static public PageSize A1 = new PageSize(1684, 2384);
    static public PageSize A2 = new PageSize(1190, 1684);
    static public PageSize A3 = new PageSize(842, 1190);
    static public PageSize A4 = new PageSize(595, 842);
    static public PageSize A5 = new PageSize(420, 595);
    static public PageSize A6 = new PageSize(298, 420);
    static public PageSize A7 = new PageSize(210, 298);
    static public PageSize A8 = new PageSize(148, 210);
    static public PageSize Default = A4;

    protected float leftMargin = 36;
    protected float rightMargin = 36;
    protected float topMargin = 36;
    protected float bottomMargin = 36;

    public PageSize(float width, float height) {
        this(width, height, 36, 36, 36, 36);
    }

    public PageSize(float width, float height, float topMargin, float rightMargin, float bottomMargin, float leftMargin) {
        super(0, 0, width, height);
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    public PageSize(Rectangle box) {
        this(box, 0, 0, 0, 0);
    }

    public PageSize(Rectangle box, float topMargin, float rightMargin, float bottomMargin, float leftMargin) {
        super(box.getX(), box.getY(), box.getWidth(), box.getHeight());
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

    public PageSize setMargins(float topMargin, float rightMargin, float bottomMargin, float leftMargin) {
        return setLeftMargin(leftMargin).setRightMargin(rightMargin).
                setTopMargin(topMargin).setBottomMargin(bottomMargin);
    }

    public Rectangle getEffectiveArea() {
        return new Rectangle(leftMargin, bottomMargin, width - leftMargin - rightMargin, height - bottomMargin - topMargin);
    }

    /**
     * Rotates PageSize clockwise with all the margins, i.e. the margins are rotated as well.
     */
    public PageSize rotate() {
        return new PageSize(height, width, leftMargin, topMargin, rightMargin, bottomMargin);
    }

    @Override
    public PageSize clone() {
        return new PageSize(width, height, topMargin, rightMargin, bottomMargin, leftMargin);
    }
}

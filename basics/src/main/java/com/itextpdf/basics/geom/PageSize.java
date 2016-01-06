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

    public PageSize(float width, float height) {
        super(0, 0, width, height);
    }


    public PageSize(Rectangle box) {
        super(box.getX(), box.getY(), box.getWidth(), box.getHeight());
    }

    /**
     * Rotates PageSize clockwise.
     */
    public PageSize rotate() {
        return new PageSize(height, width);
    }

    @Override
    public PageSize clone() {
        return new PageSize(super.clone());
    }
}

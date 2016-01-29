package com.itextpdf.layout.layout;

import com.itextpdf.kernel.geom.Rectangle;

public class LayoutArea implements Cloneable {

    protected int pageNumber;
    protected Rectangle bBox;
    protected boolean emptyArea = true;

    public LayoutArea(int pageNumber, Rectangle bBox) {
        this.pageNumber = pageNumber;
        this.bBox = bBox;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Rectangle getBBox() {
        return bBox;
    }

    public void setBBox(Rectangle bbox) {
        this.bBox = bbox;
    }

    public boolean isEmptyArea() {
        return emptyArea;
    }

    public void setEmptyArea(boolean emptyArea) {
        this.emptyArea = emptyArea;
    }

    @Override
    public LayoutArea clone() {
        LayoutArea area = new LayoutArea(pageNumber, bBox.clone());
        area.setEmptyArea(emptyArea);
        return area;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LayoutArea))
            return false;
        LayoutArea that = (LayoutArea) obj;
        return pageNumber == that.pageNumber && bBox.equals(that.bBox);
    }
}

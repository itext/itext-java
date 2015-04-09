package com.itextpdf.model.layout;

import com.itextpdf.core.geom.Rectangle;

public class LayoutArea implements Cloneable {

    protected int pageNumber;
    protected Rectangle bBox;

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

    @Override
    public LayoutArea clone() {
        return new LayoutArea(pageNumber, bBox.clone());
    }
}

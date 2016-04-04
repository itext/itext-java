package com.itextpdf.layout.layout;

import com.itextpdf.io.util.HashCode;
import com.itextpdf.kernel.geom.Rectangle;

import java.text.MessageFormat;

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

    @Override
    public int hashCode() {
        HashCode hashCode = new HashCode();
        hashCode.append(pageNumber).
                append(bBox.hashCode()).
                append(emptyArea);
        return hashCode.hashCode();
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}, page {1}", bBox.toString(), pageNumber);
    }
}

package com.itextpdf.layout.layout;

public class LayoutContext {

    protected LayoutArea area;

    public LayoutContext(LayoutArea area) {
        this.area = area;
    }

    public LayoutArea getArea() {
        return area;
    }

    @Override
    public String toString() {
        return area.toString();
    }
}

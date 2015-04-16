package com.itextpdf.model.layout;

public class LayoutContext {

    protected LayoutArea area;

    public LayoutContext(LayoutArea area) {
        this.area = area;
    }

    public LayoutArea getArea() {
        return area;
    }
}

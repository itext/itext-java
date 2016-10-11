package com.itextpdf.layout.property;


public class HeightProperty {
    protected HeightPropertyType type;
    protected float height;

    public HeightProperty(HeightPropertyType type, float height) {
        this.type = type;
        this.height = height;
    }
}

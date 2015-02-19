package com.itextpdf.canvas.image;

public class MetaObject {

    public static final int META_NOT_SUPPORTED = 0;
    public static final int META_PEN = 1;
    public static final int META_BRUSH = 2;
    public static final int META_FONT = 3;
    public int type = META_NOT_SUPPORTED;

    public MetaObject() {
    }

    public MetaObject(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }

}

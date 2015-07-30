package com.itextpdf.canvas.image;

import com.itextpdf.canvas.color.Color;
import com.itextpdf.canvas.color.DeviceRgb;

import java.io.IOException;

public class MetaPen extends MetaObject {

    public static final int PS_SOLID = 0;
    public static final int PS_DASH = 1;
    public static final int PS_DOT = 2;
    public static final int PS_DASHDOT = 3;
    public static final int PS_DASHDOTDOT = 4;
    public static final int PS_NULL = 5;
    public static final int PS_INSIDEFRAME = 6;

    int style = PS_SOLID;
    int penWidth = 1;
    Color color = DeviceRgb.BLACK;

    public MetaPen() {
        type = META_PEN;
    }

    public void init(InputMeta in) throws IOException {
        style = in.readWord();
        penWidth = in.readShort();
        in.readWord();
        color = in.readColor();
    }
    
    public int getStyle() {
        return style;
    }
    
    public int getPenWidth() {
        return penWidth;
    }
    
    public Color getColor() {
        return color;
    }
}

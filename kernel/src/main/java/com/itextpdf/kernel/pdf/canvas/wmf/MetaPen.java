package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;

import java.io.IOException;

/**
 * A Pen object of the WMF format. Holds the color, style and width information of the pen.
 */
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

    /**
     * Creates a MetaPen object.
     */
    public MetaPen() {
        super(META_PEN);
    }

    /**
     * Initializes a MetaPen object.
     *
     * @param in the InputMeta object that holds the inputstream of the WMF image
     * @throws IOException
     */
    public void init(InputMeta in) throws IOException {
        style = in.readWord();
        penWidth = in.readShort();
        in.readWord();
        color = in.readColor();
    }

    /**
     * Get the style of the MetaPen.
     *
     * @return style of the pen
     */
    public int getStyle() {
        return style;
    }

    /**
     * Get the width of the MetaPen.
     *
     * @return width of the pen
     */
    public int getPenWidth() {
        return penWidth;
    }

    /**
     * Get the color of the MetaPen.
     *
     * @return color of the pen
     */
    public Color getColor() {
        return color;
    }
}

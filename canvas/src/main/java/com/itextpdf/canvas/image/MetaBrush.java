package com.itextpdf.canvas.image;

import com.itextpdf.core.color.Color;
import com.itextpdf.core.color.DeviceRgb;

import java.io.IOException;

/**
 * A Brush bject that holds information about the style, the hatch and the color of
 * the brush.
 */
public class MetaBrush extends MetaObject {

    public static final int BS_SOLID = 0;
    public static final int BS_NULL = 1;
    public static final int BS_HATCHED = 2;
    public static final int BS_PATTERN = 3;
    public static final int BS_DIBPATTERN = 5;
    public static final int HS_HORIZONTAL = 0;
    public static final int HS_VERTICAL = 1;
    public static final int HS_FDIAGONAL = 2;
    public static final int HS_BDIAGONAL = 3;
    public static final int HS_CROSS = 4;
    public static final int HS_DIAGCROSS = 5;

    int style = BS_SOLID;
    int hatch;
    Color color = DeviceRgb.WHITE;

    /**
     * Creates a MetaBrush object.
     */
    public MetaBrush() {
        type = META_BRUSH;
    }

    /**
     * Initializes this MetaBrush object.
     *
     * @param in the InputMeta
     * @throws IOException
     */
    public void init(InputMeta in) throws IOException {
        style = in.readWord();
        color = in.readColor();
        hatch = in.readWord();
    }

    /**
     * Get the style of the MetaBrush.
     *
     * @return style of the brush
     */
    public int getStyle() {
        return style;
    }

    /**
     * Get the hatch pattern of the MetaBrush
     * @return hatch of the brush
     */
    public int getHatch() {
        return hatch;
    }

    /**
     * Get the color of the MetaBrush.
     * @return color of the brush
     */
    public Color getColor() {
        return color;
    }
}

package com.itextpdf.canvas.color;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;

import java.util.Arrays;

public class Color {

    static public final Color Red = new DeviceRgb(0xFF, 0, 0);
    static public final Color Green = new DeviceRgb(0, 0xFF, 0);
    static public final Color Blue = new DeviceRgb(0, 0, 0xFF);
    static public final Color White = new DeviceRgb(0xFF, 0xFF, 0xFF);
    static public final Color Black = new DeviceRgb(0, 0, 0);
    static public final Color Silver = new DeviceRgb(0xC0, 0xC0, 0xC0);
    static public final Color Gray = new DeviceRgb(0xA0, 0xA0, 0xA0);

    protected PdfColorSpace colorSpace;
    protected float[] colorValue;

    public Color(PdfObject pdfObject, float[] colorValue) {
        this(PdfColorSpace.makeColorSpace(pdfObject, null), colorValue);
    }

    public Color(PdfObject pdfObject) {
        this(pdfObject, null);
    }

    public Color(PdfColorSpace colorSpace, float[] colorValue) {
        this.colorSpace = colorSpace;
        if (colorValue == null)
            this.colorValue = new float[colorSpace.getNumOfComponents()];
        else
            this.colorValue = colorValue;
    }

    public static DeviceRgb convertCmykToRgb(DeviceCmyk cmykColor) {
        float cyanComp = 1 - cmykColor.getColorValue()[0];
        float magentaComp = 1 - cmykColor.getColorValue()[1];
        float yellowComp = 1 - cmykColor.getColorValue()[2];
        float blackComp = 1 - cmykColor.getColorValue()[3];

        float r = cyanComp * blackComp;
        float g = magentaComp * blackComp;
        float b = yellowComp * blackComp;
        return new DeviceRgb(r, g, b);
    }

    public static DeviceCmyk convertRgbToCmyk(DeviceRgb rgbColor) {
        float redComp = rgbColor.getColorValue()[0];
        float greenComp = rgbColor.getColorValue()[1];
        float blueComp = rgbColor.getColorValue()[2];

        float k = 1 - Math.max(Math.max(redComp, greenComp), blueComp);
        float c = (1 - redComp - k) / (1 - k);
        float m = (1 - greenComp - k) / (1 - k);
        float y = (1 - blueComp - k) / (1 - k);
        return new DeviceCmyk(c, m, y, k);
    }

    public int getNumOfComponents() {
        return colorValue.length;
    }

    public PdfColorSpace getColorSpace() {
        return colorSpace;
    }

    public float[] getColorValue() {
        return colorValue;
    }

    public void setColorValue(float[] value) {
        colorValue = value;
        if (colorValue.length != value.length)
            throw new PdfException(PdfException.IncorrectNumberOfComponents, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Color color = (Color) o;
        if (colorSpace != null ? !colorSpace.equals(color.colorSpace) : color.colorSpace != null) {
            return false;
        }
        return Arrays.equals(colorValue, color.colorValue);

    }

    @Override
    public int hashCode() {
        int result = colorSpace != null ? colorSpace.hashCode() : 0;
        result = 31 * result + (colorValue != null ? Arrays.hashCode(colorValue) : 0);
        return result;
    }
}

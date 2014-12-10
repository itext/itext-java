package com.itextpdf.canvas.colors;

import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;

public class DeviceRgb extends Color {

    static public final DeviceRgb Red = new DeviceRgb(0xFF, 0, 0);
    static public final DeviceRgb Green = new DeviceRgb(0, 0xFF, 0);
    static public final DeviceRgb Blue = new DeviceRgb(0, 0, 0xFF);
    static public final DeviceRgb White = new DeviceRgb(0xFF, 0xFF, 0xFF);
    static public final DeviceRgb Black = new DeviceRgb();

    public DeviceRgb(int r, int g, int b) {
        this(r / 255f, g / 255f, b / 255f);
    }

    public DeviceRgb(float r, float g, float b) {
        super(new PdfDeviceCs.Rgb(), new float[]{r, g, b});
    }

    public DeviceRgb() {
        this(0, 0, 0);
    }

}

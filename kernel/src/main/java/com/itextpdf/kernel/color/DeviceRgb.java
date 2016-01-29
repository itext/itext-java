package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;

public class DeviceRgb extends Color {

    public DeviceRgb(int r, int g, int b) {
        this(r / 255f, g / 255f, b / 255f);
    }

    public DeviceRgb(float r, float g, float b) {
        super(new PdfDeviceCs.Rgb(), new float[]{r, g, b});
    }

    public DeviceRgb() {
        this(0, 0, 0);
    }

    public static DeviceRgb makeLighter(DeviceRgb rgbColor) {
        float r = rgbColor.getColorValue()[0];
        float g = rgbColor.getColorValue()[1];
        float b = rgbColor.getColorValue()[2];

        float v = Math.max(r, Math.max(g, b));

        if (v == 0f) {
            return new DeviceRgb(0x54, 0x54, 0x54);
        }

        float multiplier = Math.min(1f, v + 0.33f) / v;

        r = multiplier * r;
        g = multiplier * g;
        b = multiplier * b;
        return new DeviceRgb(r, g, b);
    }

    public static DeviceRgb makeDarker(DeviceRgb rgbColor) {
        float r = rgbColor.getColorValue()[0];
        float g = rgbColor.getColorValue()[1];
        float b = rgbColor.getColorValue()[2];

        float v = Math.max(r, Math.max(g, b));

        float multiplier = Math.max(0f, (v - 0.33f) / v);

        r = multiplier * r;
        g = multiplier * g;
        b = multiplier * b;
        return new DeviceRgb(r, g, b);
    }

}

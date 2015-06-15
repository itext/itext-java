package com.itextpdf.canvas.color;

import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;

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

}

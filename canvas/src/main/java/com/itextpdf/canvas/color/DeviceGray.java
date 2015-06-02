package com.itextpdf.canvas.color;

import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;

public class DeviceGray extends Color {

    public static final DeviceGray White = new DeviceGray(1f);
    public static final DeviceGray Gray = new DeviceGray(0.5f);
    public static final DeviceGray Black = new DeviceGray();

    public DeviceGray(float value) {
        super(new PdfDeviceCs.Gray(), new float[] {value});
    }

    public DeviceGray() {
        this(0);
    }

}

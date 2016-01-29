package com.itextpdf.kernel.color;

import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;

public class DeviceGray extends Color {

    static final public DeviceGray WHITE = new DeviceGray(1f);
    static final public DeviceGray GRAY = new DeviceGray(.5f);
    static final public DeviceGray BLACK = new DeviceGray();

    public DeviceGray(float value) {
        super(new PdfDeviceCs.Gray(), new float[] {value});
    }

    public DeviceGray() {
        this(0);
    }

    public static DeviceGray makeLighter(DeviceGray grayColor) {
        float v = grayColor.getColorValue()[0];

        if (v == 0f)
            return new DeviceGray(0.3f);

        float multiplier = Math.min(1f, v + 0.33f) / v;

        return new DeviceGray(v * multiplier);
    }

    public static DeviceGray makeDarker(DeviceGray grayColor) {
        float v = grayColor.getColorValue()[0];
        float multiplier = Math.max(0f, (v - 0.33f) / v);

        return new DeviceGray(v * multiplier);
    }

}

package com.itextpdf.canvas.color;

import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;

public class DeviceCmyk extends Color {

    static public final DeviceCmyk Cyan = new DeviceCmyk(100, 0, 0, 0);
    static public final DeviceCmyk Magenta = new DeviceCmyk(0, 100, 0, 0);
    static public final DeviceCmyk Yellow = new DeviceCmyk(0, 0, 100, 0);
    static public final DeviceCmyk Black = new DeviceCmyk(0, 0, 0, 100);

    public DeviceCmyk(int c, int m, int y, int k) {
        this(c / 100f, m / 100f, y / 100f, k / 100f);
    }

    public DeviceCmyk(float c, float m, float y, float k) {
        super(new PdfDeviceCs.Cmyk(), new float[]{c, m, y, k});
    }

}

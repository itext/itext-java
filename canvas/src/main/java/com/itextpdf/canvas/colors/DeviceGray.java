package com.itextpdf.canvas.colors;

import com.itextpdf.core.pdf.objects.PdfObject;

public class DeviceGray extends DeviceColor {

    static final public DeviceGray White = new DeviceGray(1f);
    static final public DeviceGray Black = new DeviceGray(0f);

    public DeviceGray(PdfObject object) {
        super(object);
    }

    public DeviceGray(float value) {

    }

    public DeviceGray() {
        this(0);
    }

}

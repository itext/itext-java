package com.itextpdf.canvas.colors;

public class DeviceRgb extends DeviceColor {

    static public final DeviceRgb Red = new DeviceRgb(0xFF, 0, 0);
    static public final DeviceRgb Green = new DeviceRgb(0, 0xFF, 0);
    static public final DeviceRgb Blue = new DeviceRgb(0, 0, 0xFF);
    static public final DeviceRgb White = new DeviceRgb(0xFF, 0xFF, 0xFF);
    static public final DeviceRgb Black = new DeviceRgb(0, 0, 0);


    public DeviceRgb(int r, int g, int b) {

    }

    public DeviceRgb() {
        this(0, 0, 0);
    }

}

package com.itextpdf.kernel.color;

/**
 * Class containing predefined {@link DeviceRgb} colors.
 * Color space specific classes should be used for the advanced handling of colors.
 * The most common ones are {@link DeviceGray}, {@link DeviceCmyk} and {@link DeviceRgb}.
 */
public class ColorConstants {
    /**
     * Predefined black DeviceRgb color
     */
    public static final Color BLACK = new DeviceRgb(0, 0, 0);
    /**
     * Predefined blue  DeviceRgb color
     */
    public static final Color BLUE = new DeviceRgb(0, 0, 255);
    /**
     * Predefined cyan DeviceRgb color
     */
    public static final Color CYAN = new DeviceRgb(0, 255, 255);
    /**
     * Predefined dark gray DeviceRgb color
     */
    public static final Color DARK_GRAY = new DeviceRgb(64, 64, 64);
    /**
     * Predefined gray DeviceRgb color
     */
    public static final Color GRAY = new DeviceRgb(128, 128, 128);
    /**
     * Predefined green DeviceRgb color
     */
    public static final Color GREEN = new DeviceRgb(0, 255, 0);
    /**
     * Predefined light gray DeviceRgb color
     */
    public static final Color LIGHT_GRAY = new DeviceRgb(192, 192, 192);
    /**
     * Predefined magenta DeviceRgb color
     */
    public static final Color MAGENTA = new DeviceRgb(255, 0, 255);
    /**
     * Predefined orange DeviceRgb color
     */
    public static final Color ORANGE = new DeviceRgb(255, 200, 0);
    /**
     * Predefined pink DeviceRgb color
     */
    public static final Color PINK = new DeviceRgb(255, 175, 175);
    /**
     * Predefined red DeviceRgb color
     */
    public static final Color RED = new DeviceRgb(255, 0, 0);
    /**
     * Predefined white DeviceRgb color
     */
    public static final Color WHITE = new DeviceRgb(255, 255, 255);
    /**
     * Predefined yellow DeviceRgb color
     */
    public static final Color YELLOW = new DeviceRgb(255, 255, 0);
}

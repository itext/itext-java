package com.itextpdf.io.font.constants;

/**
 * Font descriptor flags
 */
public final class FontDescriptorFlags {
    private FontDescriptorFlags() {
    }

    public static int FixedPitch = 1;
    public static int Serif = 1 << 1;
    public static int Symbolic = 1 << 2;
    public static int Script = 1 << 3;
    public static int Nonsymbolic = 1 << 5;
    public static int Italic = 1 << 6;
    public static int AllCap = 1 << 16;
    public static int SmallCap = 1 << 17;
    public static int ForceBold = 1 << 18;
}

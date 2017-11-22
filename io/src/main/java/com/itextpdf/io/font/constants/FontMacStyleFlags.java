package com.itextpdf.io.font.constants;

/**
 * Represents Open Type head.macStyle bits.
 * <br/>
 * https://www.microsoft.com/typography/otspec/head.htm
 */
public final class FontMacStyleFlags {

    private FontMacStyleFlags() {
    }

    // Bit 0: Bold (if set to 1);
    public static final int BOLD = 1;
    
    // Bit 1: Italic (if set to 1)
    public static final int ITALIC = 2;
    
    // Bit 2: Underline (if set to 1)
    public static final int UNDERLINE = 4;
    
    // Bit 3: Outline (if set to 1)
    public static final int OUTLINE = 8;
    
    // Bit 4: Shadow (if set to 1)
    public static final int SHADOW = 16;
    
    // Bit 5: Condensed (if set to 1)
    public static final int CONDENSED = 32;
    
    // Bit 6: Extended (if set to 1)
    public static final int EXTENDED = 64;
}

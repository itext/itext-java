package com.itextpdf.io.font.constants;

public final class FontWidths {

    private FontWidths() {
    }

    private static final int FWIDTH_ULTRA_CONDENSED = 1;
    private static final int FWIDTH_EXTRA_CONDENSED	= 2;
    private static final int FWIDTH_CONDENSED = 3;
    private static final int FWIDTH_SEMI_CONDENSED = 4;
    private static final int FWIDTH_NORMAL = 5;
    private static final int FWIDTH_SEMI_EXPANDED = 6;
    private static final int FWIDTH_EXPANDED = 7;
    private static final int FWIDTH_EXTRA_EXPANDED = 8;
    private static final int FWIDTH_ULTRA_EXPANDED = 9;


    public static final String ULTRA_CONDENSED = "ultra-condensed";

    public static final String EXTRA_CONDENSED = "extra-condensed";

    public static final String CONDENSED = "condensed";

    public static final String SEMI_CONDENSED = "semi-condensed";

    public static final String NORMAL = "normal";

    public static final String SEMI_EXPANDED = "semi-expanded";

    public static final String EXPANDED = "expanded";

    public static final String EXTRA_EXPANDED = "extra-expanded";

    public static final String ULTRA_EXPANDED = "ultra-expanded";

    /**
     * Convert from Type 1 font width notation
     * @param fontWidth Type 1 font width.
     * @return one of the {@link FontWidths} constants.
     */
    public static String fromType1FontWidth(String fontWidth) {
        fontWidth = fontWidth.toLowerCase();
        String fontWidthValue = NORMAL;
        switch (fontWidth) {
            case "ultracondensed":
                fontWidthValue = ULTRA_CONDENSED;
                break;
            case "extracondensed":
                fontWidthValue = EXTRA_CONDENSED;
                break;
            case "condensed":
                fontWidthValue = CONDENSED;
                break;
            case "semicondensed":
                fontWidthValue = SEMI_CONDENSED;
                break;
            case "normal":
                fontWidthValue = NORMAL;
                break;
            case "semiexpanded":
                fontWidthValue = SEMI_CONDENSED;
                break;
            case "expanded":
                fontWidthValue = EXPANDED;
                break;
            case "extraexpanded":
                fontWidthValue = EXTRA_CONDENSED;
                break;
            case "ultraexpanded":
                fontWidthValue = ULTRA_CONDENSED;
                break;
        }
        return fontWidthValue;
    }

    /**
     * Convert from Open Type font width notation.
     * <br/>
     * https://www.microsoft.com/typography/otspec/os2.htm#wdc
     *
     * @param fontWidth Open Type font width.
     * @return one of the {@link FontWidths} constants.
     */
    public static String fromOpenTypeFontWidth(int fontWidth) {
        String fontWidthValue = NORMAL;
        switch (fontWidth) {
            case FWIDTH_ULTRA_CONDENSED:
                fontWidthValue = ULTRA_CONDENSED;
                break;
            case FWIDTH_EXTRA_CONDENSED:
                fontWidthValue = EXTRA_CONDENSED;
                break;
            case FWIDTH_CONDENSED:
                fontWidthValue = CONDENSED;
                break;
            case FWIDTH_SEMI_CONDENSED:
                fontWidthValue = SEMI_CONDENSED;
                break;
            case FWIDTH_NORMAL:
                fontWidthValue = NORMAL;
                break;
            case FWIDTH_SEMI_EXPANDED:
                fontWidthValue = SEMI_EXPANDED;
                break;
            case FWIDTH_EXPANDED:
                fontWidthValue = EXPANDED;
                break;
            case FWIDTH_EXTRA_EXPANDED:
                fontWidthValue = EXTRA_EXPANDED;
                break;
            case FWIDTH_ULTRA_EXPANDED:
                fontWidthValue = ULTRA_EXPANDED;
                break;
        }
        return fontWidthValue;
    }
}

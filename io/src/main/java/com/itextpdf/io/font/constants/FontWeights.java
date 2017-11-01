package com.itextpdf.io.font.constants;

public final class FontWeights {

    private FontWeights() {
    }

    // Font weight Thin
    public static final int THIN = 100;

    // Font weight Extra-light (Ultra-light)
    public static final int EXTRA_LIGHT = 200;

    // Font weight Light
    public static final int LIGHT = 300;

    // Font weight Normal
    public static final int NORMAL = 400;

    // Font weight Medium
    public static final int MEDIUM = 500;

    // Font weight Semi-bold
    public static final int SEMI_BOLD = 600;

    // Font weight Bold
    public static final int BOLD = 700;

    // Font weight Extra-bold (Ultra-bold)
    public static final int EXTRA_BOLD = 800;

    // Font weight Black (Heavy)
    public static final int BLACK = 900;

    public static int fromType1FontWeight(String weight) {
        int fontWeight = NORMAL;
        switch (weight.toLowerCase()) {
            case "ultralight":
                fontWeight = THIN;
                break;
            case "thin":
            case "extralight":
                fontWeight = EXTRA_LIGHT;
                break;
            case "light":
                fontWeight = LIGHT;
                break;
            case "book":
            case "regular":
            case "normal":
                fontWeight = NORMAL;
                break;
            case "medium":
                fontWeight = MEDIUM;
                break;
            case "demibold":
            case "semibold":
                fontWeight = SEMI_BOLD;
                break;
            case "bold":
                fontWeight = BOLD;
                break;
            case "extrabold":
            case "ultrabold":
                fontWeight = EXTRA_BOLD;
                break;
            case "heavy":
            case "black":
            case "ultra":
            case "ultrablack":
                fontWeight = BLACK;
                break;
            case "fat":
            case "extrablack":
                fontWeight = 1000;
                break;
        }
        return fontWeight;
    }
}

package com.itextpdf.basics.font;

import java.util.HashMap;
import java.util.List;

public class FontNames {

    //macStyle bits
    // Bit 0: Bold (if set to 1);
    private static final int BOLD_FLAG = 1;
    // Bit 1: Italic (if set to 1)
    private static final int ITALIC_FLAG = 2;
    // Bit 2: Underline (if set to 1)
    private static final int UNDERLINE_FLAG = 4;
    // Bit 3: Outline (if set to 1)
    private static final int OUTLINE_FLAG = 8;
    // Bit 4: Shadow (if set to 1)
    private static final int SHADOW_FLAG = 16;
    // Bit 5: Condensed (if set to 1)
    private static final int CONDENSED_FLAG = 32;
    // Bit 6: Extended (if set to 1)
    private static final int EXTENDED_FLAG = 64;

    // Font weight Thin
    private static final int FW_THIN = 100;
    // Font weight Extra-light (Ultra-light)
    private static final int FW_EXTRALIGHT = 200;
    // Font weight Light
    private static final int FW_LIGHT = 300;
    // Font weight Normal
    private static final int FW_NORMAL = 400;
    // Font weight Medium
    private static final int FW_MEDIUM = 500;
    // Font weight Semi-bold
    private static final int FW_SEMIBOLD = 600;
    // Font weight Bold
    private static final int FW_BOLD = 700;
    // Font weight Extra-bold (Ultra-bold)
    private static final int FW_EXTRABOLD = 800;
    // Font weight Black (Heavy)
    private static final int FW_BLACK = 900;


    // Font width Ultra-condensed, 50%
    private static final int FWIDTH_ULTRA_CONDENSED = 1;
    // Font width Extra-condensed, 62.5%
    private static final int FWIDTH_EXTRA_CONDENSED	= 2;
    // Font width Condensed, 75%
    private static final int FWIDTH_CONDENSED = 3;
    // Font width Semi-condensed, 87.5%
    private static final int FWIDTH_SEMI_CONDENSED = 4;
    // Font width Medium (normal), 100%
    private static final int FWIDTH_NORMAL = 5;
    // Font width Semi-expanded, 112.5%
    private static final int FWIDTH_SEMI_EXPANDED = 6;
    // Font width Expanded, 125%
    private static final int FWIDTH_EXPANDED = 7;
    // Font width Extra-expanded, 150%
    private static final int FWIDTH_EXTRA_EXPANDED = 8;
    // Font width Ultra-expanded, 200%
    private static final int FWIDTH_ULTRA_EXPANDED = 9;

    protected HashMap<Integer, List<String[]>> allNames;

    // name, ID = 4
    private String[][] fullName;
    // name, ID = 1 or 16
    private String[][] familyName;
    // name, ID = 2 or 17
    private String[][] subfamily;
    //name, ID = 6
    private String fontName;
    // name, ID = 2
    private String style = "";
    // name, ID = 20
    private String cidFontName;
    // os/2.usWeightClass
    private int weight = FW_NORMAL;
    // os/2.usWidthClass
    private int width = FWIDTH_NORMAL;
    // head.macStyle
    private int macStyle;
    // os/2.fsType != 2
    private boolean allowEmbedding;

    /**
     * Extracts the names of the font in all the languages available.
     *
     * @param id the name id to retrieve in OpenType notation
     * @return not empty {@code String[][]} if any names exists, otherwise {@code null}.
     */
    public String[][] getNames(int id) {
        List<String[]> names = allNames.get(id);
        return names != null && names.size() > 0 ? listToArray(names) : null;
    }

    public String[][] getFullName() {
        return fullName;
    }

    public String getFontName() {
        return fontName;
    }

    public String getCidFontName() {
        return cidFontName;
    }

    public String[][] getFamilyName() {
        return familyName;
    }

    public String getStyle() {
        return style;
    }

    public String getSubfamily() {
        return subfamily != null ? subfamily[0][3] : "";
    }

    public int getFontWeight() {
        return weight;
    }

    public int getFontWidth() {
        return width;
    }

    public boolean allowEmbedding() {
        return allowEmbedding;
    }

    public boolean isBold() {
        return (macStyle & BOLD_FLAG) != 0;
    }

    public boolean isItalic() {
        return (macStyle & ITALIC_FLAG) != 0;
    }

    public boolean isUnderline() {
        return (macStyle & UNDERLINE_FLAG) != 0;
    }

    public boolean isOutline() {
        return (macStyle & OUTLINE_FLAG) != 0;
    }

    public boolean isShadow() {
        return (macStyle & SHADOW_FLAG) != 0;
    }

    public boolean isCondensed() {
        return (macStyle & CONDENSED_FLAG) != 0;
    }

    public boolean isExtended() {
        return (macStyle & EXTENDED_FLAG) != 0;
    }

    protected void setAllNames(HashMap<Integer, List<String[]>> allNames) {
        this.allNames = allNames;
    }

    protected void setFullName(String[][] fullName) {
        this.fullName = fullName;
    }

    protected void setFullName(String fullName) {
        this.fullName = new String[][]{new String[]{"", "", "", fullName}};
    }

    // todo change to protected!
    public void setFontName(String psFontName) {
        this.fontName = psFontName;
    }

    protected void setCidFontName(String cidFontName) {
        this.cidFontName = cidFontName;
    }

    protected void setFamilyName(String[][] familyName) {
        this.familyName = familyName;
    }

    protected void setFamilyName(String familyName) {
        this.familyName = new String[][]{new String[]{"", "", "", familyName}};
    }

    protected void setStyle(String style) {
        this.style = style;
    }

    protected void setSubfamily(String subfamily) {
        this.subfamily = new String[][]{new String[]{"", "", "", subfamily}};
    }

    protected void setSubfamily(String[][] subfamily) {
        this.subfamily = subfamily;
    }

    protected void setWeight(int weight) {
        this.weight = weight;
    }

    protected void setWidth(int width) {
        this.width = width;
    }

    protected void setMacStyle(int macStyle) {
        this.macStyle = macStyle;
    }

    protected void setAllowEmbedding(boolean allowEmbedding) {
        this.allowEmbedding = allowEmbedding;
    }

    protected static int convertFontWeight(String weight) {
        String fontWeight = weight.toLowerCase();
        switch (fontWeight) {
            case "ultralight":
                return 100;
            case "thin":
            case "extralight":
                return 200;
            case "light":
                return 300;
            case "book":
            case "regular":
            case "normal":
                return 400;
            case "medium":
                return 500;
            case "demibold":
            case "semibold":
                return 600;
            case "bold":
                return 700;
            case "extrabold":
            case "ultrabold":
                return 800;
            case "heavy":
            case "black":
            case "ultra":
            case "ultrablack":
                return 900;
            case "fat":
            case "extrablack":
                return 1000;
            default:
                return 400;
        }
    }

    private String[][] listToArray(List<String[]> list) {
        String[][] array = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}

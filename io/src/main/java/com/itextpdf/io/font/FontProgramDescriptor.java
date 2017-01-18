package com.itextpdf.io.font;

public class FontProgramDescriptor {

    private String fontName;

    private String style = "";
    private int macStyle;
    private int weight = FontNames.FW_NORMAL;
    private float italicAngle = 0;
    private boolean isMonospace;

    private String fullNameLowerCase;
    private String fontNameLowerCase;
    private String familyNameLowerCase;

    FontProgramDescriptor(FontNames fontNames, float italicAngle, boolean isMonospace) {
        this.fontName = fontNames.getFontName();
        this.fontNameLowerCase = this.fontName.toLowerCase();
        this.fullNameLowerCase = fontNames.getFullName()[0][3].toLowerCase();
        this.familyNameLowerCase = fontNames.getFamilyName() != null && fontNames.getFamilyName()[0][3] != null ? fontNames.getFamilyName()[0][3].toLowerCase() : null;
        this.style = fontNames.getStyle();
        this.weight = fontNames.getFontWeight();
        this.macStyle = fontNames.getMacStyle();
        this.italicAngle = italicAngle;
        this.isMonospace = isMonospace;
    }

    FontProgramDescriptor(FontNames fontNames, FontMetrics fontMetrics) {
        this(fontNames, fontMetrics.getItalicAngle(), fontMetrics.isFixedPitch());
    }

    public String getFontName() {
        return fontName;
    }

    public String getStyle() {
        return style;
    }

    public int getFontWeight() {
        return weight;
    }

    public float getItalicAngle() {
        return italicAngle;
    }

    public boolean isMonospace() {
        return isMonospace;
    }

    public boolean isBold() {
        return (macStyle & FontNames.BOLD_FLAG) != 0;
    }

    public boolean isItalic() {
        return (macStyle & FontNames.ITALIC_FLAG) != 0;
    }

    public String getFullNameLowerCase() {
        return fullNameLowerCase;
    }

    public String getFontNameLowerCase() {
        return fontNameLowerCase;
    }

    public String getFamilyNameLowerCase() {
        return familyNameLowerCase;
    }

    void setItalicAngle(float italicAngle) {
        this.italicAngle = italicAngle;
    }

    void setMonospace(boolean monospace) {
        isMonospace = monospace;
    }
}

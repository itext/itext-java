package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontWeight;

public final class FontCharacteristics {
    private boolean isItalic = false;
    private boolean isBold = false;
    private short fontWeight = 400;
    private boolean undefined = true;

    public FontCharacteristics setFontWeight(FontWeight fw) {
        this.fontWeight = FontCharacteristicsUtils.calculateFontWeightNumber(fw);
        modified();
        return this;
    }

    public FontCharacteristics setFontWeight(short fw) {
        if (fw > 0) {
            this.fontWeight = FontCharacteristicsUtils.normalizeFontWeight(fw);
            modified();
        }
        return this;
    }

    public FontCharacteristics setFontWeight(String fw) {
        return setFontWeight(FontCharacteristicsUtils.parseFontWeight(fw));
    }

    public FontCharacteristics setBoldFlag(boolean isBold) {
        this.isBold = isBold;
        if (this.isBold) modified();
        return this;
    }

    public FontCharacteristics setItalicFlag(boolean isItalic) {
        this.isItalic = isItalic;
        if (this.isItalic) modified();
        return this;
    }

    /**
     * Set font style
     * @param fs shall be 'normal', 'italic' or 'oblique'.
     */
    public FontCharacteristics setFontStyle(String fs) {
        if (fs != null && fs.length() > 0) {
            fs = fs.trim().toLowerCase();
            if (fs.equals("normal")) {
                isItalic = false;
            } else if (fs.equals("italic") || fs.equals("oblique")) {
                isItalic = true;
            }
        }
        if (isItalic) modified();
        return this;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public boolean isBold() {
        return isBold || fontWeight > 600;
    }

    public short getFontWeightNumber() {
        return fontWeight;
    }

    public FontWeight getFontWeight() {
        return FontCharacteristicsUtils.calculateFontWeight(fontWeight);
    }

    public boolean isUndefined() {
        return undefined;
    }

    private void modified() {
        undefined = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FontCharacteristics that = (FontCharacteristics) o;

        return isItalic == that.isItalic
                && isBold == that.isBold
                && fontWeight == that.fontWeight;
    }

    @Override
    public int hashCode() {
        int result = (isItalic ? 1 : 0);
        result = 31 * result + (isBold ? 1 : 0);
        result = 31 * result + (int) fontWeight;
        return result;
    }
}

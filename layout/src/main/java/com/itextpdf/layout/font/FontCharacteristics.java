/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.font;

public final class FontCharacteristics {

    private boolean isItalic = false;
    private boolean isBold = false;
    private short fontWeight = 400;
    private boolean undefined = true;
    private boolean isMonospace = false;

    public FontCharacteristics() {
    }

    public FontCharacteristics(FontCharacteristics other) {
        this();
        this.isItalic = other.isItalic;
        this.isBold = other.isBold;
        this.fontWeight = other.fontWeight;
        this.undefined = other.undefined;
    }

    /**
     * Sets preferred font weight
     *
     * @param fw font weight in css notation.
     * @see com.itextpdf.io.font.constants.FontWeights
     * @return this instance.
     */
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

    public FontCharacteristics setMonospaceFlag(boolean isMonospace) {
        this.isMonospace = isMonospace;
        if (this.isMonospace) modified();
        return this;
    }

    /**
     * Set font style
     * @param fs shall be 'normal', 'italic' or 'oblique'.
     * @return this element
     */
    public FontCharacteristics setFontStyle(String fs) {
        if (fs != null && fs.length() > 0) {
            fs = fs.trim().toLowerCase();
            if ("normal".equals(fs)) {
                isItalic = false;
            } else if ("italic".equals(fs) || "oblique".equals(fs)) {
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
        return isBold || fontWeight > 500;
    }

    public boolean isMonospace() {
        return isMonospace;
    }

    public short getFontWeight() {
        return fontWeight;
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

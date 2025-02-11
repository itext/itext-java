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
package com.itextpdf.io.font;

import com.itextpdf.io.font.constants.FontMacStyleFlags;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.constants.FontStretches;

import java.util.List;
import java.util.Map;

public class FontNames {


    protected Map<Integer, List<String[]>> allNames;

    // name, ID = 4
    private String[][] fullName;
    // name, ID = 16 or 1
    private String[][] familyName;
    // name, ID = 1
    private String[][] familyName2;
    // name, ID = 2 or 17
    private String[][] subfamily;
    //name, ID = 6
    private String fontName;
    // name, ID = 2
    private String style = "";
    // name, ID = 20
    private String cidFontName;
    // os/2.usWeightClass
    private int weight = FontWeights.NORMAL;
    // os/2.usWidthClass
    private String fontStretch = FontStretches.NORMAL;
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

    /**
     * Get extra family name if exists.
     *
     * @return extra family name if exists in the font, {@code null} otherwise.
     */
    public String[][] getFamilyName2() {
        return familyName2;
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

    /**
     * Sets font weight.
     * @param weight integer form 100 to 900. See {@link FontWeights}.
     */
    protected void setFontWeight(int weight) {
        this.weight = FontWeights.normalizeFontWeight(weight);
    }

    /**
     * Gets font stretch in css notation (font-stretch property).
     *
     * @return One of {@link FontStretches} values.
     */
    public String getFontStretch() {
        return fontStretch;
    }

    /**
     * Sets font stretch in css notation (font-stretch property).
     *
     * @param fontStretch {@link FontStretches}.
     */
    protected void setFontStretch(String fontStretch) {
        this.fontStretch = fontStretch;
    }

    public boolean allowEmbedding() {
        return allowEmbedding;
    }

    public boolean isBold() {
        return (macStyle & FontMacStyleFlags.BOLD) != 0;
    }

    public boolean isItalic() {
        return (macStyle & FontMacStyleFlags.ITALIC) != 0;
    }

    public boolean isUnderline() {
        return (macStyle & FontMacStyleFlags.UNDERLINE) != 0;
    }

    public boolean isOutline() {
        return (macStyle & FontMacStyleFlags.OUTLINE) != 0;
    }

    public boolean isShadow() {
        return (macStyle & FontMacStyleFlags.SHADOW) != 0;
    }

    public boolean isCondensed() {
        return (macStyle & FontMacStyleFlags.CONDENSED) != 0;
    }

    public boolean isExtended() {
        return (macStyle & FontMacStyleFlags.EXTENDED) != 0;
    }

    protected void setAllNames(Map<Integer, List<String[]>> allNames) {
        this.allNames = allNames;
    }

    protected void setFullName(String[][] fullName) {
        this.fullName = fullName;
    }

    protected void setFullName(String fullName) {
        this.fullName = new String[][]{new String[]{"", "", "", fullName}};
    }

    protected void setFontName(String psFontName) {
        this.fontName = psFontName;
    }

    protected void setCidFontName(String cidFontName) {
        this.cidFontName = cidFontName;
    }

    protected void setFamilyName(String[][] familyName) {
        this.familyName = familyName;
    }

    /**
     * Set extra family name used for better fonts match.
     *
     * @param familyName2 family name to set.
     */
    protected void setFamilyName2(String[][] familyName2) {
        this.familyName2 = familyName2;
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

    /**
     * Sets Open Type head.macStyle.
     * <p>
     * {@link FontMacStyleFlags}
     * @param macStyle macStyle flag
     */
    protected void setMacStyle(int macStyle) {
        this.macStyle = macStyle;
    }

    protected int getMacStyle() {
        return macStyle;
    }

    protected void setAllowEmbedding(boolean allowEmbedding) {
        this.allowEmbedding = allowEmbedding;
    }

    private String[][] listToArray(List<String[]> list) {
        String[][] array = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    @Override
    public String toString() {
        String name = getFontName();
        return name != null && name.length() > 0 ? name : super.toString();
    }
}

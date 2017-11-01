/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.io.font;

import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.constants.FontWidths;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FontNames implements Serializable {

    private static final long serialVersionUID = 1005168842463622025L;

    //macStyle bits
    // Bit 0: Bold (if set to 1);
    protected static final int BOLD_FLAG = 1;
    // Bit 1: Italic (if set to 1)
    protected static final int ITALIC_FLAG = 2;
    // Bit 2: Underline (if set to 1)
    protected static final int UNDERLINE_FLAG = 4;
    // Bit 3: Outline (if set to 1)
    protected static final int OUTLINE_FLAG = 8;
    // Bit 4: Shadow (if set to 1)
    protected static final int SHADOW_FLAG = 16;
    // Bit 5: Condensed (if set to 1)
    protected static final int CONDENSED_FLAG = 32;
    // Bit 6: Extended (if set to 1)
    protected static final int EXTENDED_FLAG = 64;

    protected Map<Integer, List<String[]>> allNames;

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
    private int weight = FontWeights.NORMAL;
    // os/2.usWidthClass
    private String width = FontWidths.NORMAL;
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

    public void setFontWeight(int weight) {
        this.weight = weight;
    }

    public String getFontWidth() {
        return width;
    }

    /**
     * Sets font width in css notation (font-stretch property).
     *
     * @param width {@link com.itextpdf.io.font.constants.FontWidths}.
     */
    public void setFontWidth(String width) {
        this.width = width;
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
        return name.length() > 0 ? name : super.toString();
    }
}

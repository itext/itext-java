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

import java.util.HashSet;
import java.util.Set;

/**
 * Base font descriptor.
 */
public class FontProgramDescriptor {

    private final String fontName;

    private final String fullNameLowerCase;
    private final String fontNameLowerCase;
    private final String familyNameLowerCase;
    private final String familyName2LowerCase;

    private final String style;
    private final int macStyle;
    private final int weight;
    private final float italicAngle;
    private final boolean isMonospace;

    private final Set<String> fullNamesAllLangs;
    private final Set<String> fullNamesEnglishOpenType;
    private final String familyNameEnglishOpenType;

    // Initially needed for open type fonts only.
    // The following sequence represents four triplets.
    // In each triplet items sequentially stand for platformID encodingID languageID (see open type naming table spec).
    // Each triplet is used further to determine whether the font name item is represented in English
    private static final String[] TT_FAMILY_ORDER = {
            "3", "1", "1033",
            "3", "0", "1033",
            "1", "0", "0",
            "0", "3", "0"
    };

    FontProgramDescriptor(FontNames fontNames, float italicAngle, boolean isMonospace) {
        this.fontName = fontNames.getFontName();
        this.fontNameLowerCase = this.fontName.toLowerCase();
        this.fullNameLowerCase = fontNames.getFullName()[0][3].toLowerCase();
        this.familyNameLowerCase = fontNames.getFamilyName() != null && fontNames.getFamilyName()[0][3] != null ?
                fontNames.getFamilyName()[0][3].toLowerCase() : null;
        // For font family2 let's take the last element in array. The family in the 1st element has high chance
        // to be the same as returned by getFamilyName. Ideally we should take different families based on OS
        // but it breaks the compatibility, produces different results on different OSs etc.
        String[][] familyName2 = fontNames.getFamilyName2();
        this.familyName2LowerCase = familyName2 != null && familyName2[familyName2.length - 1][3] != null ?
                familyName2[familyName2.length - 1][3].toLowerCase() : null;
        this.style = fontNames.getStyle();
        this.weight = fontNames.getFontWeight();
        this.macStyle = fontNames.getMacStyle();
        this.italicAngle = italicAngle;
        this.isMonospace = isMonospace;
        this.familyNameEnglishOpenType = extractFamilyNameEnglishOpenType(fontNames);
        this.fullNamesAllLangs = extractFullFontNames(fontNames);
        this.fullNamesEnglishOpenType = extractFullNamesEnglishOpenType(fontNames);
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
        return (macStyle & FontMacStyleFlags.BOLD) != 0;
    }

    public boolean isItalic() {
        return (macStyle & FontMacStyleFlags.ITALIC) != 0;
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

    /**
     * Get extra family name if exists.
     *
     * @return extra family name if exists in the font, {@code null} otherwise.
     */
    public String getFamilyName2LowerCase() {
        return familyName2LowerCase;
    }

    public Set<String> getFullNameAllLangs() { return fullNamesAllLangs; }

    public Set<String> getFullNamesEnglishOpenType() { return fullNamesEnglishOpenType; }

    String getFamilyNameEnglishOpenType() { return familyNameEnglishOpenType; }

    private Set<String> extractFullFontNames(FontNames fontNames) {
        Set<String> uniqueFullNames = new HashSet<>();
        for (String[] fullName : fontNames.getFullName())
            uniqueFullNames.add(fullName[3].toLowerCase());
        return uniqueFullNames;
    }

    private String extractFamilyNameEnglishOpenType(FontNames fontNames) {
        if (fontNames.getFamilyName() != null) {
            for (int k = 0; k < TT_FAMILY_ORDER.length; k += 3) {
                for (String[] name : fontNames.getFamilyName()) {
                    if (TT_FAMILY_ORDER[k].equals(name[0]) && TT_FAMILY_ORDER[k + 1].equals(name[1]) && TT_FAMILY_ORDER[k + 2].equals(name[2])) {
                        return name[3].toLowerCase();
                    }
                }
            }
        }
        return null;
    }

    private Set<String> extractFullNamesEnglishOpenType(FontNames fontNames) {
        if (familyNameEnglishOpenType != null) {
            Set<String> uniqueTtfSuitableFullNames = new HashSet<>();
            String[][] names = fontNames.getFullName();
            for (String[] name : names) {
                for (int k = 0; k < TT_FAMILY_ORDER.length; k += 3) {
                    if (TT_FAMILY_ORDER[k].equals(name[0]) && TT_FAMILY_ORDER[k + 1].equals(name[1]) && TT_FAMILY_ORDER[k + 2].equals(name[2])) {
                        uniqueTtfSuitableFullNames.add(name[3]);
                        break;
                    }
                }
            }
            return uniqueTtfSuitableFullNames;
        }
        return new HashSet<>();
    }
}

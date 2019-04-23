/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
        this.familyNameLowerCase = fontNames.getFamilyName() != null && fontNames.getFamilyName()[0][3] != null ? fontNames.getFamilyName()[0][3].toLowerCase() : null;
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

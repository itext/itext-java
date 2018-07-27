/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontProgramDescriptor;

import java.util.*;

/**
 * Sort given set of fonts according to font name and style.
 */
public class FontSelector {

    protected List<FontInfo> fonts;

    /**
     * Create new FontSelector instance.
     * @param allFonts      Unsorted set of all available fonts.
     * @param fontFamilies  Sorted list of preferred font families.
     */
    public FontSelector(Collection<FontInfo> allFonts, List<String> fontFamilies, FontCharacteristics fc) {
        this.fonts = new ArrayList<>(allFonts);
        //Possible issue in .NET, virtual protected member in constructor.
        Collections.sort(this.fonts, getComparator(fontFamilies, fc));
    }

    /**
     * The best font match.
     * If any font from {@link #getFonts()} doesn't contain requested glyphs, this font will be used.
     *
     * @return the best matched font
     */
    public final FontInfo bestMatch() {
        return fonts.get(0); // fonts is sorted best to worst, get(0) returns the best matched FontInfo
    }

    /**
     * Sorted set of fonts.
     * @return sorted set of fonts
     */
    public final Iterable<FontInfo> getFonts() {
        return fonts;
    }

    protected Comparator<FontInfo> getComparator(List<String> fontFamilies, FontCharacteristics fc) {
        return new PdfFontComparator(fontFamilies, fc);
    }

    private static class PdfFontComparator implements Comparator<FontInfo> {
        List<String> fontFamilies;
        List<FontCharacteristics> fontStyles;

        PdfFontComparator(List<String> fontFamilies, FontCharacteristics fc) {
            this.fontFamilies = new ArrayList<>();
            this.fontStyles = new ArrayList<>();
            if (fontFamilies != null && fontFamilies.size() > 0) {
                for (String fontFamily : fontFamilies) {
                    String lowercaseFontFamily = fontFamily.toLowerCase();
                    this.fontFamilies.add(lowercaseFontFamily);
                    this.fontStyles.add(parseFontStyle(lowercaseFontFamily, fc));
                }
            } else {
                this.fontFamilies.add("");
                this.fontStyles.add(fc);
            }
        }

        @Override
        public int compare(FontInfo o1, FontInfo o2) {
            int res = 0;
            for (int i = 0; i < fontFamilies.size() && res == 0; i++) {
                FontCharacteristics fc = fontStyles.get(i);
                String fontName = fontFamilies.get(i);

                if (fontName.equalsIgnoreCase("monospace")) {
                    fc.setMonospaceFlag(true);
                }

                res = characteristicsSimilarity(fontName, fc, o2) - characteristicsSimilarity(fontName, fc, o1);
            }
            return res;
        }

        private static FontCharacteristics parseFontStyle(String fontFamily, FontCharacteristics fc) {
            if (fc == null) {
                fc = new FontCharacteristics();
            }
            if (fc.isUndefined()) {
                if (fontFamily.contains("bold")) {
                    fc.setBoldFlag(true);
                }
                if (fontFamily.contains("italic") || fontFamily.contains("oblique")) {
                    fc.setItalicFlag(true);
                }
            }
            return fc;
        }

        private static int characteristicsSimilarity(String fontName, FontCharacteristics fc, FontInfo fontInfo) {
            boolean isFontBold = fontInfo.getDescriptor().isBold() || fontInfo.getDescriptor().getFontWeight() > 500;
            boolean isFontItalic = fontInfo.getDescriptor().isItalic() || fontInfo.getDescriptor().getItalicAngle() < 0;
            boolean isFontMonospace = fontInfo.getDescriptor().isMonospace();
            int score = 0;
            if (fc.isBold()) {
                if (isFontBold) {
                    score += 5;
                } else {
                    score -= 5;
                }
            } else {
                if (isFontBold) {
                    score -= 3;
                }
            }

            if (fc.isItalic()) {
                if (isFontItalic) {
                    score += 5;
                } else {
                    score -= 5;
                }
            } else {
                if (isFontItalic) {
                    score -= 3;
                }
            }

            if (fc.isMonospace()) {
                if (isFontMonospace) {
                    score += 5;
                } else {
                    score -= 5;
                }
            } else {
                if (isFontMonospace) {
                    score -= 1;
                }
            }

            FontProgramDescriptor descriptor = fontInfo.getDescriptor();
            // Note, aliases are custom behaviour, so in FontSelector will find only exact name,
            // it should not be any 'contains' with aliases.
            boolean checkContains = true;
            if (fontName.equals(descriptor.getFullNameLowerCase())) {
                score += 4;
                checkContains = false;
            }
            if (fontName.equals(descriptor.getFontNameLowerCase())) {
                score += 4;
                checkContains = false;
            }
            if (fontName.equals(fontInfo.getAlias())) {
                score += 4;
                checkContains = false;
            }

            if (checkContains) {
                //yes, we will not find contains for each alias.
                if (descriptor.getFullNameLowerCase().contains(fontName)) score += 3;
                if (descriptor.getFontNameLowerCase().contains(fontName)) score += 3;
            }

            return score;
        }
    }
}

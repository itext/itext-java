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

import com.itextpdf.io.font.FontProgramDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sort given set of fonts according to font name and style.
 */
public class FontSelector {

    protected List<FontInfo> fonts;

    private static final int EXPECTED_FONT_IS_BOLD_AWARD = 5;
    private static final int EXPECTED_FONT_IS_NOT_BOLD_AWARD = 3;
    private static final int EXPECTED_FONT_WEIGHT_IS_EQUALS_AWARD = 1;
    private static final int EXPECTED_FONT_WEIGHT_IS_FAR_AWARD = 1;
    private static final int EXPECTED_FONT_IS_ITALIC_AWARD = 5;
    private static final int EXPECTED_FONT_IS_NOT_ITALIC_AWARD = 3;
    private static final int EXPECTED_FONT_IS_MONOSPACED_AWARD = 5;
    private static final int EXPECTED_FONT_IS_NOT_MONOSPACED_AWARD = 1;

    private static final int FONT_FAMILY_EQUALS_AWARD = 13;

    /**
     * Create new FontSelector instance.
     *
     * @param allFonts     unsorted set of all available fonts.
     * @param fontFamilies sorted list of preferred font families.
     * @param fc           instance of {@link FontCharacteristics}.
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
        // Fonts are sorted best to worst, get(0) returns the best matched FontInfo
        return fonts.get(0);
    }

    /**
     * Sorted set of fonts.
     *
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
                this.fontStyles.add(fc);
            }
        }

        @Override
        public int compare(FontInfo o1, FontInfo o2) {
            int res = 0;
            // It's important to mention that at the FontProvider level we add the default font-family
            // which is to be processed if for all provided font-families the score is 0.
            for (int i = 0; i < fontFamilies.size() && res == 0; i++) {
                FontCharacteristics fc = fontStyles.get(i);
                String fontFamily = fontFamilies.get(i);

                if ("monospace".equalsIgnoreCase(fontFamily)) {
                    fc.setMonospaceFlag(true);
                }
                boolean isLastFontFamilyToBeProcessed = i == fontFamilies.size() - 1;
                res = characteristicsSimilarity(fontFamily, fc, o2, isLastFontFamilyToBeProcessed) -
                        characteristicsSimilarity(fontFamily, fc, o1, isLastFontFamilyToBeProcessed);
                // This method is a fallback to compare family2 field if the main method wasn't able to prioritize
                // the fonts. We don't want to add this into scoring in the main method (characteristicsSimilarity)
                // not to break anything for existing solutions.
                if (res == 0) {
                    res = family2Similarity(fontFamily, fc, o2) - family2Similarity(fontFamily, fc, o1);
                }
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

        /**
         * This method is used to compare two fonts (the required one which is described by fontInfo and
         * the one to be examined which is described by fc and fontFamily) and measure their similarity.
         * The more the fonts are similar the higher the score is.
         * <p>
         * Firstly we check if the font-family described by fontInfo equals to the required one.
         * If it's not true the examination fails, it continues otherwise.
         * If the required font-family is monospace, serif or sans serif we check whether
         * the font under examination is monospace, serif or sans serif resp. Its font-family is not
         * taking into considerations.
         * <p>
         * If font-family is respected, we consider the next font-style characteristics to select the required font
         * of the respected font-family:
         * a) bold
         * b) italic
         */
        // TODO DEVSIX-2120 Update javadoc if necessary
        private static int characteristicsSimilarity(String fontFamily, FontCharacteristics fc, FontInfo fontInfo, boolean isLastFontFamilyToBeProcessed) {
            FontProgramDescriptor fontDescriptor = fontInfo.getDescriptor();
            boolean isFontBold = fontDescriptor.isBold() || fontDescriptor.getFontWeight() > 500;
            boolean isFontItalic = fontDescriptor.isItalic() || fontDescriptor.getItalicAngle() < 0;
            boolean isFontMonospace = fontDescriptor.isMonospace();
            int score = 0;

            // if font-family is monospace, serif or sans-serif, actual font's name shouldn't be checked
            boolean fontFamilySetByCharacteristics = false;

            // check whether we want to select a monospace, TODO DEVSIX-1034 serif or sans-serif font
            if (fc.isMonospace()) {
                fontFamilySetByCharacteristics = true;
                if (isFontMonospace) {
                    score += EXPECTED_FONT_IS_MONOSPACED_AWARD;
                } else {
                    score -= EXPECTED_FONT_IS_MONOSPACED_AWARD;
                }
            } else {
                if (isFontMonospace) {
                    score -= EXPECTED_FONT_IS_NOT_MONOSPACED_AWARD;
                }
            }

            if (!fontFamilySetByCharacteristics) {
                // if alias is set, fontInfo's descriptor should not be checked
                if (!"".equals(fontFamily)
                        && (null == fontInfo.getAlias()
                                && null != fontDescriptor.getFamilyNameLowerCase()
                                && fontDescriptor.getFamilyNameLowerCase().equals(fontFamily)
                        || (null != fontInfo.getAlias() && fontInfo.getAlias().toLowerCase().equals(fontFamily)))) {
                    score += FONT_FAMILY_EQUALS_AWARD;
                } else {
                    if (!isLastFontFamilyToBeProcessed) {
                        return score;
                    }
                }
            }

            // calculate style characteristics
            int maxWeight = Math.max(fontDescriptor.getFontWeight(), fc.getFontWeight());
            int minWeight = Math.min(fontDescriptor.getFontWeight(), fc.getFontWeight());
            if (maxWeight == minWeight) {
                score += EXPECTED_FONT_WEIGHT_IS_EQUALS_AWARD;
            } else if (maxWeight - minWeight >= 300) {
                score -= EXPECTED_FONT_WEIGHT_IS_FAR_AWARD;
            }
            if (fc.isBold()) {
                if (isFontBold) {
                    score += EXPECTED_FONT_IS_BOLD_AWARD;
                } else {
                    score -= EXPECTED_FONT_IS_BOLD_AWARD;
                }
            } else {
                if (isFontBold) {
                    score -= EXPECTED_FONT_IS_NOT_BOLD_AWARD;
                }
            }

            if (fc.isItalic()) {
                if (isFontItalic) {
                    score += EXPECTED_FONT_IS_ITALIC_AWARD;
                } else {
                    score -= EXPECTED_FONT_IS_ITALIC_AWARD;
                }
            } else {
                if (isFontItalic) {
                    score -= EXPECTED_FONT_IS_NOT_ITALIC_AWARD;
                }
            }

            return score;
        }

        /**
         * This method is a fallback to compare family2 field if the main method wasn't able to prioritize the fonts.
         */
        private static int family2Similarity(String fontFamily, FontCharacteristics fc, FontInfo fontInfo) {
            FontProgramDescriptor fontDescriptor = fontInfo.getDescriptor();
            if (!fc.isMonospace() && null == fontInfo.getAlias() &&
                    null != fontDescriptor.getFamilyName2LowerCase() &&
                    fontDescriptor.getFamilyName2LowerCase().equals(fontFamily)) {
                return 1;
            }

            return 0;
        }
    }
}

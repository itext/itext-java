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
package com.itextpdf.layout.font;

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
    private static final int EXPECTED_FONT_IS_ITALIC_AWARD = 5;
    private static final int EXPECTED_FONT_IS_NOT_ITALIC_AWARD = 3;
    private static final int EXPECTED_FONT_IS_MONOSPACED_AWARD = 5;
    private static final int EXPECTED_FONT_IS_NOT_MONOSPACED_AWARD = 1;

    private static final int FONT_FAMILY_EQUALS_AWARD = 13;

    /**
     * Create new FontSelector instance.
     *
     * @param allFonts     Unsorted set of all available fonts.
     * @param fontFamilies Sorted list of preferred font families.
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

                if (fontFamily.equalsIgnoreCase("monospace")) {
                    fc.setMonospaceFlag(true);
                }
                boolean isLastFontFamilyToBeProcessed = i == fontFamilies.size() - 1;
                res = characteristicsSimilarity(fontFamily, fc, o2, isLastFontFamilyToBeProcessed) - characteristicsSimilarity(fontFamily, fc, o1, isLastFontFamilyToBeProcessed);
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
         *
         * Firstly we check if the font-family described by fontInfo equals to the required one.
         * If it's not true the examination fails, it continues otherwise.
         * If the required font-family is monospace, serif or sans serif we check whether
         * the font under examination is monospace, serif or sans serif resp. Its font-family is not
         * taking into considerations.
         *
         * If font-family is respected, we consider the next font-style characteristics to select the required font
         * of the respected font-family:
         * a) bold
         * b) italic
         *
         */
        // TODO DEVSIX-2120 Update javadoc if necessary
        private static int characteristicsSimilarity(String fontFamily, FontCharacteristics fc, FontInfo fontInfo, boolean isLastFontFamilyToBeProcessed) {
            boolean isFontBold = fontInfo.getDescriptor().isBold() || fontInfo.getDescriptor().getFontWeight() > 500;
            boolean isFontItalic = fontInfo.getDescriptor().isItalic() || fontInfo.getDescriptor().getItalicAngle() < 0;
            boolean isFontMonospace = fontInfo.getDescriptor().isMonospace();
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
                if (!"".equals(fontFamily) && (null == fontInfo.getAlias() && fontInfo.getDescriptor().getFamilyNameLowerCase().equals(fontFamily) || (null != fontInfo.getAlias() && fontInfo.getAlias().toLowerCase().equals(fontFamily)))) {
                    score += FONT_FAMILY_EQUALS_AWARD;
                } else {
                    if (!isLastFontFamilyToBeProcessed) {
                        return score;
                    }
                }
            }

            // calculate style characteristics
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
    }
}

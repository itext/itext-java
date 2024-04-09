/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.font.selectorstrategy;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.renderer.TextPreprocessingUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * The class defines complex implementation of {@link IFontSelectorStrategy} which based on the following algorithm:
 * 1. Find first significant symbol (not whitespace or special).
 * 2. Find font which matches symbol according to passed {@link FontSelector}.
 * 3. Try to append as many symbols as possible using the current font.
 * 4. If symbol is not matched to the current font, go to step 1.
 * <p>
 * Algorithm takes care of the case when there is no matched font for symbol or when diacritic
 * from another font is used (previous symbol will be processed by diacritic's font).
 */
public abstract class AbstractFontSelectorStrategy implements IFontSelectorStrategy {
    private final FontProvider fontProvider;
    private final FontSet additionalFonts;
    private final FontSelector fontSelector;

    /**
     * Creates a new instance of {@link AbstractFontSelectorStrategy}.
     *
     * @param fontProvider the font provider
     * @param fontSelector the font selector
     * @param additionalFonts the set of fonts to be used additionally to the fonts added to font provider.
     */
    public AbstractFontSelectorStrategy(FontProvider fontProvider, FontSelector fontSelector,
            FontSet additionalFonts) {

        this.fontProvider = fontProvider;
        this.additionalFonts = additionalFonts;
        this.fontSelector = fontSelector;
    }

    /**
     * If it is necessary to provide a check that the best font for passed symbol equals to the current font.
     * Result of checking is used to split text into parts in case if inequality.
     *
     * @return {@code true} if check is needed, otherwise {@code false}
     */
    protected abstract boolean isCurrentFontCheckRequired();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tuple2<GlyphLine, PdfFont>> getGlyphLines(String text) {
        List<Tuple2<GlyphLine, PdfFont>> result = new ArrayList<>();
        int index = 0;
        int indexDiacritic = -1;

        while (index < text.length()) {
            // Find the best font for first significant symbol
            PdfFont currentFont = null;
            int indexSignificant = nextSignificantIndex(index, text);
            if (indexSignificant < text.length()) {
                int codePoint = extractCodePoint(text, indexDiacritic == -1 ? indexSignificant : indexDiacritic);
                currentFont = matchFont(codePoint, fontSelector, fontProvider, additionalFonts);
            }

            List<Glyph> resolvedGlyphs = new ArrayList<>();
            // Try to append as many symbols as possible to the current font
            if (currentFont != null) {
                Character.UnicodeScript firstScript = null;
                int to = indexSignificant;
                boolean breakRequested = false;
                for (int i = indexSignificant; i < text.length(); i++) {
                    int codePoint = extractCodePoint(text, i);
                    if (codePoint > 0xFFFF) {
                        i++;
                    }
                    if (isCurrentFontCheckRequired() && (i != indexDiacritic - 1)) {
                        if (currentFont != matchFont(codePoint, fontSelector, fontProvider, additionalFonts)) {
                            breakRequested = true;
                        }
                    }

                    if (i > indexDiacritic) {
                        if (TextUtil.isDiacritic(codePoint)) {
                            final PdfFont diacriticFont = matchFont(codePoint, fontSelector, fontProvider, additionalFonts);
                            // If diacritic font equals to the current font or null, don't
                            // enable special logic for diacritic and process it as usual symbol
                            if (diacriticFont != null && diacriticFont != currentFont) {
                                // If it's the first diacritic in a row, we want to break to try to find a better font for
                                // the previous letter during the next iteration
                                if (indexDiacritic != i - 1) {
                                    breakRequested = true;
                                }
                                indexDiacritic = i;
                                if (breakRequested) {
                                    to = i - 2;
                                }
                            }
                        } else {
                            indexDiacritic = -1;
                        }
                    }

                    Character.UnicodeScript currScript = Character.UnicodeScript.of(codePoint);
                    if (isSignificantUnicodeScript(currScript)) {
                        if (firstScript == null) {
                            firstScript = currScript;
                        } else if (firstScript != currScript) {
                            breakRequested = true;
                        }
                    }

                    if (breakRequested) {
                        break;
                    }

                    to = i;
                }

                if (to < index) {
                    continue;
                }

                int numOfAppendedGlyphs = currentFont.appendGlyphs(text, index, to, resolvedGlyphs);
                index += numOfAppendedGlyphs;
            }
            // If no symbols were appended, try to append any symbols
            if (resolvedGlyphs.isEmpty()) {
                currentFont = getPdfFont(fontSelector.bestMatch(), fontProvider, additionalFonts);
                if (index != indexSignificant) {
                    index += currentFont.appendGlyphs(text, index, indexSignificant - 1, resolvedGlyphs);
                }
                while (index <= indexSignificant && index < text.length()) {
                    index += currentFont.appendAnyGlyph(text, index, resolvedGlyphs);
                }
            }

            GlyphLine tempGlyphLine = new GlyphLine(resolvedGlyphs);
            GlyphLine finalGlyphLine = TextPreprocessingUtil.replaceSpecialWhitespaceGlyphs(tempGlyphLine, currentFont);
            result.add(new Tuple2<>(finalGlyphLine, currentFont));
        }

        return result;
    }
    
    /**
     * Finds the best font which matches passed symbol.
     *
     * @param codePoint the symbol to match
     * @param fontSelector the font selector
     * @param fontProvider the font provider
     * @param additionalFonts the addition fonts
     * @return font which matches the symbol
     */
    protected PdfFont matchFont(int codePoint, FontSelector fontSelector, FontProvider fontProvider, FontSet additionalFonts) {
        PdfFont matchedFont = null;
        for (FontInfo fontInfo : fontSelector.getFonts()) {
            if (fontInfo.getFontUnicodeRange().contains(codePoint)) {
                PdfFont temptFont = getPdfFont(fontInfo, fontProvider, additionalFonts);
                Glyph glyph = temptFont.getGlyph(codePoint);
                if (null != glyph && 0 != glyph.getCode()) {
                    matchedFont = temptFont;
                    break;
                }
            }
        }
        return matchedFont;
    }

    private static int nextSignificantIndex(int startIndex, String text) {
        int nextValidChar = startIndex;
        for (; nextValidChar < text.length(); nextValidChar++) {
            if (!TextUtil.isWhitespaceOrNonPrintable(text.charAt(nextValidChar))) {
                break;
            }
        }
        return nextValidChar;
    }

    private static boolean isSignificantUnicodeScript(Character.UnicodeScript unicodeScript) {
        // Character.UnicodeScript.UNKNOWN will be handled as significant unicode script
        return unicodeScript != Character.UnicodeScript.COMMON && unicodeScript != Character.UnicodeScript.INHERITED;
    }

    private static int extractCodePoint(String text, int idx) {
        return TextUtil.isSurrogatePair(text, idx) ? TextUtil.convertToUtf32(text, idx) : (int) text.charAt(idx);
    }

    /**
     * Utility method to create PdfFont.
     *
     * @param fontInfo instance of FontInfo
     *
     * @return cached or just created PdfFont on success, otherwise null
     *
     * @see FontProvider#getPdfFont(FontInfo, FontSet)
     */
    private static PdfFont getPdfFont(FontInfo fontInfo, FontProvider fontProvider, FontSet additionalFonts) {
        return fontProvider.getPdfFont(fontInfo, additionalFonts);
    }
}

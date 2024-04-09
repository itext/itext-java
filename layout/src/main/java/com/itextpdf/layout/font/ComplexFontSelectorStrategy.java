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
package com.itextpdf.layout.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.font.selectorstrategy.FirstMatchFontSelectorStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Complex FontSelectorStrategy split text based on {@link java.lang.Character.UnicodeScript}.
 * If unicode script changes, a new font will be found.
 * If there is no suitable font, only one notdef glyph from {@link FontSelector#bestMatch()} will be added.
 * @deprecated was replaced by {@link FirstMatchFontSelectorStrategy}.
 */
@Deprecated
public class ComplexFontSelectorStrategy extends FontSelectorStrategy {

    private PdfFont font;
    private FontSelector selector;


    public ComplexFontSelectorStrategy(String text, FontSelector selector, FontProvider provider, FontSet additionalFonts) {
        super(text, provider, additionalFonts);
        this.font = null;
        this.selector = selector;
    }

    public ComplexFontSelectorStrategy(String text, FontSelector selector, FontProvider provider) {
        super(text, provider, null);
        this.font = null;
        this.selector = selector;
    }

    @Override
    public PdfFont getCurrentFont() {
        return font;
    }

    @Override
    public List<Glyph> nextGlyphs() {
        font = null;
        int nextUnignorable = nextSignificantIndex();
        if (nextUnignorable < text.length()) {
            for (FontInfo f : selector.getFonts()) {
                int codePoint = isSurrogatePair(text, nextUnignorable)
                        ? TextUtil.convertToUtf32(text, nextUnignorable)
                        : (int) text.charAt(nextUnignorable);

                if (f.getFontUnicodeRange().contains(codePoint)) {
                    PdfFont currentFont = getPdfFont(f);
                    Glyph glyph = currentFont.getGlyph(codePoint);
                    if (null != glyph && 0 != glyph.getCode()) {
                        font = currentFont;
                        break;
                    }
                }
            }
        }
        List<Glyph> glyphs = new ArrayList<>();
        boolean anyGlyphsAppended = false;
        if (font != null) {
            Character.UnicodeScript unicodeScript = nextSignificantUnicodeScript(nextUnignorable);
            int to = nextUnignorable;
            for (int i = nextUnignorable; i < text.length(); i++) {
                int codePoint = isSurrogatePair(text, i) ? TextUtil.convertToUtf32(text, i) : (int) text.charAt(i);
                Character.UnicodeScript currScript = Character.UnicodeScript.of(codePoint);
                if (isSignificantUnicodeScript(currScript) && currScript != unicodeScript) {
                    break;
                }
                if (codePoint > 0xFFFF) i++;
                to = i;
            }

            int numOfAppendedGlyphs = font.appendGlyphs(text, index, to, glyphs);
            anyGlyphsAppended = numOfAppendedGlyphs > 0;
            assert anyGlyphsAppended;
            index += numOfAppendedGlyphs;
        }
        if (!anyGlyphsAppended) {
            font = getPdfFont(selector.bestMatch());
            if (index != nextUnignorable) {
                index += font.appendGlyphs(text, index, nextUnignorable - 1, glyphs);
            }
            while (index <= nextUnignorable && index < text.length()) {
                index += font.appendAnyGlyph(text, index, glyphs);
            }
        }
        return glyphs;
    }

    private int nextSignificantIndex() {
        int nextValidChar = index;
        for (; nextValidChar < text.length(); nextValidChar++) {
            if (!TextUtil.isWhitespaceOrNonPrintable(text.charAt(nextValidChar))) {
                break;
            }
        }
        return nextValidChar;
    }

    private Character.UnicodeScript nextSignificantUnicodeScript(int from) {
        for (int i = from; i < text.length(); i++) {
            int codePoint;
            if (isSurrogatePair(text, i)) {
                codePoint = TextUtil.convertToUtf32(text, i);
                i++;
            } else {
                codePoint = (int) text.charAt(i);
            }
            Character.UnicodeScript unicodeScript = Character.UnicodeScript.of(codePoint);
            if (isSignificantUnicodeScript(unicodeScript)) {
                return unicodeScript;
            }
        }
        return Character.UnicodeScript.COMMON;
    }

    private static boolean isSignificantUnicodeScript(Character.UnicodeScript unicodeScript) {
        // Character.UnicodeScript.UNKNOWN will be handled as significant unicode script
        return unicodeScript != Character.UnicodeScript.COMMON && unicodeScript != Character.UnicodeScript.INHERITED;
    }

    //This method doesn't perform additional checks if compare with TextUtil#isSurrogatePair()
    private static boolean isSurrogatePair(String text, int idx) {
        return TextUtil.isSurrogateHigh(text.charAt(idx)) && idx < text.length() - 1
                && TextUtil.isSurrogateLow(text.charAt(idx + 1));
    }
}

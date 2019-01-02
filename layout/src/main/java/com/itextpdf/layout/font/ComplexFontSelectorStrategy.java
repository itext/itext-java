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

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;

import java.util.ArrayList;
import java.util.List;

/**
 * Complex FontSelectorStrategy split text based on {@link java.lang.Character.UnicodeScript}.
 * If unicode script changes, a new font will be found.
 * If there is no suitable font, only one notdef glyph from {@link FontSelector#bestMatch()} will be added.
 */
public class ComplexFontSelectorStrategy extends FontSelectorStrategy {

    private PdfFont font;
    private FontSelector selector;


    public ComplexFontSelectorStrategy(String text, FontSelector selector, FontProvider provider, FontSet tempFonts) {
        super(text, provider, tempFonts);
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

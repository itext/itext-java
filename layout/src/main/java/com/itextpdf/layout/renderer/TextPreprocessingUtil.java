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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.font.PdfFont;

public final class TextPreprocessingUtil {

    private TextPreprocessingUtil() {
    }


    /**
     * Replaces special whitespace glyphs to new whitespace '\u0020' glyph that has custom width.
     * Special whitespace glyphs are symbols such as '\u2002', '\u2003', '\u2009' and '\t'.
     *
     * @param line the string for preprocessing
     * @param font the font that will be used when displaying the string
     *
     * @return old line with new special whitespace glyphs
     */
    public static GlyphLine replaceSpecialWhitespaceGlyphs(GlyphLine line, PdfFont font) {
        if (null != line) {
            boolean isMonospaceFont = font.getFontProgram().getFontMetrics().isFixedPitch();
            Glyph space = font.getGlyph('\u0020');
            int spaceWidth = space.getWidth();
            int lineSize = line.size();
            for (int i = 0; i < lineSize; i++) {
                final Glyph glyph = line.get(i);
                final Integer xAdvance = calculateXAdvancement(spaceWidth, isMonospaceFont, glyph);
                final boolean isSpecialWhitespaceGlyph = xAdvance != null;
                if (isSpecialWhitespaceGlyph) {
                    Glyph newGlyph = new Glyph(space);
                    newGlyph.setChars(glyph.getChars());
                    assert xAdvance <= Short.MAX_VALUE && xAdvance >= Short.MIN_VALUE;
                    newGlyph.setXAdvance((short) (int) xAdvance);
                    line.set(i, newGlyph);
                }
            }
        }
        return line;
    }

    static final int NON_MONO_SPACE_ENSP_WIDTH = 500;
    static final int NON_MONO_SPACE_THINSP_WIDTH = 200;
    static final int AMOUNT_OF_SPACE_IN_TAB = 3;

    private static Integer calculateXAdvancement(int spaceWidth, boolean isMonospaceFont, Glyph glyph) {
        Integer xAdvance = null;
        if (glyph.getCode() <= 0) {

            switch (glyph.getUnicode()) {
                // ensp
                case '\u2002':
                    xAdvance = isMonospaceFont ? 0 : (NON_MONO_SPACE_ENSP_WIDTH - spaceWidth);
                    break;
                // emsp
                case '\u2003':
                    xAdvance = isMonospaceFont ? 0 : (FontProgram.UNITS_NORMALIZATION - spaceWidth);
                    break;
                // thinsp
                case '\u2009':
                    xAdvance = isMonospaceFont ? 0 : (NON_MONO_SPACE_THINSP_WIDTH - spaceWidth);
                    break;
                case '\t':
                    xAdvance = AMOUNT_OF_SPACE_IN_TAB * spaceWidth;
                    break;
                default:
                    return xAdvance;
            }
        }
        return xAdvance;
    }


}

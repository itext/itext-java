/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
     * @return old line with new special whitespace glyphs
     */
    public static GlyphLine replaceSpecialWhitespaceGlyphs(GlyphLine line, PdfFont font) {
        if (null != line) {
            Glyph space = font.getGlyph('\u0020');
            Glyph glyph;
            for (int i = 0; i < line.size(); i++) {
                glyph = line.get(i);
                Integer xAdvance = getSpecialWhitespaceXAdvance(glyph, space, font.getFontProgram().getFontMetrics().isFixedPitch());
                if (xAdvance != null) {
                    Glyph newGlyph = new Glyph(space, glyph.getUnicode());
                    assert xAdvance <= Short.MAX_VALUE && xAdvance >= Short.MIN_VALUE;
                    newGlyph.setXAdvance((short) (int) xAdvance);
                    line.set(i, newGlyph);
                }
            }
        }
        return line;
    }

    private static Integer getSpecialWhitespaceXAdvance(Glyph glyph, Glyph spaceGlyph, boolean isMonospaceFont) {
        if (glyph.getCode() > 0) {
            return null;
        }
        switch (glyph.getUnicode()) {

            // ensp
            case '\u2002':
                return isMonospaceFont ? 0 : 500 - spaceGlyph.getWidth();

            // emsp
            case '\u2003':
                return isMonospaceFont ? 0 : 1000 - spaceGlyph.getWidth();

            // thinsp
            case '\u2009':
                return isMonospaceFont ? 0 : 200 - spaceGlyph.getWidth();
            case '\t':
                return 3 * spaceGlyph.getWidth();
        }

        return null;
    }
}

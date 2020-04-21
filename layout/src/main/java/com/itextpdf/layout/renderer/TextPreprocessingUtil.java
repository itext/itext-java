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

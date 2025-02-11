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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.constants.FontDescriptorFlags;
import com.itextpdf.io.font.constants.FontStretches;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.otf.Glyph;

import java.util.HashMap;
import java.util.Map;

/**
 * FontProgram class for Type 3 font. Contains map of {@link Type3Glyph}.
 * Type3Glyphs belong to a particular pdf document.
 * Note, an instance of Type3Font can not be reused for multiple pdf documents.
 */
public class Type3Font extends FontProgram {

    private final Map<Integer, Type3Glyph> type3Glyphs = new HashMap<>();
    /**
     * Stores glyphs without associated unicode.
     */
    private final Map<Integer, Type3Glyph> type3GlyphsWithoutUnicode = new HashMap<>();
    private boolean colorized = false;
    private int flags = 0;

    /**
     * Creates a Type 3 font program.
     *
     * @param colorized defines whether the glyph color is specified in the glyph descriptions in the font.
     */
    Type3Font(boolean colorized) {
        this.colorized = colorized;
        this.fontNames = new FontNames();
        getFontMetrics().setBbox(0, 0, 0, 0);
    }

    /**
     * Returns a glyph by unicode.
     *
     * @param unicode glyph unicode
     *
     * @return {@link Type3Glyph} glyph, or {@code null} if this font does not contain glyph for the unicode
     */
    public Type3Glyph getType3Glyph(int unicode) {
        return type3Glyphs.get(unicode);
    }

    /**
     * Returns a glyph by its code. These glyphs may not have unicode.
     *
     * @param code glyph code
     *
     * @return {@link Type3Glyph} glyph, or {@code null} if this font does not contain glyph for the code
     */
    public Type3Glyph getType3GlyphByCode(int code) {
        Type3Glyph glyph = type3GlyphsWithoutUnicode.get(code);
        if (glyph == null && codeToGlyph.get(code) != null) {
            glyph = type3Glyphs.get(codeToGlyph.get(code).getUnicode());
        }
        return glyph;
    }

    @Override
    public int getPdfFontFlags() {
        return flags;
    }

    @Override
    public boolean isFontSpecific() {
        return false;
    }

    public boolean isColorized() {
        return colorized;
    }

    @Override
    public int getKerning(Glyph glyph1, Glyph glyph2) {
        return 0;
    }


    /**
     * Returns number of glyphs for this font.
     * Its also count glyphs without unicode.
     * See {@link #type3GlyphsWithoutUnicode}.
     *
     * @return {@code int} number off all glyphs
     */
    public int getNumberOfGlyphs() {
        return type3Glyphs.size() + type3GlyphsWithoutUnicode.size();
    }

    /**
     * Sets the PostScript name of the font.
     * <p>
     * If full name is null, it will be set as well.
     *
     * @param fontName the PostScript name of the font, shall not be null or empty.
     */
    @Override
    protected void setFontName(String fontName) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setFontName(fontName);
    }

    /**
     * Sets a preferred font family name.
     *
     * @param fontFamily a preferred font family name.
     */
    @Override
    protected void setFontFamily(String fontFamily) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setFontFamily(fontFamily);
    }

    /**
     * Sets font weight.
     *
     * @param fontWeight integer form 100 to 900. See {@link FontWeights}.
     */
    @Override
    protected void setFontWeight(int fontWeight) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setFontWeight(fontWeight);
    }

    /**
     * Sets font width in css notation (font-stretch property)
     *
     * @param fontWidth {@link FontStretches}.
     */
    @Override
    protected void setFontStretch(String fontWidth) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setFontStretch(fontWidth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setCapHeight(int capHeight) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setCapHeight(capHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setItalicAngle(int italicAngle) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setItalicAngle(italicAngle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setTypoAscender(int ascender) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setTypoAscender(ascender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setTypoDescender(int descender) {
        // This dummy override allows PdfType3Font to use setter from different module.
        super.setTypoDescender(descender);
    }

    /**
     * Sets Font descriptor flags.
     * @see FontDescriptorFlags
     *
     * @param flags {@link FontDescriptorFlags}.
     */
    void setPdfFontFlags(int flags) {
        this.flags = flags;
    }

    void addGlyph(int code, int unicode, int width, int[] bbox, Type3Glyph type3Glyph) {
        if (codeToGlyph.containsKey(code)) {
            removeGlyphFromMappings(code);
        }
        Glyph glyph = new Glyph(code, width, unicode, bbox);
        codeToGlyph.put(code, glyph);
        if (unicode < 0) {
            type3GlyphsWithoutUnicode.put(code, type3Glyph);
        } else {
            unicodeToGlyph.put(unicode, glyph);
            type3Glyphs.put(unicode, type3Glyph);
        }
        recalculateAverageWidth();
    }

    private void removeGlyphFromMappings(int glyphCode) {
        Glyph removed = codeToGlyph.remove(glyphCode);
        if (removed == null) {
            return;
        }
        int unicode = removed.getUnicode();
        if (unicode < 0) {
            type3GlyphsWithoutUnicode.remove(glyphCode);
        } else {
            unicodeToGlyph.remove(unicode);
            type3Glyphs.remove(unicode);
        }
    }

    private void recalculateAverageWidth() {
        int widthSum = 0;
        int glyphsNumber = codeToGlyph.size();
        for (Glyph glyph : codeToGlyph.values()) {
            if (glyph.getWidth() == 0) {
                glyphsNumber--;
                continue;
            }
            widthSum += glyph.getWidth();
        }
        avgWidth = glyphsNumber == 0 ? 0 : widthSum / glyphsNumber;
    }
}

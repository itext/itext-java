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
package com.itextpdf.io.font;

import com.itextpdf.io.font.constants.FontMacStyleFlags;
import com.itextpdf.io.font.constants.FontStretches;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.otf.Glyph;

import java.util.HashMap;
import java.util.Map;

public abstract class FontProgram {

    public static final int HORIZONTAL_SCALING_FACTOR = 100;
    public static final int DEFAULT_WIDTH = 1000;
    public static final int UNITS_NORMALIZATION = 1000;


    public static float convertTextSpaceToGlyphSpace(float value) {
        return value / UNITS_NORMALIZATION;
    }

    public static float convertGlyphSpaceToTextSpace(float value) {
        return value * UNITS_NORMALIZATION;
    }

    public static double convertGlyphSpaceToTextSpace(double value) {
        return value * UNITS_NORMALIZATION;
    }

    public static int convertGlyphSpaceToTextSpace(int value) {
        return value * UNITS_NORMALIZATION;
    }

    // In case Type1: char code to glyph.
    // In case TrueType: glyph index to glyph.
    protected Map<Integer, Glyph> codeToGlyph = new HashMap<>();
    protected Map<Integer, Glyph> unicodeToGlyph = new HashMap<>();
    protected boolean isFontSpecific;

    protected FontNames fontNames;
    protected FontMetrics fontMetrics = new FontMetrics();
    protected FontIdentification fontIdentification = new FontIdentification();

    protected int avgWidth;

    /**
     * The font's encoding name. This encoding is 'StandardEncoding' or 'AdobeStandardEncoding' for a font
     * that can be totally encoded according to the characters names. For all other names the font is treated as
     * symbolic.
     */
    protected String encodingScheme = FontEncoding.FONT_SPECIFIC;

    protected String registry;

    public int countOfGlyphs() {
        return Math.max(codeToGlyph.size(), unicodeToGlyph.size());
    }

    public FontNames getFontNames() {
        return fontNames;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public FontIdentification getFontIdentification() {
        return fontIdentification;
    }

    public String getRegistry() {
        return registry;
    }

    public abstract int getPdfFontFlags();

    public boolean isFontSpecific() {
        return isFontSpecific;
    }

    /**
     * Get glyph's width.
     *
     * @param unicode a unicode symbol or FontSpecif code.
     *
     * @return Gets width in normalized 1000 units.
     */
    public int getWidth(int unicode) {
        Glyph glyph = getGlyph(unicode);
        return glyph != null ? glyph.getWidth() : 0;
    }

    public int getAvgWidth() {
        return avgWidth;
    }

    /**
     * Get glyph's bbox.
     *
     * @param unicode a unicode symbol or FontSpecif code.
     *
     * @return Gets bbox in normalized 1000 units.
     */
    public int[] getCharBBox(int unicode) {
        Glyph glyph = getGlyph(unicode);
        return glyph != null ? glyph.getBbox() : null;
    }

    public Glyph getGlyph(int unicode) {
        return unicodeToGlyph.get(unicode);
    }

    // char code in case Type1 or index in case OpenType
    public Glyph getGlyphByCode(int charCode) {
        return codeToGlyph.get(charCode);
    }

    public boolean hasKernPairs() {
        return false;
    }

    /**
     * Gets the kerning between two glyphs.
     *
     * @param first  the first unicode value
     * @param second the second unicode value
     *
     * @return the kerning to be applied
     */
    public int getKerning(int first, int second) {
        return getKerning(unicodeToGlyph.get(first), unicodeToGlyph.get(second));
    }

    /**
     * Gets the kerning between two glyphs.
     *
     * @param first  the first glyph
     * @param second the second glyph
     *
     * @return the kerning to be applied
     */
    public abstract int getKerning(Glyph first, Glyph second);

    /**
     * Checks whether the {@link FontProgram} was built with corresponding fontName.
     * Default value is false unless overridden.
     *
     * @param fontName a font name or path to a font program
     *
     * @return true, if the FontProgram was built with the fontProgram. Otherwise false.
     */
    public boolean isBuiltWith(String fontName) {
        return false;
    }

    protected void setRegistry(String registry) {
        this.registry = registry;
    }

    /**
     * Gets the name without the modifiers Bold, Italic or BoldItalic.
     *
     * @param name the full name of the font
     *
     * @return the name without the modifiers Bold, Italic or BoldItalic
     */
    static String trimFontStyle(String name) {
        if (name == null) {
            return null;
        }
        if (name.endsWith(",Bold")) {
            return name.substring(0, name.length() - 5);
        } else if (name.endsWith(",Italic")) {
            return name.substring(0, name.length() - 7);
        } else if (name.endsWith(",BoldItalic")) {
            return name.substring(0, name.length() - 11);
        } else {
            return name;
        }
    }

    /**
     * Sets typo ascender. See also {@link FontMetrics#setTypoAscender(int)}.
     *
     * @param ascender typo ascender value in 1000-units
     */
    protected void setTypoAscender(int ascender) {
        fontMetrics.setTypoAscender(ascender);
    }

    /**
     * Sets typo descender. See also {@link FontMetrics#setTypoDescender(int)}.
     *
     * @param descender typo descender value in 1000-units
     */
    protected void setTypoDescender(int descender) {
        fontMetrics.setTypoDescender(descender);
    }

    /**
     * Sets the capital letters height. See also {@link FontMetrics#setCapHeight(int)}.
     *
     * @param capHeight cap height in 1000-units
     */
    protected void setCapHeight(int capHeight) {
        fontMetrics.setCapHeight(capHeight);
    }

    protected void setXHeight(int xHeight) {
        fontMetrics.setXHeight(xHeight);
    }

    /**
     * Sets the PostScript italic angle.
     *
     * <p>
     * Italic angle in counter-clockwise degrees from the vertical. Zero for upright text, negative for text that leans
     * to the right (forward).
     *
     * @param italicAngle in counter-clockwise degrees from the vertical
     */
    protected void setItalicAngle(int italicAngle) {
        fontMetrics.setItalicAngle(italicAngle);
    }

    protected void setStemV(int stemV) {
        fontMetrics.setStemV(stemV);
    }

    protected void setStemH(int stemH) {
        fontMetrics.setStemH(stemH);
    }

    /**
     * Sets font weight.
     *
     * @param fontWeight integer form 100 to 900. See {@link FontWeights}.
     */
    protected void setFontWeight(int fontWeight) {
        fontNames.setFontWeight(fontWeight);
    }

    /**
     * Sets font width in css notation (font-stretch property)
     *
     * @param fontWidth {@link FontStretches}.
     */
    protected void setFontStretch(String fontWidth) {
        fontNames.setFontStretch(fontWidth);
    }

    protected void setFixedPitch(boolean isFixedPitch) {
        fontMetrics.setIsFixedPitch(isFixedPitch);
    }

    protected void setBold(boolean isBold) {
        if (isBold) {
            fontNames.setMacStyle(fontNames.getMacStyle() | FontMacStyleFlags.BOLD);
        } else {
            fontNames.setMacStyle(fontNames.getMacStyle() & (~FontMacStyleFlags.BOLD));
        }
    }

    protected void setBbox(int[] bbox) {
        fontMetrics.setBbox(bbox[0], bbox[1], bbox[2], bbox[3]);
    }

    /**
     * Sets a preferred font family name.
     *
     * @param fontFamily a preferred font family name.
     */
    protected void setFontFamily(String fontFamily) {
        fontNames.setFamilyName(fontFamily);
    }

    /**
     * Sets the PostScript name of the font.
     * <p>
     * If full name is null, it will be set as well.
     *
     * @param fontName the PostScript name of the font, shall not be null or empty.
     */
    protected void setFontName(String fontName) {
        fontNames.setFontName(fontName);
        if (fontNames.getFullName() == null) {
            fontNames.setFullName(fontName);
        }
    }

    protected void fixSpaceIssue() {
        Glyph space = unicodeToGlyph.get(32);
        if (space != null) {
            codeToGlyph.put(space.getCode(), space);
        }
    }

    @Override
    public String toString() {
        String name = getFontNames().getFontName();
        return name != null && name.length() > 0 ? name : super.toString();
    }
}

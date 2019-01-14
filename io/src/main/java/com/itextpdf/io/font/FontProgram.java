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
package com.itextpdf.io.font;

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.constants.FontMacStyleFlags;
import com.itextpdf.io.font.constants.FontStretches;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.util.FileUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class FontProgram implements Serializable {

    private static final long serialVersionUID = -3488910249070253659L;

    public static final int DEFAULT_WIDTH = 1000;
    public static final int UNITS_NORMALIZATION = 1000;

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
     * that can be totally encoded according to the characters names. For all other names the font is treated as symbolic.
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
     * @return the kerning to be applied
     */
    public abstract int getKerning(Glyph first, Glyph second);

    /**
     * Checks whether the {@link FontProgram} was built with corresponding fontName.
     * Default value is false unless overridden.
     *
     * @param fontName a font name or path to a font program
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

    protected void setTypoAscender(int ascender) {
        fontMetrics.setTypoAscender(ascender);
    }

    protected void setTypoDescender(int descender) {
        fontMetrics.setTypoDescender(descender);
    }

    protected void setCapHeight(int capHeight) {
        fontMetrics.setCapHeight(capHeight);
    }

    protected void setXHeight(int xHeight) {
        fontMetrics.setXHeight(xHeight);
    }

    /**
     * Sets the PostScript italic angel.
     * <br/>
     * Italic angle in counter-clockwise degrees from the vertical. Zero for upright text, negative for text that leans to the right (forward).
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
     * <br />
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
        return name.length() > 0 ? name : super.toString();
    }
}

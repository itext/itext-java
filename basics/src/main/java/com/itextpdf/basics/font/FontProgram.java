package com.itextpdf.basics.font;

import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;

import java.util.HashMap;

public abstract class FontProgram {

    public static final int DEFAULT_WIDTH = 1000;
    public static final int UNITS_NORMALIZATION = 1000;

    // In case Type1: char code to glyph.
    // In case TrueType: glyph index to glyph.
    HashMap<Integer, Glyph> codeToGlyph = new HashMap<>();
    HashMap<Integer, Glyph> unicodeToGlyph = new HashMap<>();

    protected FontEncoding encoding;
    protected FontNames fontNames = new FontNames();
    protected FontMetrics fontMetrics = new FontMetrics();
    protected FontIdentification fontIdentification = new FontIdentification();

    /**
     * The font's encoding name. This encoding is 'StandardEncoding' or 'AdobeStandardEncoding' for a font
     * that can be totally encoded according to the characters names. For all other names the font is treated as symbolic.
     */
    protected String encodingScheme = "FontSpecific";

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

    public abstract GlyphLine createGlyphLine(String content);

    /**
     * Get glyph's width.
     *
     * @param unicode a unicode symbol or FontSpecif code.
     * @return Gets width in normalized 1000 units.
     */
    public int getWidth(int unicode) {
        Glyph glyph = getGlyph(unicode);
        return glyph != null ? glyph.width : 0;
    }

    /**
     * Get glyph's bbox.
     *
     * @param unicode a unicode symbol or FontSpecif code.
     * @return Gets bbox in normalized 1000 units.
     */
    public int[] getCharBBox(int unicode) {
        Glyph glyph = getGlyph(unicode);
        return glyph != null ? glyph.bbox : null;
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
     * @param first the first unicode value
     * @param second the second unicode value
     * @return the kerning to be applied
     */
    public int getKerning(int first, int second) {
        return getKerning(unicodeToGlyph.get(first), unicodeToGlyph.get(second));
    }

    /**
     * Gets the kerning between two glyphs.
     *
     * @param first the first glyph
     * @param second the second glyph
     * @return the kerning to be applied
     */
    public abstract int getKerning(Glyph first, Glyph second);

    //TODO change to protected!
    public void setRegistry(String registry) {
        this.registry = registry;
    }

    /**
     * Gets the name without the modifiers Bold, Italic or BoldItalic.
     *
     * @param name the full name of the font
     * @return the name without the modifiers Bold, Italic or BoldItalic
     */
    protected static String getBaseName(String name) {
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
}

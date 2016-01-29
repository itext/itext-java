package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.otf.Glyph;

import java.util.HashMap;
import java.util.Map;

public class Type3FontProgram extends FontProgram {

    private final Map<Integer, Type3Glyph> type3Glyphs = new HashMap<>();
    private boolean colorized = false;


    public Type3FontProgram(boolean colorized) {
        this.colorized = colorized;
        getFontMetrics().setBbox(0, 0, 0, 0);
    }

    public Type3Glyph getType3Glyph(int unicode) {
        return type3Glyphs.get(unicode);
    }

    @Override
    public int getPdfFontFlags() {
        return 0;
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

    public int getGlyphsCount() {
        return type3Glyphs.size();
    }

    void addGlyph(int code, int unicode, int width, int[] bbox, Type3Glyph type3Glyph) {
        Glyph glyph = new Glyph(code, width, unicode, bbox);
        codeToGlyph.put(code, glyph);
        unicodeToGlyph.put(unicode, glyph);
        type3Glyphs.put(unicode, type3Glyph);
    }
}

package com.itextpdf.basics.font;

import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;

import java.util.ArrayList;
import java.util.List;

public class Type3Font extends FontProgram {

    private double[] fontMatrix = {0.001, 0, 0, 0.001, 0, 0};

    @Override
    public int getPdfFontFlags() {
        return 0;
    }

    @Override
    public boolean isFontSpecific() {
        return false;
    }

    //TODO remove
    public GlyphLine createGlyphLine(String content) {
        List<Glyph> glyphs = new ArrayList<>(content.length());
        for (int i = 0; i < content.length(); i++) {
            Glyph glyph = codeToGlyph.get(content.charAt(i) & 0xff);
            if (glyph != null) {
                glyphs.add(glyph);
            }
        }
        return new GlyphLine(glyphs);
    }

    @Override
    public int getWidth(int ch) {
        if (codeToGlyph.containsKey(ch)) {
            return codeToGlyph.get(ch).getWidth();
        }
        return 0;
    }

    @Override
    public int[] getCharBBox(int ch) {
        if (codeToGlyph.containsKey(ch)) {
            return codeToGlyph.get(ch).getBbox();
        }
        return null;
    }

    @Override
    public int getKerning(int ch1, int ch2) {
        return 0;
    }

    @Override
    public int getKerning(Glyph glyph1, Glyph glyph2) {
        return 0;
    }

    public double[] getFontMatrix() {
        return fontMatrix;
    }

    public void setFontMatrix(double[] fontMatrix) {
        this.fontMatrix = fontMatrix;
    }

    public void addGlyph(int ch, int width, int[] bbox) {
        if (ch > -1 && ch < 256) {
            codeToGlyph.put(ch, new Glyph(ch, width, null, bbox));
        }
    }

}

package com.itextpdf.layout.font;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.font.PdfFont;

import java.util.List;

public abstract class FontSelectorStrategy {

    protected String text;
    protected int index;

    protected FontSelectorStrategy(String text) {
        this.text = text;
        this.index = 0;
    }

    public boolean EndOfText() {
        return text == null || index >= text.length();
    }

    public abstract PdfFont getFont();

    //TODO List or GlyphLine?
    public abstract List<Glyph> nextGlyphs();
}

package com.itextpdf.layout.font;

import com.itextpdf.kernel.font.PdfFont;

import java.util.List;

public class NamedFontSelector extends FontSelector {

    List<PdfFont> fonts;

    public NamedFontSelector(List<PdfFont> allFonts, String fontFamily) {
        this.fonts = allFonts;
    }

    @Override
    public PdfFont bestMatch() {
        return fonts.get(0);
    }

    @Override
    public Iterable<PdfFont> getFonts() {
        return fonts;
    }
}

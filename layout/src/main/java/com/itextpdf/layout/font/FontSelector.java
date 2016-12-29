package com.itextpdf.layout.font;

import com.itextpdf.kernel.font.PdfFont;

// select font from sublist
public abstract class FontSelector {
    /**
     * The best PdfFont match for given font family.
     */
    public abstract PdfFont bestMatch();

    public abstract Iterable<PdfFont> getFonts();
}

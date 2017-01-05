package com.itextpdf.layout.font;

// select font from sublist
public abstract class FontSelector {
    /**
     * The best PdfFont match for given font family.
     */
    public abstract FontProgramInfo bestMatch();

    public abstract Iterable<FontProgramInfo> getFonts();
}

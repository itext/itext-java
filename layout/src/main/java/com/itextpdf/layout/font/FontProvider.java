package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;

import java.util.ArrayList;
import java.util.List;

// initial big collection of fonts, entry point for all font selector logic.
// FontProvider depends from PdfDocument, due to PdfFont.
// TODO it might works with FontPrograms.
public class FontProvider {

    private List<PdfFont> fonts = new ArrayList<>();

    public List<PdfFont> getAllFonts() {
        return fonts;
    }

    public void addFont(PdfFont font) {
        fonts.add(font);
    }

    protected FontSelector getSelector(String fontFamily) {
        return getSelector(fontFamily, FontConstants.UNDEFINED);
    }

    /**
     *
     * @param fontFamily
     * @param style Shall be {@link FontConstants#UNDEFINED}, {@link FontConstants#NORMAL}, {@link FontConstants#ITALIC},
     * {@link FontConstants#BOLD}, or {@link FontConstants#BOLDITALIC}
     * @return an instance of {@link FontSelector}.
     */
    protected FontSelector getSelector(String fontFamily, int style) {
        return new NamedFontSelector(getAllFonts(), fontFamily, style);
    }

    public FontSelectorStrategy getStrategy(String text, String fontFamily) {
        return new ComplexFontSelectorStrategy(text, getSelector(fontFamily));
    }
}

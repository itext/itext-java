package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// initial big collection of fonts, entry point for all font selector logic.
// FontProvider depends from PdfDocument, due to PdfFont.
public class FontProvider {

    private List<PdfFont> fonts = new ArrayList<>();
    protected Map<FontSelectorKey, FontSelector> fontSelectorCache;

    public FontProvider(List<PdfFont> pdfFonts) {
        this.fonts = pdfFonts;
        this.fontSelectorCache = new HashMap<>();
    }

    public FontProvider() {
        this(new ArrayList<PdfFont>());
    }

    /**
     * Note, this operation will reset internal FontSelector cache.
     * @param font
     */
    public void addFont(PdfFont font) {
        fonts.add(font);
        fontSelectorCache.clear();
    }

    public FontSelectorStrategy getStrategy(String text, String fontFamily, int style) {
        return new ComplexFontSelectorStrategy(text, getSelector(fontFamily, style));
    }

    public FontSelectorStrategy getStrategy(String text, String fontFamily) {
        return getStrategy(text, fontFamily, FontConstants.UNDEFINED);
    }

    /**
     *
     * @param fontFamily
     * @param style Shall be {@link FontConstants#UNDEFINED}, {@link FontConstants#NORMAL}, {@link FontConstants#ITALIC},
     * {@link FontConstants#BOLD}, or {@link FontConstants#BOLDITALIC}
     * @return an instance of {@link FontSelector}.
     */
    protected FontSelector getSelector(String fontFamily, int style) {
        FontSelectorKey key = new FontSelectorKey(fontFamily, style);
        if (fontSelectorCache.containsKey(key)) {
            return fontSelectorCache.get(key);
        } else {
            FontSelector fontSelector = new NamedFontSelector(fonts, fontFamily, style);
            fontSelectorCache.put(key, fontSelector);
            return fontSelector;
        }
    }

    private static class FontSelectorKey {
        String fontFamily;
        int style;

        public FontSelectorKey(String fontFamily, int style) {
            this.fontFamily = fontFamily;
            this.style = style;
        }

        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            FontSelectorKey that = (FontSelectorKey) o;

            return style == that.style
                    && (fontFamily != null ? fontFamily.equals(that.fontFamily) : that.fontFamily == null);
        }

        @Override
        public int hashCode() {
            int result = fontFamily != null ? fontFamily.hashCode() : 0;
            result = 31 * result + style;
            return result;
        }
    }
}

package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// initial big collection of fonts, entry point for all font selector logic.
// FontProvider depends from PdfDocument, due to PdfFont.
public class FontProvider {

    private FontSet fontSet;
    private Map<FontProgramInfo, PdfFont> pdfFonts = new HashMap<>();

    public FontProvider(FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public FontProvider() {
        this.fontSet = new FontSet();
    }

    public boolean addFont(FontProgram fontProgram, String encoding) {
        return fontSet.addFont(fontProgram, encoding);
    }

    public boolean addFont(String fontProgram, String encoding) {
        return fontSet.addFont(fontProgram, encoding);
    }

    public boolean addFont(byte[] fontProgram, String encoding) {
        return fontSet.addFont(fontProgram, encoding);
    }

    public void addFont(String fontProgram) {
        addFont(fontProgram, null);
    }

    public void addFont(FontProgram fontProgram) {
        addFont(fontProgram, getDefaultEncoding(fontProgram));
    }

    public void addFont(byte[] fontProgram) {
        addFont(fontProgram, null);
    }

    public String getDefaultEncoding(FontProgram fontProgram) {
        if (fontProgram instanceof Type1Font) {
            return PdfEncodings.WINANSI;
        } else {
            return PdfEncodings.IDENTITY_H;
        }
    }

    public boolean getDefaultCacheFlag() {
        return true;
    }

    public boolean getDefaultEmbeddingFlag() {
        return true;
    }

    public FontSelectorStrategy getStrategy(String text, String fontFamily, int style) {
        return new ComplexFontSelectorStrategy(text, getSelector(fontFamily, style), this);
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
    public final FontSelector getSelector(String fontFamily, int style) {
        FontSelectorKey key = new FontSelectorKey(fontFamily, style);
        if (fontSet.getFontSelectorCache().containsKey(key)) {
            return fontSet.getFontSelectorCache().get(key);
        } else {
            FontSelector fontSelector = createFontSelector(fontSet.getFonts(), fontFamily, style);
            fontSet.getFontSelectorCache().put(key, fontSelector);
            return fontSelector;
        }
    }

    protected FontSelector createFontSelector(Set<FontProgramInfo> fonts, String fontFamily, int style) {
        return new NamedFontSelector(fonts, fontFamily, style);
    }

    protected PdfFont createPdfFont(FontProgramInfo fontInfo) throws IOException {
        if (pdfFonts.containsKey(fontInfo)) {
            return pdfFonts.get(fontInfo);
        } else {
            FontProgram fontProgram;
            if (fontSet.getFontPrograms().containsKey(fontInfo)) {
                fontProgram = fontSet.getFontPrograms().get(fontInfo);
            } else if (fontInfo.getFontProgram() != null) {
                fontProgram = FontProgramFactory.createFont(fontInfo.getFontProgram(), getDefaultCacheFlag());
            } else {
                fontProgram = FontProgramFactory.createFont(fontInfo.getFontName(), getDefaultCacheFlag());
            }
            String encoding = fontInfo.getEncoding();
            if (encoding == null || encoding.length() == 0) {
                encoding = getDefaultEncoding(fontProgram);
            }
            PdfFont pdfFont = PdfFontFactory.createFont(fontProgram, encoding, getDefaultEmbeddingFlag());
            pdfFonts.put(fontInfo, pdfFont);
            return pdfFont;
        }
    }
}

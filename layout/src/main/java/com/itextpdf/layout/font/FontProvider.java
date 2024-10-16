/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.font;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.font.FontCache;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.font.selectorstrategy.BestMatchFontSelectorStrategy.BestMatchFontSelectorStrategyFactory;
import com.itextpdf.layout.font.selectorstrategy.IFontSelectorStrategy;
import com.itextpdf.layout.font.selectorstrategy.IFontSelectorStrategyFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main entry point of font selector logic.
 * Contains reusable {@link FontSet} and collection of {@link PdfFont}s.
 * FontProvider depends on {@link PdfDocument} due to {@link PdfFont}, so it cannot be reused for different documents
 * unless reset with {@link FontProvider#reset()} or recreated with {@link FontProvider#getFontSet()}.
 * In the former case the {@link FontSelectorCache} is reused and in the latter it's reinitialised.
 * FontProvider the only end point for creating {@link PdfFont}.
 * <p>
 * It is allowed to use only one {@link FontProvider} per document. If additional fonts per element needed,
 * another instance of  {@link FontSet} can be used. For more details see {@link com.itextpdf.layout.properties.Property#FONT_SET},
 * {@link #getPdfFont(FontInfo, FontSet)}, {@link #createFontSelectorStrategy(List, FontCharacteristics, FontSet)}.
 * <p>
 * Note, FontProvider does not close created {@link FontProgram}s, because of possible conflicts with {@link FontCache}.
 */
public class FontProvider {

    private static final String DEFAULT_FONT_FAMILY = "Helvetica";

    private final FontSet fontSet;
    private final FontSelectorCache fontSelectorCache;
    /**
     * The default font-family is used by {@link FontSelector} if it's impossible to select a font for all other set font-families
     */
    protected final String defaultFontFamily;
    protected final Map<FontInfo, PdfFont> pdfFonts;

    private IFontSelectorStrategyFactory fontSelectorStrategyFactory;

    /**
     * Creates a new instance of FontProvider.
     *
     * @param fontSet predefined set of fonts, could be null.
     */
    public FontProvider(FontSet fontSet) {
        this(fontSet, DEFAULT_FONT_FAMILY);
    }

    /**
     * Creates a new instance of FontProvider.
     */
    public FontProvider() {
        this(new FontSet());
    }

    /**
     * Creates a new instance of FontProvider.
     *
     * @param defaultFontFamily default font family.
     */
    public FontProvider(String defaultFontFamily) {
        this(new FontSet(), defaultFontFamily);
    }

    /**
     * Creates a new instance of FontProvider.
     *
     * @param fontSet predefined set of fonts, could be null.
     * @param defaultFontFamily default font family.
     */
    public FontProvider(FontSet fontSet, String defaultFontFamily) {
        this.fontSet = fontSet != null ? fontSet : new FontSet();
        pdfFonts = new HashMap<>();
        fontSelectorCache = new FontSelectorCache(this.fontSet);
        this.defaultFontFamily = defaultFontFamily;
        this.fontSelectorStrategyFactory = new BestMatchFontSelectorStrategyFactory();
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontProgram the font file which will be added to font cache.
     *                    The {@link FontProgram} instances are normally created via {@link FontProgramFactory}.
     * @param encoding font encoding to create {@link com.itextpdf.kernel.font.PdfFont}. Possible values for this
     *                 argument are the same as for {@link PdfFontFactory#createFont()} family of methods.
     * @param unicodeRange sets the specific range of characters to be used from the font.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontProgram fontProgram, String encoding, Range unicodeRange) {
        return fontSet.addFont(fontProgram, encoding, null, unicodeRange);
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontProgram the font file which will be added to font cache.
     *                    The {@link FontProgram} instances are normally created via {@link FontProgramFactory}.
     * @param encoding font encoding to create {@link com.itextpdf.kernel.font.PdfFont}. Possible values for this
     *                 argument are the same as for {@link PdfFontFactory#createFont()} family of methods.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontProgram fontProgram, String encoding) {
        return addFont(fontProgram, encoding, null);
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontProgram the font file which will be added to font cache.
     *                    The {@link FontProgram} instances are normally created via {@link FontProgramFactory}.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(FontProgram fontProgram) {
        return addFont(fontProgram, getDefaultEncoding(fontProgram));
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontPath path to the font file to add. Can be a path to file or font name,
     *                 see {@link FontProgramFactory#createFont(String)}.
     * @param encoding font encoding to create {@link com.itextpdf.kernel.font.PdfFont}. Possible values for this
     *                 argument are the same as for {@link PdfFontFactory#createFont()} family of methods.
     * @param unicodeRange sets the specific range of characters to be used from the font.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(String fontPath, String encoding, Range unicodeRange) {
        return fontSet.addFont(fontPath, encoding, null, unicodeRange);
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontPath path to the font file to add. Can be a path to file or font name,
     *                 see {@link FontProgramFactory#createFont(String)}.
     * @param encoding font encoding to create {@link com.itextpdf.kernel.font.PdfFont}. Possible values for this
     *                 argument are the same as for {@link PdfFontFactory#createFont()} family of methods.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(String fontPath, String encoding) {
        return addFont(fontPath, encoding, null);
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontPath path to the font file to add. Can be a path to file or font name,
     *                 see {@link FontProgramFactory#createFont(String)}.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(String fontPath) {
        return addFont(fontPath, null);
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontData byte content of the font program file.
     * @param encoding font encoding to create {@link com.itextpdf.kernel.font.PdfFont}. Possible values for this
     *                 argument are the same as for {@link PdfFontFactory#createFont()} family of methods.
     * @param unicodeRange sets the specific range of characters to be used from the font.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(byte[] fontData, String encoding, Range unicodeRange) {
        return fontSet.addFont(fontData, encoding, null, unicodeRange);
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontData byte content of the font program file.
     * @param encoding font encoding to create {@link com.itextpdf.kernel.font.PdfFont}. Possible values for this
     *                 argument are the same as for {@link PdfFontFactory#createFont()} family of methods.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(byte[] fontData, String encoding) {
        return addFont(fontData, encoding, null);
    }

    /**
     * Add font to {@link FontSet} cache.
     *
     * @param fontData byte content of the font program file.
     *
     * @return true, if font was successfully added, otherwise false.
     */
    public boolean addFont(byte[] fontData) {
        return addFont(fontData, null);
    }

    /**
     * Add all the fonts from a directory.
     *
     * @param dir path to directory.
     *
     * @return number of added fonts.
     */
    public int addDirectory(String dir) {
        return fontSet.addDirectory(dir);
    }

    /**
     * Add all fonts from system directories to {@link FontSet} cache.
     *
     * @return number of added fonts.
     */
    public int addSystemFonts() {
        int count = 0;
        String[] withSubDirs = {
                FileUtil.getFontsDir(),
                "/usr/share/X11/fonts",
                "/usr/X/lib/X11/fonts",
                "/usr/openwin/lib/X11/fonts",
                "/usr/share/fonts",
                "/usr/X11R6/lib/X11/fonts"
        };
        for (String directory : withSubDirs) {
            count += fontSet.addDirectory(directory, true);
        }

        String[] withoutSubDirs = {
                "/Library/Fonts",
                "/System/Library/Fonts"
        };
        for (String directory : withoutSubDirs) {
            count += fontSet.addDirectory(directory, false);
        }

        return count;
    }

    /**
     * Add standard fonts to {@link FontSet} cache.
     *
     * @return number of added fonts.
     * @see com.itextpdf.io.font.constants.StandardFonts
     */
    public int addStandardPdfFonts() {
        addFont(StandardFonts.COURIER);
        addFont(StandardFonts.COURIER_BOLD);
        addFont(StandardFonts.COURIER_BOLDOBLIQUE);
        addFont(StandardFonts.COURIER_OBLIQUE);
        addFont(StandardFonts.HELVETICA);
        addFont(StandardFonts.HELVETICA_BOLD);
        addFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
        addFont(StandardFonts.HELVETICA_OBLIQUE);
        addFont(StandardFonts.SYMBOL);
        addFont(StandardFonts.TIMES_ROMAN);
        addFont(StandardFonts.TIMES_BOLD);
        addFont(StandardFonts.TIMES_BOLDITALIC);
        addFont(StandardFonts.TIMES_ITALIC);
        addFont(StandardFonts.ZAPFDINGBATS);
        return 14;
    }

    /**
     * Gets {@link FontSet}.
     *
     * @return the font set
     */
    public FontSet getFontSet() {
        return fontSet;
    }

    /**
     * Gets the default font-family.
     *
     * @return the default font-family
     */
    public String getDefaultFontFamily() {
        return defaultFontFamily;
    }

    /**
     * Gets the default encoding for specific font.
     *
     * @param fontProgram to get default encoding
     *
     * @return the default encoding
     * @see com.itextpdf.io.font.PdfEncodings
     */
    public String getDefaultEncoding(FontProgram fontProgram) {
        if (fontProgram instanceof Type1Font) {
            return PdfEncodings.WINANSI;
        } else {
            return PdfEncodings.IDENTITY_H;
        }
    }

    /**
     * The method is used to determine whether the font should be cached or not by default.
     *
     * <p>
     * NOTE: This method can be overridden to customize behaviour.
     *
     * @return the default cache flag
     */
    public boolean getDefaultCacheFlag() {
        return true;
    }

    /**
     * The method is used to determine whether the font should be embedded or not by default.
     *
     * <p>
     * NOTE: This method can be overridden to customize behaviour.
     *
     * @return the default embedding flag
     */
    public boolean getDefaultEmbeddingFlag() {
        return true;
    }

    /**
     * Sets factory which will be used in {@link #createFontSelectorStrategy(List, FontCharacteristics, FontSet)}
     * method.
     *
     * @param factory the factory which will be used to create font selector strategies
     */
    public void setFontSelectorStrategyFactory(IFontSelectorStrategyFactory factory) {
        this.fontSelectorStrategyFactory = factory;
    }

    /**
     * Creates the {@link IFontSelectorStrategy} to split text into sequences of glyphs, already tied
     * to the fonts which contain them. The fonts can be taken from the added fonts to the font provider and
     * are chosen based on font-families list and desired font characteristics.
     *
     * @param fontFamilies target font families to create {@link FontSelector} for sequences of glyphs.
     * @param fc instance of {@link FontCharacteristics} to create {@link FontSelector} for sequences of glyphs.
     * @param additionalFonts set which provides fonts additionally to the fonts added to font provider.
     *                        Combined set of font provider fonts and additional fonts is used when choosing
     *                        a single font for a sequence of glyphs. Additional fonts will only be used for the given
     *                        font selector strategy instance and will not be otherwise preserved in font provider.
     *
     * @return {@link IFontSelectorStrategy} instance
     */
    public IFontSelectorStrategy createFontSelectorStrategy(List<String> fontFamilies,
            FontCharacteristics fc, FontSet additionalFonts) {

        final FontSelector fontSelector = getFontSelector(fontFamilies, fc, additionalFonts);
        return fontSelectorStrategyFactory.createFontSelectorStrategy(this, fontSelector, additionalFonts);
    }

    /**
     * Create {@link FontSelector} or get from cache.
     *
     * @param fontFamilies target font families.
     * @param fc instance of {@link FontCharacteristics}.
     *
     * @return an instance of {@link FontSelector}.
     * @see #createFontSelector(Collection, List, FontCharacteristics)
     * @see #getFontSelector(List, FontCharacteristics, FontSet)
     */
    public final FontSelector getFontSelector(List<String> fontFamilies, FontCharacteristics fc) {
        FontSelectorKey key = new FontSelectorKey(fontFamilies, fc);
        FontSelector fontSelector = fontSelectorCache.get(key);
        if (fontSelector == null) {
            fontSelector = createFontSelector(fontSet.getFonts(), fontFamilies, fc);
            fontSelectorCache.put(key, fontSelector);
        }
        return fontSelector;
    }

    /**
     * Create {@link FontSelector} or get from cache.
     *
     * @param fontFamilies target font families.
     * @param fc instance of {@link FontCharacteristics}.
     * @param additionalFonts set which provides fonts additionally to the fonts added to font provider.
     *                        Combined set of font provider fonts and additional fonts is used when choosing
     *                        a single font for {@link FontSelector}. Additional fonts will only be used for the given
     *                        font selector strategy instance and will not be otherwise preserved in font provider.
     *
     * @return an instance of {@link FontSelector}.
     * @see #createFontSelector(Collection, List, FontCharacteristics) }
     */
    public final FontSelector getFontSelector(List<String> fontFamilies, FontCharacteristics fc,
                                              FontSet additionalFonts) {
        FontSelectorKey key = new FontSelectorKey(fontFamilies, fc);
        FontSelector fontSelector = fontSelectorCache.get(key, additionalFonts);
        if (fontSelector == null) {
            fontSelector = createFontSelector(fontSet.getFonts(additionalFonts), fontFamilies, fc);
            fontSelectorCache.put(key, fontSelector, additionalFonts);
        }
        return fontSelector;
    }

    /**
     * Create a new instance of {@link FontSelector}. While caching is main responsibility of
     * {@link #getFontSelector(List, FontCharacteristics, FontSet)}.
     * This method just create a new instance of {@link FontSelector}.
     *
     * @param fonts        Set of all available fonts in current context.
     * @param fontFamilies target font families.
     * @param fc           instance of {@link FontCharacteristics}.
     *
     * @return an instance of {@link FontSelector}.
     */
    protected FontSelector createFontSelector(Collection<FontInfo> fonts,
                                              List<String> fontFamilies, FontCharacteristics fc) {
        List<String> fontFamiliesToBeProcessed = new ArrayList<>(fontFamilies);
        fontFamiliesToBeProcessed.add(defaultFontFamily);
        return new FontSelector(fonts, fontFamiliesToBeProcessed, fc);
    }

    /**
     * Get from cache or create a new instance of {@link PdfFont}.
     *
     * @param fontInfo font info, to create {@link FontProgram} and {@link PdfFont}.
     *
     * @return cached or new instance of {@link PdfFont}.
     */
    public PdfFont getPdfFont(FontInfo fontInfo) {
        return getPdfFont(fontInfo, null);
    }

    /**
     * Get from cache or create a new instance of {@link PdfFont}.
     *
     * @param fontInfo  font info, to create {@link FontProgram} and {@link PdfFont}.
     * @param additionalFonts set of additional fonts to consider.
     *
     * @return cached or new instance of {@link PdfFont}.
     */
    public PdfFont getPdfFont(FontInfo fontInfo, FontSet additionalFonts) {
        if (pdfFonts.containsKey(fontInfo)) {
            return pdfFonts.get(fontInfo);
        } else {
            FontProgram fontProgram = null;
            if (additionalFonts != null) {
                fontProgram = additionalFonts.getFontProgram(fontInfo);
            }
            if (fontProgram == null) {
                fontProgram = fontSet.getFontProgram(fontInfo);
            }
            PdfFont pdfFont;
            try {
                if (fontProgram == null) {
                    if (fontInfo.getFontData() != null) {
                        fontProgram = FontProgramFactory.createFont(fontInfo.getFontData(), getDefaultCacheFlag());
                    } else {
                        fontProgram = FontProgramFactory.createFont(fontInfo.getFontName(), getDefaultCacheFlag());
                    }
                }
                String encoding = fontInfo.getEncoding();
                if (encoding == null || encoding.length() == 0) {
                    encoding = getDefaultEncoding(fontProgram);
                }

                EmbeddingStrategy embeddingStrategy = getDefaultEmbeddingFlag()
                        ? EmbeddingStrategy.PREFER_EMBEDDED
                        : EmbeddingStrategy.PREFER_NOT_EMBEDDED;
                pdfFont = PdfFontFactory.createFont(fontProgram, encoding, embeddingStrategy);

            } catch (IOException e) {
                // Converting checked exceptions to unchecked RuntimeException (java-specific comment).
                //
                // FontProvider is usually used in highlevel API, which requests fonts in deep underlying logic.
                // IOException would mean that font is chosen and it is supposed to exist, however it cannot be read.
                // Using fallbacks in such situations would make FontProvider less intuitive.
                //
                // Even though softening of checked exceptions can be handled at higher levels in order to let
                // the caller of this method know that font creation failed, we prefer to avoid bloating highlevel API
                // and avoid making higher level code depend on low-level code because of the exceptions handling.
                throw new PdfException(LayoutExceptionMessageConstant.IO_EXCEPTION_WHILE_CREATING_FONT, e);
            }

            pdfFonts.put(fontInfo, pdfFont);
            return pdfFont;
        }
    }

    /**
     * Resets {@link FontProvider#pdfFonts PdfFont cache}.
     * After calling that method {@link FontProvider} can be reused with another {@link PdfDocument}
     */
    public void reset() {
        pdfFonts.clear();
    }
}

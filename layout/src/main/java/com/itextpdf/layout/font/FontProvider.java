/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontCache;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;

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
 * It is allowed to use only one {@link FontProvider} per document. If temporary fonts per element needed,
 * additional {@link FontSet} can be used. For more details see {@link com.itextpdf.layout.property.Property#FONT_SET},
 * {@link #getPdfFont(FontInfo, FontSet)}, {@link #getStrategy(String, List, FontCharacteristics, FontSet)}.
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

    /**
     * Creates a new instance of FontProvider
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
     * Creates a new instance of FontProvider
     *
     * @param fontSet predefined set of fonts, could be null.
     * @param defaultFontFamily default font family.
     */
    public FontProvider(FontSet fontSet, String defaultFontFamily) {
        this.fontSet = fontSet != null ? fontSet : new FontSet();
        pdfFonts = new HashMap<>();
        fontSelectorCache = new FontSelectorCache(this.fontSet);
        this.defaultFontFamily = defaultFontFamily;
    }

    public boolean addFont(FontProgram fontProgram, String encoding, Range unicodeRange) {
        return fontSet.addFont(fontProgram, encoding, null, unicodeRange);
    }

    public boolean addFont(FontProgram fontProgram, String encoding) {
        return addFont(fontProgram, encoding, null);
    }

    public boolean addFont(FontProgram fontProgram) {
        return addFont(fontProgram, getDefaultEncoding(fontProgram));
    }

    public boolean addFont(String fontPath, String encoding, Range unicodeRange) {
        return fontSet.addFont(fontPath, encoding, null, unicodeRange);
    }

    public boolean addFont(String fontPath, String encoding) {
        return addFont(fontPath, encoding, null);
    }

    public boolean addFont(String fontPath) {
        return addFont(fontPath, null);
    }

    public boolean addFont(byte[] fontData, String encoding, Range unicodeRange) {
        return fontSet.addFont(fontData, encoding, null, unicodeRange);
    }

    public boolean addFont(byte[] fontData, String encoding) {
        return addFont(fontData, encoding, null);
    }

    public boolean addFont(byte[] fontData) {
        return addFont(fontData, null);
    }

    public int addDirectory(String dir) {
        return fontSet.addDirectory(dir);
    }

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
     * @return the fontset
     */
    public FontSet getFontSet() {
        return fontSet;
    }

    /**
     * Gets the default font-family
     * @return the default font-family
     */
    public String getDefaultFontFamily() {
        return defaultFontFamily;
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

    public FontSelectorStrategy getStrategy(String text, List<String> fontFamilies, FontCharacteristics fc, FontSet additonalFonts) {
        return new ComplexFontSelectorStrategy(text, getFontSelector(fontFamilies, fc, additonalFonts), this, additonalFonts);
    }

    public FontSelectorStrategy getStrategy(String text, List<String> fontFamilies, FontCharacteristics fc) {
        return getStrategy(text, fontFamilies, fc, null);
    }

    public FontSelectorStrategy getStrategy(String text, List<String> fontFamilies) {
        return getStrategy(text, fontFamilies, null);
    }

    /**
     * Create {@link FontSelector} or get from cache.
     *
     * @param fontFamilies target font families
     * @param fc           instance of {@link FontCharacteristics}.
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
     * @param fontFamilies target font families
     * @param fc           instance of {@link FontCharacteristics}.
     * @param tempFonts    set of temporary fonts.
     * @return an instance of {@link FontSelector}.
     * @see #createFontSelector(Collection, List, FontCharacteristics) }
     */
    public final FontSelector getFontSelector(List<String> fontFamilies, FontCharacteristics fc,
                                              FontSet tempFonts) {
        FontSelectorKey key = new FontSelectorKey(fontFamilies, fc);
        FontSelector fontSelector = fontSelectorCache.get(key, tempFonts);
        if (fontSelector == null) {
            fontSelector = createFontSelector(fontSet.getFonts(tempFonts), fontFamilies, fc);
            fontSelectorCache.put(key, fontSelector, tempFonts);
        }
        return fontSelector;
    }

    /**
     * Create a new instance of {@link FontSelector}. While caching is main responsibility of
     * {@link #getFontSelector(List, FontCharacteristics, FontSet)}.
     * This method just create a new instance of {@link FontSelector}.
     *
     * @param fonts        Set of all available fonts in current context.
     * @param fontFamilies target font families
     * @param fc           instance of {@link FontCharacteristics}.
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
     * @return cached or new instance of {@link PdfFont}.
     */
    public PdfFont getPdfFont(FontInfo fontInfo) {
        return getPdfFont(fontInfo, null);
    }

    /**
     * Get from cache or create a new instance of {@link PdfFont}.
     *
     * @param fontInfo  font info, to create {@link FontProgram} and {@link PdfFont}.
     * @param tempFonts Set of temporary fonts.
     * @return cached or new instance of {@link PdfFont}.
     */
    public PdfFont getPdfFont(FontInfo fontInfo, FontSet tempFonts) {
        if (pdfFonts.containsKey(fontInfo)) {
            return pdfFonts.get(fontInfo);
        } else {
            FontProgram fontProgram = null;
            if (tempFonts != null) {
                fontProgram = tempFonts.getFontProgram(fontInfo);
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

                pdfFont = PdfFontFactory.createFont(fontProgram, encoding, getDefaultEmbeddingFlag());

            } catch (IOException e) {
                throw new PdfException(PdfException.IoExceptionWhileCreatingFont, e);
            }

            pdfFonts.put(fontInfo, pdfFont);
            return pdfFont;
        }
    }

    /**
     * Resets {@link FontProvider#pdfFonts PdfFont cache}. After calling that method {@link FontProvider} can be reused with another {@link PdfDocument}
     */
    public void reset() {
        pdfFonts.clear();
    }
}

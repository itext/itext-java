/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.Type1Font;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main entry point of font selector logic.
 * Contains reusable {@link FontSet} and collection of {@link PdfFont}s.
 * FontProvider depends from {@link PdfDocument}, due to {@link PdfFont}, it cannot be reused for different documents,
 * but a new instance of FontProvider could be created with {@link FontProvider#getFontSet()}.
 * FontProvider the only end point for creating PdfFont, {@link #getPdfFont(FontProgramInfo)},
 * {@link FontProgramInfo} shal call this method.
 * <p>
 * Note, FontProvider does not close created {@link FontProgram}s, because of possible conflicts with {@link FontCache}.
 */
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

    public boolean addFont(String fontProgram) {
        return addFont(fontProgram, null);
    }

    public boolean addFont(FontProgram fontProgram) {
        return addFont(fontProgram, getDefaultEncoding(fontProgram));
    }

    public boolean addFont(byte[] fontProgram) {
        return addFont(fontProgram, null);
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
        addFont(FontConstants.COURIER);
        addFont(FontConstants.COURIER_BOLD);
        addFont(FontConstants.COURIER_BOLDOBLIQUE);
        addFont(FontConstants.COURIER_OBLIQUE);
        addFont(FontConstants.HELVETICA);
        addFont(FontConstants.HELVETICA_BOLD);
        addFont(FontConstants.HELVETICA_BOLDOBLIQUE);
        addFont(FontConstants.HELVETICA_OBLIQUE);
        addFont(FontConstants.SYMBOL);
        addFont(FontConstants.TIMES_ROMAN);
        addFont(FontConstants.TIMES_BOLD);
        addFont(FontConstants.TIMES_BOLDITALIC);
        addFont(FontConstants.TIMES_ITALIC);
        addFont(FontConstants.ZAPFDINGBATS);
        return 14;
    }

    public FontSet getFontSet() {
        return fontSet;
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

    public FontSelectorStrategy getStrategy(String text, List<String> fontFamilies, int style) {
        return new ComplexFontSelectorStrategy(text, getFontSelector(fontFamilies, style), this);
    }

    public FontSelectorStrategy getStrategy(String text, List<String> fontFamilies) {
        return getStrategy(text, fontFamilies, FontConstants.UNDEFINED);
    }

    /**
     * Create {@link FontSelector} or get from cache.
     *
     * @param fontFamilies target font families
     * @param style      Shall be {@link FontConstants#UNDEFINED}, {@link FontConstants#NORMAL}, {@link FontConstants#ITALIC},
     *                   {@link FontConstants#BOLD}, or {@link FontConstants#BOLDITALIC}
     * @return an instance of {@link FontSelector}.
     * @see #createFontSelector(Set, List, int) }
     */
    public final FontSelector getFontSelector(List<String> fontFamilies, int style) {
        FontSelectorKey key = new FontSelectorKey(fontFamilies, style);
        if (fontSet.getFontSelectorCache().containsKey(key)) {
            return fontSet.getFontSelectorCache().get(key);
        } else {
            FontSelector fontSelector = createFontSelector(fontSet.getFonts(), fontFamilies, style);
            fontSet.getFontSelectorCache().put(key, fontSelector);
            return fontSelector;
        }
    }

    /**
     * Create a new instance of {@link FontSelector}. While caching is main responsibility of {@link #getFontSelector(List, int)},
     * this method just create a new instance of {@link FontSelector}.
     *
     * @param fonts      Set of all available fonts in current context.
     * @param fontFamilies target font families
     * @param style      Shall be {@link FontConstants#UNDEFINED}, {@link FontConstants#NORMAL}, {@link FontConstants#ITALIC},
     *                   {@link FontConstants#BOLD}, or {@link FontConstants#BOLDITALIC}
     * @return an instance of {@link FontSelector}.
     */
    protected FontSelector createFontSelector(Set<FontProgramInfo> fonts, List<String> fontFamilies, int style) {
        return new FontSelector(fonts, fontFamilies, style);
    }

    /**
     * Get from cache or create a new instance of {@link PdfFont}.
     *
     * @param fontInfo font info, to create {@link FontProgram} and {@link PdfFont}.
     * @return cached or new instance of {@link PdfFont}.
     * @throws IOException on I/O exceptions in {@link FontProgramFactory}.
     */
    protected PdfFont getPdfFont(FontProgramInfo fontInfo) throws IOException {
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

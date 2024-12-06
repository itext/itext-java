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
package com.itextpdf.styledxmlparser.resolver.font;

import com.itextpdf.io.util.ResourceUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.font.Range;
import com.itextpdf.layout.font.RangeBuilder;
import com.itextpdf.layout.renderer.TypographyUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

/**
 * A basic {@link FontProvider} that allows configuring in the constructor which fonts are loaded by default.
 */
public class BasicFontProvider extends FontProvider {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BasicFontProvider.class);
    private static final String DEFAULT_FONT_FAMILY = "Times";
    // This range excludes Hebrew, Arabic, Syriac, Arabic Supplement, Thaana, NKo, Samaritan,
    // Mandaic, Syriac Supplement, Arabic Extended-A, Devanagari, Bengali, Gurmukhi, Gujarati,
    // Oriya, Tamil, Telugu, Kannada, Malayalam, Sinhala, Thai unicode blocks.
    // Those blocks either require pdfCalligraph or aren't supported by GNU Free Fonts.
    private static final Range FREE_FONT_RANGE = new RangeBuilder()
            .addRange(0, 0x058F).addRange(0x0E80, Integer.MAX_VALUE).create();

    /** The path to the html2pdf shipped fonts. */
    private static final String HTML_TO_PDF_SHIPPED_FONT_RESOURCE_PATH = "com/itextpdf/html2pdf/font/";

    /** The file names of the html2pdf shipped fonts. */
    private static final String[] HTML_TO_PDF_SHIPPED_FONT_NAMES = new String[] {
            "NotoSansMono-Regular.ttf",
            "NotoSansMono-Bold.ttf",
            "NotoSans-Regular.ttf",
            "NotoSans-Bold.ttf",
            "NotoSans-BoldItalic.ttf",
            "NotoSans-Italic.ttf",
            "NotoSerif-Regular.ttf",
            "NotoSerif-Bold.ttf",
            "NotoSerif-BoldItalic.ttf",
            "NotoSerif-Italic.ttf",
    };

    //we want to add free fonts to font provider before calligraph fonts. However, the existing public API states
    // that addCalligraphFonts() should be used first to load calligraph fonts and to define the range for loading free fonts.
    // In order to maintain backward compatibility, this temporary field is used to stash calligraph fonts before free fonts are loaded.
    private final List<byte[]> calligraphyFontsTempList = new ArrayList<>();

    /** The path to the shipped fonts. */
    protected String shippedFontResourcePath;

    /** The file names of the shipped fonts. */
    protected List<String> shippedFontNames;

    /**
     * Creates a new {@link BasicFontProvider} instance.
     */
    public BasicFontProvider() {
        this(true, false);
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param registerStandardPdfFonts use true if you want to register the standard Type 1 fonts (can't be embedded)
     * @param registerSystemFonts      use true if you want to register the system fonts (can require quite some resources)
     */
    public BasicFontProvider(boolean registerStandardPdfFonts, boolean registerSystemFonts) {
        this(registerStandardPdfFonts, registerSystemFonts, DEFAULT_FONT_FAMILY);
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param registerStandardPdfFonts use true if you want to register the standard Type 1 fonts (can't be embedded)
     * @param registerShippedFonts use true if you want to register the shipped fonts (can be embedded)
     * @param registerSystemFonts use true if you want to register the system fonts (can require quite some resources)
     */
    public BasicFontProvider(boolean registerStandardPdfFonts, boolean registerShippedFonts,
                               boolean registerSystemFonts) {
        this(registerStandardPdfFonts, registerShippedFonts, registerSystemFonts, DEFAULT_FONT_FAMILY);
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param registerStandardPdfFonts use true if you want to register the standard Type 1 fonts (can't be embedded)
     * @param registerSystemFonts      use true if you want to register the system fonts (can require quite some resources)
     * @param defaultFontFamily        default font family
     */
    public BasicFontProvider(boolean registerStandardPdfFonts, boolean registerSystemFonts, String defaultFontFamily) {
        this(registerStandardPdfFonts, true, registerSystemFonts, defaultFontFamily);
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param registerStandardPdfFonts use true if you want to register the standard Type 1 fonts (can't be embedded)
     * @param registerShippedFonts use true if you want to register the shipped fonts (can be embedded)
     * @param registerSystemFonts use true if you want to register the system fonts (can require quite some resources)
     * @param defaultFontFamily default font family
     */
    public BasicFontProvider(boolean registerStandardPdfFonts, boolean registerShippedFonts,
                               boolean registerSystemFonts, String defaultFontFamily) {
        super(defaultFontFamily);
        if (registerStandardPdfFonts) {
            addStandardPdfFonts();
        }
        if (registerSystemFonts) {
            addSystemFonts();
        }
        if (registerShippedFonts) {
            initShippedFontsResourcePath();
            addAllAvailableFonts(addCalligraphFonts());
        }
    }

    /**
     * Creates a new {@link BasicFontProvider} instance.
     *
     * @param fontSet           predefined set of fonts, could be null.
     * @param defaultFontFamily default font family.
     */
    public BasicFontProvider(FontSet fontSet, String defaultFontFamily) {
        super(fontSet, defaultFontFamily);
    }

    /**
     * This method loads a list of noto fonts from pdfCalligraph (if it is present in the classpath) into FontProvider.
     * The list is the following (each font is represented in regular and bold types): NotoSansArabic, NotoSansGurmukhi,
     * NotoSansOriya, NotoSerifBengali, NotoSerifDevanagari, NotoSerifGujarati, NotoSerifHebrew, NotoSerifKannada,
     * NotoSerifKhmer, NotoSerifMalayalam, NotoSerifTamil, NotoSerifTelugu, NotoSerifThai.
     * If it's needed to have a BasicFontProvider without typography fonts loaded,
     * create an extension of BasicFontProvider and override this method, so it does nothing and only returns null.
     *
     * @return a unicode {@link Range} that excludes the loaded from pdfCalligraph fonts,
     * i.e. the unicode range that is to be rendered with any other font contained in this FontProvider
     */
    protected Range addCalligraphFonts() {
        if (TypographyUtils.isPdfCalligraphAvailable()) {
            try {
                Map<String, byte[]> fontStreams = TypographyUtils.loadShippedFonts();
                this.calligraphyFontsTempList.addAll(fontStreams.values());
                // here we return a unicode range that excludes the loaded from the calligraph module fonts
                // i.e. the unicode range that is to be rendered with standard or shipped free fonts
                return FREE_FONT_RANGE;
            } catch (Exception e) {
                LOGGER.error(StyledXmlParserLogMessageConstant.ERROR_LOADING_FONT, e);
            }
        }
        return null;
    }

    /**
     * Adds fonts shipped with the font provider.
     * For {@link BasicFontProvider} this method does nothing but can be overridden to load additional fonts.
     *
     * @param rangeToLoad a unicode {@link Range} to load characters
     */
    protected void addShippedFonts(Range rangeToLoad) {
        if (!isResourcePathAvailable()) {
            return;
        }
        for (String fontName : shippedFontNames) {
            try (InputStream stream = ResourceUtil.getResourceStream(shippedFontResourcePath + fontName)) {
                byte[] fontProgramBytes = StreamUtil.inputStreamToArray(stream);
                addFont(fontProgramBytes, null, rangeToLoad);
            } catch (Exception e) {
                LOGGER.error(StyledXmlParserLogMessageConstant.ERROR_LOADING_FONT, e);
            }
        }
    }

    /**
     * Initialize path to shipped fonts and list of font files.
     */
    protected void initShippedFontsResourcePath() {
        shippedFontResourcePath = HTML_TO_PDF_SHIPPED_FONT_RESOURCE_PATH;
        shippedFontNames = new ArrayList<>();
        //not using Collection.addAll() for auto porting
        for (String font : HTML_TO_PDF_SHIPPED_FONT_NAMES) {
            shippedFontNames.add(font);
        }
    }

    private boolean isResourcePathAvailable() {
        try (InputStream stream = ResourceUtil.getResourceStream(shippedFontResourcePath + shippedFontNames.get(0))) {
            if (stream == null) {
                return false;
            }
        } catch (IOException e) {
            //ignore this exception, since we're just checking that such resource path is available
            return false;
        }
        return true;
    }

    private void addAllAvailableFonts(Range rangeToLoad) {
        addShippedFonts(rangeToLoad);
        for(byte[] fontData : calligraphyFontsTempList) {
            addFont(fontData, null);
        }
        calligraphyFontsTempList.clear();
    }
}

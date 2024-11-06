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
package com.itextpdf.svg.processors.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.font.Range;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.css.SvgCssContext;
import com.itextpdf.svg.processors.ISvgConverterProperties;

/**
 * Context class with accessors to properties/objects used in processing Svg documents
 */
public class SvgProcessorContext {
    /**
     * The font provider.
     */
    private FontProvider fontProvider;
    /**
     * Temporary set of fonts used in the PDF.
     */
    private FontSet tempFonts;

    private final ResourceResolver resourceResolver;
    /**
     * The device description.
     */
    private MediaDeviceDescription deviceDescription;

    /**
     * The SVG CSS context.
     */
    private final SvgCssContext cssContext;

    private CssStyleSheet cssStyleSheet;

    /**
     * Instantiates a new {@link SvgProcessorContext} instance.
     *
     * @param converterProperties a {@link ISvgConverterProperties} instance
     */
    public SvgProcessorContext(ISvgConverterProperties converterProperties) {
        deviceDescription = converterProperties.getMediaDeviceDescription();
        if (deviceDescription == null) {
            deviceDescription = MediaDeviceDescription.getDefault();
        }

        fontProvider = converterProperties.getFontProvider();
        if (fontProvider == null) {
            fontProvider = new BasicFontProvider();
        }
        resourceResolver = new ResourceResolver(converterProperties.getBaseUri(),
                converterProperties.getResourceRetriever());

        cssStyleSheet = converterProperties.getCssStyleSheet();
        if (cssStyleSheet == null) {
            cssStyleSheet = new CssStyleSheet();
        }

        cssContext = new SvgCssContext();
    }

    /**
     * Gets the font provider.
     *
     * @return the font provider
     */
    public FontProvider getFontProvider() {
        return fontProvider;
    }

    /**
     * Gets the resource resolver.
     *
     * @return the resource resolver
     */
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    /**
     * Gets the device description.
     *
     * @return the device description
     */
    public MediaDeviceDescription getDeviceDescription() {
        return deviceDescription;
    }

    /**
     * Gets the temporary set of fonts.
     *
     * @return the set of fonts
     */
    public FontSet getTempFonts() {
        return tempFonts;
    }

    /**
     * Gets the SVG CSS context.
     *
     * @return the SVG CSS context
     */
    public SvgCssContext getCssContext() {
        return cssContext;
    }

    /**
     * Add temporary font from @font-face.
     *
     * @param fontProgram  the font program
     * @param encoding     the encoding
     * @param alias        the alias
     * @param unicodeRange the specific range of characters to be used from the font
     */
    public void addTemporaryFont(FontProgram fontProgram, String encoding, String alias,
            Range unicodeRange) {
        if (tempFonts == null) {
            tempFonts = new FontSet();
        }
        tempFonts.addFont(fontProgram, encoding, alias, unicodeRange);
    }

    /**
     * Add temporary font from @font-face.
     *
     * @param fontProgram the font program
     * @param encoding    the encoding
     * @param alias       the alias
     */
    public void addTemporaryFont(FontProgram fontProgram, String encoding, String alias) {
        if (tempFonts == null) tempFonts = new FontSet();
        tempFonts.addFont(fontProgram, encoding, alias);
    }

    /**
     * Add temporary font from @font-face.
     *
     * @param fontInfo the font info
     * @param alias    the alias
     */
    public void addTemporaryFont(FontInfo fontInfo, String alias) {
        if (tempFonts == null) tempFonts = new FontSet();
        tempFonts.addFont(fontInfo, alias);
    }

    /**
     * Gets the CSS style sheet.
     *
     * @return the CSS style sheet
     */
    public CssStyleSheet getCssStyleSheet() {
        return cssStyleSheet;
    }
}

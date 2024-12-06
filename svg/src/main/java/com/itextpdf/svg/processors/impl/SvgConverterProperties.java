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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

import java.nio.charset.StandardCharsets;

/**
 * Default and fallback implementation of {@link ISvgConverterProperties} for
 * {@link DefaultSvgProcessor}.
 */
public class SvgConverterProperties implements ISvgConverterProperties {
    /** The media device description. */
    private MediaDeviceDescription mediaDeviceDescription;

    /** The font provider. */
    private FontProvider fontProvider;

    /** The base URI. */
    private String baseUri = "";

    /** The resource retriever. */
    private IResourceRetriever resourceRetriever;

    private ISvgNodeRendererFactory rendererFactory;

    private String charset = StandardCharsets.UTF_8.name();

    private CssStyleSheet cssStyleSheet = null;

    private Rectangle customViewport = null;

    /**
     * Creates a new {@link SvgConverterProperties} instance.
     * Instantiates its members, IResourceRetriever and ISvgNodeRendererFactory, to its default implementations.
     */
    public SvgConverterProperties() {
        this.resourceRetriever = new DefaultResourceRetriever();
        this.rendererFactory = new DefaultSvgNodeRendererFactory();
    }

    /**
     * Gets the custom viewport of SVG.
     * <p>
     * The custom viewport is used to resolve percent values of the top level svg.
     *
     * @return the custom viewport
     */
    public Rectangle getCustomViewport() {
        // TODO DEVSIX-8808 add this getter to the interface ISvgConverterProperties and remove class casting where getCustomViewport is called
        return customViewport;
    }

    /**
     * Sets the custom viewport of SVG.
     * <p>
     * The custom viewport is used to resolve percent values of the top level svg.
     *
     * @param customViewport the custom viewport
     */
    public void setCustomViewport(Rectangle customViewport) {
        this.customViewport = customViewport;
    }

    public SvgConverterProperties setRendererFactory(ISvgNodeRendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;
        return this;
    }

    public SvgConverterProperties setFontProvider(FontProvider fontProvider) {
        this.fontProvider = fontProvider;
        return this;
    }

    @Override
    public ISvgNodeRendererFactory getRendererFactory() {
        return this.rendererFactory;
    }

    @Override
    public String getCharset() {
        // may also return null, but null will always default to UTF-8 in JSoup
        return charset;
    }

    public SvgConverterProperties setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Gets the base URI.
     *
     * @return the base URI
     */
    @Override
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * Gets the font provider.
     *
     * @return the font provider
     */
    @Override
    public FontProvider getFontProvider() {
        return fontProvider;
    }

    /**
     * Gets the media device description.
     *
     * @return the media device description
     */
    @Override
    public MediaDeviceDescription getMediaDeviceDescription() {
        return mediaDeviceDescription;
    }

    /**
     * Sets the media device description.
     *
     * @param mediaDeviceDescription the media device description
     * @return the ConverterProperties instance
     */
    public SvgConverterProperties setMediaDeviceDescription(MediaDeviceDescription mediaDeviceDescription) {
        this.mediaDeviceDescription = mediaDeviceDescription;
        return this;
    }

    /**
     * Sets the base URI.
     *
     * @param baseUri the base URI
     * @return the ConverterProperties instance
     */
    public SvgConverterProperties setBaseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    @Override
    public IResourceRetriever getResourceRetriever() {
        return resourceRetriever;
    }

    /**
     * Sets the resource retriever.
     *
     * The resourceRetriever is used to retrieve data from resources by URL.
     *
     * @param resourceRetriever the resource retriever
     * @return the {@link SvgConverterProperties} instance
     */
    public SvgConverterProperties setResourceRetriever(IResourceRetriever resourceRetriever) {
        this.resourceRetriever = resourceRetriever;
        return this;
    }

    @Override
    public CssStyleSheet getCssStyleSheet() {
        return cssStyleSheet;
    }

    /**
     * Sets the CSS style sheet.
     * Style sheet is used to apply CSS statements to elements.
     *
     * @param cssStyleSheet the CSS style sheet
     *
     * @return the {@link SvgConverterProperties} instance
     */
    public SvgConverterProperties setCssStyleSheet(CssStyleSheet cssStyleSheet) {
        this.cssStyleSheet = cssStyleSheet;
        return this;
    }
}

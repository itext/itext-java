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
package com.itextpdf.svg.processors;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

import java.nio.charset.Charset;

/**
 * Interface for the configuration classes used by {@link ISvgProcessor}
 */
public interface ISvgConverterProperties {

    /**
     * Retrieve the factory responsible for creating
     * {@link com.itextpdf.svg.renderers.ISvgNodeRenderer}
     *
     * @return A {@link ISvgNodeRendererFactory} implementation
     */
    ISvgNodeRendererFactory getRendererFactory();

    /**
     * Gets the font provider.
     *
     * @return the font provider
     */
    FontProvider getFontProvider();

    /**
     * Get the name of the Charset to be used when decoding an InputStream. This
     * method is allowed to return null, in which case {@code UTF-8} will
     * be used (by JSoup).
     * <p>
     * Please be aware that this method is NOT used when handling a
     * {@code String} variable in the {@link SvgConverter}.
     *
     * @return the String name of the {@link Charset} used for decoding
     */
    String getCharset();


    /**
     * Gets the base URI.
     *
     * @return the base URI
     */
    String getBaseUri();

    /**
     * Gets the media device description.
     *
     * @return the media device description
     */
    MediaDeviceDescription getMediaDeviceDescription();

    /**
     * Gets the resource retriever.
     * The resourceRetriever is used to retrieve data from resources by URL.
     *
     * @return the resource retriever
     */
    IResourceRetriever getResourceRetriever();

    /**
     * Gets the CSS style sheet.
     * Style sheet is used to apply CSS statements to elements.
     *
     * @return the CSS style sheet
     */
    CssStyleSheet getCssStyleSheet();
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.processors.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
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

    private ResourceResolver resourceResolver;
    /**
     * The device description.
     */
    private MediaDeviceDescription deviceDescription;

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

        String baseUri = converterProperties.getBaseUri();
        if (baseUri == null) {
            baseUri = "";
        }
        //TODO DEVSIX-2095
        resourceResolver = new ResourceResolver(baseUri);
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
}

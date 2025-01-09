/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.svg.dummy.processors.impl;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.CssStyleSheet;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;
import com.itextpdf.svg.dummy.factories.DummySvgNodeFactory;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

public class DummySvgConverterProperties implements ISvgConverterProperties {
    ISvgNodeRendererFactory rendererFactory;
    String baseUri;
    MediaDeviceDescription mediaDeviceDescription;
    IResourceRetriever resourceRetriever;

    public DummySvgConverterProperties(){
        rendererFactory = new DummySvgNodeFactory();
        mediaDeviceDescription = new MediaDeviceDescription( "" );
        baseUri = "";
        resourceRetriever = new DefaultResourceRetriever();
    }

    @Override
    public ISvgNodeRendererFactory getRendererFactory() {
        return rendererFactory;
    }

    @Override
    public FontProvider getFontProvider() {
        return null;
    }

    @Override
    public String getCharset() {
        return null;
    }

    @Override
    public String getBaseUri() {
        return baseUri;
    }

    public DummySvgConverterProperties setBaseUri(String baseUri) {
        return this;
    }

    @Override
    public MediaDeviceDescription getMediaDeviceDescription() {
        return mediaDeviceDescription;
    }

    @Override
    public IResourceRetriever getResourceRetriever() {
        return resourceRetriever;
    }

    @Override
    public CssStyleSheet getCssStyleSheet() {
        return null;
    }

    public DummySvgConverterProperties setMediaDeviceDescription(MediaDeviceDescription mediaDeviceDescription) {
        return this;
    }

    public DummySvgConverterProperties setFontProvider(FontProvider fontProvider) {
        return this;
    }
}

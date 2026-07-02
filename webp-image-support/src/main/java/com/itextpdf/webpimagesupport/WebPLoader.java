/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.webpimagesupport;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.AbstractWebPLoader;
import com.itextpdf.io.image.ImageDataFactory;

import java.net.URL;

/**
 * The class for WebP image data handling and plugging in webp-image-support module.
 */
final class WebPLoader extends AbstractWebPLoader {

    private WebPLoader() {
        // do nothing
    }

    /**
     * Register webp-image-support module.
     */
    public static void registerForIo() {
        ImageDataFactory.setWebPLoaderInstance(new WebPLoader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImageData getImageData(byte[] bytes) {
        return new WebPImageData(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImageData getImageData(URL url) {
        return new WebPImageData(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWebPSupported() {
        return true;
    }
}

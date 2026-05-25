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
package com.itextpdf.io.image;

import java.net.URL;

/**
 * An abstract class to control WebP image data handling.
 */
public abstract class AbstractWebPLoader {

    /**
     * Creates an instance of {@link AbstractWebPLoader}.
     */
    protected AbstractWebPLoader() {
        // do nothing
    }

    /**
     * Gets {@link ImageData} from provided WebP raw image bytes.
     *
     * @param bytes raw bytes to create WebP image data from
     *
     * @return a new WebP {@link ImageData} from raw bytes
     */
    protected abstract ImageData getImageData(byte[] bytes);

    /**
     * Gets {@link ImageData} from provided WebP URL.
     *
     * @param url URL to create WebP image data from
     *
     * @return a new WebP {@link ImageData} from URL
     */
    protected abstract ImageData getImageData(URL url);

    /**
     * Checks if webp-image-support module is loaded.
     *
     * @return {@code true} if webp-image-support module is loaded
     */
    protected abstract boolean isWebPSupported();
}

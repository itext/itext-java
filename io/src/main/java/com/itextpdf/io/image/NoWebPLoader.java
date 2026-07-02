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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * A no-op class for WebP image data handling.
 */
public final class NoWebPLoader extends AbstractWebPLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoWebPLoader.class);

    /**
     * Standard constructor.
     */
    NoWebPLoader() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImageData getImageData(byte[] bytes) {
        LOGGER.warn(WebPLogMessageConstant.WEBP_NOT_FOUND);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImageData getImageData(URL url) {
        LOGGER.warn(WebPLogMessageConstant.WEBP_NOT_FOUND);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWebPSupported() {
        LOGGER.warn(WebPLogMessageConstant.WEBP_NOT_FOUND);
        return false;
    }
}

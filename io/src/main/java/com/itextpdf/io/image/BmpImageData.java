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
package com.itextpdf.io.image;

import java.net.URL;

public class BmpImageData extends RawImageData {

    private final boolean noHeader;

    /**
     * Creates instance of {@link BmpImageData}
     * @param url url of the image
     * @param noHeader indicates that the source image does not have a header
     */
    protected BmpImageData(URL url, boolean noHeader) {
        super(url, ImageType.BMP);
        this.noHeader = noHeader;
    }

    /**
     * Creates instance of {@link BmpImageData}
     * @param bytes contents of the image
     * @param noHeader indicates that the source image does not have a header
     */
    protected BmpImageData(byte[] bytes, boolean noHeader) {
        super(bytes, ImageType.BMP);
        this.noHeader = noHeader;
    }

    /**
     * @return True if the bitmap image does not contain a header
     */
    public boolean isNoHeader() {
        return noHeader;
    }
}

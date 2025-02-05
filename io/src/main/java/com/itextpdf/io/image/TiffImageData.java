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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.codec.TIFFDirectory;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RandomAccessSourceFactory;

import java.net.URL;

public class TiffImageData extends RawImageData {

    private boolean recoverFromImageError;
    private int page;
    private boolean direct;

    protected TiffImageData(URL url, boolean recoverFromImageError, int page, boolean direct) {
        super(url, ImageType.TIFF);
        this.recoverFromImageError = recoverFromImageError;
        this.page = page;
        this.direct = direct;
    }

    protected TiffImageData(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        super(bytes, ImageType.TIFF);
        this.recoverFromImageError = recoverFromImageError;
        this.page = page;
        this.direct = direct;
    }

    private static ImageData getImage(URL url, boolean recoverFromImageError, int page, boolean direct) {
        return new TiffImageData(url, recoverFromImageError, page, direct);
    }

    private static ImageData getImage(byte[] bytes, boolean recoverFromImageError, int page, boolean direct) {
        return new TiffImageData(bytes, recoverFromImageError, page, direct);
    }

    /**
     * Gets the number of pages the TIFF document has.
     * @param raf a {@code RandomAccessFileOrArray} containing a TIFF image.
     * @return the number of pages.
     */
    public static int getNumberOfPages(RandomAccessFileOrArray raf) {
        try {
            return TIFFDirectory.getNumDirectories(raf);
        } catch (Exception e) {
            throw new IOException(IoExceptionMessageConstant.TIFF_IMAGE_EXCEPTION, e);
        }
    }

    /** Gets the number of pages the TIFF document has.
     * @param bytes	a byte array containing a TIFF image.
     * @return the number of pages.
     */
    public static int getNumberOfPages(byte[] bytes) {
        IRandomAccessSource ras = new RandomAccessSourceFactory().createSource(bytes);
        return getNumberOfPages(new RandomAccessFileOrArray(ras));
    }

    public boolean isRecoverFromImageError() {
        return recoverFromImageError;
    }

    public int getPage() {
        return page;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setOriginalType(ImageType originalType) {
        this.originalType = originalType;
    }
}

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
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.codec.Jbig2SegmentReader;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RandomAccessSourceFactory;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jbig2ImageData extends ImageData {

    private int page;

    protected Jbig2ImageData(URL url, int page) {
        super(url, ImageType.JBIG2);
        this.page = page;
    }

    protected Jbig2ImageData(byte[] bytes, int page) {
        super(bytes, ImageType.JBIG2);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    /**
     * Gets the number of pages in a JBIG2 image.
     * @param bytes	a byte array containing a JBIG2 image
     * @return the number of pages
     */
    public static int getNumberOfPages(byte[] bytes) {
        IRandomAccessSource ras = new RandomAccessSourceFactory().createSource(bytes);
        return getNumberOfPages(new RandomAccessFileOrArray(ras));
    }

    /**
     * Gets the number of pages in a JBIG2 image.
     * @param raf a {@code RandomAccessFileOrArray} containing a JBIG2 image
     * @return the number of pages
     */
    public static int getNumberOfPages(RandomAccessFileOrArray raf) {
        try {
            Jbig2SegmentReader sr = new Jbig2SegmentReader(raf);
            sr.read();
            return sr.numberOfPages();
        } catch (Exception e) {
            throw new IOException(IoExceptionMessageConstant.JBIG2_IMAGE_EXCEPTION, e);
        }
    }

    @Override
    public boolean canImageBeInline() {
        Logger logger = LoggerFactory.getLogger(ImageData.class);
        logger.warn(IoLogMessageConstant.IMAGE_HAS_JBIG2DECODE_FILTER);
        return false;
    }
}

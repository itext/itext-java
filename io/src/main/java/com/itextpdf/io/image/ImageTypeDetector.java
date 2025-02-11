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
import com.itextpdf.io.util.UrlUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Helper class that detects image type by magic bytes
 */
public final class ImageTypeDetector {

    private static final byte[] gif = new byte[]{(byte) 'G', (byte) 'I', (byte) 'F'};
    private static final byte[] jpeg = new byte[]{(byte) 0xFF, (byte) 0xD8};
    private static final byte[] jpeg2000_1 = new byte[]{0x00, 0x00, 0x00, 0x0c};
    private static final byte[] jpeg2000_2 = new byte[]{(byte) 0xff, (byte) 0x4f, (byte) 0xff, 0x51};
    private static final byte[] png = new byte[]{(byte) 137, 80, 78, 71};
    private static final byte[] wmf = new byte[]{(byte) 0xD7, (byte) 0xCD};
    private static final byte[] bmp = new byte[]{(byte) 'B', (byte) 'M'};
    private static final byte[] tiff_1 = new byte[]{(byte) 'M', (byte) 'M', 0, 42};
    private static final byte[] tiff_2 = new byte[]{(byte) 'I', (byte) 'I', 42, 0};
    private static final byte[] jbig2 = new byte[]{(byte) 0x97, (byte) 'J', (byte) 'B', (byte) '2', (byte) '\r', (byte) '\n', 0x1a, (byte) '\n'};

    private ImageTypeDetector() {
    }

    /**
     * Detect image type by magic bytes given the byte array source.
     *
     * @param source image bytes
     * @return detected image type, see{@link ImageType}. Returns {@link ImageType#NONE} if image type is unknown
     */
    public static ImageType detectImageType(byte[] source) {
        byte[] header = readImageType(source);
        return detectImageTypeByHeader(header);
    }

    /**
     * Detect image type by magic bytes given the source URL.
     *
     * @param source image URL
     * @return detected image type, see{@link ImageType}. Returns {@link ImageType#NONE} if image type is unknown
     */
    public static ImageType detectImageType(URL source) {
        byte[] header = readImageType(source);
        return detectImageTypeByHeader(header);
    }

    /**
     * Detect image type by magic bytes given the input stream.
     *
     * @param stream image stream
     * @return detected image type, see{@link ImageType}. Returns {@link ImageType#NONE} if image type is unknown
     */
    public static ImageType detectImageType(InputStream stream) {
        byte[] header = readImageType(stream);
        return detectImageTypeByHeader(header);
    }

    private static ImageType detectImageTypeByHeader(byte[] header) {
        if (imageTypeIs(header, gif)) {
            return ImageType.GIF;
        } else if (imageTypeIs(header, jpeg)) {
            return ImageType.JPEG;
        } else if (imageTypeIs(header, jpeg2000_1) || imageTypeIs(header, jpeg2000_2)) {
            return ImageType.JPEG2000;
        } else if (imageTypeIs(header, png)) {
            return ImageType.PNG;
        } else if (imageTypeIs(header, bmp)) {
            return ImageType.BMP;
        } else if (imageTypeIs(header, tiff_1) || imageTypeIs(header, tiff_2)) {
            return ImageType.TIFF;
        } else if (imageTypeIs(header, jbig2)) {
            return ImageType.JBIG2;
        } else if (imageTypeIs(header, wmf)) {
            return ImageType.WMF;
        }
        return ImageType.NONE;
    }

    private static boolean imageTypeIs(byte[] imageType, byte[] compareWith) {
        for (int i = 0; i < compareWith.length; i++) {
            if (imageType[i] != compareWith[i])
                return false;
        }
        return true;
    }

    private static byte[] readImageType(URL source) {
        try (InputStream stream = UrlUtil.openStream(source)) {
            return readImageType(stream);
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.IO_EXCEPTION, e);
        }
    }

    private static byte[] readImageType(InputStream stream) {
        try {
            byte[] bytes = new byte[8];
            stream.read(bytes);
            return bytes;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.IO_EXCEPTION, e);
        }
    }

    private static byte[] readImageType(byte[] source) {
        try {
            InputStream stream = new ByteArrayInputStream(source);
            byte[] bytes = new byte[8];
            stream.read(bytes);
            return bytes;
        } catch (java.io.IOException e) {
            return null;
        }
    }

}

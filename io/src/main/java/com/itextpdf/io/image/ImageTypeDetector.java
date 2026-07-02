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

    private static final byte[] GIF = new byte[]{(byte) 'G', (byte) 'I', (byte) 'F'};
    private static final byte[] JPEG = new byte[]{(byte) 0xFF, (byte) 0xD8};
    private static final byte[] JPEG_2000_1 = new byte[]{0x00, 0x00, 0x00, 0x0c};
    private static final byte[] JPEG_2000_2 = new byte[]{(byte) 0xff, (byte) 0x4f, (byte) 0xff, 0x51};
    private static final byte[] PNG = new byte[]{(byte) 137, 80, 78, 71};
    private static final byte[] WMF = new byte[]{(byte) 0xD7, (byte) 0xCD};
    private static final byte[] BMP = new byte[]{(byte) 'B', (byte) 'M'};
    private static final byte[] TIFF_1 = new byte[]{(byte) 'M', (byte) 'M', 0, 42};
    private static final byte[] TIFF_2 = new byte[]{(byte) 'I', (byte) 'I', 42, 0};
    private static final byte[] JBIG_2 = new byte[]{(byte) 0x97, (byte) 'J', (byte) 'B', (byte) '2', (byte) '\r', (byte) '\n', 0x1a, (byte) '\n'};
    //WebP header only needs to be checked for bytes 0 - 3 and 8 - 11, picture size should be in between
    private static final byte[] WEBP = new byte[]{(byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F',
            0x00, 0x00, 0x00, 0x00, (byte) 'W', (byte) 'E', (byte) 'B', (byte) 'P'};

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
        if (imageTypeIs(header, GIF)) {
            return ImageType.GIF;
        } else if (imageTypeIs(header, JPEG)) {
            return ImageType.JPEG;
        } else if (imageTypeIs(header, JPEG_2000_1) || imageTypeIs(header, JPEG_2000_2)) {
            return ImageType.JPEG2000;
        } else if (imageTypeIs(header, PNG)) {
            return ImageType.PNG;
        } else if (imageTypeIs(header, BMP)) {
            return ImageType.BMP;
        } else if (imageTypeIs(header, TIFF_1) || imageTypeIs(header, TIFF_2)) {
            return ImageType.TIFF;
        } else if (imageTypeIs(header, JBIG_2)) {
            return ImageType.JBIG2;
        } else if (imageTypeIs(header, WMF)) {
            return ImageType.WMF;
        } else if (imageTypeIsWebP(header)) {
            return ImageType.WEBP;
        } else {
            return ImageType.NONE;
        }
    }

    private static boolean imageTypeIs(byte[] imageType, byte[] compareWith) {
        for (int i = 0; i < compareWith.length; i++) {
            if (imageType[i] != compareWith[i])
                return false;
        }

        return true;
    }

    private static boolean imageTypeIsWebP(byte[] imageType) {
        //WebP header only needs to be checked for bytes 0 - 3 and 8 - 11, picture size should be in between
        for (int i = 0; i < 3; i++) {
            if (imageType[i] != WEBP[i]) {
                return false;
            }
        }

        for (int i = 8; i < 11; i++) {
            if (imageType[i] != WEBP[i]) {
                return false;
            }
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
            byte[] bytes = new byte[12];
            stream.read(bytes);
            return bytes;
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.IO_EXCEPTION, e);
        }
    }

    private static byte[] readImageType(byte[] source) {
        try {
            InputStream stream = new ByteArrayInputStream(source);
            byte[] bytes = new byte[12];
            stream.read(bytes);
            return bytes;
        } catch (java.io.IOException e) {
            return null;
        }
    }

}

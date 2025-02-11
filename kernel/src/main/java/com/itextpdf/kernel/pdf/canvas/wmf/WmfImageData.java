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
package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Image implementation for WMF, Windows Metafile.
 */
public class WmfImageData extends ImageData {

    private static final byte[] wmf = new byte[]{(byte) 0xD7, (byte) 0xCD};

    /**
     * Creates a WmfImage from a file.
     *
     * @param fileName pah to the file
     * @throws MalformedURLException a {@link MalformedURLException}
     */
    public WmfImageData(String fileName) throws MalformedURLException {
        this(UrlUtil.toURL(fileName));
    }

    /**
     * Creates a WmfImage from a URL.
     *
     * @param url URL to the file
     */
    public WmfImageData(URL url) {
        super(url, ImageType.WMF);
        byte[] imageType = readImageType(url);
        if (!imageTypeIs(imageType, wmf)) {
            throw new PdfException(KernelExceptionMessageConstant.NOT_A_WMF_IMAGE);
        }
    }

    /**
     * Creates a WmfImage from a byte[].
     *
     * @param bytes the image bytes
     */
    public WmfImageData(byte[] bytes) {
        super(bytes, ImageType.WMF);
        byte[] imageType = readImageType(bytes);
        if (!imageTypeIs(imageType, wmf)) {
            throw new PdfException(KernelExceptionMessageConstant.NOT_A_WMF_IMAGE);
        }
    }

    private static boolean imageTypeIs(byte[] imageType, byte[] compareWith) {
        for (int i = 0; i < compareWith.length; i++) {
            if (imageType[i] != compareWith[i])
                return false;
        }
        return true;
    }

    private static byte[] readImageType(URL source) {
        InputStream is = null;
        try {
            is = source.openStream();
            byte[] bytes = new byte[8];
            is.read(bytes);
            return bytes;
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.IO_EXCEPTION, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }

    }
    
    private static byte[] readImageType(byte[] bytes) {
    	return Arrays.copyOfRange(bytes, 0, 8);
    }
}

/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.PdfException;

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
     * @throws MalformedURLException
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
            throw new PdfException(PdfException.NotAWmfImage);
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
            throw new PdfException(PdfException.NotAWmfImage);
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
            throw new PdfException(PdfException.IoException, e);
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

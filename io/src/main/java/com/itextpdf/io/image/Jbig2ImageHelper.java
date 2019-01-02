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
package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.codec.Jbig2SegmentReader;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RandomAccessSourceFactory;

import java.util.HashMap;
import java.util.Map;

class Jbig2ImageHelper {

    private byte[] globals;

    /**
     * Gets a byte array that can be used as a /JBIG2Globals,
     * or null if not applicable to the given jbig2.
     * @param	ra	an random access file or array
     * @return	a byte array
     */
    public static byte[] getGlobalSegment(RandomAccessFileOrArray ra ) {
        try {
            Jbig2SegmentReader sr = new Jbig2SegmentReader(ra);
            sr.read();
            return sr.getGlobal(true);
        } catch (Exception e) {
            return null;
        }
    }

    public static void processImage(ImageData jbig2) {
        if (jbig2.getOriginalType() != ImageType.JBIG2)
            throw new IllegalArgumentException("JBIG2 image expected");
        Jbig2ImageData image = (Jbig2ImageData)jbig2;
        try {
            IRandomAccessSource ras;
            if (image.getData() == null) {
                image.loadData();
            }
            ras = new RandomAccessSourceFactory().createSource(image.getData());
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray(ras);
            Jbig2SegmentReader sr = new Jbig2SegmentReader(raf);
            sr.read();
            Jbig2SegmentReader.Jbig2Page p = sr.getPage(image.getPage());
            raf.close();

            image.setHeight(p.pageBitmapHeight);
            image.setWidth(p.pageBitmapWidth);
            image.setBpc(1);
            image.setColorSpace(1);
            //TODO JBIG2 globals caching
            byte[] globals = sr.getGlobal(true);

            //TODO due to the fact, that streams now may be transformed to indirect objects only on writing,
            //pdfStream.getDocument() cannot longer be the sign of inline/indirect images

            // in case inline image pdfStream.getDocument() will be null
            if (globals != null /*&& stream.getDocument() != null*/) {
                Map<String, Object> decodeParms = new HashMap<>();
//                PdfStream globalsStream = new PdfStream().makeIndirect(pdfStream.getDocument());
//                globalsStream.getOutputStream().write(globals);
                decodeParms.put("JBIG2Globals", globals);
                image.decodeParms = decodeParms;
            }

            image.setFilter("JBIG2Decode");
            image.setColorSpace(1);
            image.setBpc(1);
            image.data = p.getData(true);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.Jbig2ImageException, e);
        }
    }
}

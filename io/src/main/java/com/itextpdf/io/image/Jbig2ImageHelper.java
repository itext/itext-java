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

import com.itextpdf.io.codec.Jbig2SegmentReader;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.exceptions.IoExceptionMessageConstant;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RandomAccessFileOrArray;
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

            image.setHeight(p.getPageBitmapHeight());
            image.setWidth(p.getPageBitmapWidth());
            image.setBpc(1);
            image.setColorEncodingComponentsNumber(1);

            byte[] globals = sr.getGlobal(true);

            if (globals != null) {
                Map<String, Object> decodeParms = new HashMap<>();
                decodeParms.put("JBIG2Globals", globals);
                image.decodeParms = decodeParms;
            }

            image.setFilter("JBIG2Decode");
            image.setColorEncodingComponentsNumber(1);
            image.setBpc(1);
            image.data = p.getData(true);
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.JBIG2_IMAGE_EXCEPTION, e);
        }
    }
}

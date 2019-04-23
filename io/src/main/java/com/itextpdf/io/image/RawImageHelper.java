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
import com.itextpdf.io.codec.CCITTG4Encoder;
import com.itextpdf.io.codec.TIFFFaxDecoder;

import java.util.HashMap;
import java.util.Map;

public final class RawImageHelper {

    public static void updateImageAttributes(RawImageData image, Map<String, Object> additional) {
        if (!image.isRawImage())
            throw new IllegalArgumentException("Raw image expected.");
        // will also have the CCITT parameters
        int colorSpace = image.getColorSpace();
        int typeCCITT = image.getTypeCcitt();
        if (typeCCITT > 0xff) {
            if (!image.isMask())
                image.setColorSpace(1);
            image.setBpc(1);
            image.setFilter("CCITTFaxDecode");
            int k = typeCCITT - RawImageData.CCITTG3_1D;
            Map<String, Object> decodeparms = new HashMap<>();
            if (k != 0)
                decodeparms.put("K", k);
            if ((colorSpace & RawImageData.CCITT_BLACKIS1) != 0)
                decodeparms.put("BlackIs1", true);
            if ((colorSpace & RawImageData.CCITT_ENCODEDBYTEALIGN) != 0)
                decodeparms.put("EncodedByteAlign", true);
            if ((colorSpace & RawImageData.CCITT_ENDOFLINE) != 0)
                decodeparms.put("EndOfLine", true);
            if ((colorSpace & RawImageData.CCITT_ENDOFBLOCK) != 0)
                decodeparms.put("EndOfBlock", false);
            decodeparms.put("Columns", image.getWidth());
            decodeparms.put("Rows", image.getHeight());
            image.decodeParms = decodeparms;
        } else {
            switch (colorSpace) {
                case 1:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0};
                    break;
                case 3:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0, 1, 0, 1, 0};
                    break;
                case 4:
                default:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0, 1, 0, 1, 0, 1, 0};
            }
            if (additional != null) {
                image.setImageAttributes(additional);
            }
            if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8))
                image.setColorSpace(-1);
            if (image.isDeflated()) {
                image.setFilter("FlateDecode");
            }
        }
    }

    /**
     * Update original image with Raw Image parameters.
     *
     * @param image
     * @param width the exact width of the image
     * @param height the exact height of the image
     * @param components 1,3 or 4 for GrayScale, RGB and CMYK
     * @param bpc bits per component. Must be 1,2,4 or 8
     * @param data the image data
     * @throws IOException on error
     */
    protected static void updateRawImageParameters(RawImageData image, int width, int height, int components,
                                                   int bpc, byte[] data) {
        image.setHeight(height);
        image.setWidth(width);
        if (components != 1 && components != 3 && components != 4)
            throw new IOException(IOException.ComponentsMustBe1_3Or4);
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8)
            throw new IOException(IOException.BitsPerComponentMustBe1_2_4or8);
        image.setColorSpace(components);
        image.setBpc(bpc);
        image.data = data;
    }

    protected static void updateRawImageParameters(RawImageData image, int width, int height, int components,
                                                int bpc, byte[] data, int[] transparency) {
        if (transparency != null && transparency.length != components * 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (components == 1 && bpc == 1) {
            byte[] g4 = CCITTG4Encoder.compress(data, width, height);
            updateRawImageParameters(image, width, height, false, RawImageData.CCITTG4,
                    RawImageData.CCITT_BLACKIS1, g4, transparency);
        } else {
            updateRawImageParameters(image, width, height, components, bpc, data);
            image.setTransparency(transparency);
        }
    }

    protected static void updateRawImageParameters(RawImageData image, int width, int height, boolean reverseBits,
                                                int typeCCITT, int parameters, byte[] data, int[] transparency) {
        if (transparency != null && transparency.length != 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        updateCcittImageParameters(image, width, height, reverseBits, typeCCITT, parameters, data);
        image.setTransparency(transparency);
    }

    protected static void updateCcittImageParameters(RawImageData image, int width, int height, boolean reverseBits, int typeCcitt, int parameters, byte[] data) {
        if (typeCcitt != RawImageData.CCITTG4 && typeCcitt != RawImageData.CCITTG3_1D && typeCcitt != RawImageData.CCITTG3_2D)
            throw new IOException(IOException.CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d);
        if (reverseBits)
            TIFFFaxDecoder.reverseBits(data);
        image.setHeight(height);
        image.setWidth(width);
        image.setColorSpace(parameters);
        image.setTypeCcitt(typeCcitt);
        image.data = data;
    }
}

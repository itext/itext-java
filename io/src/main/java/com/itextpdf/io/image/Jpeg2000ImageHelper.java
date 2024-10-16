/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.io.image.Jpeg2000ImageData.ColorSpecBox;
import com.itextpdf.io.util.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

final class Jpeg2000ImageHelper {

    private static class Jpeg2000Box {
        int length;
        int type;
    }

    private static class ZeroBoxSizeException extends java.io.IOException {
        ZeroBoxSizeException(String s) {
            super(s);
        }
    }

    private static final int JPIP_JPIP = 0x6a706970;

    private static final int JP2_JP = 0x6a502020;
    private static final int JP2_IHDR = 0x69686472;
    private static final int JP2_FTYP = 0x66747970;
    private static final int JP2_JP2H = 0x6a703268;
    private static final int JP2_COLR = 0x636f6c72;
    private static final int JP2_JP2C = 0x6a703263;
    private static final int JP2_URL = 0x75726c20;
    private static final int JP2_DBTL = 0x6474626c;
    private static final int JP2_BPCC = 0x62706363;
    private static final int JP2_JP2 = 0x6a703220;

    private static final int JPX_JPXB = 0x6a707862;

    public static void processImage(ImageData image) {
        if (image.getOriginalType() != ImageType.JPEG2000)
            throw new IllegalArgumentException("JPEG2000 image expected");
        processParameters((Jpeg2000ImageData) image);
        image.setFilter("JPXDecode");
    }

    /**
     * This method checks if the image is a valid JPEG and processes some parameters.
     */
    private static void processParameters(Jpeg2000ImageData jp2) {
        jp2.parameters = new Jpeg2000ImageData.Parameters();
        try {
            if (jp2.getData() == null) {
                jp2.loadData();
            }
            InputStream jpeg2000Stream = new ByteArrayInputStream(jp2.getData());
            Jpeg2000Box box = new Jpeg2000Box();
            box.length = cio_read(4, jpeg2000Stream);
            if (box.length == 0x0000000c) {
                jp2.parameters.setJp2(true);
                box.type = cio_read(4, jpeg2000Stream);
                if (JP2_JP != box.type) {
                    throw new IOException(IoExceptionMessageConstant.EXPECTED_JP_MARKER);
                }
                if (0x0d0a870a != cio_read(4, jpeg2000Stream)) {
                    throw new IOException(IoExceptionMessageConstant.ERROR_WITH_JP_MARKER);
                }

                jp2_read_boxhdr(box, jpeg2000Stream);
                if (JP2_FTYP != box.type) {
                    throw new IOException(IoExceptionMessageConstant.EXPECTED_FTYP_MARKER);
                }
                StreamUtil.skip(jpeg2000Stream, 8);
                for (int i = 4; i < box.length / 4; ++i) {
                    if (cio_read(4, jpeg2000Stream) == JPX_JPXB) {
                        jp2.parameters.setJpxBaseline(true);
                    }
                }

                jp2_read_boxhdr(box, jpeg2000Stream);
                do {
                    if (JP2_JP2H != box.type) {
                        if (box.type == JP2_JP2C) {
                            throw new IOException(IoExceptionMessageConstant.EXPECTED_JP2H_MARKER);
                        }
                        StreamUtil.skip(jpeg2000Stream, box.length - 8);
                        jp2_read_boxhdr(box, jpeg2000Stream);
                    }
                } while (JP2_JP2H != box.type);
                jp2_read_boxhdr(box, jpeg2000Stream);
                if (JP2_IHDR != box.type) {
                    throw new IOException(IoExceptionMessageConstant.EXPECTED_IHDR_MARKER);
                }
                jp2.setHeight(cio_read(4, jpeg2000Stream));
                jp2.setWidth(cio_read(4, jpeg2000Stream));
                jp2.parameters.setNumOfComps(cio_read(2, jpeg2000Stream));
                jp2.setBpc(cio_read(1, jpeg2000Stream));
                StreamUtil.skip(jpeg2000Stream, 3);
                jp2_read_boxhdr(box, jpeg2000Stream);
                if (box.type == JP2_BPCC) {
                    jp2.parameters.setBpcBoxData(new byte[box.length - 8]);
                    jpeg2000Stream.read(jp2.parameters.getBpcBoxData(), 0, box.length - 8);
                } else if (box.type == JP2_COLR) {
                    do {
                        if (jp2.parameters.getColorSpecBoxes() == null)
                            jp2.parameters.setColorSpecBoxes(new ArrayList<>());
                        List<ColorSpecBox> colorSpecBoxes = jp2.parameters.getColorSpecBoxes();
                        colorSpecBoxes.add(jp2_read_colr(box, jpeg2000Stream));
                        jp2.parameters.setColorSpecBoxes(colorSpecBoxes);
                        try {
                            jp2_read_boxhdr(box, jpeg2000Stream);
                        } catch (ZeroBoxSizeException ioe) {
                            //Probably we have reached the contiguous codestream box which is the last in jpeg2000 and has no length.
                        }
                    } while (JP2_COLR == box.type);
                }
            } else if (box.length == 0xff4fff51) {
                StreamUtil.skip(jpeg2000Stream, 4);
                int x1 = cio_read(4, jpeg2000Stream);
                int y1 = cio_read(4, jpeg2000Stream);
                int x0 = cio_read(4, jpeg2000Stream);
                int y0 = cio_read(4, jpeg2000Stream);
                StreamUtil.skip(jpeg2000Stream, 16);
                jp2.setColorEncodingComponentsNumber(cio_read(2, jpeg2000Stream));
                jp2.setBpc(8);
                jp2.setHeight(y1 - y0);
                jp2.setWidth(x1 - x0);
            } else {
                throw new IOException(IoExceptionMessageConstant.INVALID_JPEG2000_FILE);
            }
        } catch (java.io.IOException e) {
            throw new IOException(IoExceptionMessageConstant.JPEG2000_IMAGE_EXCEPTION, e);
        }
    }

    private static Jpeg2000ImageData.ColorSpecBox jp2_read_colr(Jpeg2000Box box, InputStream jpeg2000Stream) throws java.io.IOException {
        int readBytes = 8;
        Jpeg2000ImageData.ColorSpecBox colorSpecBox = new Jpeg2000ImageData.ColorSpecBox();
        for (int i = 0; i < 3; i++) {
            colorSpecBox.add(cio_read(1, jpeg2000Stream));
            readBytes++;
        }
        if (colorSpecBox.getMeth() == 1) {
            colorSpecBox.add(cio_read(4, jpeg2000Stream));
            readBytes += 4;
        } else {
            colorSpecBox.add(0);
        }

        if (box.length - readBytes > 0) {
            byte[] colorProfile = new byte[box.length - readBytes];
            jpeg2000Stream.read(colorProfile, 0, box.length - readBytes);
            colorSpecBox.setColorProfile(colorProfile);
        }
        return colorSpecBox;
    }

    private static void jp2_read_boxhdr(Jpeg2000Box box, InputStream jpeg2000Stream) throws java.io.IOException {
        box.length = cio_read(4, jpeg2000Stream);
        box.type = cio_read(4, jpeg2000Stream);
        if (box.length == 1) {
            if (cio_read(4, jpeg2000Stream) != 0) {
                throw new IOException(IoExceptionMessageConstant.CANNOT_HANDLE_BOX_SIZES_HIGHER_THAN_2_32);
            }
            box.length = cio_read(4, jpeg2000Stream);
            if (box.length == 0)
                throw new IOException(IoExceptionMessageConstant.UNSUPPORTED_BOX_SIZE_EQ_EQ_0);
        } else if (box.length == 0) {
            throw new ZeroBoxSizeException("Unsupported box size == 0");
        }
    }

    private static int cio_read(int n, InputStream jpeg2000Stream) throws java.io.IOException {
        int v = 0;
        for (int i = n - 1; i >= 0; i--) {
            v += jpeg2000Stream.read() << (i << 3);
        }
        return v;
    }
}

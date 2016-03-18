package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.util.Utilities;
import com.itextpdf.io.source.ByteArrayOutputStream;

import java.io.InputStream;
import java.util.ArrayList;

public final class Jpeg2000ImageHelper {

    private static class Jpeg2000Box {

        private int length;
        private int type;
    }
    private static class ZeroBoxSizeException extends java.io.IOException {

        public ZeroBoxSizeException(String s) {
            super(s);
        }
    }
    private static final int JP2_JP = 0x6a502020;
    private static final int JP2_IHDR = 0x69686472;
    private static final int JPIP_JPIP = 0x6a706970;
    private static final int JP2_FTYP = 0x66747970;
    private static final int JP2_JP2H = 0x6a703268;
    private static final int JP2_COLR = 0x636f6c72;
    private static final int JP2_JP2C = 0x6a703263;
    private static final int JP2_URL = 0x75726c20;
    private static final int JP2_DBTL = 0x6474626c;
    private static final int JP2_BPCC = 0x62706363;
    private static final int JP2_JP2 = 0x6a703220;

    private static final int JPX_JPXB = 0x6a707862;

    public static void processImage(Image image, ByteArrayOutputStream stream) {
        if (image.getOriginalType() != Image.JPEG2000)
            throw new IllegalArgumentException("JPEG2000 image expected");
        Jpeg2000Image.Parameters jp2 = new Jpeg2000Image.Parameters();
        Jpeg2000Image jpeg2000Image = (Jpeg2000Image)image;
        processParameters(jpeg2000Image, jp2);
        if (stream != null) {
            updateStream(stream, jpeg2000Image);
        }
        jpeg2000Image.parameters = jp2;
    }

    private static void updateStream(ByteArrayOutputStream stream, Jpeg2000Image image) {
        image.setFilter("JPXDecode");
        if (image.getData() != null) {
            byte[] imgBytes = image.getData();
            stream.assignBytes(imgBytes, imgBytes.length);
        } else {
            InputStream jpeg2000Stream = null;
            try {
                jpeg2000Stream = image.getUrl().openStream();
                Utilities.transferBytes(jpeg2000Stream, stream);
            } catch (java.io.IOException ignored) {
            } finally {
                if (jpeg2000Stream != null) {
                    try {
                        jpeg2000Stream.close();
                    } catch (java.io.IOException ignored) { }
                }
            }
        }
    }


    /**
     * This method checks if the image is a valid JPEG and processes some parameters.
     */
    private static void processParameters(Jpeg2000Image image, Jpeg2000Image.Parameters jp2) {
        InputStream jpeg2000Stream = null;
        try {
            if (image.getData() == null) {
                jpeg2000Stream = image.getUrl().openStream();
            } else {
                jpeg2000Stream = new java.io.ByteArrayInputStream(image.getData());
            }
            Jpeg2000Box box = new Jpeg2000Box();
            box.length = cio_read(4, jpeg2000Stream);
            if (box.length == 0x0000000c) {
                jp2.isJp2 = true;
                box.type = cio_read(4, jpeg2000Stream);
                if (JP2_JP != box.type) {
                    throw new IOException(IOException.ExpectedJpMarker);
                }
                if (0x0d0a870a != cio_read(4, jpeg2000Stream)) {
                    throw new IOException(IOException.ErrorWithJpMarker);
                }

                jp2_read_boxhdr(box, jpeg2000Stream);
                if (JP2_FTYP != box.type) {
                    throw new IOException(IOException.ExpectedFtypMarker);
                }
                Utilities.skip(jpeg2000Stream, 8);
                for (int i = 4; i < box.length / 4; ++i) {
                    if (cio_read(4, jpeg2000Stream) == JPX_JPXB) {
                        jp2.isJpxBaseline = true;
                    }
                }

                jp2_read_boxhdr(box, jpeg2000Stream);
                do {
                    if (JP2_JP2H != box.type) {
                        if (box.type == JP2_JP2C) {
                            throw new IOException(IOException.ExpectedJp2hMarker);
                        }
                        Utilities.skip(jpeg2000Stream, box.length - 8);
                        jp2_read_boxhdr(box, jpeg2000Stream);
                    }
                } while (JP2_JP2H != box.type);
                jp2_read_boxhdr(box, jpeg2000Stream);
                if (JP2_IHDR != box.type) {
                    throw new IOException(IOException.ExpectedIhdrMarker);
                }
                image.setHeight(cio_read(4, jpeg2000Stream));
                image.setWidth(cio_read(4, jpeg2000Stream));
                jp2.numOfComps = cio_read(2, jpeg2000Stream);
                image.setBpc(cio_read(1, jpeg2000Stream));
                Utilities.skip(jpeg2000Stream, 3);
                jp2_read_boxhdr(box, jpeg2000Stream);
                if (box.type == JP2_BPCC) {
                    jp2.bpcBoxData = new byte[box.length - 8];
                    jpeg2000Stream.read(jp2.bpcBoxData, 0, box.length - 8);
                } else if (box.type == JP2_COLR) {
                    do {
                        if (jp2.colorSpecBoxes == null)
                            jp2.colorSpecBoxes = new ArrayList<Jpeg2000Image.ColorSpecBox>();
                        jp2.colorSpecBoxes.add(jp2_read_colr(box, jpeg2000Stream));
                        try {
                            jp2_read_boxhdr(box, jpeg2000Stream);
                        } catch (ZeroBoxSizeException ioe) {
                            //Probably we have reached the contiguous codestream box which is the last in jpeg2000 and has no length.
                        }
                    } while (JP2_COLR == box.type);
                }
            } else if (box.length == 0xff4fff51) {
                Utilities.skip(jpeg2000Stream, 4);
                int x1 = cio_read(4, jpeg2000Stream);
                int y1 = cio_read(4, jpeg2000Stream);
                int x0 = cio_read(4, jpeg2000Stream);
                int y0 = cio_read(4, jpeg2000Stream);
                Utilities.skip(jpeg2000Stream, 16);
                image.setColorSpace(cio_read(2, jpeg2000Stream));
                image.setBpc(8);
                image.setHeight(y1 - y0);
                image.setWidth(x1 - x0);
            } else {
                throw new IOException(IOException.InvalidJpeg2000File);
            }
        } catch (java.io.IOException e) {
            throw new IOException(IOException.Jpeg2000ImageException, e);
        } finally {
            if (jpeg2000Stream != null) {
                try {
                    jpeg2000Stream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static Jpeg2000Image.ColorSpecBox jp2_read_colr(Jpeg2000Box box, InputStream jpeg2000Stream) throws java.io.IOException {
        int readBytes = 8;
        Jpeg2000Image.ColorSpecBox colorSpecBox = new Jpeg2000Image.ColorSpecBox();
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
                throw new IOException(IOException.CannotHandleBoxSizesHigherThan2_32);
            }
            box.length = cio_read(4, jpeg2000Stream);
            if (box.length == 0)
                throw new IOException(IOException.UnsupportedBoxSizeEqEq0);
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

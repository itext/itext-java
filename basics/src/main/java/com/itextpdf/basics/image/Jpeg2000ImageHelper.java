package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public final class Jpeg2000ImageHelper {

    private static class Jpeg2000Parameters {
        private Jpeg2000Image image;

        private InputStream inputStream;
        private int boxLength;
        private int boxType;
        private int numOfComps;
        private ArrayList<ColorSpecBox> colorSpecBoxes = null;
        private boolean isJp2 = false;
        private byte[] bpcBoxData;
    }

    private static class ColorSpecBox extends ArrayList<Integer> {
        private byte[] colorProfile;

        public int getMeth() {
            return get(0);
        }

        public int getPrec() {
            return get(1);
        }

        public int getApprox() {
            return get(2);
        }

        public int getEnumCs() {
            return get(3);
        }

        public byte[] getColorProfile() {
            return colorProfile;
        }

        void setColorProfile(byte[] colorProfile) {
            this.colorProfile = colorProfile;
        }
    }

    private static class ZeroBoxSizeException extends IOException {
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

    public static void processImage(Image image, ByteArrayOutputStream stream) {
        if (image.getOriginalType() != Image.JPEG2000)
            throw new IllegalArgumentException("JPEG2000 image expected");
        Jpeg2000Parameters jp2 = new Jpeg2000Parameters();
        jp2.image = (Jpeg2000Image)image;
        processParameters(jp2);
        if (stream != null) {
            updateStream(stream, jp2);
        }
    }

    private static void updateStream(ByteArrayOutputStream stream, Jpeg2000Parameters jp2) {
        jp2.image.setFilter("JPXDecode");
        if (jp2.image.getData() != null) {
            byte[] imgBytes = jp2.image.getData();
            stream.assignBytes(imgBytes, imgBytes.length);
        } else {
            InputStream is = null;
            try {
                is = jp2.image.getUrl().openStream();
                Utilities.transferBytes(is, stream);
            } catch (IOException ignored) {
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) { }
                }
            }
        }
    }


    /**
     * This method checks if the image is a valid JPEG and processes some parameters.
     */
    private static void processParameters(Jpeg2000Parameters jp2) {
        try {
            if (jp2.image.getData() == null) {
                jp2.inputStream = jp2.image.getUrl().openStream();
            } else {
                jp2.inputStream = new java.io.ByteArrayInputStream(jp2.image.getData());
            }
            jp2.boxLength = cio_read(4, jp2);
            if (jp2.boxLength == 0x0000000c) {
                jp2.isJp2 = true;
                jp2.boxType = cio_read(4, jp2);
                if (JP2_JP != jp2.boxType) {
                    throw new PdfException(PdfException.ExpectedJpMarker);
                }
                if (0x0d0a870a != cio_read(4, jp2)) {
                    throw new PdfException(PdfException.ErrorWithJpMarker);
                }

                jp2_read_boxhdr(jp2);
                if (JP2_FTYP != jp2.boxType) {
                    throw new PdfException(PdfException.ExpectedFtypMarker);
                }
                Utilities.skip(jp2.inputStream, jp2.boxLength - 8);
                jp2_read_boxhdr(jp2);
                do {
                    if (JP2_JP2H != jp2.boxType) {
                        if (jp2.boxType == JP2_JP2C) {
                            throw new PdfException(PdfException.ExpectedJp2hMarker);
                        }
                        Utilities.skip(jp2.inputStream, jp2.boxLength - 8);
                        jp2_read_boxhdr(jp2);
                    }
                } while (JP2_JP2H != jp2.boxType);
                jp2_read_boxhdr(jp2);
                if (JP2_IHDR != jp2.boxType) {
                    throw new PdfException(PdfException.ExpectedIhdrMarker);
                }
                jp2.image.setHeight(cio_read(4, jp2));
                jp2.image.setWidth(cio_read(4, jp2));
                jp2.numOfComps = cio_read(2, jp2);
                jp2.image.setBpc(cio_read(1, jp2));
                Utilities.skip(jp2.inputStream, 3);
                jp2_read_boxhdr(jp2);
                if (jp2.boxType == JP2_BPCC) {
                    jp2.bpcBoxData = new byte[jp2.boxLength - 8];
                    jp2.inputStream.read(jp2.bpcBoxData, 0, jp2.boxLength - 8);
                } else if (jp2.boxType == JP2_COLR) {
                    do {
                        if (jp2.colorSpecBoxes == null)
                            jp2.colorSpecBoxes = new ArrayList<ColorSpecBox>();
                        jp2.colorSpecBoxes.add(jp2_read_colr(jp2));
                        try {
                            jp2_read_boxhdr(jp2);
                        } catch (ZeroBoxSizeException ioe) {
                            //Probably we have reached the contiguous codestream box which is the last in jpeg2000 and has no length.
                        }
                    } while (JP2_COLR == jp2.boxType);
                }
            } else if (jp2.boxLength == 0xff4fff51) {
                Utilities.skip(jp2.inputStream, 4);
                int x1 = cio_read(4, jp2);
                int y1 = cio_read(4, jp2);
                int x0 = cio_read(4, jp2);
                int y0 = cio_read(4, jp2);
                Utilities.skip(jp2.inputStream, 16);
                jp2.image.setColorSpace(cio_read(2, jp2));
                jp2.image.setBpc(8);
                jp2.image.setHeight(y1 - y0);
                jp2.image.setWidth(x1 - x0);
            } else {
                throw new PdfException(PdfException.InvalidJpeg2000File);
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.Jpeg2000ImageException, e);
        } finally {
            if (jp2.inputStream != null) {
                try {
                    jp2.inputStream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static ColorSpecBox jp2_read_colr(Jpeg2000Parameters jp2) throws IOException {
        int readBytes = 8;
        ColorSpecBox colorSpecBox = new ColorSpecBox();
        for (int i = 0; i < 3; i++) {
            colorSpecBox.add(cio_read(1, jp2));
            readBytes++;
        }
        if (colorSpecBox.getMeth() == 1) {
            colorSpecBox.add(cio_read(4, jp2));
            readBytes += 4;
        } else {
            colorSpecBox.add(0);
        }

        if (jp2.boxLength - readBytes > 0) {
            byte[] colorProfile = new byte[jp2.boxLength - readBytes];
            jp2.inputStream.read(colorProfile, 0, jp2.boxLength - readBytes);
            colorSpecBox.setColorProfile(colorProfile);
        }
        return colorSpecBox;
    }

    private static void jp2_read_boxhdr(Jpeg2000Parameters jp2) throws IOException {
        jp2.boxLength = cio_read(4, jp2);
        jp2.boxType = cio_read(4, jp2);
        if (jp2.boxLength == 1) {
            if (cio_read(4, jp2) != 0) {
                throw new PdfException(PdfException.CannotHandleBoxSizesHigherThan2_32);
            }
            jp2.boxLength = cio_read(4, jp2);
            if (jp2.boxLength == 0)
                throw new PdfException(PdfException.UnsupportedBoxSizeEqEq0);
        } else if (jp2.boxLength == 0) {
            throw new ZeroBoxSizeException("Unsupported box size == 0");
        }
    }

    private static int cio_read(int n, Jpeg2000Parameters jp2) throws IOException {
        int v = 0;
        for (int i = n - 1; i >= 0; i--) {
            v += jp2.inputStream.read() << (i << 3);
        }
        return v;
    }
}

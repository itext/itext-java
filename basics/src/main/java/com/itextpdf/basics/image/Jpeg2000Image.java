package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Jpeg2000Image extends Image {

    public static final int JP2_JP = 0x6a502020;
    public static final int JP2_IHDR = 0x69686472;
    public static final int JPIP_JPIP = 0x6a706970;

    public static final int JP2_FTYP = 0x66747970;
    public static final int JP2_JP2H = 0x6a703268;
    public static final int JP2_COLR = 0x636f6c72;
    public static final int JP2_JP2C = 0x6a703263;
    public static final int JP2_URL = 0x75726c20;
    public static final int JP2_DBTL = 0x6474626c;
    public static final int JP2_BPCC = 0x62706363;
    public static final int JP2_JP2 = 0x6a703220;

    InputStream inp;
    int boxLength;
    int boxType;
    int numOfComps;
    ArrayList<ColorSpecBox> colorSpecBoxes = null;
    boolean isJp2 = false;
    byte[] bpcBoxData;

    /**
     * Constructs a <CODE>Jpeg2000</CODE>-object, using an <VAR>url</VAR>.
     *
     * @param url the <CODE>URL</CODE> where the image can be found
     * @throws com.itextpdf.basics.PdfException
     * @throws java.io.IOException
     */
    public Jpeg2000Image(URL url) throws PdfException, IOException {
        super(url);
        processParameters();
    }

    /**
     * Constructs a <CODE>Jpeg2000</CODE>-object from memory.
     *
     * @param img the memory image
     * @throws com.itextpdf.basics.PdfException
     * @throws IOException
     */

    public Jpeg2000Image(byte[] img) throws PdfException, IOException {
        super();
        rawData = img;
        originalData = img;
        processParameters();
    }

    /**
     * Constructs a <CODE>Jpeg2000</CODE>-object from memory.
     *
     * @param img    the memory image.
     * @param width  the width you want the image to have
     * @param height the height you want the image to have
     * @throws com.itextpdf.basics.PdfException
     * @throws IOException
     */

    public Jpeg2000Image(byte[] img, float width, float height) throws PdfException, IOException {
        this(img);
        this.width = width;
        this.height = height;
    }

    public int getNumOfComps() {
        return numOfComps;
    }

    public byte[] getBpcBoxData() {
        return bpcBoxData;
    }

    public ArrayList<ColorSpecBox> getColorSpecBoxes() {
        return colorSpecBoxes;
    }

    public void jp2_read_boxhdr() throws IOException, PdfException {
        boxLength = cio_read(4);
        boxType = cio_read(4);
        if (boxLength == 1) {
            if (cio_read(4) != 0) {
                throw new PdfException(PdfException.CannotHandleBoxSizesHigherThan2_32);
            }
            boxLength = cio_read(4);
            if (boxLength == 0)
                throw new PdfException(PdfException.UnsupportedBoxSizeEqEq0);
        } else if (boxLength == 0) {
            throw new PdfException(PdfException.UnsupportedBoxSizeEqEq0);
        }
    }

    /**
     * @return <code>true</code> if the image is JP2, <code>false</code> if a codestream.
     */
    public boolean isJp2() {
        return isJp2;
    }

    private int cio_read(int n) throws IOException {
        int v = 0;
        for (int i = n - 1; i >= 0; i--) {
            v += inp.read() << (i << 3);
        }
        return v;
    }

    /**
     * This method checks if the image is a valid JPEG and processes some parameters.
     *
     * @throws IOException
     */
    private void processParameters() throws IOException, PdfException {
        type = JPEG2000;
        originalType = JPEG2000;
        inp = null;
        try {
            if (rawData == null) {
                inp = url.openStream();
            } else {
                inp = new java.io.ByteArrayInputStream(rawData);
            }
            boxLength = cio_read(4);
            if (boxLength == 0x0000000c) {
                isJp2 = true;
                boxType = cio_read(4);
                if (JP2_JP != boxType) {
                    throw new PdfException(PdfException.ExpectedJpMarker);
                }
                if (0x0d0a870a != cio_read(4)) {
                    throw new PdfException(PdfException.ErrorWithJpMarker);
                }

                jp2_read_boxhdr();
                if (JP2_FTYP != boxType) {
                    throw new PdfException(PdfException.ExpectedFtypMarker);
                }
                Utilities.skip(inp, boxLength - 8);
                jp2_read_boxhdr();
                do {
                    if (JP2_JP2H != boxType) {
                        if (boxType == JP2_JP2C) {
                            throw new PdfException(PdfException.ExpectedJp2hMarker);
                        }
                        Utilities.skip(inp, boxLength - 8);
                        jp2_read_boxhdr();
                    }
                } while (JP2_JP2H != boxType);
                jp2_read_boxhdr();
                if (JP2_IHDR != boxType) {
                    throw new PdfException(PdfException.ExpectedIhdrMarker);
                }
                height = cio_read(4);
                width = cio_read(4);
                numOfComps = cio_read(2);
                bpc = -1;
                bpc = cio_read(1);

                Utilities.skip(inp, 3);

                jp2_read_boxhdr();
                if (boxType == JP2_BPCC) {
                    bpcBoxData = new byte[boxLength - 8];
                    inp.read(bpcBoxData, 0, boxLength - 8);
                } else if (boxType == JP2_COLR) {
                    do {
                        if (colorSpecBoxes == null)
                            colorSpecBoxes = new ArrayList<ColorSpecBox>();
                        colorSpecBoxes.add(jp2_read_colr());
                        try {
                            jp2_read_boxhdr();
                        } catch (ZeroBoxSizeException ioe) {
                            //Probably we have reached the contiguous codestream box which is the last in jpeg2000 and has no length.
                        }
                    } while (JP2_COLR == boxType);
                }
            } else if (boxLength == 0xff4fff51) {
                Utilities.skip(inp, 4);
                int x1 = cio_read(4);
                int y1 = cio_read(4);
                int x0 = cio_read(4);
                int y0 = cio_read(4);
                Utilities.skip(inp, 16);
                colorSpace = cio_read(2);
                bpc = 8;
                height = y1 - y0;
                width = x1 - x0;
            } else {
                throw new PdfException(PdfException.InvalidJpeg2000File);
            }
        } finally {
            if (inp != null) {
                try {
                    inp.close();
                } catch (Exception e) {
                }
                inp = null;
            }
        }
    }

    private ColorSpecBox jp2_read_colr() throws IOException {
        int readBytes = 8;
        ColorSpecBox colr = new ColorSpecBox();
        for (int i = 0; i < 3; i++) {
            colr.add(cio_read(1));
            readBytes++;
        }
        if (colr.getMeth() == 1) {
            colr.add(cio_read(4));
            readBytes += 4;
        } else {
            colr.add(0);
        }

        if (boxLength - readBytes > 0) {
            byte[] colorProfile = new byte[boxLength - readBytes];
            inp.read(colorProfile, 0, boxLength - readBytes);
            colr.setColorProfile(colorProfile);
        }
        return colr;
    }

    public static class ColorSpecBox extends ArrayList<Integer> {
        private byte[] colorProfile;

        public int getMeth() {
            return get(0).intValue();
        }

        public int getPrec() {
            return get(1).intValue();
        }

        public int getApprox() {
            return get(2).intValue();
        }

        public int getEnumCs() {
            return get(3).intValue();
        }

        public byte[] getColorProfile() {
            return colorProfile;
        }

        void setColorProfile(byte[] colorProfile) {
            this.colorProfile = colorProfile;
        }
    }

    private class ZeroBoxSizeException extends IOException {
        public ZeroBoxSizeException() {
            super();
        }

        public ZeroBoxSizeException(String s) {
            super(s);
        }
    }


}

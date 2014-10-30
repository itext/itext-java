package com.itextpdf.basics.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.codec.InputMeta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WmfImage extends Image {

    /**
     * Constructs an <CODE>ImgWMF</CODE>-object, using an <VAR>url</VAR>.
     *
     * @param url the <CODE>URL</CODE> where the image can be found
     * @throws com.itextpdf.basics.PdfException on error
     * @throws java.io.IOException              on error
     */

    public WmfImage(URL url) throws PdfException, IOException {
        super(url);
        processParameters();
    }

    /**
     * Constructs an <CODE>ImgWMF</CODE>-object, using a <VAR>filename</VAR>.
     *
     * @param filename a <CODE>String</CODE>-representation of the file that contains the image.
     * @throws com.itextpdf.basics.PdfException on error
     * @throws IOException                      on error
     */

    public WmfImage(String filename) throws PdfException, IOException {
        this(Utilities.toURL(filename));
    }

    /**
     * Constructs an <CODE>ImgWMF</CODE>-object from memory.
     *
     * @param img the memory image
     * @throws com.itextpdf.basics.PdfException on error
     * @throws IOException                      on error
     */

    public WmfImage(byte[] img) throws PdfException, IOException {
        super();
        rawData = img;
        originalData = img;
        processParameters();
    }

    /**
     * This method checks if the image is a valid WMF and processes some parameters.
     *
     * @throws com.itextpdf.basics.PdfException
     * @throws IOException
     */

    private void processParameters() throws PdfException, IOException {
        type = WMF;
        originalType = WMF;
        InputStream is = null;
        try {
            String errorID;
            if (rawData == null) {
                is = url.openStream();
                errorID = url.toString();
            } else {
                is = new java.io.ByteArrayInputStream(rawData);
                errorID = "Byte array";
            }
            InputMeta in = new InputMeta(is);
            if (in.readInt() != 0x9AC6CDD7) {
                throw new PdfException(PdfException._1IsNotAValidPlaceableWindowsMetafile).setMessageParams(errorID);
            }
            in.readWord();
            int left = in.readShort();
            int top = in.readShort();
            int right = in.readShort();
            int bottom = in.readShort();
            int inch = in.readWord();
            dpiX = 72;
            dpiY = 72;
            height = (float) (bottom - top) / inch * 72f;
            width = (float) (right - left) / inch * 72f;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}

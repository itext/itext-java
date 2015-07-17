package com.itextpdf.barcodes;

import com.itextpdf.barcodes.qrcode.ByteMatrix;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.QRCodeWriter;
import com.itextpdf.barcodes.qrcode.WriterException;
import com.itextpdf.basics.codec.CCITTG4Encoder;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.basics.image.RawImage;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

import java.util.Map;

/**
 * A QRCode implementation based on the zxing code.
 */
public class BarcodeQRCode extends Barcode2D {
    ByteMatrix bm;

    /**
     * Creates the QR barcode.
     * @param content the text to be encoded
     * @param hints modifiers to change the way the barcode is create. They can be EncodeHintType.ERROR_CORRECTION
     * and EncodeHintType.CHARACTER_SET. For EncodeHintType.ERROR_CORRECTION the values can be ErrorCorrectionLevel.L, M, Q, H.
     * For EncodeHintType.CHARACTER_SET the values are strings and can be Cp437, Shift_JIS and ISO-8859-1 to ISO-8859-16.
     * You can also use UTF-8, but correct behaviour is not guaranteed as Unicode is not supported in QRCodes.
     * The default value is ISO-8859-1.
     */
    public BarcodeQRCode(String content, Map<EncodeHintType,Object> hints) {
        try {
            QRCodeWriter qc = new QRCodeWriter();
            bm = qc.encode(content, 1, 1, hints);
        }
        catch (WriterException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Creates the QR barcode with default error correction level (ErrorCorrectionLevel.L)
     * and default character set (ISO-8859-1).
     * @param content the text to be encoded
     */
    public BarcodeQRCode(String content) {
        this(content, null);
    }

    /**
     * Gets the size of the barcode grid
     */
    @Override
    public Rectangle getBarcodeSize() {
        return new Rectangle(0, 0, bm.getWidth(), bm.getHeight());
    }

    /**
     * Gets the barcode size
     */
    public Rectangle getBarcodeSize(float moduleSide) {
        return new Rectangle(0, 0, bm.getWidth() * moduleSide, bm.getHeight() * moduleSide);
    }

    @Override
    public Rectangle placeBarcode(PdfCanvas canvas, Color foreground) {
        return placeBarcode(canvas, foreground, 1);
    }

    /**
     ** Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.
     * @param canvas the <CODE>PdfCanvas</CODE> where the barcode will be placed
     * @param foreground the foreground color. It can be <CODE>null</CODE>
     * @param moduleSide the size of the square grid cell
     * @return the dimensions the barcode occupies
     */
    public Rectangle placeBarcode(PdfCanvas canvas, Color foreground, float moduleSide) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        byte[][] mt = bm.getArray();

        if (foreground != null) {
            canvas.setFillColor(foreground);
        }

        for (int y = 0; y < height; ++y) {
            byte[] line = mt[y];
            for (int x = 0; x < width; ++x) {
                if (line[x] == 0) {
                    canvas.rectangle(x * moduleSide, (height - y - 1) * moduleSide, moduleSide, moduleSide);
                }
            }
        }
        canvas.fill();

        return getBarcodeSize(moduleSide);
    }

    /** Creates a <CODE>java.awt.Image</CODE>.
     * @param foreground the color of the bars
     * @param background the color of the background
     * @return the image
     */
    public java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background) {
        int f = foreground.getRGB();
        int g = background.getRGB();
        java.awt.Canvas canvas = new java.awt.Canvas();

        int width = bm.getWidth();
        int height = bm.getHeight();
        int pix[] = new int[width * height];
        byte[][] mt = bm.getArray();
        for (int y = 0; y < height; ++y) {
            byte[] line = mt[y];
            for (int x = 0; x < width; ++x) {
                pix[y * width + x] = line[x] == 0 ? f : g;
            }
        }

        java.awt.Image img = canvas.createImage(new java.awt.image.MemoryImageSource(width, height, pix, 0, width));
        return img;
    }


    /** Gets an <CODE>Image</CODE> with the barcode.
     * @return the barcode <CODE>Image</CODE>
     */
    @Deprecated
    public Image getImage() {
        byte[] b = getBitMatrix();
        byte g4[] = CCITTG4Encoder.compress(b, bm.getWidth(), bm.getHeight());
        RawImage image = (RawImage) ImageFactory.getImage(bm.getWidth(), bm.getHeight(), false, RawImage.CCITTG4, RawImage.CCITT_BLACKIS1, g4, null);
        image.setRawData(g4);
        image.setBpc(RawImage.CCITTG4);
        return image;
    }


    private byte[] getBitMatrix() {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int stride = (width + 7) / 8;
        byte[] b = new byte[stride * height];
        byte[][] mt = bm.getArray();
        for (int y = 0; y < height; ++y) {
            byte[] line = mt[y];
            for (int x = 0; x < width; ++x) {
                if (line[x] != 0) {
                    int offset = stride * y + x / 8;
                    b[offset] |= (byte)(0x80 >> (x % 8));
                }
            }
        }
        return b;
    }
}

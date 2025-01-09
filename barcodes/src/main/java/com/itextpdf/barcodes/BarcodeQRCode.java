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
package com.itextpdf.barcodes;

import com.itextpdf.barcodes.exceptions.WriterException;
import com.itextpdf.barcodes.qrcode.ByteMatrix;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.QRCodeWriter;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.util.Map;

/**
 * A QRCode implementation based on the zxing code.
 */
public class BarcodeQRCode extends Barcode2D {
    ByteMatrix bm;
    /**
     * modifiers to change the way the barcode is create.
     **/
    Map<EncodeHintType, Object> hints;
    String code;

    /**
     * Creates the QR barcode.
     *
     * @param code  the text to be encoded
     * @param hints barcode hints. See #setHints for description.
     */
    public BarcodeQRCode(String code, Map<EncodeHintType, Object> hints) {
        this.code = code;
        this.hints = hints;
        regenerate();
    }

    /**
     * Creates the QR barcode with default error correction level (ErrorCorrectionLevel.L)
     * and default character set (ISO-8859-1).
     *
     * @param content the text to be encoded
     */
    public BarcodeQRCode(String content) {
        this(content, null);
    }

    /**
     * Creates an instance of the {@link BarcodeQRCode} class.
     */
    public BarcodeQRCode() {
        // empty constructor
    }

    /**
     * Gets the current data.
     * @return the encoded data
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the data to be encoded by the barcode. If not specified in hints otherwise, the character set should be ISO-8859-1.
     * @param code The data to encode
     */
    public void setCode(String code) {
        this.code = code;
        regenerate();
    }

    /**
     * @return modifiers to change the way the barcode is created.
     */
    public Map<EncodeHintType, Object> getHints() {
        return hints;
    }

    /**
     * @param hints modifiers to change the way the barcode is created. They can be EncodeHintType.ERROR_CORRECTION
     *              and EncodeHintType.CHARACTER_SET. For EncodeHintType.ERROR_CORRECTION the values can be ErrorCorrectionLevel.L, M, Q, H.
     *              For EncodeHintType.CHARACTER_SET the values are strings and can be Cp437, Shift_JIS and ISO-8859-1 to ISO-8859-16.
     *              You can also use UTF-8, but correct behaviour is not guaranteed as Unicode is not supported in QRCodes.
     *              The default value is ISO-8859-1.
     */
    public void setHints(Map<EncodeHintType, Object> hints) {
        this.hints = hints;
        regenerate();
    }

    /**
     * Regenerates barcode after changes in hints or code.
     */
    public void regenerate() {
        if (code != null) {
            try {
                QRCodeWriter qc = new QRCodeWriter();
                bm = qc.encode(code, 1, 1, hints);
            } catch (WriterException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex.getCause());
            }
        }
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
     * @param moduleSize    The module size
     * @return              The size of the barcode
     */
    public Rectangle getBarcodeSize(float moduleSize) {
        return new Rectangle(0, 0, bm.getWidth() * moduleSize, bm.getHeight() * moduleSize);
    }

    @Override
    public Rectangle placeBarcode(PdfCanvas canvas, Color foreground) {
        return placeBarcode(canvas, foreground, DEFAULT_MODULE_SIZE);
    }

    /**
     * * Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.
     *
     * @param canvas     the <CODE>PdfCanvas</CODE> where the barcode will be placed
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

    /**
     * Creates a PdfFormXObject with the barcode.
     *
     * @param foreground the color of the pixels. It can be <CODE>null</CODE>
     * @return the XObject.
     */
    @Override
    public PdfFormXObject createFormXObject(Color foreground, PdfDocument document) {
        return createFormXObject(foreground, DEFAULT_MODULE_SIZE, document);
    }

    /**
     * Creates a PdfFormXObject with the barcode.
     *
     * @param foreground    The color of the pixels. It can be <CODE>null</CODE>
     * @param moduleSize    The size of the pixels.
     * @param document      The document
     * @return the XObject.
     */
    public PdfFormXObject createFormXObject(Color foreground, float moduleSize, PdfDocument document) {
        PdfFormXObject xObject = new PdfFormXObject((Rectangle) null);
        Rectangle rect = placeBarcode(new PdfCanvas(xObject, document), foreground, moduleSize);
        xObject.setBBox(new PdfArray(rect));

        return xObject;
    }

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    /**
     * Creates a <CODE>java.awt.Image</CODE>.
     *
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
        int[] pix = new int[width * height];
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
    // Android-Conversion-Skip-Block-End

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
                    b[offset] |= (byte) (0x80 >> (x % 8));
                }
            }
        }
        return b;
    }
}

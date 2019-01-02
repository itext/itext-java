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
package com.itextpdf.barcodes;

import com.itextpdf.barcodes.qrcode.ByteMatrix;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.QRCodeWriter;
import com.itextpdf.barcodes.qrcode.WriterException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
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

    public BarcodeQRCode() {
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

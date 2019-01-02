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

import com.itextpdf.barcodes.dmcode.DmParams;
import com.itextpdf.barcodes.dmcode.Placement;
import com.itextpdf.barcodes.dmcode.ReedSolomon;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class BarcodeDataMatrix extends Barcode2D {

    /**
     * No error.
     */
    public static final int DM_NO_ERROR = 0;
    /**
     * The text is too big for the symbology capabilities.
     */
    public static final int DM_ERROR_TEXT_TOO_BIG = 1;
    /**
     * The dimensions given for the symbol are illegal.
     */
    public static final int DM_ERROR_INVALID_SQUARE = 3;
    /**
     * An error while parsing an extension.
     */
    public static final int DM_ERROR_EXTENSION = 5;

    /**
     * The best encodation will be used.
     */
    public static final int DM_AUTO = 0;
    /**
     * ASCII encodation.
     */
    public static final int DM_ASCII = 1;
    /**
     * C40 encodation.
     */
    public static final int DM_C40 = 2;
    /**
     * TEXT encodation.
     */
    public static final int DM_TEXT = 3;
    /**
     * Binary encodation.
     */
    public static final int DM_B256 = 4;
    /**
     * X12 encodation.
     */
    public static final int DM_X12 = 5;
    /**
     * EDIFACT encodation.
     */
    public static final int DM_EDIFACT = 6;
    /**
     * No encodation needed. The bytes provided are already encoded.
     */
    public static final int DM_RAW = 7;

    /**
     * Allows extensions to be embedded at the start of the text.
     */
    public static final int DM_EXTENSION = 32;
    /**
     * Doesn't generate the image but returns all the other information.
     */
    public static final int DM_TEST = 64;

    public static final String DEFAULT_DATA_MATRIX_ENCODING = "iso-8859-1";

    private static final byte LATCH_B256 = (byte) 231;

    private static final byte LATCH_EDIFACT = (byte) 240;

    private static final byte LATCH_X12 = (byte) 238;

    private static final byte LATCH_TEXT = (byte) 239;

    private static final byte LATCH_C40 = (byte) 230;

    private static final byte UNLATCH = (byte) 254;

    private static final byte EXTENDED_ASCII = (byte) 235;

    private static final byte PADDING = (byte) 129;

    private String encoding;

    private static final DmParams[] dmSizes = {
            new DmParams(10, 10, 10, 10, 3, 3, 5),
            new DmParams(12, 12, 12, 12, 5, 5, 7),
            new DmParams(8, 18, 8, 18, 5, 5, 7),
            new DmParams(14, 14, 14, 14, 8, 8, 10),
            new DmParams(8, 32, 8, 16, 10, 10, 11),
            new DmParams(16, 16, 16, 16, 12, 12, 12),
            new DmParams(12, 26, 12, 26, 16, 16, 14),
            new DmParams(18, 18, 18, 18, 18, 18, 14),
            new DmParams(20, 20, 20, 20, 22, 22, 18),
            new DmParams(12, 36, 12, 18, 22, 22, 18),
            new DmParams(22, 22, 22, 22, 30, 30, 20),
            new DmParams(16, 36, 16, 18, 32, 32, 24),
            new DmParams(24, 24, 24, 24, 36, 36, 24),
            new DmParams(26, 26, 26, 26, 44, 44, 28),
            new DmParams(16, 48, 16, 24, 49, 49, 28),
            new DmParams(32, 32, 16, 16, 62, 62, 36),
            new DmParams(36, 36, 18, 18, 86, 86, 42),
            new DmParams(40, 40, 20, 20, 114, 114, 48),
            new DmParams(44, 44, 22, 22, 144, 144, 56),
            new DmParams(48, 48, 24, 24, 174, 174, 68),
            new DmParams(52, 52, 26, 26, 204, 102, 42),
            new DmParams(64, 64, 16, 16, 280, 140, 56),
            new DmParams(72, 72, 18, 18, 368, 92, 36),
            new DmParams(80, 80, 20, 20, 456, 114, 48),
            new DmParams(88, 88, 22, 22, 576, 144, 56),
            new DmParams(96, 96, 24, 24, 696, 174, 68),
            new DmParams(104, 104, 26, 26, 816, 136, 56),
            new DmParams(120, 120, 20, 20, 1050, 175, 68),
            new DmParams(132, 132, 22, 22, 1304, 163, 62),
            new DmParams(144, 144, 24, 24, 1558, 156, 62)};

    private static final String X12 = "\r*> 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int extOut;
    private short[] place;
    private byte[] image;
    private int height;
    private int width;
    private int ws;
    private int options;
    // value f[i][j] is the optimal amount of bytes required to encode substring(0, j)
    private int[][] f;
    // switchMode[i][j] = k means that when encoding j-th symbol with mode = i + 1,
    // we have to encode the previous symbol with mode = k in order to get optimal f[i][j] value
    private int[][] switchMode;

    /**
     * Creates an instance of this class.
     */
    public BarcodeDataMatrix() {
        encoding = DEFAULT_DATA_MATRIX_ENCODING;
    }

    public BarcodeDataMatrix(String code) {
        encoding = DEFAULT_DATA_MATRIX_ENCODING;
        setCode(code);
    }

    public BarcodeDataMatrix(String code, String encoding) {
        this.encoding = encoding;
        setCode(code);
    }

    @Override
    public Rectangle getBarcodeSize() {
        return new Rectangle(0, 0, width + 2 * ws, height + 2 * ws);
    }

    @Override
    public Rectangle placeBarcode(PdfCanvas canvas, Color foreground) {
        return placeBarcode(canvas, foreground, DEFAULT_MODULE_SIZE);
    }

    @Override
    public PdfFormXObject createFormXObject(Color foreground, PdfDocument document) {
        return createFormXObject(foreground, DEFAULT_MODULE_SIZE, document);
    }

    /**
     * Creates a PdfFormXObject with the barcode with given module width and module height.
     *
     * @param foreground The color of the pixels. It can be <CODE>null</CODE>
     * @param moduleSide The side (width and height) of the pixels.
     * @param document   The document
     * @return the XObject.
     */
    public PdfFormXObject createFormXObject(Color foreground, float moduleSide, PdfDocument document) {
        PdfFormXObject xObject = new PdfFormXObject((Rectangle) null);
        Rectangle rect = placeBarcode(new PdfCanvas(xObject, document), foreground, moduleSide);
        xObject.setBBox(new PdfArray(rect));

        return xObject;
    }

    public Rectangle placeBarcode(PdfCanvas canvas, Color foreground, float moduleSide) {
        if (image == null) {
            return null;
        }

        if (foreground != null) {
            canvas.setFillColor(foreground);
        }

        int w = width + 2 * ws;
        int h = height + 2 * ws;
        int stride = (w + 7) / 8;

        for (int k = 0; k < h; ++k) {
            int p = k * stride;
            for (int j = 0; j < w; ++j) {
                int b = image[p + j / 8] & 0xff;
                b <<= j % 8;
                if ((b & 0x80) != 0) {
                    canvas.rectangle(j * moduleSide, (h - k - 1) * moduleSide, moduleSide, moduleSide);
                }
            }
        }
        canvas.fill();

        return getBarcodeSize();
    }

    // AWT related methods (remove this if you port to Android / GAE)

    /**
     * Creates a <CODE>java.awt.Image</CODE>. A successful call to the method <CODE>generate()</CODE>
     * before calling this method is required.
     *
     * @param foreground the color of the bars
     * @param background the color of the background
     * @return the image
     */
    public java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background) {
        if (image == null)
            return null;
        int f = foreground.getRGB();
        int g = background.getRGB();
        java.awt.Canvas canvas = new java.awt.Canvas();

        int w = width + 2 * ws;
        int h = height + 2 * ws;
        int[] pix = new int[w * h];
        int stride = (w + 7) / 8;
        int ptr = 0;
        for (int k = 0; k < h; ++k) {
            int p = k * stride;
            for (int j = 0; j < w; ++j) {
                int b = image[p + j / 8] & 0xff;
                b <<= j % 8;
                pix[ptr++] = (b & 0x80) == 0 ? g : f;
            }
        }
        java.awt.Image img = canvas.createImage(new java.awt.image.MemoryImageSource(w, h, pix, 0, w));
        return img;
    }


    /**
     * Gets the barcode size
     * @param moduleHeight The height of the module
     * @param moduleWidth  The width of the module
     * @return The size of the barcode
     */
    public Rectangle getBarcodeSize(float moduleHeight, float moduleWidth) {
        return new Rectangle(0, 0, (width + 2 * ws) * moduleHeight, (height + 2 * ws) * moduleWidth);
    }

    /**
     * Creates a barcode. The <CODE>String</CODE> is interpreted with the ISO-8859-1 encoding
     *
     * @param text the text
     * @return the status of the generation. It can be one of this values:
     *
     * <CODE>DM_NO_ERROR</CODE> - no error.<br>
     * <CODE>DM_ERROR_TEXT_TOO_BIG</CODE> - the text is too big for the symbology capabilities.<br>
     * <CODE>DM_ERROR_INVALID_SQUARE</CODE> - the dimensions given for the symbol are illegal.<br>
     * <CODE>DM_ERROR_EXTENSION</CODE> - an error was while parsing an extension.
     */
    public int setCode(String text) {
        byte[] t;
        try {
            t = text.getBytes(encoding);
        } catch (UnsupportedEncodingException exc) {
            throw new IllegalArgumentException("text has to be encoded in iso-8859-1");
        }
        return setCode(t, 0, t.length);
    }

    /**
     * Creates a barcode.
     *
     * @param text       the text
     * @param textOffset the offset to the start of the text
     * @param textSize   the text size
     * @return the status of the generation. It can be one of this values:
     *
     * <CODE>DM_NO_ERROR</CODE> - no error.<br>
     * <CODE>DM_ERROR_TEXT_TOO_BIG</CODE> - the text is too big for the symbology capabilities.<br>
     * <CODE>DM_ERROR_INVALID_SQUARE</CODE> - the dimensions given for the symbol are illegal.<br>
     * <CODE>DM_ERROR_EXTENSION</CODE> - an error was while parsing an extension.
     */
    public int setCode(byte[] text, int textOffset, int textSize) {
        if (textOffset < 0) {
            throw new IndexOutOfBoundsException("" + textOffset);
        }
        if (textOffset + textSize > text.length || textSize < 0) {
            throw new IndexOutOfBoundsException("" + textSize);
        }
        int extCount, e, k, full;
        DmParams dm, last;
        byte[] data = new byte[2500];
        extOut = 0;
        extCount = processExtensions(text, textOffset, textSize, data);
        if (extCount < 0) {
            return DM_ERROR_EXTENSION;
        }
        e = -1;
        f = new int[6][textSize - extOut];
        switchMode = new int[6][textSize - extOut];
        if (height == 0 || width == 0) {
            last = dmSizes[dmSizes.length - 1];
            e = getEncodation(text, textOffset + extOut, textSize - extOut, data, extCount, last.dataSize - extCount, options, false);
            if (e < 0) {
                return DM_ERROR_TEXT_TOO_BIG;
            }
            e += extCount;
            for (k = 0; k < dmSizes.length; ++k) {
                if (dmSizes[k].dataSize >= e)
                    break;
            }
            dm = dmSizes[k];
            height = dm.height;
            width = dm.width;
        } else {
            for (k = 0; k < dmSizes.length; ++k) {
                if (height == dmSizes[k].height && width == dmSizes[k].width)
                    break;
            }
            if (k == dmSizes.length) {
                return DM_ERROR_INVALID_SQUARE;
            }
            dm = dmSizes[k];
            e = getEncodation(text, textOffset + extOut, textSize - extOut, data, extCount, dm.dataSize - extCount, options, true);
            if (e < 0) {
                return DM_ERROR_TEXT_TOO_BIG;
            }
            e += extCount;
        }
        if ((options & DM_TEST) != 0) {
            return DM_NO_ERROR;
        }
        image = new byte[(dm.width + 2 * ws + 7) / 8 * (dm.height + 2 * ws)];
        makePadding(data, e, dm.dataSize - e);
        place = Placement.doPlacement(dm.height - dm.height / dm.heightSection * 2, dm.width - dm.width / dm.widthSection * 2);
        full = dm.dataSize + (dm.dataSize + 2) / dm.dataBlock * dm.errorBlock;
        ReedSolomon.generateECC(data, dm.dataSize, dm.dataBlock, dm.errorBlock);
        draw(data, full, dm);
        return DM_NO_ERROR;
    }

    /**
     * Gets the height of the barcode. Will contain the real height used after a successful call
     * to <CODE>generate()</CODE>. This height doesn't include the whitespace border, if any.
     *
     * @return the height of the barcode
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the barcode. If the height is zero it will be calculated.
     * This height doesn't include the whitespace border, if any.
     *
     * The allowed dimensions are (width, height):<p>
     * 10, 10<br>
     * 12, 12<br>
     * 18, 8<br>
     * 14, 14<br>
     * 32, 8<br>
     * 16, 16<br>
     * 26, 12<br>
     * 18, 18<br>
     * 20, 20<br>
     * 36, 12<br>
     * 22, 22<br>
     * 36, 16<br>
     * 24, 24<br>
     * 26, 26<br>
     * 48, 16<br>
     * 32, 32<br>
     * 36, 36<br>
     * 40, 40<br>
     * 44, 44<br>
     * 48, 48<br>
     * 52, 52<br>
     * 64, 64<br>
     * 72, 72<br>
     * 80, 80<br>
     * 88, 88<br>
     * 96, 96<br>
     * 104, 104<br>
     * 120, 120<br>
     * 132, 132<br>
     * 144, 144<br>
     *
     * @param height the height of the barcode
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the width of the barcode. Will contain the real width used after a successful call
     * to <CODE>generate()</CODE>. This width doesn't include the whitespace border, if any.
     *
     * @return the width of the barcode
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the barcode. If the width is zero it will be calculated.
     * This width doesn't include the whitespace border, if any.
     *
     * The allowed dimensions are (width, height):<p>
     * 10, 10<br>
     * 12, 12<br>
     * 18, 8<br>
     * 14, 14<br>
     * 32, 8<br>
     * 16, 16<br>
     * 26, 12<br>
     * 18, 18<br>
     * 20, 20<br>
     * 36, 12<br>
     * 22, 22<br>
     * 36, 16<br>
     * 24, 24<br>
     * 26, 26<br>
     * 48, 16<br>
     * 32, 32<br>
     * 36, 36<br>
     * 40, 40<br>
     * 44, 44<br>
     * 48, 48<br>
     * 52, 52<br>
     * 64, 64<br>
     * 72, 72<br>
     * 80, 80<br>
     * 88, 88<br>
     * 96, 96<br>
     * 104, 104<br>
     * 120, 120<br>
     * 132, 132<br>
     * 144, 144<br>
     *
     * @param width the width of the barcode
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the whitespace border around the barcode.
     *
     * @return the whitespace border around the barcode
     */
    public int getWs() {
        return ws;
    }

    /**
     * Sets the whitespace border around the barcode.
     *
     * @param ws the whitespace border around the barcode
     */
    public void setWs(int ws) {
        this.ws = ws;
    }

    /**
     * Gets the barcode options.
     *
     * @return the barcode options
     */
    public int getOptions() {
        return options;
    }


    /**
     * Sets the options for the barcode generation. The options can be:<p>
     * One of:<br>
     * <CODE>DM_AUTO</CODE> - the best encodation will be used<br>
     * <CODE>DM_ASCII</CODE> - ASCII encodation<br>
     * <CODE>DM_C40</CODE> - C40 encodation<br>
     * <CODE>DM_TEXT</CODE> - TEXT encodation<br>
     * <CODE>DM_B256</CODE> - binary encodation<br>
     * <CODE>DM_X12</CODE> - X12 encodation<br>
     * <CODE>DM_EDIFACT</CODE> - EDIFACT encodation<br>
     * <CODE>DM_RAW</CODE> - no encodation. The bytes provided are already encoded and will be added directly to the barcode, using padding if needed. It assumes that the encodation state is left at ASCII after the last byte.<br>
     * <br>
     * One of:<br>
     * <CODE>DM_EXTENSION</CODE> - allows extensions to be embedded at the start of the text:<p>
     * exxxxxx - ECI number xxxxxx<br>
     * m5 - macro 5<br>
     * m6 - macro 6<br>
     * f - FNC1<br>
     * saabbccccc - Structured Append, aa symbol position (1-16), bb total number of symbols (2-16), ccccc file identification (0-64515)<br>
     * p - Reader programming<br>
     * . - extension terminator<p>
     * Example for a structured append, symbol 2 of 6, with FNC1 and ECI 000005. The actual text is "Hello".<p>
     * s020600075fe000005.Hello<p>
     * One of:<br>
     * <CODE>DM_TEST</CODE> - doesn't generate the image but returns all the other information.
     *
     * @param options the barcode options
     */
    public void setOptions(int options) {
        this.options = options;
    }

    /**
     * setting encoding for data matrix code ( default  encoding iso-8859-1)
     *
     * @param encoding encoding for data matrix code
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }


    private static void makePadding(byte[] data, int position, int count) {
        //already in ascii mode
        if (count <= 0)
            return;
        data[position++] = PADDING;
        while (--count > 0) {
            int t = 129 + (position + 1) * 149 % 253 + 1;
            if (t > 254)
                t -= 254;
            data[position++] = (byte) t;
        }
    }

    private static boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    // when symbolIndex is non-negative, textLength should equal 1. All other encodations behave the same way.
    private int asciiEncodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength, int symbolIndex, int prevEnc, int origDataOffset) {
        int ptrIn, ptrOut, c;
        ptrIn = textOffset;
        ptrOut = dataOffset;
        textLength += textOffset;
        dataLength += dataOffset;
        while (ptrIn < textLength) {
            c = text[ptrIn++] & 0xff;
            if (isDigit(c) && symbolIndex > 0 && prevEnc == DM_ASCII && isDigit(text[ptrIn - 2] & 0xff)
                    && data[dataOffset - 1] > 48 && data[dataOffset - 1] < 59) {
                data[ptrOut - 1] = (byte) (((text[ptrIn - 2] & 0xff) - '0') * 10 + c - '0' + 130);
                return ptrOut - origDataOffset;
            }
            if (ptrOut >= dataLength)
                return -1;
            if (isDigit(c) && symbolIndex < 0 && ptrIn < textLength && isDigit(text[ptrIn] & 0xff)) {
                data[ptrOut++] = (byte) ((c - '0') * 10 + (text[ptrIn++] & 0xff) - '0' + 130);
            } else if (c > 127) {
                if (ptrOut + 1 >= dataLength)
                    return -1;
                data[ptrOut++] = EXTENDED_ASCII;
                data[ptrOut++] = (byte) (c - 128 + 1);
            } else {
                data[ptrOut++] = (byte) (c + 1);
            }
        }
        return ptrOut - origDataOffset;
    }

    private int b256Encodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength, int symbolIndex, int prevEnc, int origDataOffset) {
        int minRequiredDataIncrement;
        if (textLength == 0)
            return 0;
        int simulatedDataOffset = dataOffset;
        if (prevEnc != DM_B256) {
            if (textLength < 250 && textLength + 2 > dataLength)
                return -1;
            if (textLength >= 250 && textLength + 3 > dataLength)
                return -1;
            data[dataOffset] = LATCH_B256;
        } else {
            int latestModeEntry = symbolIndex - 1;
            while (latestModeEntry > 0 && switchMode[DM_B256 - 1][latestModeEntry] == DM_B256) {
                latestModeEntry--;
            }
            textLength = symbolIndex - latestModeEntry + 1;
            if (textLength != 250 && 1 > dataLength)
                return -1;
            if (textLength == 250 && 2 > dataLength)
                return -1;
            simulatedDataOffset -= (textLength - 1) + (textLength < 250 ? 2 : 3);
        }
        if (textLength < 250) {
            data[simulatedDataOffset + 1] = (byte) textLength;
            minRequiredDataIncrement = prevEnc != DM_B256 ? 2 : 0;
        } else if (textLength == 250 && prevEnc == DM_B256) {
            data[simulatedDataOffset + 1] = (byte) (textLength / 250 + 249);
            for (int i = dataOffset + 1; i > simulatedDataOffset + 2; i--)
                data[i] = data[i - 1];
            data[simulatedDataOffset + 2] = (byte) (textLength % 250);
            minRequiredDataIncrement = 1;
        } else {
            data[simulatedDataOffset + 1] = (byte) (textLength / 250 + 249);
            data[simulatedDataOffset + 2] = (byte) (textLength % 250);
            minRequiredDataIncrement = prevEnc != DM_B256 ? 3 : 0;
        }
        if (prevEnc == DM_B256)
            textLength = 1;
        System.arraycopy(text, textOffset, data, minRequiredDataIncrement + dataOffset, textLength);
        for (int j = prevEnc != DM_B256 ? dataOffset + 1 : dataOffset; j < minRequiredDataIncrement + textLength + dataOffset; ++j) {
            randomizationAlgorithm255(data, j);
        }
        if (prevEnc == DM_B256)
            randomizationAlgorithm255(data, simulatedDataOffset + 1);
        return textLength + dataOffset + minRequiredDataIncrement - origDataOffset;
    }

    private void randomizationAlgorithm255(byte[] data, int j) {
        int c = data[j] & 0xff;
        int prn = 149 * (j + 1) % 255 + 1;
        int tv = c + prn;
        if (tv > 255)
            tv -= 256;
        data[j] = (byte) tv;
    }

    private int X12Encodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength, int symbolIndex, int prevEnc, int origDataOffset) {
        int ptrIn, ptrOut, count, k, n, ci;
        boolean latch = true;
        byte c;
        if (textLength == 0)
            return 0;
        ptrIn = 0;
        ptrOut = 0;
        byte[] x = new byte[textLength];
        count = 0;
        for (; ptrIn < textLength; ++ptrIn) {
            int i = X12.indexOf((char) text[ptrIn + textOffset]);
            if (i >= 0) {
                x[ptrIn] = (byte) i;
                ++count;
            } else {
                x[ptrIn] = 100;
                if (count >= 6)
                    count -= count / 3 * 3;
                for (k = 0; k < count; ++k)
                    x[ptrIn - k - 1] = 100;
                count = 0;
            }
        }
        if (count >= 6)
            count -= count / 3 * 3;
        for (k = 0; k < count; ++k)
            x[ptrIn - k - 1] = 100;
        ptrIn = 0;
        c = 0;
        for (; ptrIn < textLength; ++ptrIn) {
            c = x[ptrIn];
            if (ptrOut > dataLength)
                break;
            if (c < 40) {
                if (ptrIn == 0 && latch || ptrIn > 0 && x[ptrIn - 1] > 40)
                    data[dataOffset + ptrOut++] = LATCH_X12;
                if (ptrOut + 2 > dataLength)
                    break;
                n = 1600 * x[ptrIn] + 40 * x[ptrIn + 1] + x[ptrIn + 2] + 1;
                data[dataOffset + ptrOut++] = (byte) (n / 256);
                data[dataOffset + ptrOut++] = (byte) n;
                ptrIn += 2;
            } else {
                boolean enterASCII = true;
                if (symbolIndex <= 0) {
                    if (ptrIn > 0 && x[ptrIn - 1] < 40)
                        data[dataOffset + ptrOut++] = UNLATCH;
                } else if (symbolIndex > 4 && prevEnc == DM_X12 && X12.indexOf((char) text[textOffset]) >= 0 && X12.indexOf((char) text[textOffset - 1]) >= 0) {
                    int latestModeEntry = symbolIndex - 1;
                    while (latestModeEntry > 0 && switchMode[DM_X12 - 1][latestModeEntry] == DM_X12
                            && (X12.indexOf((char) text[textOffset - (symbolIndex - latestModeEntry + 1)])) >= 0) {
                        latestModeEntry--;
                    }
                    int unlatch = -1;
                    if (symbolIndex - latestModeEntry >= 5) {
                        for (int i = 1; i <= symbolIndex - latestModeEntry; i++) {
                            if (data[dataOffset - i] == UNLATCH) {
                                unlatch = dataOffset - i;
                                break;
                            }
                        }
                        int amountOfEncodedWithASCII = unlatch >= 0 ? dataOffset - unlatch - 1 : symbolIndex - latestModeEntry;
                        if (amountOfEncodedWithASCII % 3 == 2) {
                            enterASCII = false;
                            textLength = amountOfEncodedWithASCII + 1;
                            textOffset -= amountOfEncodedWithASCII;
                            dataLength += unlatch < 0 ? amountOfEncodedWithASCII : amountOfEncodedWithASCII + 1;
                            dataOffset -= unlatch < 0 ? amountOfEncodedWithASCII : amountOfEncodedWithASCII + 1;
                            ptrIn = -1;
                            latch = unlatch != dataOffset;
                            x = new byte[amountOfEncodedWithASCII + 1];
                            for (int i = 0; i <= amountOfEncodedWithASCII; i++) {
                                x[i] = (byte) X12.indexOf((char) text[textOffset + i]);
                            }
                        } else {
                            x = new byte[1];
                            x[0] = 100;
                        }
                    }
                }
                if (enterASCII) {
                    int i = asciiEncodation(text, textOffset + ptrIn, 1, data, dataOffset + ptrOut, dataLength, -1, -1, origDataOffset);
                    if (i < 0)
                        return -1;
                    if (data[dataOffset + ptrOut] == EXTENDED_ASCII)
                        ptrOut++;
                    ptrOut++;
                }
            }
        }
        c = 100;
        if (textLength > 0)
            c = x[textLength - 1];
        if (ptrIn != textLength)
            return -1;
        if (c < 40)
            data[dataOffset + ptrOut++] = UNLATCH;
        if (ptrOut > dataLength)
            return -1;
        return ptrOut + dataOffset - origDataOffset;
    }

    private int EdifactEncodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength, int symbolIndex, int prevEnc, int origDataOffset, boolean sizeFixed) {
        int ptrIn, ptrOut, edi, pedi, c;
        if (textLength == 0)
            return 0;
        ptrIn = 0;
        ptrOut = 0;
        edi = 0;
        pedi = 18;
        boolean ascii = true;
        int latestModeEntryActual = -1, latestModeEntryC40orX12 = -1, prevMode = -1;
        if (prevEnc == DM_EDIFACT && ((text[textOffset] & 0xff & 0xe0) == 0x40 || (text[textOffset] & 0xff & 0xe0) == 0x20) && (text[textOffset] & 0xff) != '_'
                && ((text[textOffset - 1] & 0xff & 0xe0) == 0x40 || (text[textOffset - 1] & 0xff & 0xe0) == 0x20) && (text[textOffset - 1] & 0xff) != '_') {
            latestModeEntryActual = symbolIndex - 1;
            while (latestModeEntryActual > 0 && switchMode[DM_EDIFACT - 1][latestModeEntryActual] == DM_EDIFACT) {
                c = text[textOffset - (symbolIndex - latestModeEntryActual + 1)] & 0xff;
                if (((c & 0xe0) == 0x40 || (c & 0xe0) == 0x20) && c != '_') {
                    latestModeEntryActual--;
                } else
                    break;
            }
            prevMode = switchMode[DM_EDIFACT - 1][latestModeEntryActual] == DM_C40
                    || switchMode[DM_EDIFACT - 1][latestModeEntryActual] == DM_X12 ? switchMode[DM_EDIFACT - 1][latestModeEntryActual] : -1;
            if (prevMode > 0)
                latestModeEntryC40orX12 = latestModeEntryActual;
            while (prevMode > 0 && latestModeEntryC40orX12 > 0 && switchMode[prevMode - 1][latestModeEntryC40orX12] == prevMode) {
                c = text[textOffset - (symbolIndex - latestModeEntryC40orX12 + 1)] & 0xff;
                if (((c & 0xe0) == 0x40 || (c & 0xe0) == 0x20) && c != '_') {
                    latestModeEntryC40orX12--;
                } else {
                    latestModeEntryC40orX12 = -1;
                    break;
                }
            }
        }
        int dataSize = dataOffset + dataLength;
        boolean asciiOneSymbol = false;
        if (symbolIndex != -1)
            asciiOneSymbol = true;
        int dataTaken = 0, dataRequired = 0;
        if (latestModeEntryC40orX12 >= 0 && symbolIndex - latestModeEntryC40orX12 + 1 > 9) {
            textLength = symbolIndex - latestModeEntryC40orX12 + 1;
            dataTaken = 0;
            dataRequired = 0;
            dataRequired += 1 + (textLength / 4 * 3);
            if (!sizeFixed && (symbolIndex == text.length - 1 || symbolIndex < 0) && textLength % 4 < 3) {
                dataSize = Integer.MAX_VALUE;
                for (int i = 0; i < dmSizes.length; ++i) {
                    if (dmSizes[i].dataSize >= dataRequired + textLength % 4) {
                        dataSize = dmSizes[i].dataSize;
                        break;
                    }
                }
            }
            if (dataSize - dataOffset - dataRequired <= 2 && textLength % 4 <= 2)
                dataRequired += (textLength % 4);
            else {
                dataRequired += (textLength % 4) + 1;
                if (textLength % 4 == 3)
                    dataRequired--;
            }
            for (int i = dataOffset - 1; i >= 0; i--) {
                dataTaken++;
                if (data[i] == (prevMode == DM_C40 ? LATCH_C40 : LATCH_X12)) {
                    break;
                }
            }
            if (dataRequired <= dataTaken) {
                asciiOneSymbol = false;
                textOffset -= textLength - 1;
                dataOffset -= dataTaken;
                dataLength += dataTaken;
            }
        } else if (latestModeEntryActual >= 0 && symbolIndex - latestModeEntryActual + 1 > 9) {
            textLength = symbolIndex - latestModeEntryActual + 1;
            dataRequired += 1 + (textLength / 4 * 3);
            if (dataSize - dataOffset - dataRequired <= 2 && textLength % 4 <= 2)
                dataRequired += (textLength % 4);
            else {
                dataRequired += (textLength % 4) + 1;
                if (textLength % 4 == 3)
                    dataRequired--;
            }
            int dataNewOffset = 0;
            int latchEdi = -1;
            for (int i = origDataOffset; i < dataOffset; i++)
                if (data[i] == LATCH_EDIFACT && dataOffset - i <= dataRequired) {
                    latchEdi = i;
                    break;
                }
            if (latchEdi != -1) {
                dataTaken += dataOffset - latchEdi;
                if ((text[textOffset] & 0xff) > 127)
                    dataTaken += 2;
                else {
                    if (isDigit(text[textOffset] & 0xff) && isDigit(text[textOffset - 1] & 0xff) &&
                            data[dataOffset - 1] >= 49 && data[dataOffset - 1] <= 58) {
                        dataTaken--;
                    }
                    dataTaken++;
                }
                dataNewOffset = dataOffset - latchEdi;
            } else {
                for (int j = symbolIndex - latestModeEntryActual; j >= 0; j--) {
                    if ((text[textOffset - j] & 0xff) > 127)
                        dataTaken += 2;
                    else {
                        if (j > 0 && isDigit(text[textOffset - j] & 0xff) && isDigit(text[textOffset - j + 1] & 0xff)) {
                            if (j == 1)
                                dataNewOffset = dataTaken;
                            j--;
                        }
                        dataTaken++;
                    }
                    if (j == 1)
                        dataNewOffset = dataTaken;
                }
            }
            if (dataRequired <= dataTaken) {
                asciiOneSymbol = false;
                textOffset -= textLength - 1;
                dataOffset -= dataNewOffset;
                dataLength += dataNewOffset;
            }
        }
        if (asciiOneSymbol) {
            c = text[textOffset] & 0xff;
            if (isDigit(c) && textOffset + ptrIn > 0 && isDigit(text[textOffset - 1] & 0xff)
                    && prevEnc == DM_EDIFACT && data[dataOffset - 1] >= 49 && data[dataOffset - 1] <= 58) {
                data[dataOffset + ptrOut - 1] = (byte) (((text[textOffset - 1] & 0xff) - '0') * 10 + c - '0' + 130);
                return dataOffset - origDataOffset;
            } else {
                return asciiEncodation(text, textOffset + ptrIn, 1, data, dataOffset + ptrOut, dataLength, -1, -1, origDataOffset);
            }
        }
        for (; ptrIn < textLength; ++ptrIn) {
            c = text[ptrIn + textOffset] & 0xff;
            if (((c & 0xe0) == 0x40 || (c & 0xe0) == 0x20) && c != '_') {
                if (ascii) {
                    if (ptrOut + 1 > dataLength)
                        break;
                    data[dataOffset + ptrOut++] = LATCH_EDIFACT;
                    ascii = false;
                }
                c &= 0x3f;
                edi |= c << pedi;
                if (pedi == 0) {
                    if (ptrOut + 3 > dataLength)
                        break;
                    data[dataOffset + ptrOut++] = (byte) (edi >> 16);
                    data[dataOffset + ptrOut++] = (byte) (edi >> 8);
                    data[dataOffset + ptrOut++] = (byte) edi;
                    edi = 0;
                    pedi = 18;
                } else
                    pedi -= 6;
            } else {
                if (!ascii) {
                    edi |= ('_' & 0x3f) << pedi;
                    if (ptrOut + 3 - pedi / 8 > dataLength)
                        break;
                    data[dataOffset + ptrOut++] = (byte) (edi >> 16);
                    if (pedi <= 12)
                        data[dataOffset + ptrOut++] = (byte) (edi >> 8);
                    if (pedi <= 6)
                        data[dataOffset + ptrOut++] = (byte) edi;
                    ascii = true;
                    pedi = 18;
                    edi = 0;
                }
                if (isDigit(c) && textOffset + ptrIn > 0 && isDigit(text[textOffset + ptrIn - 1] & 0xff) &&
                        prevEnc == DM_EDIFACT && data[dataOffset - 1] >= 49 && data[dataOffset - 1] <= 58) {
                    data[dataOffset + ptrOut - 1] = (byte) (((text[textOffset - 1] & 0xff) - '0') * 10 + c - '0' + 130);
                    ptrOut--;
                } else {
                    int i = asciiEncodation(text, textOffset + ptrIn, 1, data, dataOffset + ptrOut, dataLength, -1, -1, origDataOffset);
                    if (i < 0)
                        return -1;
                    if (data[dataOffset + ptrOut] == EXTENDED_ASCII)
                        ptrOut++;
                    ptrOut++;
                }
            }
        }
        if (ptrIn != textLength)
            return -1;
        if (!sizeFixed && (symbolIndex == text.length - 1 || symbolIndex < 0)) {
            dataSize = Integer.MAX_VALUE;
            for (int i = 0; i < dmSizes.length; ++i) {
                if (dmSizes[i].dataSize >= dataOffset + ptrOut + (3 - pedi / 6)) {
                    dataSize = dmSizes[i].dataSize;
                    break;
                }
            }
        }
        if (dataSize - dataOffset - ptrOut <= 2 && pedi >= 6) {
            if (pedi != 18 && ptrOut + 2 - pedi / 8 > dataLength)
                return -1;
            if (pedi <= 12) {
                byte val = (byte) ((edi >> 18) & 0x3F);
                if ((val & 0x20) == 0)
                    val |= 0x40;
                data[dataOffset + ptrOut++] = (byte) (val + 1);
            }
            if (pedi <= 6) {
                byte val = (byte) ((edi >> 12) & 0x3F);
                if ((val & 0x20) == 0)
                    val |= 0x40;
                data[dataOffset + ptrOut++] = (byte) (val + 1);
            }
        } else if (!ascii) {
            edi |= ('_' & 0x3f) << pedi;
            if (ptrOut + 3 - pedi / 8 > dataLength)
                return -1;
            data[dataOffset + ptrOut++] = (byte) (edi >> 16);
            if (pedi <= 12)
                data[dataOffset + ptrOut++] = (byte) (edi >> 8);
            if (pedi <= 6)
                data[dataOffset + ptrOut++] = (byte) edi;
        }
        return ptrOut + dataOffset - origDataOffset;
    }

    private int C40OrTextEncodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength, boolean c40, int symbolIndex, int prevEnc, int origDataOffset) {
        int ptrIn, ptrOut, encPtr, last0, last1, i, a, c;
        String basic, shift2, shift3;
        if (textLength == 0)
            return 0;
        ptrIn = 0;
        ptrOut = 0;
        shift2 = "!\"#$%&'()*+,-./:;<=>?@[\\]^_";
        if (c40) {
            basic = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            shift3 = "`abcdefghijklmnopqrstuvwxyz{|}~\177";
        } else {
            basic = " 0123456789abcdefghijklmnopqrstuvwxyz";
            shift3 = "`ABCDEFGHIJKLMNOPQRSTUVWXYZ{|}~\177";
        }
        boolean addLatch = true, usingASCII = false;
        int mode = c40 ? DM_C40 : DM_TEXT;
        if (prevEnc == mode) {
            usingASCII = true;
            int latestModeEntry = symbolIndex - 1;
            while (latestModeEntry > 0 && switchMode[mode - 1][latestModeEntry] == mode) {
                latestModeEntry--;
            }
            int unlatch = -1;
            int dataAmountOfEncodedWithASCII = 0;
            if (symbolIndex - latestModeEntry >= 5) {
                for (i = symbolIndex - latestModeEntry; i > 0; i--) {
                    c = text[textOffset - i] & 0xff;
                    if (c > 127) {
                        dataAmountOfEncodedWithASCII += 2;
                    } else
                        dataAmountOfEncodedWithASCII++;
                }
                for (i = 1; i <= dataAmountOfEncodedWithASCII; i++) {
                    if (i > dataOffset)
                        break;
                    if (data[dataOffset - i] == UNLATCH) {
                        unlatch = dataOffset - i;
                        break;
                    }
                }
                int amountOfEncodedWithASCII = 0;
                if (unlatch >= 0)
                    for (i = unlatch + 1; i < dataOffset; i++) {
                        if (data[i] == EXTENDED_ASCII)
                            i++;
                        if (data[i] >= -127 && data[i] <= -27)
                            amountOfEncodedWithASCII++;
                        amountOfEncodedWithASCII++;
                    }
                else
                    amountOfEncodedWithASCII = symbolIndex - latestModeEntry;
                int dataOffsetNew = 0;
                for (i = amountOfEncodedWithASCII; i > 0; i--) {
                    int requiredCapacityForASCII = 0;
                    int requiredCapacityForC40orText = 0;
                    for (int j = i; j >= 0; j--) {
                        c = text[textOffset - j] & 0xff;
                        if (c > 127) {
                            c -= 128;
                            requiredCapacityForC40orText += 2;
                        }
                        requiredCapacityForC40orText += basic.indexOf((char) c) >= 0 ? 1 : 2;
                        if (c > 127)
                            requiredCapacityForASCII += 2;
                        else {
                            if (j > 0 && isDigit(c) && isDigit(text[textOffset - j + 1] & 0xff)) {
                                requiredCapacityForC40orText += basic.indexOf((char) text[textOffset - j + 1]) >= 0 ? 1 : 2;
                                j--;
                                dataOffsetNew = requiredCapacityForASCII + 1;
                            }
                            requiredCapacityForASCII++;
                        }
                        if (j == 1)
                            dataOffsetNew = requiredCapacityForASCII;
                    }
                    addLatch = (unlatch < 0) || ((dataOffset - requiredCapacityForASCII) != unlatch);
                    if (requiredCapacityForC40orText % 3 == 0 &&
                            requiredCapacityForC40orText / 3 * 2 + (addLatch ? 2 : 0) < requiredCapacityForASCII) {
                        usingASCII = false;
                        textLength = i + 1;
                        textOffset -= i;
                        dataOffset -= addLatch ? dataOffsetNew : dataOffsetNew + 1;
                        dataLength += addLatch ? dataOffsetNew : dataOffsetNew + 1;
                        break;
                    }
                    if (isDigit(text[textOffset - i] & 0xff) && isDigit(text[textOffset - i + 1] & 0xff))
                        i--;
                }
            }
        } else if (symbolIndex != -1) {
            usingASCII = true;
        }
        if (dataOffset < 0) {
            return -1;
        }
        if (usingASCII) {
            return asciiEncodation(text, textOffset, 1, data, dataOffset, dataLength, prevEnc == mode ? 1 : -1, DM_ASCII, origDataOffset);
        }
        if (addLatch) {
            data[dataOffset + ptrOut++] = c40 ? LATCH_C40 : LATCH_TEXT;
        }
        int[] enc = new int[textLength * 4 + 10];
        encPtr = 0;
        last0 = 0;
        last1 = 0;
        while (ptrIn < textLength) {
            if (encPtr % 3 == 0) {
                last0 = ptrIn;
                last1 = encPtr;
            }
            c = text[textOffset + ptrIn++] & 0xff;
            if (c > 127) {
                c -= 128;
                enc[encPtr++] = 1;
                enc[encPtr++] = 30;
            }
            int idx = basic.indexOf((char) c);
            if (idx >= 0) {
                enc[encPtr++] = idx + 3;
            } else if (c < 32) {
                enc[encPtr++] = 0;
                enc[encPtr++] = c;
            } else if ((idx = shift2.indexOf((char) c)) >= 0) {
                enc[encPtr++] = 1;
                enc[encPtr++] = idx;
            } else if ((idx = shift3.indexOf((char) c)) >= 0) {
                enc[encPtr++] = 2;
                enc[encPtr++] = idx;
            }
        }
        if (encPtr % 3 != 0) {
            ptrIn = last0;
            encPtr = last1;
        }
        if (encPtr / 3 * 2 > dataLength - 2) {
            return -1;
        }
        i = 0;
        for (; i < encPtr; i += 3) {
            a = 1600 * enc[i] + 40 * enc[i + 1] + enc[i + 2] + 1;
            data[dataOffset + ptrOut++] = (byte) (a / 256);
            data[dataOffset + ptrOut++] = (byte) a;
        }
        if (dataLength - ptrOut > 2)
            data[dataOffset + ptrOut++] = UNLATCH;
        if (symbolIndex < 0 && textLength > ptrIn) {
            i = asciiEncodation(text, textOffset + ptrIn, textLength - ptrIn, data, dataOffset + ptrOut, dataLength - ptrOut, -1, -1, origDataOffset);
            return i;
        }
        return ptrOut + dataOffset - origDataOffset;
    }

    private void setBit(int x, int y, int xByte) {
        image[y * xByte + x / 8] |= (byte) (128 >> (x & 7));
    }

    private void draw(byte[] data, int dataSize, DmParams dm) {
        int i, j, p, x, y, xs, ys, z;
        int xByte = (dm.width + ws * 2 + 7) / 8;
        Arrays.fill(image, (byte) 0);
        //alignment patterns
        //dotted horizontal line
        for (i = ws; i < dm.height + ws; i += dm.heightSection) {
            for (j = ws; j < dm.width + ws; j += 2) {
                setBit(j, i, xByte);
            }
        }
        //solid horizontal line
        for (i = dm.heightSection - 1 + ws; i < dm.height + ws; i += dm.heightSection) {
            for (j = ws; j < dm.width + ws; ++j) {
                setBit(j, i, xByte);
            }
        }
        //solid vertical line
        for (i = ws; i < dm.width + ws; i += dm.widthSection) {
            for (j = ws; j < dm.height + ws; ++j) {
                setBit(i, j, xByte);
            }
        }
        //dotted vertical line
        for (i = dm.widthSection - 1 + ws; i < dm.width + ws; i += dm.widthSection) {
            for (j = 1 + ws; j < dm.height + ws; j += 2) {
                setBit(i, j, xByte);
            }
        }
        p = 0;
        for (ys = 0; ys < dm.height; ys += dm.heightSection) {
            for (y = 1; y < dm.heightSection - 1; ++y) {
                for (xs = 0; xs < dm.width; xs += dm.widthSection) {
                    for (x = 1; x < dm.widthSection - 1; ++x) {
                        z = place[p++];
                        if (z == 1 || z > 1 && (data[z / 8 - 1] & 0xff & 128 >> z % 8) != 0)
                            setBit(x + xs + ws, y + ys + ws, xByte);
                    }
                }
            }
        }
    }

    private static int minValueInColumn(int[][] array, int column) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 6; i++)
            if (array[i][column] < min && array[i][column] >= 0)
                min = array[i][column];
        return min != Integer.MAX_VALUE ? min : -1;
    }

    private static int valuePositionInColumn(int[][] array, int column, int value) {
        for (int i = 0; i < 6; i++)
            if (array[i][column] == value)
                return i;
        return -1;
    }

    private void solveFAndSwitchMode(int[] forMin, int mode, int currIndex) {
        if (forMin[mode] >= 0 && f[mode][currIndex - 1] >= 0) {
            f[mode][currIndex] = forMin[mode];
            switchMode[mode][currIndex] = mode + 1;
        } else {
            f[mode][currIndex] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < 6; i++) {
            if (forMin[i] < f[mode][currIndex] && forMin[i] >= 0 && f[i][currIndex - 1] >= 0) {
                f[mode][currIndex] = forMin[i];
                switchMode[mode][currIndex] = i + 1;
            }
        }
        if (f[mode][currIndex] == Integer.MAX_VALUE) {
            f[mode][currIndex] = -1;
        }
    }

    private int getEncodation(byte[] text, int textOffset, int textSize, byte[] data, int dataOffset, int dataSize, int options, boolean sizeFixed) {
        int e;
        if (dataSize < 0)
            return -1;
        options &= 7;
        if (options == 0) {
            if (textSize == 0)
                return 0;
            byte[][] dataDynamic = new byte[6][data.length];
            for (int i = 0; i < 6; i++) {
                System.arraycopy(data, 0, dataDynamic[i], 0, data.length);
                switchMode[i][0] = i + 1;
            }
            f[0][0] = asciiEncodation(text, textOffset, 1, dataDynamic[0], dataOffset, dataSize, 0, -1, dataOffset);
            f[1][0] = C40OrTextEncodation(text, textOffset, 1, dataDynamic[1], dataOffset, dataSize, true, 0, -1, dataOffset);
            f[2][0] = C40OrTextEncodation(text, textOffset, 1, dataDynamic[2], dataOffset, dataSize, false, 0, -1, dataOffset);
            f[3][0] = b256Encodation(text, textOffset, 1, dataDynamic[3], dataOffset, dataSize, 0, -1, dataOffset);
            f[4][0] = X12Encodation(text, textOffset, 1, dataDynamic[4], dataOffset, dataSize, 0, -1, dataOffset);
            f[5][0] = EdifactEncodation(text, textOffset, 1, dataDynamic[5], dataOffset, dataSize, 0, -1, dataOffset, sizeFixed);
            for (int i = 1; i < textSize; i++) {
                int tempForMin[] = new int[6];
                for (int currEnc = 0; currEnc < 6; currEnc++) {
                    byte[][] dataDynamicInner = new byte[6][data.length];
                    for (int prevEnc = 0; prevEnc < 6; prevEnc++) {
                        System.arraycopy(dataDynamic[prevEnc], 0, dataDynamicInner[prevEnc], 0, data.length);
                        if (f[prevEnc][i - 1] < 0)
                            tempForMin[prevEnc] = -1;
                        else {
                            if (currEnc == 0)
                                tempForMin[prevEnc] = asciiEncodation(text, textOffset + i, 1, dataDynamicInner[prevEnc], f[prevEnc][i - 1] + dataOffset, dataSize - f[prevEnc][i - 1], i, prevEnc + 1, dataOffset);
                            if (currEnc == 1)
                                tempForMin[prevEnc] = C40OrTextEncodation(text, textOffset + i, 1, dataDynamicInner[prevEnc], f[prevEnc][i - 1] + dataOffset, dataSize - f[prevEnc][i - 1], true, i, prevEnc + 1, dataOffset);
                            if (currEnc == 2)
                                tempForMin[prevEnc] = C40OrTextEncodation(text, textOffset + i, 1, dataDynamicInner[prevEnc], f[prevEnc][i - 1] + dataOffset, dataSize - f[prevEnc][i - 1], false, i, prevEnc + 1, dataOffset);
                            if (currEnc == 3)
                                tempForMin[prevEnc] = b256Encodation(text, textOffset + i, 1, dataDynamicInner[prevEnc], f[prevEnc][i - 1] + dataOffset, dataSize - f[prevEnc][i - 1], i, prevEnc + 1, dataOffset);
                            if (currEnc == 4)
                                tempForMin[prevEnc] = X12Encodation(text, textOffset + i, 1, dataDynamicInner[prevEnc], f[prevEnc][i - 1] + dataOffset, dataSize - f[prevEnc][i - 1], i, prevEnc + 1, dataOffset);
                            if (currEnc == 5)
                                tempForMin[prevEnc] = EdifactEncodation(text, textOffset + i, 1, dataDynamicInner[prevEnc], f[prevEnc][i - 1] + dataOffset, dataSize - f[prevEnc][i - 1], i, prevEnc + 1, dataOffset, sizeFixed);
                        }
                    }
                    solveFAndSwitchMode(tempForMin, currEnc, i);
                    if (switchMode[currEnc][i] != 0)
                        System.arraycopy(dataDynamicInner[switchMode[currEnc][i] - 1], 0, dataDynamic[currEnc], 0, data.length);
                }
            }
            e = minValueInColumn(f, textSize - 1);
            if (e > dataSize || e < 0)
                return -1;
            int bestDataDynamicResultIndex = valuePositionInColumn(f, textSize - 1, e);
            System.arraycopy(dataDynamic[bestDataDynamicResultIndex], 0, data, 0, data.length);
            return e;
        }
        switch (options) {
            case DM_ASCII:
                return asciiEncodation(text, textOffset, textSize, data, dataOffset, dataSize, -1, -1, dataOffset);
            case DM_C40:
                return C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, true, -1, -1, dataOffset);
            case DM_TEXT:
                return C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, false, -1, -1, dataOffset);
            case DM_B256:
                return b256Encodation(text, textOffset, textSize, data, dataOffset, dataSize, -1, -1, dataOffset);
            case DM_X12:
                return X12Encodation(text, textOffset, textSize, data, dataOffset, dataSize, -1, -1, dataOffset);
            case DM_EDIFACT:
                return EdifactEncodation(text, textOffset, textSize, data, dataOffset, dataSize, -1, -1, dataOffset, sizeFixed);
            case DM_RAW:
                if (textSize > dataSize)
                    return -1;
                System.arraycopy(text, textOffset, data, dataOffset, textSize);
                return textSize;
        }
        return -1;
    }

    private static int getNumber(byte[] text, int ptrIn, int n) {
        int v, j, c;
        v = 0;
        for (j = 0; j < n; ++j) {
            c = text[ptrIn++] & 0xff;
            if (c < '0' || c > '9')
                return -1;
            v = v * 10 + c - '0';
        }
        return v;
    }

    private int processExtensions(byte[] text, int textOffset, int textSize, byte[] data) {
        int order, ptrIn, ptrOut, eci, fn, ft, fi, c;
        if ((options & DM_EXTENSION) == 0)
            return 0;
        order = 0;
        ptrIn = 0;
        ptrOut = 0;
        while (ptrIn < textSize) {
            if (order > 20)
                return -1;
            c = text[textOffset + ptrIn++] & 0xff;
            ++order;
            switch (c) {
                case '.':
                    extOut = ptrIn;
                    return ptrOut;
                case 'e':
                    if (ptrIn + 6 > textSize)
                        return -1;
                    eci = getNumber(text, textOffset + ptrIn, 6);
                    if (eci < 0)
                        return -1;
                    ptrIn += 6;
                    data[ptrOut++] = (byte) 241;
                    if (eci < 127)
                        data[ptrOut++] = (byte) (eci + 1);
                    else if (eci < 16383) {
                        data[ptrOut++] = (byte) ((eci - 127) / 254 + 128);
                        data[ptrOut++] = (byte) ((eci - 127) % 254 + 1);
                    } else {
                        data[ptrOut++] = (byte) ((eci - 16383) / 64516 + 192);
                        data[ptrOut++] = (byte) ((eci - 16383) / 254 % 254 + 1);
                        data[ptrOut++] = (byte) ((eci - 16383) % 254 + 1);
                    }
                    break;
                case 's':
                    if (order != 1)
                        return -1;
                    if (ptrIn + 9 > textSize)
                        return -1;
                    fn = getNumber(text, textOffset + ptrIn, 2);
                    if (fn <= 0 || fn > 16)
                        return -1;
                    ptrIn += 2;
                    ft = getNumber(text, textOffset + ptrIn, 2);
                    if (ft <= 1 || ft > 16)
                        return -1;
                    ptrIn += 2;
                    fi = getNumber(text, textOffset + ptrIn, 5);
                    if (fi < 0 || fn >= 64516)
                        return -1;
                    ptrIn += 5;
                    data[ptrOut++] = (byte) 233;
                    data[ptrOut++] = (byte) (fn - 1 << 4 | 17 - ft);
                    data[ptrOut++] = (byte) (fi / 254 + 1);
                    data[ptrOut++] = (byte) (fi % 254 + 1);
                    break;
                case 'p':
                    if (order != 1)
                        return -1;
                    data[ptrOut++] = (byte) 234;
                    break;
                case 'm':
                    if (order != 1)
                        return -1;
                    if (ptrIn + 1 > textSize)
                        return -1;
                    c = text[textOffset + ptrIn++] & 0xff;
                    if (c != '5')
                        return -1;
                    data[ptrOut++] = (byte) 234;
                    data[ptrOut++] = (byte) 236;
                    break;
                case 'f':
                    if (order != 1 && (order != 2 || text[textOffset] != 's' && text[textOffset] != 'm'))
                        return -1;
                    data[ptrOut++] = (byte) 232;
            }
        }
        return -1;
    }
}

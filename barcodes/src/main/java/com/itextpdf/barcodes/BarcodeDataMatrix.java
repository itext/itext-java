package com.itextpdf.barcodes;

import com.itextpdf.barcodes.dmcode.DmParams;
import com.itextpdf.barcodes.dmcode.Placement;
import com.itextpdf.barcodes.dmcode.ReedSolomon;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

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
     * X21 encodation.
     */
    public static final int DM_X21 = 5;
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

    private final static DmParams[] dmSizes = {
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

    private static final String x12 = "\r*> 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int extOut;
    private short[] place;
    private byte[] image;
    private int height;
    private int width;
    private int ws;
    private int options;

    /**
     * Creates an instance of this class.
     */
    public BarcodeDataMatrix() {
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
     * @param foreground   the color of the pixels. It can be <CODE>null</CODE>
     * @param moduleSide  the side (width and height) of the pixels.
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
        int pix[] = new int[w * h];
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
     */
    public Rectangle getBarcodeSize(float moduleHeight, float moduleWidth) {
        return new Rectangle(0, 0, (width + 2 * ws) * moduleHeight, (height + 2 * ws) * moduleWidth);
    }

    /**
     * Creates a barcode. The <CODE>String</CODE> is interpreted with the ISO-8859-1 encoding
     *
     * @param text the text
     * @return the status of the generation. It can be one of this values:
     * <p/>
     * <CODE>DM_NO_ERROR</CODE> - no error.<br>
     * <CODE>DM_ERROR_TEXT_TOO_BIG</CODE> - the text is too big for the symbology capabilities.<br>
     * <CODE>DM_ERROR_INVALID_SQUARE</CODE> - the dimensions given for the symbol are illegal.<br>
     * <CODE>DM_ERROR_EXTENSION</CODE> - an error was while parsing an extension.
     */
    public int setCode(String text) {
        byte[] t;
        try {
            t = text.getBytes("iso-8859-1");
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
     * <p/>
     * <CODE>DM_NO_ERROR</CODE> - no error.<br>
     * <CODE>DM_ERROR_TEXT_TOO_BIG</CODE> - the text is too big for the symbology capabilities.<br>
     * <CODE>DM_ERROR_INVALID_SQUARE</CODE> - the dimensions given for the symbol are illegal.<br>
     * <CODE>DM_ERROR_EXTENSION</CODE> - an error was while parsing an extension.
     */
    public int setCode(byte[] text, int textOffset, int textSize) {
        int extCount, e, k, full;
        DmParams dm, last;
        byte[] data = new byte[2500];
        extOut = 0;
        extCount = processExtensions(text, textOffset, textSize, data);
        if (extCount < 0) {
            return DM_ERROR_EXTENSION;
        }
        e = -1;
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
     * Sets the height of the barcode. If the height is zero it will be calculated. This height doesn't include the whitespace border, if any.
     * <p/>
     * The allowed dimensions are (height, width):<p>
     * 10, 10<br>
     * 12, 12<br>
     * 8, 18<br>
     * 14, 14<br>
     * 8, 32<br>
     * 16, 16<br>
     * 12, 26<br>
     * 18, 18<br>
     * 20, 20<br>
     * 12, 36<br>
     * 22, 22<br>
     * 16, 36<br>
     * 24, 24<br>
     * 26, 26<br>
     * 16, 48<br>
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
     * Sets the width of the barcode. If the width is zero it will be calculated. This width doesn't include the whitespace border, if any.
     * <p/>
     * The allowed dimensions are (height, width):<p>
     * 10, 10<br>
     * 12, 12<br>
     * 8, 18<br>
     * 14, 14<br>
     * 8, 32<br>
     * 16, 16<br>
     * 12, 26<br>
     * 18, 18<br>
     * 20, 20<br>
     * 12, 36<br>
     * 22, 22<br>
     * 16, 36<br>
     * 24, 24<br>
     * 26, 26<br>
     * 16, 48<br>
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
     * <CODE>DM_X21</CODE> - X21 encodation<br>
     * <CODE>DM_EDIFACT</CODE> - EDIFACT encodation<br>
     * <CODE>DM_RAW</CODE> - no encodation. The bytes provided are already encoded and will be added directly to the barcode, using padding if needed. It assumes that the encodation state is left at ASCII after the last byte.<br>
     * <p/>
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


    private static void makePadding(byte[] data, int position, int count) {
        //already in ascii mode
        if (count <= 0)
            return;
        data[position++] = (byte) 129;
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

    private static int asciiEncodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength) {
        int ptrIn, ptrOut, c;
        ptrIn = textOffset;
        ptrOut = dataOffset;
        textLength += textOffset;
        dataLength += dataOffset;
        while (ptrIn < textLength) {
            if (ptrOut >= dataLength)
                return -1;
            c = text[ptrIn++] & 0xff;
            if (isDigit(c) && ptrIn < textLength && isDigit(text[ptrIn] & 0xff)) {
                data[ptrOut++] = (byte) ((c - '0') * 10 + (text[ptrIn++] & 0xff) - '0' + 130);
            } else if (c > 127) {
                if (ptrOut + 1 >= dataLength)
                    return -1;
                data[ptrOut++] = (byte) 235;
                data[ptrOut++] = (byte) (c - 128 + 1);
            } else {
                data[ptrOut++] = (byte) (c + 1);
            }
        }
        return ptrOut - dataOffset;
    }

    private static int b256Encodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength) {
        int k, j, prn, tv, c;
        if (textLength == 0)
            return 0;
        if (textLength < 250 && textLength + 2 > dataLength)
            return -1;
        if (textLength >= 250 && textLength + 3 > dataLength)
            return -1;
        data[dataOffset] = (byte) 231;
        if (textLength < 250) {
            data[dataOffset + 1] = (byte) textLength;
            k = 2;
        } else {
            data[dataOffset + 1] = (byte) (textLength / 250 + 249);
            data[dataOffset + 2] = (byte) (textLength % 250);
            k = 3;
        }
        System.arraycopy(text, textOffset, data, k + dataOffset, textLength);
        k += textLength + dataOffset;
        for (j = dataOffset + 1; j < k; ++j) {
            c = data[j] & 0xff;
            prn = 149 * (j + 1) % 255 + 1;
            tv = c + prn;
            if (tv > 255)
                tv -= 256;
            data[j] = (byte) tv;

        }
        return k - dataOffset;
    }

    private static int X12Encodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength) {
        int ptrIn, ptrOut, count, k, n, ci;
        byte c;
        if (textLength == 0)
            return 0;
        ptrIn = 0;
        ptrOut = 0;
        byte[] x = new byte[textLength];
        count = 0;
        for (; ptrIn < textLength; ++ptrIn) {
            int i = x12.indexOf((char) text[ptrIn + textOffset]);
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
            if (ptrOut >= dataLength)
                break;
            if (c < 40) {
                if (ptrIn == 0 || ptrIn > 0 && x[ptrIn - 1] > 40)
                    data[dataOffset + ptrOut++] = (byte) 238;
                if (ptrOut + 2 > dataLength)
                    break;
                n = 1600 * x[ptrIn] + 40 * x[ptrIn + 1] + x[ptrIn + 2] + 1;
                data[dataOffset + ptrOut++] = (byte) (n / 256);
                data[dataOffset + ptrOut++] = (byte) n;
                ptrIn += 2;
            } else {
                if (ptrIn > 0 && x[ptrIn - 1] < 40)
                    data[dataOffset + ptrOut++] = (byte) 254;
                ci = text[ptrIn + textOffset] & 0xff;
                if (ci > 127) {
                    data[dataOffset + ptrOut++] = (byte) 235;
                    ci -= 128;
                }
                if (ptrOut >= dataLength)
                    break;
                data[dataOffset + ptrOut++] = (byte) (ci + 1);
            }
        }
        c = 100;
        if (textLength > 0)
            c = x[textLength - 1];
        if (ptrIn != textLength || c < 40 && ptrOut >= dataLength)
            return -1;
        if (c < 40)
            data[dataOffset + ptrOut++] = (byte) 254;
        return ptrOut;
    }

    private static int EdifactEncodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength) {
        int ptrIn, ptrOut, edi, pedi, c;
        if (textLength == 0)
            return 0;
        ptrIn = 0;
        ptrOut = 0;
        edi = 0;
        pedi = 18;
        boolean ascii = true;
        for (; ptrIn < textLength; ++ptrIn) {
            c = text[ptrIn + textOffset] & 0xff;
            if (((c & 0xe0) == 0x40 || (c & 0xe0) == 0x20) && c != '_') {
                if (ascii) {
                    if (ptrOut + 1 > dataLength)
                        break;
                    data[dataOffset + ptrOut++] = (byte) 240;
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
                if (c > 127) {
                    if (ptrOut >= dataLength)
                        break;
                    data[dataOffset + ptrOut++] = (byte) 235;
                    c -= 128;
                }
                if (ptrOut >= dataLength)
                    break;
                data[dataOffset + ptrOut++] = (byte) (c + 1);
            }
        }
        if (ptrIn != textLength)
            return -1;
        int dataSize = Integer.MAX_VALUE;
        for (int i = 0; i < dmSizes.length; ++i) {
            if (dmSizes[i].dataSize >= dataOffset + ptrOut + (3 - pedi / 6)) {
                dataSize = dmSizes[i].dataSize;
                break;
            }
        }

        if (dataSize - dataOffset - ptrOut <= 2 && pedi >= 6) {
            //have to write up to 2 bytes and up to 2 symbols
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
        return ptrOut;
    }

    private static int C40OrTextEncodation(byte[] text, int textOffset, int textLength, byte[] data, int dataOffset, int dataLength, boolean c40) {
        int ptrIn, ptrOut, encPtr, last0, last1, i, a, c;
        String basic, shift2, shift3;
        if (textLength == 0)
            return 0;
        ptrIn = 0;
        ptrOut = 0;
        if (c40)
            data[dataOffset + ptrOut++] = (byte) 230;
        else
            data[dataOffset + ptrOut++] = (byte) 239;
        shift2 = "!\"#$%&'()*+,-./:;<=>?@[\\]^_";
        if (c40) {
            basic = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            shift3 = "`abcdefghijklmnopqrstuvwxyz{|}~\177";
        } else {
            basic = " 0123456789abcdefghijklmnopqrstuvwxyz";
            shift3 = "`ABCDEFGHIJKLMNOPQRSTUVWXYZ{|}~\177";
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
        data[ptrOut++] = (byte) 254;
        i = asciiEncodation(text, ptrIn, textLength - ptrIn, data, ptrOut, dataLength - ptrOut);
        if (i < 0)
            return i;
        return ptrOut + i;
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

    private static int getEncodation(byte[] text, int textOffset, int textSize, byte[] data, int dataOffset, int dataSize, int options, boolean firstMatch) {
        int e, j, k;
        int[] e1 = new int[6];
        if (dataSize < 0)
            return -1;
        e = -1;
        options &= 7;
        if (options == 0) {
            e1[0] = asciiEncodation(text, textOffset, textSize, data, dataOffset, dataSize);
            if (firstMatch && e1[0] >= 0)
                return e1[0];
            e1[1] = C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, false);
            if (firstMatch && e1[1] >= 0)
                return e1[1];
            e1[2] = C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, true);
            if (firstMatch && e1[2] >= 0)
                return e1[2];
            e1[3] = b256Encodation(text, textOffset, textSize, data, dataOffset, dataSize);
            if (firstMatch && e1[3] >= 0)
                return e1[3];
            e1[4] = X12Encodation(text, textOffset, textSize, data, dataOffset, dataSize);
            if (firstMatch && e1[4] >= 0)
                return e1[4];
            e1[5] = EdifactEncodation(text, textOffset, textSize, data, dataOffset, dataSize);
            if (firstMatch && e1[5] >= 0)
                return e1[5];
            if (e1[0] < 0 && e1[1] < 0 && e1[2] < 0 && e1[3] < 0 && e1[4] < 0 && e1[5] < 0) {
                return -1;
            }
            j = 0;
            e = 99999;
            for (k = 0; k < 6; ++k) {
                if (e1[k] >= 0 && e1[k] < e) {
                    e = e1[k];
                    j = k;
                }
            }
            if (j == 0)
                e = asciiEncodation(text, textOffset, textSize, data, dataOffset, dataSize);
            else if (j == 1)
                e = C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, false);
            else if (j == 2)
                e = C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, true);
            else if (j == 3)
                e = b256Encodation(text, textOffset, textSize, data, dataOffset, dataSize);
            else if (j == 4)
                e = X12Encodation(text, textOffset, textSize, data, dataOffset, dataSize);
            return e;
        }
        switch (options) {
            case DM_ASCII:
                return asciiEncodation(text, textOffset, textSize, data, dataOffset, dataSize);
            case DM_C40:
                return C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, true);
            case DM_TEXT:
                return C40OrTextEncodation(text, textOffset, textSize, data, dataOffset, dataSize, false);
            case DM_B256:
                return b256Encodation(text, textOffset, textSize, data, dataOffset, dataSize);
            case DM_X21:
                return X12Encodation(text, textOffset, textSize, data, dataOffset, dataSize);
            case DM_EDIFACT:
                return EdifactEncodation(text, textOffset, textSize, data, dataOffset, dataSize);
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
                    if (c != '5' && c != '5')
                        return -1;
                    data[ptrOut++] = (byte) 234;
                    data[ptrOut++] = (byte) (c == '5' ? 236 : 237);
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

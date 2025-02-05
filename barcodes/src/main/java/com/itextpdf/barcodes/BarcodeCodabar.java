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

import com.itextpdf.barcodes.exceptions.BarcodesExceptionMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of the Codabar barcode.
 * <p>
 * Codabar was designed to be accurately read even when printed on dot-matrix printers for multi-part forms such as
 * FedEx airbills and blood bank forms, where variants are still in use as of 2007. Although newer symbologies hold more
 * information in a smaller space.
 */
public class BarcodeCodabar extends Barcode1D {

    /**
     * The index chars to <CODE>BARS</CODE>.
     */
    private static final String CHARS = "0123456789-$:/.+ABCD";

    private static final int START_STOP_IDX = 16;

    /**
     * The bars to generate the code.
     */
    private static final byte[][] BARS =
            {

                    // 0
                    {0, 0, 0, 0, 0, 1, 1},

                    // 1
                    {0, 0, 0, 0, 1, 1, 0},

                    // 2
                    {0, 0, 0, 1, 0, 0, 1},

                    // 3
                    {1, 1, 0, 0, 0, 0, 0},

                    // 4
                    {0, 0, 1, 0, 0, 1, 0},

                    // 5
                    {1, 0, 0, 0, 0, 1, 0},

                    // 6
                    {0, 1, 0, 0, 0, 0, 1},

                    // 7
                    {0, 1, 0, 0, 1, 0, 0},

                    // 8
                    {0, 1, 1, 0, 0, 0, 0},

                    // 9
                    {1, 0, 0, 1, 0, 0, 0},

                    // -
                    {0, 0, 0, 1, 1, 0, 0},

                    // $
                    {0, 0, 1, 1, 0, 0, 0},

                    // :
                    {1, 0, 0, 0, 1, 0, 1},

                    // /
                    {1, 0, 1, 0, 0, 0, 1},

                    // .
                    {1, 0, 1, 0, 1, 0, 0},

                    // +
                    {0, 0, 1, 0, 1, 0, 1},

                    // a
                    {0, 0, 1, 1, 0, 1, 0},

                    // b
                    {0, 1, 0, 1, 0, 0, 1},

                    // c
                    {0, 0, 0, 1, 0, 1, 1},

                    // d
                    {0, 0, 0, 1, 1, 1, 0}
            };

    /**
     * Creates a new BarcodeCodabar.
     * To generate the font the {@link PdfDocument#getDefaultFont()} will be implicitly called.
     * If you want to use this barcode in PDF/A documents, please consider using {@link #BarcodeCodabar(PdfDocument, PdfFont)}.
     *
     * @param document The document to which the barcode will be added
     */
    public BarcodeCodabar(PdfDocument document) {
        this(document, document.getDefaultFont());
    }

    /**
     * Creates a new BarcodeCodabar.
     *
     * @param document The document to which the barcode will be added
     * @param font The font to use
     */
    public BarcodeCodabar(PdfDocument document, PdfFont font) {
        super(document);
        this.x = 0.8f;
        this.n = 2;
        this.font = font;
        this.size = 8;
        this.baseline = size;
        this.barHeight = size * 3;
        this.textAlignment = ALIGN_CENTER;
        this.generateChecksum = false;
        this.checksumText = false;
        this.startStopText = false;
    }

    /**
     * Creates the bars.
     *
     * @param text the text to create the bars
     * @return the bars
     */
    public static byte[] getBarsCodabar(String text) {
        text = text.toUpperCase();
        int len = text.length();
        if (len < 2) {
            throw new IllegalArgumentException(
                    BarcodesExceptionMessageConstant.CODABAR_MUST_HAVE_AT_LEAST_START_AND_STOP_CHARACTER);
        }
        if (CHARS.indexOf(text.charAt(0)) < START_STOP_IDX || CHARS.indexOf(text.charAt(len - 1)) < START_STOP_IDX) {
            throw new IllegalArgumentException(
                    BarcodesExceptionMessageConstant.CODABAR_MUST_HAVE_ONE_ABCD_AS_START_STOP_CHARACTER);
        }
        byte[] bars = new byte[text.length() * 8 - 1];
        for (int k = 0; k < len; ++k) {
            int idx = CHARS.indexOf(text.charAt(k));
            if (idx >= START_STOP_IDX && k > 0 && k < len - 1) {
                throw new IllegalArgumentException(BarcodesExceptionMessageConstant.
                        IN_CODABAR_START_STOP_CHARACTERS_ARE_ONLY_ALLOWED_AT_THE_EXTREMES);
            }
            if (idx < 0) {
                throw new IllegalArgumentException(
                        BarcodesExceptionMessageConstant.ILLEGAL_CHARACTER_IN_CODABAR_BARCODE);
            }
            System.arraycopy(BARS[idx], 0, bars, k * 8, 7);
        }
        return bars;
    }

    /**
     * Calculates the checksum.
     *
     * @param code the value to calculate the checksum for
     *
     * @return the checksum for the given value
     */
    public static String calculateChecksum(String code) {
        if (code.length() < 2)
            return code;
        String text = code.toUpperCase();
        int sum = 0;
        int len = text.length();
        for (int k = 0; k < len; ++k) {
            sum += CHARS.indexOf(text.charAt(k));
        }
        sum = (sum + 15) / 16 * 16 - sum;
        return code.substring(0, len - 1) + CHARS.charAt(sum) + code.substring(len - 1);
    }

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     *
     * @return the size the barcode occupies.
     */
    public Rectangle getBarcodeSize() {
        float fontX = 0;
        float fontY = 0;
        String text = code;
        if (generateChecksum && checksumText) {
            text = calculateChecksum(code);
        }
        if (!startStopText) {
            text = text.substring(1, text.length() - 1);
        }
        if (font != null) {
            if (baseline > 0) {
                fontY = baseline - getDescender();
            } else {
                fontY = -baseline + size;
            }
            fontX = font.getWidth(altText != null ? altText : text, size);
        }
        text = code;
        if (generateChecksum) {
            text = calculateChecksum(code);
        }
        byte[] bars = getBarsCodabar(text);
        int wide = 0;
        for (int k = 0; k < bars.length; ++k) {
            wide += bars[k];
        }
        int narrow = bars.length - wide;
        float fullWidth = x * (narrow + wide * n);
        fullWidth = Math.max(fullWidth, fontX);
        float fullHeight = barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }

    /**
     * Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.<p>
     * The bars and text are written in the following colors:
     * <br>
     * <TABLE BORDER="1" SUMMARY="barcode properties">
     * <TR>
     * <TH><CODE>barColor</CODE></TH>
     * <TH><CODE>textColor</CODE></TH>
     * <TH>Result</TH>
     * </TR>
     * <TR>
     * <TD><CODE>null</CODE></TD>
     * <TD><CODE>null</CODE></TD>
     * <TD>bars and text painted with current fill color</TD>
     * </TR>
     * <TR>
     * <TD><CODE>barColor</CODE></TD>
     * <TD><CODE>null</CODE></TD>
     * <TD>bars and text painted with <CODE>barColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><CODE>null</CODE></TD>
     * <TD><CODE>textColor</CODE></TD>
     * <TD>bars painted with current color<br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><CODE>barColor</CODE></TD>
     * <TD><CODE>textColor</CODE></TD>
     * <TD>bars painted with <CODE>barColor</CODE><br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * </TABLE>
     *
     * @param canvas    the <CODE>PdfCanvas</CODE> where the barcode will be placed
     * @param barColor  the color of the bars. It can be <CODE>null</CODE>
     * @param textColor the color of the text. It can be <CODE>null</CODE>
     * @return the dimensions the barcode occupies
     */
    public Rectangle placeBarcode(PdfCanvas canvas, Color barColor, Color textColor) {
        String fullCode = code;
        if (generateChecksum && checksumText) {
            fullCode = calculateChecksum(code);
        }
        if (!startStopText) {
            fullCode = fullCode.substring(1, fullCode.length() - 1);
        }
        float fontX = 0;
        if (font != null) {
            fontX = font.getWidth(fullCode = altText != null ? altText : fullCode, size);
        }
        byte[] bars = getBarsCodabar(generateChecksum ? calculateChecksum(code) : code);
        int wide = 0;
        for (int k = 0; k < bars.length; ++k) {
            wide += bars[k];
        }
        int narrow = bars.length - wide;
        float fullWidth = x * (narrow + wide * n);
        float barStartX = 0;
        float textStartX = 0;
        switch (textAlignment) {
            case ALIGN_LEFT:
                break;
            case ALIGN_RIGHT:
                if (fontX > fullWidth) {
                    barStartX = fontX - fullWidth;
                } else {
                    textStartX = fullWidth - fontX;
                }
                break;
            default:
                if (fontX > fullWidth) {
                    barStartX = (fontX - fullWidth) / 2;
                } else {
                    textStartX = (fullWidth - fontX) / 2;
                }
                break;
        }
        float barStartY = 0;
        float textStartY = 0;
        if (font != null) {
            if (baseline <= 0) {
                textStartY = barHeight - baseline;
            } else {
                textStartY = -getDescender();
                barStartY = textStartY + baseline;
            }
        }
        boolean print = true;
        if (barColor != null) {
            canvas.setFillColor(barColor);
        }
        for (int k = 0; k < bars.length; ++k) {
            float w = (bars[k] == 0 ? x : x * n);
            if (print) {
                canvas.rectangle(barStartX, barStartY, w - inkSpreading, barHeight);
            }
            print = !print;
            barStartX += w;
        }
        canvas.fill();
        if (font != null) {
            if (textColor != null) {
                canvas.setFillColor(textColor);
            }
            canvas.beginText();
            canvas.setFontAndSize(font, size);
            canvas.setTextMatrix(textStartX, textStartY);
            canvas.showText(fullCode);
            canvas.endText();
        }
        return getBarcodeSize();
    }

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    /**
     * Creates a <CODE>java.awt.Image</CODE>. This image only
     * contains the bars without any text.
     *
     * @param foreground the color of the bars
     * @param background the color of the background
     * @return the image
     */
    public java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background) {
        int f = (foreground == null) ? DEFAULT_BAR_FOREGROUND_COLOR.getRGB() : foreground.getRGB();
        int g = (background == null) ? DEFAULT_BAR_BACKGROUND_COLOR.getRGB() : background.getRGB();
        java.awt.Canvas canvas = new java.awt.Canvas();

        byte[] bars = getBarsCodabar(generateChecksum ? calculateChecksum(code) : code);
        int wide = 0;
        for (int k = 0; k < bars.length; ++k) {
            wide += bars[k];
        }
        int narrow = bars.length - wide;
        int fullWidth = narrow + wide * (int) n;
        boolean print = true;
        int ptr = 0;
        int height = (int) barHeight;
        int[] pix = new int[fullWidth * height];
        for (int k = 0; k < bars.length; ++k) {
            int w = (bars[k] == 0 ? 1 : (int) n);
            int c = g;
            if (print) {
                c = f;
            }
            print = !print;
            for (int j = 0; j < w; ++j) {
                pix[ptr++] = c;
            }
        }
        for (int k = fullWidth; k < pix.length; k += fullWidth) {
            System.arraycopy(pix, 0, pix, k, fullWidth);
        }
        return canvas.createImage(new java.awt.image.MemoryImageSource(fullWidth, height, pix, 0, fullWidth));
    }
    // Android-Conversion-Skip-Block-End
}

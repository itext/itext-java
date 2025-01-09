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
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;


/**
 * Implements the code interleaved 2 of 5. The text can include
 * non numeric characters that are printed but do not generate bars.
 * The default parameters are:
 * <pre>
 * x = 0.8f;
 * n = 2;
 * font = new PdfType1Font(document, new TYPE_1_FONT(StandardFonts.HELVETICA, PdfEncodings.WINANSI));
 * size = 8;
 * baseline = size;
 * barHeight = size * 3;
 * textAlignment = ALIGN_CENTER;
 * generateChecksum = false;
 * checksumText = false;
 * </pre>
 */
public class BarcodeInter25 extends Barcode1D {


    /**
     * The bars to generate the code.
     */
    private static final byte[][] BARS =
            {
                    {0, 0, 1, 1, 0},
                    {1, 0, 0, 0, 1},
                    {0, 1, 0, 0, 1},
                    {1, 1, 0, 0, 0},
                    {0, 0, 1, 0, 1},
                    {1, 0, 1, 0, 0},
                    {0, 1, 1, 0, 0},
                    {0, 0, 0, 1, 1},
                    {1, 0, 0, 1, 0},
                    {0, 1, 0, 1, 0}
            };

    /**
     * Creates new BarcodeInter25.
     * To generate the font the {@link PdfDocument#getDefaultFont()} will be implicitly called.
     * If you want to use this barcode in PDF/A documents, please consider using {@link #BarcodeInter25(PdfDocument, PdfFont)}.
     *
     * @param document The document to which the barcode will be added
     */
    public BarcodeInter25(PdfDocument document) {
        this(document, document.getDefaultFont());
    }

    /**
     * Creates new BarcodeInter25
     *
     * @param document The document to which the barcode will be added
     * @param font The font to use
     */
    public BarcodeInter25(PdfDocument document, PdfFont font) {
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
    }

    /**
     * Deletes all the non numeric characters from <CODE>text</CODE>.
     *
     * @param text the text
     * @return a <CODE>String</CODE> with only numeric characters
     */
    public static String keepNumbers(String text) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < text.length(); ++k) {
            char c = text.charAt(k);
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Calculates the checksum.
     *
     * @param text the numeric text
     * @return the checksum
     */
    public static char getChecksum(String text) {
        int mul = 3;
        int total = 0;
        for (int k = text.length() - 1; k >= 0; --k) {
            int n = text.charAt(k) - '0';
            total += mul * n;
            mul ^= 2;
        }
        return (char) (((10 - (total % 10)) % 10) + '0');
    }

    /**
     * Creates the bars for the barcode.
     *
     * @param text the text. It can contain non numeric characters
     * @return the barcode
     */
    public static byte[] getBarsInter25(String text) {
        text = keepNumbers(text);
        if ((text.length() & 1) != 0) {
            throw new PdfException(BarcodesExceptionMessageConstant.TEXT_MUST_BE_EVEN);
        }
        byte[] bars = new byte[text.length() * 5 + 7];
        int pb = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        int len = text.length() / 2;
        for (int k = 0; k < len; ++k) {
            int c1 = text.charAt(k * 2) - '0';
            int c2 = text.charAt(k * 2 + 1) - '0';
            byte[] b1 = BARS[c1];
            byte[] b2 = BARS[c2];
            for (int j = 0; j < 5; ++j) {
                bars[pb++] = b1[j];
                bars[pb++] = b2[j];
            }
        }
        bars[pb++] = 1;
        bars[pb++] = 0;
        bars[pb++] = 0;
        return bars;
    }

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     *
     * @return the size the barcode occupies.
     */
    @Override
    public Rectangle getBarcodeSize() {
        float fontX = 0;
        float fontY = 0;
        if (font != null) {
            if (baseline > 0) {
                fontY = baseline - getDescender();
            } else {
                fontY = -baseline + size;
            }
            String fullCode = code;
            if (generateChecksum && checksumText) {
                fullCode += getChecksum(fullCode);
            }
            fontX = font.getWidth(altText != null ? altText : fullCode, size);
        }
        String fullCode = keepNumbers(code);
        int len = fullCode.length();
        if (generateChecksum) {
            ++len;
        }
        float fullWidth = len * (3 * x + 2 * x * n) + (6 + n) * x;
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
    @Override
    public Rectangle placeBarcode(PdfCanvas canvas, Color barColor, Color textColor) {
        String fullCode = code;
        float fontX = 0;
        if (font != null) {
            if (generateChecksum && checksumText)
                fullCode += getChecksum(fullCode);
            fontX = font.getWidth(fullCode = altText != null ? altText : fullCode, size);
        }
        String bCode = keepNumbers(code);
        if (generateChecksum)
            bCode += getChecksum(bCode);
        int len = bCode.length();
        float fullWidth = len * (3 * x + 2 * x * n) + (6 + n) * x;
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
        byte[] bars = getBarsInter25(bCode);
        boolean print = true;
        if (barColor != null)
            canvas.setFillColor(barColor);
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
    @Override
    public java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background) {
        int f = (foreground == null) ? DEFAULT_BAR_FOREGROUND_COLOR.getRGB() : foreground.getRGB();
        int g = (background == null) ? DEFAULT_BAR_BACKGROUND_COLOR.getRGB() : background.getRGB();
        java.awt.Canvas canvas = new java.awt.Canvas();
        String bCode = keepNumbers(code);
        if (generateChecksum) {
            bCode += getChecksum(bCode);
        }
        int len = bCode.length();
        int nn = (int) n;
        int fullWidth = len * (3 + 2 * nn) + (6 + nn);
        byte[] bars = getBarsInter25(bCode);
        boolean print = true;
        int ptr = 0;
        int height = (int) barHeight;
        int[] pix = new int[fullWidth * height];
        for (int k = 0; k < bars.length; ++k) {
            int w = (bars[k] == 0 ? 1 : nn);
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

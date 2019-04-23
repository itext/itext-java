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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;

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
                    {0, 0, 0, 0, 0, 1, 1}, // 0
                    {0, 0, 0, 0, 1, 1, 0}, // 1
                    {0, 0, 0, 1, 0, 0, 1}, // 2
                    {1, 1, 0, 0, 0, 0, 0}, // 3
                    {0, 0, 1, 0, 0, 1, 0}, // 4
                    {1, 0, 0, 0, 0, 1, 0}, // 5
                    {0, 1, 0, 0, 0, 0, 1}, // 6
                    {0, 1, 0, 0, 1, 0, 0}, // 7
                    {0, 1, 1, 0, 0, 0, 0}, // 8
                    {1, 0, 0, 1, 0, 0, 0}, // 9
                    {0, 0, 0, 1, 1, 0, 0}, // -
                    {0, 0, 1, 1, 0, 0, 0}, // $
                    {1, 0, 0, 0, 1, 0, 1}, // :
                    {1, 0, 1, 0, 0, 0, 1}, // /
                    {1, 0, 1, 0, 1, 0, 0}, // .
                    {0, 0, 1, 0, 1, 0, 1}, // +
                    {0, 0, 1, 1, 0, 1, 0}, // a
                    {0, 1, 0, 1, 0, 0, 1}, // b
                    {0, 0, 0, 1, 0, 1, 1}, // c
                    {0, 0, 0, 1, 1, 1, 0}  // d
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
            throw new IllegalArgumentException(PdfException.CodabarMustHaveAtLeastStartAndStopCharacter);
        }
        if (CHARS.indexOf(text.charAt(0)) < START_STOP_IDX || CHARS.indexOf(text.charAt(len - 1)) < START_STOP_IDX) {
            throw new IllegalArgumentException(PdfException.CodabarMustHaveOneAbcdAsStartStopCharacter);
        }
        byte[] bars = new byte[text.length() * 8 - 1];
        for (int k = 0; k < len; ++k) {
            int idx = CHARS.indexOf(text.charAt(k));
            if (idx >= START_STOP_IDX && k > 0 && k < len - 1) {
                throw new IllegalArgumentException(PdfException.InCodabarStartStopCharactersAreOnlyAllowedAtTheExtremes);
            }
            if (idx < 0) {
                throw new IllegalArgumentException(PdfException.IllegalCharacterInCodabarBarcode);
            }
            System.arraycopy(BARS[idx], 0, bars, k * 8, 7);
        }
        return bars;
    }

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
     * <TABLE BORDER=1 SUMMARY="barcode properties">
     * <TR>
     * <TH><P><CODE>barColor</CODE></TH>
     * <TH><P><CODE>textColor</CODE></TH>
     * <TH><P>Result</TH>
     * </TR>
     * <TR>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P>bars and text painted with current fill color</TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>barColor</CODE></TD>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P>bars and text painted with <CODE>barColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P><CODE>textColor</CODE></TD>
     * <TD><P>bars painted with current color<br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>barColor</CODE></TD>
     * <TD><P><CODE>textColor</CODE></TD>
     * <TD><P>bars painted with <CODE>barColor</CODE><br>text painted with <CODE>textColor</CODE></TD>
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

    // AWT related methods (remove this if you port to Android / GAE)

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
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.awt.Image;

/**
 * Implements the MSI Barcode.
 * The <CODE>code</CODE> may only contain numeric characters.
 * The {@link #getChecksum(String) getChecksum} method returns the mod 10 checksum digit which is the most widely used for MSI barcodes.
 */
public class BarcodeMSI extends Barcode1D {
    /**
     * The index chars to <CODE>BARS</CODE> representing valid characters in the <CODE>code</CODE>
     */
    private static final String CHARS = "0123456789";

    /**
     * The sequence prepended to the start of all MSI Barcodes.
     */
    private static final byte[] BARS_START = new byte[]{1, 1, 0};

    /**
     * The sequence appended to the end of all MSI Barcodes.
     */
    private static final byte[] BARS_END = new byte[]{1, 0, 0, 1};

    /**
     * The bars to generate the code.
     */
    private static final byte[][] BARS = new byte[][]{
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0}, // 0
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0}, // 1
            {1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0}, // 2
            {1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0}, // 3
            {1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0}, // 4
            {1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0}, // 5
            {1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0}, // 6
            {1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0}, // 7
            {1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0}, // 8
            {1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0}  // 9
    };

    /**
     * The number of individual bars either drawn or not drawn per character of the <CODE>code</CODE>
     */
    private static final int BARS_PER_CHARACTER = 12;

    /**
     * Creates a new BarcodeMSI.
     * To generate the font the {@link PdfDocument#getDefaultFont()} will be implicitly called.
     * If you want to use this barcode in PDF/A documents, please consider using {@link #BarcodeMSI(PdfDocument, PdfFont)}.
     *
     * @param document The document to which the barcode will be added
     */
    public BarcodeMSI(PdfDocument document) {
        this(document, document.getDefaultFont());
    }

    /**
     * Creates a new BarcodeMSI
     *
     * @param document The document to which the barcode will be added
     * @param font The font to use
     */
    public BarcodeMSI(PdfDocument document, PdfFont font) {
        super(document);
        this.x = 0.8f;
        this.n = 2.0f;
        this.font = font;
        this.size = 8.0f;
        this.baseline = this.size;
        this.barHeight = this.size * 3.0f;
        this.generateChecksum = false;
        this.checksumText = false;
    }

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     *
     * @return the size the barcode occupies.
     */
    @Override
    public Rectangle getBarcodeSize() {
        float fontX = 0.0f;
        float fontY = 0.0f;
        String fCode = this.code;
        if (this.font != null) {
            if (this.baseline > 0.0f) {
                fontY = this.baseline - this.getDescender();
            } else {
                fontY = -this.baseline + this.size;
            }
            String fullCode = this.code;
            fontX = this.font.getWidth(this.altText != null ? this.altText : fullCode, this.size);
        }

        int len = fCode.length() + 2;
        if (this.generateChecksum) {
            ++len;
        }

        float fullWidth = (float) len * (6.0f * this.x + 3.0f * this.x * this.n) + (float) (len - 1) * this.x;
        fullWidth = Math.max(fullWidth, fontX);
        float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }

    /**
     * Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.<p>
     * The bars and text are written in the following colors:<p>
     * <P><TABLE BORDER=1>
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
    @Override
    public Rectangle placeBarcode(PdfCanvas canvas, Color barColor, Color textColor) {
        String fullCode = this.code;
        if (this.checksumText) {
            fullCode = fullCode + Integer.toString(getChecksum(this.code));
        }
        float fontX = 0.0f;
        if (this.font != null) {
            String var10001 = this.altText != null ? this.altText : fullCode;
            fullCode = this.altText != null ? this.altText : fullCode;
            fontX = this.font.getWidth(var10001, this.size);
        }

        String bCode = this.code;
        if (this.generateChecksum) {
            bCode += getChecksum(bCode);
        }
        int idx;
        idx = bCode.length();
        float fullWidth = (float) ((idx + 2) * 11) * this.x + 2.0f * this.x;
        float barStartX = 0.0f;
        float textStartX = 0.0f;
        switch (this.textAlignment) {
            case 1:
                break;
            case 2:
                if (fontX > fullWidth) {
                    barStartX = fontX - fullWidth;
                } else {
                    textStartX = fullWidth - fontX;
                }
                break;
            default:
                if (fontX > fullWidth) {
                    barStartX = (fontX - fullWidth) / 2.0f;
                } else {
                    textStartX = (fullWidth - fontX) / 2.0f;
                }
        }

        float barStartY = 0.0f;
        float textStartY = 0.0f;
        if (this.font != null) {
            if (this.baseline <= 0.0f) {
                textStartY = this.barHeight - this.baseline;
            } else {
                textStartY = -this.getDescender();
                barStartY = textStartY + this.baseline;
            }
        }
        byte[] bars = getBarsMSI(bCode);
        if (barColor != null) {
            canvas.setFillColor(barColor);
        }
        for (int k = 0; k < bars.length; ++k) {
            float w = (float) bars[k] * this.x;
            if (bars[k] == 1)
                canvas.rectangle((double) barStartX, (double) barStartY, (double) (w - this.inkSpreading), (double) this.barHeight);
            barStartX += this.x;
        }
        canvas.fill();
        if (this.font != null) {
            if (textColor != null) {
                canvas.setFillColor(textColor);
            }
            canvas.beginText();
            canvas.setFontAndSize(this.font, this.size);
            canvas.setTextMatrix(textStartX, textStartY);
            canvas.showText(fullCode);
            canvas.endText();
        }
        return this.getBarcodeSize();
    }

    /**
     * Creates a <CODE>java.awt.Image</CODE>. This image only
     * contains the bars without any text.
     *
     * @param foreground the color of the bars
     * @param background the color of the background
     * @return the image
     */
    @Override
    public Image createAwtImage(java.awt.Color foreground, java.awt.Color background) {
        int foregroundColor = (foreground == null) ? DEFAULT_BAR_FOREGROUND_COLOR.getRGB() : foreground.getRGB();
        int backgroundColor = (background == null) ? DEFAULT_BAR_BACKGROUND_COLOR.getRGB() : background.getRGB();
        java.awt.Canvas canvas = new java.awt.Canvas();
        String bCode = this.code;
        if (this.generateChecksum) {
            bCode = bCode + Integer.toString(getChecksum(this.code));
        }

        byte[] bars = getBarsMSI(bCode);
        int fullWidth = bars.length;
        int fullHeight = (int) this.barHeight;
        int[] pix = new int[fullWidth * fullHeight];

        for (int x = 0; x < bars.length; x++) {
            int color = (bars[x] == 1 ? foregroundColor : backgroundColor);
            for (int y = 0; y < fullHeight; y++) {
                int currentPixel = x + (y * fullWidth);
                pix[currentPixel] = color;
            }
        }
        return canvas.createImage(new java.awt.image.MemoryImageSource(fullWidth, fullHeight, pix, 0, fullWidth));
    }

    /**
     * Creates the bars.
     *
     * @param text the text to create the bars.
     * @return the bars
     */
    public static byte[] getBarsMSI(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Valid code required to generate MSI barcode.");
        }
        byte[] bars = new byte[((text.length()) * BARS_PER_CHARACTER) + 7];
        System.arraycopy(BARS_START, 0, bars, 0, 3);
        for (int x = 0; x < text.length(); x++) {
            char ch = text.charAt(x);
            int idx = CHARS.indexOf(ch);
            if (idx < 0) {
                throw new IllegalArgumentException("The character " + text.charAt(x) + " is illegal in MSI bar codes.");
            }
            System.arraycopy(BARS[idx], 0, bars, 3 + x * 12, 12);
        }
        System.arraycopy(BARS_END, 0, bars, bars.length - 4, 4);
        return bars;
    }

    /**
     * Calculates the mod 10 checksum digit using the Luhn algorithm.
     *
     * @param text the barcode data
     * @return the checksum digit
     */
    public static int getChecksum(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Valid code required to generate checksum for MSI barcode");
        }
        int[] digits = new int[text.length()];
        for (int x = 0; x < text.length(); x++) {
            digits[x] = (int)(text.charAt(x) - '0');
            if (digits[x] < 0 || digits[x] > 9) {
                throw new IllegalArgumentException("The character " + text.charAt(x) + " is illegal in MSI bar codes.");
            }
        }
        int sum = 0;
        int length = digits.length;
        for (int i = 0; i < length; i++) {
            int digit = digits[length - i - 1];
            if (i % 2 == 0) {
                digit *= 2;
            }
            sum += digit > 9 ? digit - 9 : digit;
        }
        return (sum * 9) % 10;
    }
}

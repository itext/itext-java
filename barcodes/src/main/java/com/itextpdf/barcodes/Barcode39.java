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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.awt.Image;

public class Barcode39 extends Barcode1D {

    /**
     * The bars to generate the code.
     */
    private static final byte[][] BARS =
            {
                    {0, 0, 0, 1, 1, 0, 1, 0, 0},
                    {1, 0, 0, 1, 0, 0, 0, 0, 1},
                    {0, 0, 1, 1, 0, 0, 0, 0, 1},
                    {1, 0, 1, 1, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0, 0, 0, 1},
                    {1, 0, 0, 1, 1, 0, 0, 0, 0},
                    {0, 0, 1, 1, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 0, 0, 1, 0, 1},
                    {1, 0, 0, 1, 0, 0, 1, 0, 0},
                    {0, 0, 1, 1, 0, 0, 1, 0, 0},
                    {1, 0, 0, 0, 0, 1, 0, 0, 1},
                    {0, 0, 1, 0, 0, 1, 0, 0, 1},
                    {1, 0, 1, 0, 0, 1, 0, 0, 0},
                    {0, 0, 0, 0, 1, 1, 0, 0, 1},
                    {1, 0, 0, 0, 1, 1, 0, 0, 0},
                    {0, 0, 1, 0, 1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 1, 1, 0, 1},
                    {1, 0, 0, 0, 0, 1, 1, 0, 0},
                    {0, 0, 1, 0, 0, 1, 1, 0, 0},
                    {0, 0, 0, 0, 1, 1, 1, 0, 0},
                    {1, 0, 0, 0, 0, 0, 0, 1, 1},
                    {0, 0, 1, 0, 0, 0, 0, 1, 1},
                    {1, 0, 1, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 1, 0, 0, 1, 1},
                    {1, 0, 0, 0, 1, 0, 0, 1, 0},
                    {0, 0, 1, 0, 1, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 1, 1, 0},
                    {0, 0, 1, 0, 0, 0, 1, 1, 0},
                    {0, 0, 0, 0, 1, 0, 1, 1, 0},
                    {1, 1, 0, 0, 0, 0, 0, 0, 1},
                    {0, 1, 1, 0, 0, 0, 0, 0, 1},
                    {1, 1, 1, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 0, 1, 0, 0, 0, 1},
                    {1, 1, 0, 0, 1, 0, 0, 0, 0},
                    {0, 1, 1, 0, 1, 0, 0, 0, 0},
                    {0, 1, 0, 0, 0, 0, 1, 0, 1},
                    {1, 1, 0, 0, 0, 0, 1, 0, 0},
                    {0, 1, 1, 0, 0, 0, 1, 0, 0},
                    {0, 1, 0, 1, 0, 1, 0, 0, 0},
                    {0, 1, 0, 1, 0, 0, 0, 1, 0},
                    {0, 1, 0, 0, 0, 1, 0, 1, 0},
                    {0, 0, 0, 1, 0, 1, 0, 1, 0},
                    {0, 1, 0, 0, 1, 0, 1, 0, 0}
            };

    /**
     * The index chars to <CODE>BARS</CODE>, symbol * use only start and stop  characters,
     * the * character will not appear in the input data.
     */
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%*";



    /**
     * The character combinations to make the code 39 extended.
     */
    private static final String EXTENDED = "%U" +
            "$A$B$C$D$E$F$G$H$I$J$K$L$M$N$O$P$Q$R$S$T$U$V$W$X$Y$Z" +
            "%A%B%C%D%E  /A/B/C/D/E/F/G/H/I/J/K/L - ./O" +
            " 0 1 2 3 4 5 6 7 8 9/Z%F%G%H%I%J%V" +
            " A B C D E F G H I J K L M N O P Q R S T U V W X Y Z" +
            "%K%L%M%N%O%W" +
            "+A+B+C+D+E+F+G+H+I+J+K+L+M+N+O+P+Q+R+S+T+U+V+W+X+Y+Z" +
            "%P%Q%R%S%T";

    /**
     * Creates a new Barcode39.
     * To generate the font the {@link PdfDocument#getDefaultFont()} will be implicitly called.
     * If you want to use this barcode in PDF/A documents, please consider using {@link #Barcode39(PdfDocument, PdfFont)}.
     *
     * @param document The document to which the barcode will be added
     */
    public Barcode39(PdfDocument document) {
        this(document, document.getDefaultFont());
    }

    /**
     * Creates a new Barcode39.
     *
     * @param document The document to which the barcode will be added
     * @param font The font to use
     */
    public Barcode39(PdfDocument document, PdfFont font) {
        super(document);
        this.x = 0.8f;
        this.n = 2;
        this.font = font;
        this.size = 8;
        this.baseline = size;
        this.barHeight = size * 3;
        this.generateChecksum = false;
        this.checksumText = false;
        this.startStopText = true;
        this.extended = false;
    }
    /**
     * Creates the bars.
     *
     * @param text the text to create the bars. This text does not include the start and
     *             stop characters
     * @return the bars
     */
    public static byte[] getBarsCode39(String text) {
        text = "*" + text + "*";
        byte[] bars = new byte[text.length() * 10 - 1];
        for (int k = 0; k < text.length(); ++k) {
            char ch = text.charAt(k);
            int idx = CHARS.indexOf(ch);
            if(ch == '*' && k != 0 && k != (text.length() - 1)){
                throw new IllegalArgumentException("The character " + ch + " is illegal in code 39");
            }
            if (idx < 0 ) {
                throw new IllegalArgumentException("The character " + text.charAt(k) + " is illegal in code 39");
            }
            System.arraycopy(BARS[idx], 0, bars, k * 10, 9);
        }
        return bars;
    }

    /**
     * Converts the extended text into a normal, escaped text,
     * ready to generate bars.
     *
     * @param text the extended text
     * @return the escaped text
     */
    public static String getCode39Ex(String text) {
        StringBuilder out = new StringBuilder("");
        for (int k = 0; k < text.length(); ++k) {
            char c = text.charAt(k);
            if (c > 127) {
                throw new IllegalArgumentException("The character " + c + " is illegal in code 39");
            }
            char c1 = EXTENDED.charAt(c * 2);
            char c2 = EXTENDED.charAt(c * 2 + 1);
            if (c1 != ' ') {
                out.append(c1);
            }
            out.append(c2);
        }
        return out.toString();
    }

    /**
     * Calculates the checksum.
     *
     * @param text the text
     * @return the checksum
     */
    static char getChecksum(String text) {
        int chk = 0;
        for (int k = 0; k < text.length(); ++k) {
            int idx = CHARS.indexOf(text.charAt(k));
            char ch = text.charAt(k);
            if(ch == '*' && k != 0 && k != (text.length() - 1)){
                throw new IllegalArgumentException("The character " + ch + " is illegal in code 39");
            }
            if (idx < 0) {
                throw new IllegalArgumentException("The character " + text.charAt(k) + " is illegal in code 39");
            }
            chk += idx;
        }
        return CHARS.charAt(chk % 43);
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
        String fCode = code;
        if (extended) {
            fCode = getCode39Ex(code);
        }
        if (font != null) {
            if (baseline > 0) {
                fontY = baseline - getDescender();
            } else {
                fontY = -baseline + size;
            }
            String fullCode = code;
            if (generateChecksum && checksumText) {
                fullCode += getChecksum(fCode);
            }
            if (startStopText) {
                fullCode = "*" + fullCode + "*";
            }
            fontX = font.getWidth(altText != null ? altText : fullCode, size);
        }
        int len = fCode.length() + 2;
        if (generateChecksum) {
            ++len;
        }
        float fullWidth = len * (6 * x + 3 * x * n) + (len - 1) * x;
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
        String bCode = code;
        if (extended) {
            bCode = getCode39Ex(code);
        }
        if (font != null) {
            if (generateChecksum && checksumText) {
                fullCode += getChecksum(bCode);
            }
            if (startStopText) {
                fullCode = "*" + fullCode + "*";
            }
            fontX = font.getWidth(fullCode = altText != null ? altText : fullCode, size);
        }
        if (generateChecksum) {
            bCode += getChecksum(bCode);
        }
        int len = bCode.length() + 2;
        float fullWidth = len * (6 * x + 3 * x * n) + (len - 1) * x;
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
        byte[] bars = getBarsCode39(bCode);
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
            canvas.
                    beginText().
                    setFontAndSize(font, size).
                    setTextMatrix(textStartX, textStartY).
                    showText(fullCode).
                    endText();
        }
        return getBarcodeSize();
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
        int f = (foreground == null) ? DEFAULT_BAR_FOREGROUND_COLOR.getRGB() : foreground.getRGB();
        int g = (background == null) ? DEFAULT_BAR_BACKGROUND_COLOR.getRGB() : background.getRGB();
        java.awt.Canvas canvas = new java.awt.Canvas();
        String bCode = code;
        if (extended) {
            bCode = getCode39Ex(code);
        }
        if (generateChecksum) {
            bCode += getChecksum(bCode);
        }
        int len = bCode.length() + 2;
        int nn = (int) n;
        int fullWidth = len * (6 + 3 * nn) + (len - 1);
        byte[] bars = getBarsCode39(bCode);
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
}

package com.itextpdf.barcodes;


import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.awt.Image;


/**
 * Implements the code interleaved 2 of 5. The text can include
 * non numeric characters that are printed but do not generate bars.
 * The default parameters are:
 * <pre>
 * x = 0.8f;
 * n = 2;
 * font = new PdfType1Font(document, new Type1Font(FontConstants.HELVETICA, PdfEncodings.WINANSI));
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
     * Creates new BarcodeInter25
     */
    public BarcodeInter25(PdfDocument document) {
        super(document);
        try {
            x = 0.8f;
            n = 2;
            font = PdfFontFactory.createStandardFont(FontConstants.HELVETICA, PdfEncodings.WINANSI);
            size = 8;
            baseline = size;
            barHeight = size * 3;
            textAlignment = ALIGN_CENTER;
            generateChecksum = false;
            checksumText = false;
        } catch (Exception e) {
            throw new PdfException(e.getLocalizedMessage());
        }
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
            throw new PdfException(PdfException.TextMustBeEven);
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

    // AWT related methods (remove this if you port to Android / GAE)

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
        int f = foreground.getRGB();
        int g = background.getRGB();
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
}

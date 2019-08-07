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
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.util.HashMap;
import java.util.Map;

public class Barcode128 extends Barcode1D {

    /** A type of barcode */
    public static final int CODE128 = 1;
    /** A type of barcode */
    public static final int CODE128_UCC = 2;
    /** A type of barcode */
    public static final int CODE128_RAW = 3;
    /**
     * The bars to generate the code.
     */
    private static final byte[][] BARS = new byte[][]
            {
                    new byte[] {2, 1, 2, 2, 2, 2},
                    new byte[] {2, 2, 2, 1, 2, 2},
                    new byte[] {2, 2, 2, 2, 2, 1},
                    new byte[] {1, 2, 1, 2, 2, 3},
                    new byte[] {1, 2, 1, 3, 2, 2},
                    new byte[] {1, 3, 1, 2, 2, 2},
                    new byte[] {1, 2, 2, 2, 1, 3},
                    new byte[] {1, 2, 2, 3, 1, 2},
                    new byte[] {1, 3, 2, 2, 1, 2},
                    new byte[] {2, 2, 1, 2, 1, 3},
                    new byte[] {2, 2, 1, 3, 1, 2},
                    new byte[] {2, 3, 1, 2, 1, 2},
                    new byte[] {1, 1, 2, 2, 3, 2},
                    new byte[] {1, 2, 2, 1, 3, 2},
                    new byte[] {1, 2, 2, 2, 3, 1},
                    new byte[] {1, 1, 3, 2, 2, 2},
                    new byte[] {1, 2, 3, 1, 2, 2},
                    new byte[] {1, 2, 3, 2, 2, 1},
                    new byte[] {2, 2, 3, 2, 1, 1},
                    new byte[] {2, 2, 1, 1, 3, 2},
                    new byte[] {2, 2, 1, 2, 3, 1},
                    new byte[] {2, 1, 3, 2, 1, 2},
                    new byte[] {2, 2, 3, 1, 1, 2},
                    new byte[] {3, 1, 2, 1, 3, 1},
                    new byte[] {3, 1, 1, 2, 2, 2},
                    new byte[] {3, 2, 1, 1, 2, 2},
                    new byte[] {3, 2, 1, 2, 2, 1},
                    new byte[] {3, 1, 2, 2, 1, 2},
                    new byte[] {3, 2, 2, 1, 1, 2},
                    new byte[] {3, 2, 2, 2, 1, 1},
                    new byte[] {2, 1, 2, 1, 2, 3},
                    new byte[] {2, 1, 2, 3, 2, 1},
                    new byte[] {2, 3, 2, 1, 2, 1},
                    new byte[] {1, 1, 1, 3, 2, 3},
                    new byte[] {1, 3, 1, 1, 2, 3},
                    new byte[] {1, 3, 1, 3, 2, 1},
                    new byte[] {1, 1, 2, 3, 1, 3},
                    new byte[] {1, 3, 2, 1, 1, 3},
                    new byte[] {1, 3, 2, 3, 1, 1},
                    new byte[] {2, 1, 1, 3, 1, 3},
                    new byte[] {2, 3, 1, 1, 1, 3},
                    new byte[] {2, 3, 1, 3, 1, 1},
                    new byte[] {1, 1, 2, 1, 3, 3},
                    new byte[] {1, 1, 2, 3, 3, 1},
                    new byte[] {1, 3, 2, 1, 3, 1},
                    new byte[] {1, 1, 3, 1, 2, 3},
                    new byte[] {1, 1, 3, 3, 2, 1},
                    new byte[] {1, 3, 3, 1, 2, 1},
                    new byte[] {3, 1, 3, 1, 2, 1},
                    new byte[] {2, 1, 1, 3, 3, 1},
                    new byte[] {2, 3, 1, 1, 3, 1},
                    new byte[] {2, 1, 3, 1, 1, 3},
                    new byte[] {2, 1, 3, 3, 1, 1},
                    new byte[] {2, 1, 3, 1, 3, 1},
                    new byte[] {3, 1, 1, 1, 2, 3},
                    new byte[] {3, 1, 1, 3, 2, 1},
                    new byte[] {3, 3, 1, 1, 2, 1},
                    new byte[] {3, 1, 2, 1, 1, 3},
                    new byte[] {3, 1, 2, 3, 1, 1},
                    new byte[] {3, 3, 2, 1, 1, 1},
                    new byte[] {3, 1, 4, 1, 1, 1},
                    new byte[] {2, 2, 1, 4, 1, 1},
                    new byte[] {4, 3, 1, 1, 1, 1},
                    new byte[] {1, 1, 1, 2, 2, 4},
                    new byte[] {1, 1, 1, 4, 2, 2},
                    new byte[] {1, 2, 1, 1, 2, 4},
                    new byte[] {1, 2, 1, 4, 2, 1},
                    new byte[] {1, 4, 1, 1, 2, 2},
                    new byte[] {1, 4, 1, 2, 2, 1},
                    new byte[] {1, 1, 2, 2, 1, 4},
                    new byte[] {1, 1, 2, 4, 1, 2},
                    new byte[] {1, 2, 2, 1, 1, 4},
                    new byte[] {1, 2, 2, 4, 1, 1},
                    new byte[] {1, 4, 2, 1, 1, 2},
                    new byte[] {1, 4, 2, 2, 1, 1},
                    new byte[] {2, 4, 1, 2, 1, 1},
                    new byte[] {2, 2, 1, 1, 1, 4},
                    new byte[] {4, 1, 3, 1, 1, 1},
                    new byte[] {2, 4, 1, 1, 1, 2},
                    new byte[] {1, 3, 4, 1, 1, 1},
                    new byte[] {1, 1, 1, 2, 4, 2},
                    new byte[] {1, 2, 1, 1, 4, 2},
                    new byte[] {1, 2, 1, 2, 4, 1},
                    new byte[] {1, 1, 4, 2, 1, 2},
                    new byte[] {1, 2, 4, 1, 1, 2},
                    new byte[] {1, 2, 4, 2, 1, 1},
                    new byte[] {4, 1, 1, 2, 1, 2},
                    new byte[] {4, 2, 1, 1, 1, 2},
                    new byte[] {4, 2, 1, 2, 1, 1},
                    new byte[] {2, 1, 2, 1, 4, 1},
                    new byte[] {2, 1, 4, 1, 2, 1},
                    new byte[] {4, 1, 2, 1, 2, 1},
                    new byte[] {1, 1, 1, 1, 4, 3},
                    new byte[] {1, 1, 1, 3, 4, 1},
                    new byte[] {1, 3, 1, 1, 4, 1},
                    new byte[] {1, 1, 4, 1, 1, 3},
                    new byte[] {1, 1, 4, 3, 1, 1},
                    new byte[] {4, 1, 1, 1, 1, 3},
                    new byte[] {4, 1, 1, 3, 1, 1},
                    new byte[] {1, 1, 3, 1, 4, 1},
                    new byte[] {1, 1, 4, 1, 3, 1},
                    new byte[] {3, 1, 1, 1, 4, 1},
                    new byte[] {4, 1, 1, 1, 3, 1},
                    new byte[] {2, 1, 1, 4, 1, 2},
                    new byte[] {2, 1, 1, 2, 1, 4},
                    new byte[] {2, 1, 1, 2, 3, 2}
            };

    /**
     * The stop bars.
     */
    private static final byte[] BARS_STOP = new byte[] {2, 3, 3, 1, 1, 1, 2};
    /**
     * The charset code change.
     */
    public static final char CODE_AB_TO_C = (char) 99;
    /**
     * The charset code change.
     */
    public static final char CODE_AC_TO_B = (char) 100;
    /**
     * The charset code change.
     */
    public static final char CODE_BC_TO_A = (char) 101;
    /**
     * The code for UCC/EAN-128.
     */
    public static final char FNC1_INDEX = (char) 102;
    /**
     * The start code.
     */
    public static final char START_A = (char) 103;
    /**
     * The start code.
     */
    public static final char START_B = (char) 104;
    /**
     * The start code.
     */
    public static final char START_C = (char) 105;

    public static final char FNC1 = '\u00ca';
    public static final char DEL = '\u00c3';
    public static final char FNC3 = '\u00c4';
    public static final char FNC2 = '\u00c5';
    public static final char SHIFT = '\u00c6';
    public static final char CODE_C = '\u00c7';
    public static final char CODE_A = '\u00c8';
    public static final char FNC4 = '\u00c8';
    public static final char STARTA = '\u00cb';
    public static final char STARTB = '\u00cc';
    public static final char STARTC = '\u00cd';

    private static Map<Integer, Integer> ais = new HashMap<>();

    /**
     * Creates new Barcode128.
     * To generate the font the {@link PdfDocument#getDefaultFont()} will be implicitly called.
     * If you want to use this barcode in PDF/A documents, please consider using {@link #Barcode128(PdfDocument, PdfFont)}.
     *
     * @param document The document to which the barcode will be added
     */
    public Barcode128(PdfDocument document) {
        this(document, document.getDefaultFont());
    }

    /**
     * Creates new Barcode128, which will use the provided font
     *
     * @param document The document to which the barcode will be added
     * @param font The font to use
     */
    public Barcode128(PdfDocument document, PdfFont font) {
        super(document);
        this.x = 0.8f;
        this.font = font;
        this.size = 8;
        this.baseline = size;
        this.barHeight = size * 3;
        this.textAlignment = ALIGN_CENTER;
        this.codeType = CODE128;
    }
    public enum Barcode128CodeSet {
        A,
        B,
        C,
        AUTO
    }

    public void setCodeSet(Barcode128CodeSet codeSet) {
        this.codeSet = codeSet;
    }

    public Barcode128CodeSet getCodeSet() {
        return this.codeSet;
    }

    private Barcode128CodeSet codeSet = Barcode128CodeSet.AUTO;

    /**
     * Removes the FNC1 codes in the text.
     * @param code  The text to clean
     * @return      The cleaned text
     */
    public static String removeFNC1(String code) {
        int len = code.length();
        StringBuilder buf = new StringBuilder(len);
        for (int k = 0; k < len; ++k) {
            char c = code.charAt(k);
            if (c >= 32 && c <= 126)
                buf.append(c);
        }
        return buf.toString();
    }

    /**
     * Gets the human readable text of a sequence of AI.
     *
     * @param code the text
     * @return the human readable text
     */
    public static String getHumanReadableUCCEAN(String code) {
        StringBuilder buf = new StringBuilder();
        String fnc1 = new String(new char[]{FNC1});
        while (true) {
            if (code.startsWith(fnc1)) {
                code = code.substring(1);
                continue;
            }
            int n = 0;
            int idlen = 0;
            for (int k = 2; k < 5; ++k) {
                if (code.length() < k)
                    break;
                int subcode = Integer.parseInt(code.substring(0, k));
                n = ais.containsKey(subcode) ? (int)ais.get(subcode) : 0;
                if (n != 0) {
                    idlen = k;
                    break;
                }
            }
            if (idlen == 0)
                break;
            buf.append('(').append(code.substring(0, idlen)).append(')');
            code = code.substring(idlen);
            if (n > 0) {
                n -= idlen;
                if (code.length() <= n)
                    break;
                buf.append(removeFNC1(code.substring(0, n)));
                code = code.substring(n);
            } else {
                int idx = code.indexOf(FNC1);
                if (idx < 0)
                    break;
                buf.append(code.substring(0, idx));
                code = code.substring(idx + 1);
            }
        }
        buf.append(removeFNC1(code));
        return buf.toString();
    }

    /**
     * Converts the human readable text to the characters needed to
     * create a barcode using the specified code set.
     *
     * @param text    the text to convert
     * @param ucc     <CODE>true</CODE> if it is an UCC/EAN-128. In this case
     *                the character FNC1 is added
     * @param codeSet forced code set, or AUTO for optimized barcode.
     * @return the code ready to be fed to getBarsCode128Raw()
     */
    public static String getRawText(String text, boolean ucc, Barcode128CodeSet codeSet) {
        String out = "";
        int tLen = text.length();
        if (tLen == 0) {
            out += getStartSymbol(codeSet);
            if (ucc)
                out += FNC1_INDEX;
            return out;
        }
        int c;
        for (int k = 0; k < tLen; ++k) {
            c = text.charAt(k);
            if (c > 127 && c != FNC1)
                throw new PdfException(PdfException.ThereAreIllegalCharactersForBarcode128In1);
        }
        c = text.charAt(0);
        char currentCode = getStartSymbol(codeSet);
        int index = 0;
        if ((codeSet == Barcode128CodeSet.AUTO || codeSet == Barcode128CodeSet.C) && isNextDigits(text, index, 2)) {
            currentCode = START_C;
            out += currentCode;
            if (ucc)
                out += FNC1_INDEX;
            String out2 = getPackedRawDigits(text, index, 2);
            index += out2.charAt(0);
            out += out2.substring(1);
        } else if (c < ' ') {
            currentCode = START_A;
            out += currentCode;
            if (ucc)
                out += FNC1_INDEX;
            out += (char) (c + 64);
            ++index;
        } else {
            out += currentCode;
            if (ucc)
                out += FNC1_INDEX;
            if (c == FNC1)
                out += FNC1_INDEX;
            else
                out += (char) (c - ' ');
            ++index;
        }
        if (codeSet != Barcode128CodeSet.AUTO && currentCode != getStartSymbol(codeSet))
            throw new PdfException(PdfException.ThereAreIllegalCharactersForBarcode128In1);
        while (index < tLen) {
            switch (currentCode) {
                case START_A: {
                    if (codeSet == Barcode128CodeSet.AUTO && isNextDigits(text, index, 4)) {
                        currentCode = START_C;
                        out += CODE_AB_TO_C;
                        String out2 = getPackedRawDigits(text, index, 4);
                        index += out2.charAt(0);
                        out += out2.substring(1);
                    } else {
                        c = text.charAt(index++);
                        if (c == FNC1)
                            out += FNC1_INDEX;
                        else if (c > '_') {
                            currentCode = START_B;
                            out += CODE_AC_TO_B;
                            out += (char) (c - ' ');
                        } else if (c < ' ')
                            out += (char) (c + 64);
                        else
                            out += (char) (c - ' ');
                    }
                }
                break;
                case START_B: {
                    if (codeSet == Barcode128CodeSet.AUTO && isNextDigits(text, index, 4)) {
                        currentCode = START_C;
                        out += CODE_AB_TO_C;
                        String out2 = getPackedRawDigits(text, index, 4);
                        index += out2.charAt(0);
                        out += out2.substring(1);
                    } else {
                        c = text.charAt(index++);
                        if (c == FNC1)
                            out += FNC1_INDEX;
                        else if (c < ' ') {
                            currentCode = START_A;
                            out += CODE_BC_TO_A;
                            out += (char) (c + 64);
                        } else {
                            out += (char) (c - ' ');
                        }
                    }
                }
                break;
                case START_C: {
                    if (isNextDigits(text, index, 2)) {
                        String out2 = getPackedRawDigits(text, index, 2);
                        index += out2.charAt(0);
                        out += out2.substring(1);
                    } else {
                        c = text.charAt(index++);
                        if (c == FNC1)
                            out += FNC1_INDEX;
                        else if (c < ' ') {
                            currentCode = START_A;
                            out += CODE_BC_TO_A;
                            out += (char) (c + 64);
                        } else {
                            currentCode = START_B;
                            out += CODE_AC_TO_B;
                            out += (char) (c - ' ');
                        }
                    }
                }
                break;
            }
            if (codeSet != Barcode128CodeSet.AUTO && currentCode != getStartSymbol(codeSet))
                throw new PdfException(PdfException.ThereAreIllegalCharactersForBarcode128In1);
        }
        return out;
    }

    /**
     * Converts the human readable text to the characters needed to
     * create a barcode. Some optimization is done to get the shortest code.
     *
     * @param text the text to convert
     * @param ucc  <CODE>true</CODE> if it is an UCC/EAN-128. In this case
     *             the character FNC1 is added
     * @return the code ready to be fed to getBarsCode128Raw()
     */
    public static String getRawText(String text, boolean ucc) {
        return getRawText(text, ucc, Barcode128CodeSet.AUTO);
    }

    /**
     * Generates the bars. The input has the actual barcodes, not
     * the human readable text.
     *
     * @param text the barcode
     * @return the bars
     */
    public static byte[] getBarsCode128Raw(String text) {
        int idx = text.indexOf('\uffff');
        if (idx >= 0)
            text = text.substring(0, idx);
        int chk = text.charAt(0);
        for (int k = 1; k < text.length(); ++k)
            chk += k * text.charAt(k);
        chk = chk % 103;
        text += (char) chk;
        byte[] bars = new byte[(text.length() + 1) * 6 + 7];
        int k;
        for (k = 0; k < text.length(); ++k)
            System.arraycopy(BARS[text.charAt(k)], 0, bars, k * 6, 6);
        System.arraycopy(BARS_STOP, 0, bars, k * 6, 7);
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
        String fullCode;
        if (font != null) {
            if (baseline > 0) {
                fontY = baseline - getDescender();
            } else {
                fontY = -baseline + size;
            }
            if (codeType == CODE128_RAW) {
                int idx = code.indexOf('\uffff');
                if (idx < 0) {
                    fullCode = "";
                } else {
                    fullCode = code.substring(idx + 1);
                }
            } else if (codeType == CODE128_UCC) {
                fullCode = getHumanReadableUCCEAN(code);
            } else {
                fullCode = removeFNC1(code);
            }
            fontX = font.getWidth(altText != null ? altText : fullCode, size);
        }
        if (codeType == CODE128_RAW) {
            int idx = code.indexOf('\uffff');
            if (idx >= 0)
                fullCode = code.substring(0, idx);
            else
                fullCode = code;
        } else {
            fullCode = getRawText(code, codeType == CODE128_UCC, codeSet);
        }
        int len = fullCode.length();
        float fullWidth = (len + 2) * 11 * x + 2 * x;
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
        String fullCode;
        if (codeType == CODE128_RAW) {
            int idx = code.indexOf('\uffff');
            if (idx < 0) {
                fullCode = "";
            } else {
                fullCode = code.substring(idx + 1);
            }
        } else if (codeType == CODE128_UCC) {
            fullCode = getHumanReadableUCCEAN(code);
        } else {
            fullCode = removeFNC1(code);
        }
        float fontX = 0;
        if (font != null) {
            fontX = font.getWidth(fullCode = altText != null ? altText : fullCode, size);
        }
        String bCode;
        if (codeType == CODE128_RAW) {
            int idx = code.indexOf('\uffff');
            if (idx >= 0)
                bCode = code.substring(0, idx);
            else
                bCode = code;
        } else {
            bCode = getRawText(code, codeType == CODE128_UCC, codeSet);
        }
        int len = bCode.length();
        float fullWidth = (len + 2) * 11 * x + 2 * x;
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
            if (baseline <= 0)
                textStartY = barHeight - baseline;
            else {
                textStartY = -getDescender();
                barStartY = textStartY + baseline;
            }
        }
        byte[] bars = getBarsCode128Raw(bCode);
        boolean print = true;
        if (barColor != null) {
            canvas.setFillColor(barColor);
        }
        for (int k = 0; k < bars.length; ++k) {
            float w = bars[k] * x;
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

    /**
     * Sets the code to generate. If it's an UCC code and starts with '(' it will
     * be split by the AI. This code in UCC mode is valid:
     * <br>
     * <code>(01)00000090311314(10)ABC123(15)060916</code>
     *
     * @param code the code to generate
     */
    @Override
    public void setCode(String code) {
        if (getCodeType() == Barcode128.CODE128_UCC && code.startsWith("(")) {
            int idx = 0;
            StringBuilder ret = new StringBuilder("");
            while (idx >= 0) {
                int end = code.indexOf(')', idx);
                if (end < 0) {
                    throw new IllegalArgumentException("Badly formed ucc string");
                }
                String sai = code.substring(idx + 1, end);
                if (sai.length() < 2) {
                    throw new IllegalArgumentException("AI is too short");
                }
                int ai = Integer.parseInt(sai);
                int len = (int)ais.get(ai);
                if (len == 0) {
                    throw new IllegalArgumentException("AI not found");
                }
                sai = Integer.valueOf(ai).toString();
                if (sai.length() == 1) {
                    sai = "0" + sai;
                }
                idx = code.indexOf('(', end);
                int next = (idx < 0 ? code.length() : idx);
                ret.append(sai).append(code.substring(end + 1, next));
                if (len < 0) {
                    if (idx >= 0) {
                        ret.append(FNC1);
                    }
                } else if (next - end - 1 + sai.length() != len) {
                    throw new IllegalArgumentException("Invalid AI length");
                }
            }
            super.setCode(ret.toString());
        } else {
            super.setCode(code);
        }
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
    public java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background) {
        int f = (foreground == null) ? DEFAULT_BAR_FOREGROUND_COLOR.getRGB() : foreground.getRGB();
        int g = (background == null) ? DEFAULT_BAR_BACKGROUND_COLOR.getRGB() : background.getRGB();
        java.awt.Canvas canvas = new java.awt.Canvas();
        String bCode;
        if (codeType == CODE128_RAW) {
            int idx = code.indexOf('\uffff');
            if (idx >= 0) {
                bCode = code.substring(0, idx);
            } else {
                bCode = code;
            }
        } else {
            bCode = getRawText(code, codeType == CODE128_UCC);
        }
        int len = bCode.length();
        int fullWidth = (len + 2) * 11 + 2;
        byte[] bars = getBarsCode128Raw(bCode);

        boolean print = true;
        int ptr = 0;
        int height = (int) barHeight;
        int[] pix = new int[fullWidth * height];
        for (int k = 0; k < bars.length; ++k) {
            int w = bars[k];
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

    private static char getStartSymbol(Barcode128CodeSet codeSet) {
        switch (codeSet) {
            case A:
                return START_A;
            case B:
                return START_B;
            case C:
                return START_C;
            default:
                return START_B;
        }
    }

    static {
        ais.put(0, 20);
        ais.put(1, 16);
        ais.put(2, 16);
        ais.put(10, -1);
        ais.put(11, 9);
        ais.put(12, 8);
        ais.put(13, 8);
        ais.put(15, 8);
        ais.put(17, 8);
        ais.put(20, 4);
        ais.put(21, -1);
        ais.put(22, -1);
        ais.put(23, -1);
        ais.put(240, -1);
        ais.put(241, -1);
        ais.put(250, -1);
        ais.put(251, -1);
        ais.put(252, -1);
        ais.put(30, -1);
        for (int k = 3100; k < 3700; ++k) {
            ais.put(k, 10);
        }
        ais.put(37, -1);
        for (int k = 3900; k < 3940; ++k) {
            ais.put(k, -1);
        }
        ais.put(400, -1);
        ais.put(401, -1);
        ais.put(402, 20);
        ais.put(403, -1);
        for (int k = 410; k < 416; ++k) {
            ais.put(k, 16);
        }
        ais.put(420, -1);
        ais.put(421, -1);
        ais.put(422, 6);
        ais.put(423, -1);
        ais.put(424, 6);
        ais.put(425, 6);
        ais.put(426, 6);
        ais.put(7001, 17);
        ais.put(7002, -1);
        for (int k = 7030; k < 7040; ++k) {
            ais.put(k, -1);
        }
        ais.put(8001, 18);
        ais.put(8002, -1);
        ais.put(8003, -1);
        ais.put(8004, -1);
        ais.put(8005, 10);
        ais.put(8006, 22);
        ais.put(8007, -1);
        ais.put(8008, -1);
        ais.put(8018, 22);
        ais.put(8020, -1);
        ais.put(8100, 10);
        ais.put(8101, 14);
        ais.put(8102, 6);
        for (int k = 90; k < 100; ++k) {
            ais.put(k, -1);
        }
    }

    /**
     * Returns <CODE>true</CODE> if the next <CODE>numDigits</CODE>
     * starting from index <CODE>textIndex</CODE> are numeric skipping any FNC1.
     *
     * @param text      the text to check
     * @param textIndex where to check from
     * @param numDigits the number of digits to check
     * @return the check result
     */
    static boolean isNextDigits(String text, int textIndex, int numDigits) {
        int len = text.length();
        while (textIndex < len && numDigits > 0) {
            if (text.charAt(textIndex) == FNC1) {
                ++textIndex;
                continue;
            }
            int n = Math.min(2, numDigits);
            if (textIndex + n > len) {
                return false;
            }
            while (n-- > 0) {
                char c = text.charAt(textIndex++);
                if (c < '0' || c > '9') {
                    return false;
                }
                --numDigits;
            }
        }
        return numDigits == 0;
    }

    /**
     * Packs the digits for charset C also considering FNC1. It assumes that all the parameters
     * are valid.
     *
     * @param text      the text to pack
     * @param textIndex where to pack from
     * @param numDigits the number of digits to pack. It is always an even number
     * @return the packed digits, two digits per character
     */
    static String getPackedRawDigits(String text, int textIndex, int numDigits) {
        StringBuilder out = new StringBuilder("");
        int start = textIndex;
        while (numDigits > 0) {
            if (text.charAt(textIndex) == FNC1) {
                out.append(FNC1_INDEX);
                ++textIndex;
                continue;
            }
            numDigits -= 2;
            int c1 = text.charAt(textIndex++) - '0';
            int c2 = text.charAt(textIndex++) - '0';
            out.append((char) (c1 * 10 + c2));
        }
        return (char) (textIndex - start) + out.toString();
    }
}

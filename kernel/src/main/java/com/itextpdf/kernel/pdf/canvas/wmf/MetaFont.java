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
package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.FontStyles;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A Meta Font.
 */
public class MetaFont extends MetaObject {
    static final String[] fontNames = {
        "Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique",
        "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique",
        "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic",
        "Symbol", "ZapfDingbats"};

    static final int MARKER_BOLD = 1;
    static final int MARKER_ITALIC = 2;
    static final int MARKER_COURIER = 0;
    static final int MARKER_HELVETICA = 4;
    static final int MARKER_TIMES = 8;
    static final int MARKER_SYMBOL = 12;

    static final int DEFAULT_PITCH = 0;
    static final int FIXED_PITCH = 1;
    static final int VARIABLE_PITCH = 2;
    static final int FF_DONTCARE = 0;
    static final int FF_ROMAN = 1;
    static final int FF_SWISS = 2;
    static final int FF_MODERN = 3;
    static final int FF_SCRIPT = 4;
    static final int FF_DECORATIVE = 5;
    static final int BOLDTHRESHOLD = 600;    
    static final int NAME_SIZE = 32;
    static final int ETO_OPAQUE = 2;
    static final int ETO_CLIPPED = 4;

    int height;
    float angle;
    int bold;
    int italic;
    boolean underline;
    boolean strikeout;
    int charset;
    int pitchAndFamily;
    String faceName = "arial";
    FontProgram font = null;
    FontEncoding encoding = null;

    /**
     * Creates a MetaFont instance.
     */
    public MetaFont() {
        super(META_FONT);
    }

    /**
     * Initializes the MetaFont instance.
     *
     * @param in InputMeta containing the WMF data
     * @throws IOException
     */
    public void init(InputMeta in) throws IOException {
        height = Math.abs(in.readShort());
        in.skip(2);
        angle = (float)(in.readShort() / 1800.0 * Math.PI);
        in.skip(2);
        bold = (in.readShort() >= BOLDTHRESHOLD ? MARKER_BOLD : 0);
        italic = (in.readByte() != 0 ? MARKER_ITALIC : 0);
        underline = (in.readByte() != 0);
        strikeout = (in.readByte() != 0);
        charset = in.readByte();
        in.skip(3);
        pitchAndFamily = in.readByte();
        byte[] name = new byte[NAME_SIZE];
        int k;
        for (k = 0; k < NAME_SIZE; ++k) {
            int c = in.readByte();
            if (c == 0) {
                break;
            }
            name[k] = (byte)c;
        }
        try {
            faceName = new String(name, 0, k, "Cp1252");
        } catch (UnsupportedEncodingException e) {
            faceName = new String(name, 0, k);
        }
        faceName = faceName.toLowerCase();
    }

    /**
     * Returns the Font.
     *
     * @return the font
     * @throws IOException
     */
    public FontProgram getFont() throws IOException {
        if (font != null)
            return font;
        FontProgram ff2 = FontProgramFactory.createRegisteredFont(faceName, ((italic != 0) ? FontStyles.ITALIC : 0) | ((bold != 0) ? FontStyles.BOLD : 0));
        encoding = FontEncoding.createFontEncoding(PdfEncodings.WINANSI);
        font = ff2;
        if (font != null)
            return font;
        String fontName;
        if (faceName.contains("courier") || faceName.contains("terminal")
            || faceName.contains("fixedsys")) {
            fontName = fontNames[MARKER_COURIER + italic + bold];
        }
        else if (faceName.contains("ms sans serif") || faceName.contains("arial")
            || faceName.contains("system")) {
            fontName = fontNames[MARKER_HELVETICA + italic + bold];
        }
        else if (faceName.contains("arial black")) {
            fontName = fontNames[MARKER_HELVETICA + italic + MARKER_BOLD];
        }
        else if (faceName.contains("times") || faceName.contains("ms serif")
            || faceName.contains("roman")) {
            fontName = fontNames[MARKER_TIMES + italic + bold];
        }
        else if (faceName.contains("symbol")) {
            fontName = fontNames[MARKER_SYMBOL];
        }
        else {
            int pitch = pitchAndFamily & 3;
            int family = (pitchAndFamily >> 4) & 7;
            switch (family) {
                case FF_MODERN:
                    fontName = fontNames[MARKER_COURIER + italic + bold];
                    break;
                case FF_ROMAN:
                    fontName = fontNames[MARKER_TIMES + italic + bold];
                    break;
                case FF_SWISS:
                case FF_SCRIPT:
                case FF_DECORATIVE:
                    fontName = fontNames[MARKER_HELVETICA + italic + bold];
                    break;
                default:
                {
                    switch (pitch) {
                        case FIXED_PITCH:
                            fontName = fontNames[MARKER_COURIER + italic + bold];
                            break;
                        default:
                            fontName = fontNames[MARKER_HELVETICA + italic + bold];
                            break;
                    }
                }
            }
        }
        try {
            font = FontProgramFactory.createFont(fontName);
            encoding = FontEncoding.createFontEncoding(PdfEncodings.WINANSI);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return font;
    }

    /**
     * Returns the encoding used in the MetaFont.
     *
     * @return the font encoding
     */
    public FontEncoding getEncoding() {
        return encoding;
    }

    /**
     * Returns the angle of the MetaFont.
     *
     * @return the angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Returns a boolean value indicating if the font is underlined or not.
     *
     * @return true if the font is underlined
     */
    public boolean isUnderline() {
        return underline;
    }

    /**
     * Returns a boolean value indicating if a font has a strikeout.
     *
     * @return true if the font set strikeout
     */
    public boolean isStrikeout() {
        return strikeout;
    }

    /**
     * Returns the font size.
     *
     * @param state the MetaState
     * @return font size
     */
    public float getFontSize(MetaState state) {
        return Math.abs(state.transformY(height) - state.transformY(0)) * WmfImageHelper.wmfFontCorrection;
    }
}

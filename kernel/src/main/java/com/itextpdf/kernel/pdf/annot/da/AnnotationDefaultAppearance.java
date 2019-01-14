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
package com.itextpdf.kernel.pdf.annot.da;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for setting
 */
public class AnnotationDefaultAppearance {

    private static final Map<StandardAnnotationFont, String> stdAnnotFontNames = new HashMap<>();
    private static final Map<ExtendedAnnotationFont, String> extAnnotFontNames = new HashMap<>();
    static {
        stdAnnotFontNames.put(StandardAnnotationFont.CourierBoldOblique, "/" + StandardFonts.COURIER_BOLDOBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.CourierBold, "/" + StandardFonts.COURIER_BOLD);
        stdAnnotFontNames.put(StandardAnnotationFont.CourierOblique, "/" + StandardFonts.COURIER_OBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.Courier, "/" + StandardFonts.COURIER);
        stdAnnotFontNames.put(StandardAnnotationFont.HelveticaBoldOblique, "/" + StandardFonts.HELVETICA_BOLDOBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.HelveticaBold, "/" + StandardFonts.HELVETICA_BOLD);
        stdAnnotFontNames.put(StandardAnnotationFont.HelveticaOblique, "/" + StandardFonts.COURIER_OBLIQUE);
        stdAnnotFontNames.put(StandardAnnotationFont.Helvetica, "/" + StandardFonts.HELVETICA);
        stdAnnotFontNames.put(StandardAnnotationFont.Symbol, "/" + StandardFonts.SYMBOL);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesBoldItalic, "/" + StandardFonts.TIMES_BOLDITALIC);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesBold, "/" + StandardFonts.TIMES_BOLD);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesItalic, "/" + StandardFonts.TIMES_ITALIC);
        stdAnnotFontNames.put(StandardAnnotationFont.TimesRoman, "/" + StandardFonts.TIMES_ROMAN);
        stdAnnotFontNames.put(StandardAnnotationFont.ZapfDingbats, "/" + StandardFonts.ZAPFDINGBATS);

        extAnnotFontNames.put(ExtendedAnnotationFont.HYSMyeongJoMedium, "/HySm");
        extAnnotFontNames.put(ExtendedAnnotationFont.HYGoThicMedium, "/HyGo");
        extAnnotFontNames.put(ExtendedAnnotationFont.HeiseiKakuGoW5, "/KaGo");
        extAnnotFontNames.put(ExtendedAnnotationFont.HeiseiMinW3, "/KaMi");
        extAnnotFontNames.put(ExtendedAnnotationFont.MHeiMedium, "/MHei");
        extAnnotFontNames.put(ExtendedAnnotationFont.MSungLight, "/MSun");
        extAnnotFontNames.put(ExtendedAnnotationFont.STSongLight, "/STSo");
        extAnnotFontNames.put(ExtendedAnnotationFont.MSungStdLight, "/MSun");
        extAnnotFontNames.put(ExtendedAnnotationFont.STSongStdLight, "/STSo");
        extAnnotFontNames.put(ExtendedAnnotationFont.HYSMyeongJoStdMedium, "/HySm");
        extAnnotFontNames.put(ExtendedAnnotationFont.KozMinProRegular, "/KaMi");
    }

    private String colorOperand = "0 g";
    private String rawFontName = "/Helv";
    private float fontSize = 0;

    public AnnotationDefaultAppearance() {
        setFont(StandardAnnotationFont.Helvetica);
        setFontSize(12);
    }

    public AnnotationDefaultAppearance setFont(StandardAnnotationFont font) {
        setRawFontName(stdAnnotFontNames.get(font));
        return this;
    }

    public AnnotationDefaultAppearance setFont(ExtendedAnnotationFont font) {
        setRawFontName(extAnnotFontNames.get(font));
        return this;
    }

    public AnnotationDefaultAppearance setFontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public AnnotationDefaultAppearance setColor(DeviceRgb rgbColor) {
        setColorOperand(rgbColor.getColorValue(), "rg");
        return this;
    }

    public AnnotationDefaultAppearance setColor(DeviceCmyk cmykColor) {
        setColorOperand(cmykColor.getColorValue(), "k");
        return this;
    }

    public AnnotationDefaultAppearance setColor(DeviceGray grayColor) {
        setColorOperand(grayColor.getColorValue(), "g");
        return this;
    }

    public PdfString toPdfString() {
        return new PdfString(MessageFormatUtil.format("{0} {1} Tf {2}", rawFontName, fontSize, colorOperand));
    }

    private void setColorOperand(float[] colorValues, String operand) {
        StringBuilder builder = new StringBuilder();
        for (float value : colorValues) {
            builder.append(MessageFormatUtil.format("{0} ", value));
        }
        builder.append(operand);
        this.colorOperand = builder.toString();
    }

    private void setRawFontName(String rawFontName) {
        if (rawFontName == null) {
            throw new IllegalArgumentException("Passed raw font name can not be null");
        }
        this.rawFontName = rawFontName;
    }
}

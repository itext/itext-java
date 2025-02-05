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
package com.itextpdf.kernel.pdf.annot.da;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for setting annotation default appearance. The class provides setters for
 * font color, font size and font itself.
 *
 * <p>
 * Note that only standard font names that do not require font resources are supported.
 *
 * <p>
 * Note that it is possible to create annotation with custom font name in DA, but this require
 * manual resource modifications (you have to put font in DR of AcroForm and use
 * its resource name in DA) and only Acrobat supports that workflow.
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

    /**
     * Creates the default instance of {@link AnnotationDefaultAppearance}.
     *
     * <p>
     * The default font is {@link StandardAnnotationFont#Helvetica}. The default font size is 12.
     */
    public AnnotationDefaultAppearance() {
        setFont(StandardAnnotationFont.Helvetica);
        setFontSize(12);
    }

    /**
     * Sets the {@link AnnotationDefaultAppearance}'s default font.
     *
     * @param font one of {@link StandardAnnotationFont standard annotation fonts} to be set as
     *             the default one for this {@link AnnotationDefaultAppearance}
     * @return this {@link AnnotationDefaultAppearance}
     */
    public AnnotationDefaultAppearance setFont(StandardAnnotationFont font) {
        setRawFontName(stdAnnotFontNames.get(font));
        return this;
    }

    /**
     * Sets the {@link AnnotationDefaultAppearance}'s default font.
     *
     * @param font one of {@link ExtendedAnnotationFont extended annotation fonts} to be set as
     *             the default one for this {@link AnnotationDefaultAppearance}
     * @return this {@link AnnotationDefaultAppearance}
     */
    public AnnotationDefaultAppearance setFont(ExtendedAnnotationFont font) {
        setRawFontName(extAnnotFontNames.get(font));
        return this;
    }

    /**
     * Sets the {@link AnnotationDefaultAppearance}'s default font size.
     *
     * @param fontSize font size to be set as the {@link AnnotationDefaultAppearance}'s default font size
     * @return this {@link AnnotationDefaultAppearance}
     */
    public AnnotationDefaultAppearance setFontSize(float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * Sets the {@link AnnotationDefaultAppearance}'s default font color.
     *
     * @param rgbColor {@link DeviceRgb} to be set as the {@link AnnotationDefaultAppearance}'s
     *                                  default font color
     * @return this {@link AnnotationDefaultAppearance}
     */
    public AnnotationDefaultAppearance setColor(DeviceRgb rgbColor) {
        setColorOperand(rgbColor.getColorValue(), "rg");
        return this;
    }

    /**
     * Sets the {@link AnnotationDefaultAppearance}'s default font color.
     *
     * @param cmykColor {@link DeviceCmyk} to be set as the {@link AnnotationDefaultAppearance}'s
     *                                  default font color
     * @return this {@link AnnotationDefaultAppearance}
     */
    public AnnotationDefaultAppearance setColor(DeviceCmyk cmykColor) {
        setColorOperand(cmykColor.getColorValue(), "k");
        return this;
    }

    /**
     * Sets the {@link AnnotationDefaultAppearance}'s default font color.
     *
     * @param grayColor {@link DeviceGray} to be set as the {@link AnnotationDefaultAppearance}'s
     *                                  default font color
     * @return this {@link AnnotationDefaultAppearance}
     */
    public AnnotationDefaultAppearance setColor(DeviceGray grayColor) {
        setColorOperand(grayColor.getColorValue(), "g");
        return this;
    }

    /**
     * Gets the {@link AnnotationDefaultAppearance}'s representation as {@link PdfString}.
     *
     * @return the {@link PdfString} representation of this {@link AnnotationDefaultAppearance}
     */
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

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;

/**
 * Representation of a CalRgb color space.
 */
public class CalRgb extends Color {


    /**
     * Creates a new CalRgb color using the given {@link PdfCieBasedCs} color space.
     *
     * @param cs Color space
     */
    public CalRgb(PdfCieBasedCs.CalRgb cs) {
        this(cs, new float[cs.getNumberOfComponents()]);
    }

    /**
     * Creates a new CalRgb color using the given {@link PdfCieBasedCs} color space and RGB color values.
     *
     * @param cs    Color space
     * @param value RGB color values
     */
    public CalRgb(PdfCieBasedCs.CalRgb cs, float[] value) {
        super(cs, value);
    }

    /**
     * Creates a new CalRgb color using the given white point and RGB color values.
     *
     * @param whitePoint Color values for defining the white point
     * @param value      RGB color values
     */
    public CalRgb(float[] whitePoint, float[] value) {
        super(new PdfCieBasedCs.CalRgb(whitePoint), value);
    }

    /**
     * Creates a new CalRgb color using the given white point, black point, gamma, matrix and RGB color values.
     *
     * @param whitePoint Color values for defining the white point
     * @param blackPoint Color values for defining the black point
     * @param gamma      Gamma correction
     * @param matrix     Matrix correction
     * @param value      RGB color value
     */
    public CalRgb(float[] whitePoint, float[] blackPoint, float[] gamma, float[] matrix, float[] value) {
        this(new PdfCieBasedCs.CalRgb(whitePoint, blackPoint, gamma, matrix), value);
    }

}

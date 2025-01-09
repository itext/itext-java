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
package com.itextpdf.forms.fields.borders;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.FixedDashedBorder;
import com.itextpdf.layout.borders.SolidBorder;

/**
 *  A factory for creating {@link AbstractFormBorder} implementations.
 */
public final class FormBorderFactory {

    private FormBorderFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@link Border} for specific borderStyle.
     *
     * @param borderStyle     border style dictionary. ISO 32000-1 12.5.4
     * @param borderWidth     width of the border
     * @param borderColor     color of the border
     * @param backgroundColor element background color. This param used for drawing beveled border type
     * @return {@link Border} implementation or {@code null}
     */
    public static Border getBorder(PdfDictionary borderStyle, float borderWidth, Color borderColor,
            Color backgroundColor) {
        if (borderStyle == null || borderStyle.getAsName(PdfName.S) == null
                || borderColor == null || borderWidth <= 0) {
            return null;
        }
        Border resultBorder;
        PdfName borderType = borderStyle.getAsName(PdfName.S);
        if (PdfName.U.equals(borderType)) {
            resultBorder = new UnderlineBorder(borderColor, borderWidth);
        } else if (PdfName.S.equals(borderType)) {
            resultBorder = new SolidBorder(borderColor, borderWidth);
        } else if (PdfName.D.equals(borderType)) {
            PdfArray dashArray = borderStyle.getAsArray(PdfName.D);
            float unitsOn = FixedDashedBorder.DEFAULT_UNITS_VALUE;
            if (dashArray != null && dashArray.size() > 0 && dashArray.getAsNumber(0) != null) {
                unitsOn = dashArray.getAsNumber(0).intValue();
            }
            float unitsOff = unitsOn;
            if (dashArray != null && dashArray.size() > 1 && dashArray.getAsNumber(1) != null) {
                unitsOff = dashArray.getAsNumber(1).intValue();
            }
            resultBorder = new FixedDashedBorder(borderColor, borderWidth, unitsOn, unitsOff, 0);
        } else if (PdfName.I.equals(borderType)) {
            resultBorder = new InsetBorder(borderColor, borderWidth);
        } else if (PdfName.B.equals(borderType)) {
            resultBorder = new BeveledBorder(borderColor, borderWidth, backgroundColor);
        } else {
            resultBorder = null;
        }
        return resultBorder;
    }
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.property.LineHeight;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.RenderingMode;

class LineHeightHelper {
    private static float DEFAULT_LINE_HEIGHT_COEFF = 1.15f;

    private LineHeightHelper() {
    }

    static float[] getActualAscenderDescender(AbstractRenderer renderer) {
        float ascender;
        float descender;
        float lineHeight = LineHeightHelper.calculateLineHeight(renderer);
        float[] fontAscenderDescender = LineHeightHelper.getFontAscenderDescenderNormalized(renderer);
        float leading = lineHeight - (fontAscenderDescender[0] - fontAscenderDescender[1]);
        ascender = fontAscenderDescender[0] + leading / 2f;
        descender = fontAscenderDescender[1] - leading / 2f;
        return new float[] {ascender, descender};
    }

    static float[] getFontAscenderDescenderNormalized(AbstractRenderer renderer) {
        PdfFont font = renderer.resolveFirstPdfFont();
        float fontSize = renderer.getPropertyAsUnitValue(Property.FONT_SIZE).getValue();
        float[] fontAscenderDescenderFromMetrics = TextRenderer.calculateAscenderDescender(font, RenderingMode.HTML_MODE);
        float fontAscender = fontAscenderDescenderFromMetrics[0] / FontProgram.UNITS_NORMALIZATION * fontSize;
        float fontDescender = fontAscenderDescenderFromMetrics[1] / FontProgram.UNITS_NORMALIZATION * fontSize;
        return new float[] {fontAscender, fontDescender};
    }

    static float calculateLineHeight(AbstractRenderer renderer) {
        LineHeight lineHeight = renderer.<LineHeight>getProperty(Property.LINE_HEIGHT);
        float fontSize = renderer.getPropertyAsUnitValue(Property.FONT_SIZE).getValue();
        float lineHeightValue;
        if (lineHeight == null || lineHeight.isNormalValue() || lineHeight.getValue() < 0) {
            lineHeightValue = DEFAULT_LINE_HEIGHT_COEFF * fontSize;
            float[] fontAscenderDescender = getFontAscenderDescenderNormalized(renderer);
            float fontAscenderDescenderSum = fontAscenderDescender[0] - fontAscenderDescender[1];
            if (fontAscenderDescenderSum > lineHeightValue) {
                lineHeightValue = fontAscenderDescenderSum;
            }
        } else {
            if (lineHeight.isFixedValue()) {
                lineHeightValue = lineHeight.getValue();
            } else {
                lineHeightValue = lineHeight.getValue() * fontSize;
            }
        }
        return lineHeightValue;
    }
}

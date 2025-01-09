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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.properties.LineHeight;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;

class LineHeightHelper {
    public static final int ASCENDER_INDEX = 0;
    public static final int DESCENDER_INDEX = 1;
    public static final int XHEIGHT_INDEX = 2;
    public static final int LEADING_INDEX = 3;
    private static final float DEFAULT_LINE_HEIGHT_COEFF = 1.15f;

    private LineHeightHelper() {
    }

    /**
     * Get actual ascender, descender.
     *
     * @param renderer the renderer to retrieve the ascender and descender from
     *
     * @return an array containing in this order actual ascender
     */
    static float[] getActualAscenderDescender(AbstractRenderer renderer) {
        float[] result = getActualFontInfo(renderer);
        return new float[] {result[0], result[1]};
    }

    /**
     * Get actual ascender, descender, xHeight and leading.
     *
     * @param renderer the renderer to retrieve the font info from
     *
     * @return an array containing in this order actual ascender, descender, xHeight and leading
     */
    static float[] getActualFontInfo(AbstractRenderer renderer) {
        float ascender;
        float descender;
        float lineHeight = LineHeightHelper.calculateLineHeight(renderer);
        float[] fontAscenderDescender = LineHeightHelper.getFontAscenderDescenderNormalized(renderer);
        float leading = lineHeight - (fontAscenderDescender[0] - fontAscenderDescender[1]);
        ascender = fontAscenderDescender[0] + leading / 2F;
        descender = fontAscenderDescender[1] - leading / 2F;
        return new float[] {ascender, descender, fontAscenderDescender[2], leading};
    }

    static float[] getFontAscenderDescenderNormalized(AbstractRenderer renderer) {
        PdfFont font = renderer.resolveFirstPdfFont();
        float fontSize = renderer.getPropertyAsUnitValue(Property.FONT_SIZE).getValue();
        float[] fontAscenderDescenderFromMetrics = TextRenderer.calculateAscenderDescender(font,
                RenderingMode.HTML_MODE);
        final float fontAscender =
                FontProgram.convertTextSpaceToGlyphSpace(fontAscenderDescenderFromMetrics[0]) * fontSize;
        final float fontDescender =
                FontProgram.convertTextSpaceToGlyphSpace(fontAscenderDescenderFromMetrics[1]) * fontSize;
        final float xHeight =
                FontProgram.convertTextSpaceToGlyphSpace(font.getFontProgram().getFontMetrics().getXHeight())
                        * fontSize;
        return new float[] {fontAscender, fontDescender, xHeight};
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

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
package com.itextpdf.forms.util;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;


/**
 * Utility class for font size calculations.
 */
public final class FontSizeUtil {

    private FontSizeUtil() {
        //utility class
    }

    /**
     * Calculates the font size that will fit the text in the given rectangle.
     *
     * @param localFont   the font to be used
     * @param bBox        the bounding box of the field
     * @param value       the value of the field
     * @param minValue    the minimum font size
     * @param borderWidth the border width of the field
     *
     * @return the font size that will fit the text in the given rectangle
     */
    public static float approximateFontSizeToFitSingleLine(PdfFont localFont, Rectangle bBox, String value,
            float minValue, float borderWidth) {
        // For text field that value shall be min 4, for checkbox there is no min value.
        float fs;
        float height = bBox.getHeight() - borderWidth * 2;
        int[] fontBbox = localFont.getFontProgram().getFontMetrics().getBbox();
        fs = FontProgram.convertGlyphSpaceToTextSpace(height / (fontBbox[2] - fontBbox[1]));

        float baseWidth = localFont.getWidth(value, 1);
        if (baseWidth != 0) {
            float availableWidth = Math.max(bBox.getWidth() - borderWidth * 2, 0);
            // This constant is taken based on what was the resultant padding in previous version
            // of this algorithm in case border width was zero.
            float absMaxPadding = 4F;
            // relative value is quite big in order to preserve visible padding on small field sizes.
            // This constant is taken arbitrary, based on visual similarity to Acrobat behaviour.
            float relativePaddingForSmallSizes = 0.15F;
            // with current constants, if availableWidth is less than ~26 points, padding will be made relative
            if (availableWidth * relativePaddingForSmallSizes < absMaxPadding) {
                availableWidth -= availableWidth * relativePaddingForSmallSizes * 2;
            } else {
                availableWidth -= absMaxPadding * 2;
            }
            fs = Math.min(fs, availableWidth / baseWidth);
        }
        return Math.max(fs, minValue);
    }

}

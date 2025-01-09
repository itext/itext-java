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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfArray;

class InteriorColorUtil {

    private InteriorColorUtil() {
    }

    /**
     * The interior color which is used to fill areas specific for different types of annotation. For {@link PdfLineAnnotation}
     * and polyline annotation ({@link PdfPolyGeomAnnotation} - the annotation's line endings, for {@link PdfSquareAnnotation}
     * and {@link PdfCircleAnnotation} - the annotation's rectangle or ellipse, for {@link PdfRedactAnnotation} - the redacted
     * region after the affected content has been removed.
     * @return {@link Color} of either {@link DeviceGray}, {@link DeviceRgb} or {@link DeviceCmyk} type which defines
     * interior color of the annotation, or null if interior color is not specified.
     */
    public static Color parseInteriorColor(PdfArray color) {
        if (color == null) {
            return null;
        }
        switch (color.size()) {
            case 1:
                return new DeviceGray(color.getAsNumber(0).floatValue());
            case 3:
                return new DeviceRgb(color.getAsNumber(0).floatValue(), color.getAsNumber(1).floatValue(), color.getAsNumber(2).floatValue());
            case 4:
                return new DeviceCmyk(color.getAsNumber(0).floatValue(), color.getAsNumber(1).floatValue(), color.getAsNumber(2).floatValue(), color.getAsNumber(3).floatValue());
            default:
                return null;
        }
    }
}

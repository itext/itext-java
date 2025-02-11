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

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

class BorderStyleUtil {

    private BorderStyleUtil(){
    }

    /**
     * Setter for the border style. Possible values are
     * <ul>
     *     <li>{@link PdfAnnotation#STYLE_SOLID} - A solid rectangle surrounding the annotation.
     *     <li>{@link PdfAnnotation#STYLE_DASHED} - A dashed rectangle surrounding the annotation.
     *     <li>{@link PdfAnnotation#STYLE_BEVELED} - A simulated embossed rectangle that appears to be raised above the surface of the page.
     *     <li>{@link PdfAnnotation#STYLE_INSET} - A simulated engraved rectangle that appears to be recessed below the surface of the page.
     *     <li>{@link PdfAnnotation#STYLE_UNDERLINE} - A single line along the bottom of the annotation rectangle.
     * </ul>
     * See also ISO-320001, Table 166.
     * @param bs original border style dictionary.
     * @param style The new value for the annotation's border style.
     * @return Updated border style dictionary entry.
     */
    public static final PdfDictionary setStyle(PdfDictionary bs, PdfName style) {
        if (null == bs) {
            bs = new PdfDictionary();
        }
        bs.put(PdfName.S, style);
        return bs;
    }

    /**
     * Setter for the dashed border style. This property has affect only if {@link PdfAnnotation#STYLE_DASHED}
     * style was used for border style (see {@link #setStyle(PdfDictionary, PdfName)}.
     * See ISO-320001 8.4.3.6, "Line Dash Pattern" for the format in which dash pattern shall be specified.
     *
     * @param bs original border style dictionary.
     * @param dashPattern a dash array defining a pattern of dashes and gaps that
     *                    shall be used in drawing a dashed border.
     * @return Updated border style dictionary entry.
     */
    public static final PdfDictionary setDashPattern(PdfDictionary bs, PdfArray dashPattern) {
        if (null == bs) {
            bs = new PdfDictionary();
        }
        bs.put(PdfName.D, dashPattern);
        return bs;
    }
}

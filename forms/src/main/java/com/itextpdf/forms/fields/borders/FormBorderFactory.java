/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

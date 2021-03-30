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
package com.itextpdf.layout.property;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;

/**
 * Defines all possible blend modes and their mapping to pdf names.
 */
public enum BlendMode {

    // Standard separable blend modes
    NORMAL(PdfExtGState.BM_NORMAL),
    MULTIPLY(PdfExtGState.BM_MULTIPLY),
    SCREEN(PdfExtGState.BM_SCREEN),
    OVERLAY(PdfExtGState.BM_OVERLAY),
    DARKEN(PdfExtGState.BM_DARKEN),
    LIGHTEN(PdfExtGState.BM_LIGHTEN),
    COLOR_DODGE(PdfExtGState.BM_COLOR_DODGE),
    COLOR_BURN(PdfExtGState.BM_COLOR_BURN),
    HARD_LIGHT(PdfExtGState.BM_HARD_LIGHT),
    SOFT_LIGHT(PdfExtGState.BM_SOFT_LIGHT),
    DIFFERENCE(PdfExtGState.BM_DIFFERENCE),
    EXCLUSION(PdfExtGState.BM_EXCLUSION),
    // Standard nonseparable blend modes
    HUE(PdfExtGState.BM_HUE),
    SATURATION(PdfExtGState.BM_SATURATION),
    COLOR(PdfExtGState.BM_COLOR),
    LUMINOSITY(PdfExtGState.BM_LUMINOSITY);

    private final PdfName pdfRepresentation;

    BlendMode(PdfName pdfRepresentation) {
        this.pdfRepresentation = pdfRepresentation;
    }

    /**
     * Get the pdf representation of the current blend mode.
     *
     * @return the {@link PdfName} representation of the current blend mode.
     */
    public PdfName getPdfRepresentation() {
        return this.pdfRepresentation;
    }
}

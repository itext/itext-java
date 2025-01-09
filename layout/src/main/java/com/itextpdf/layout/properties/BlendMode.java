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
package com.itextpdf.layout.properties;

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

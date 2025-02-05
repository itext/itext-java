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
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;

/**
 * A PdfCanvas instance with an inherent tiling pattern.
 */
public class PdfPatternCanvas extends PdfCanvas {

    private final PdfPattern.Tiling tilingPattern;

    /**
     * Creates PdfPatternCanvas from content stream of page, form XObject, pattern etc.
     *
     * @param contentStream The content stream
     * @param resources     The resources, a specialized dictionary that can be used by PDF instructions in the content stream
     * @param document      The document that the resulting content stream will be written to
     */
    public PdfPatternCanvas(PdfStream contentStream, PdfResources resources, PdfDocument document) {
        super(contentStream, resources, document);
        this.tilingPattern = new PdfPattern.Tiling(contentStream);
    }

    /**
     * Creates PdfPatternCanvas for a document from a provided Tiling pattern
     * @param pattern   The Tiling pattern must be colored
     * @param document  The document that the resulting content stream will be written to
     */
    public PdfPatternCanvas(PdfPattern.Tiling pattern, PdfDocument document) {
        super((PdfStream) pattern.getPdfObject(), pattern.getResources(), document);
        this.tilingPattern = pattern;
    }

    @Override
    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern, boolean fill) {
        checkNoColor();
        return super.setColor(colorSpace, colorValue, pattern, fill);
    }

    private void checkNoColor() {
        if (!tilingPattern.isColored()) {
            throw new PdfException(
                    KernelExceptionMessageConstant.CONTENT_STREAM_MUST_NOT_INVOKE_OPERATORS_THAT_SPECIFY_COLORS_OR_OTHER_COLOR_RELATED_PARAMETERS);
        }
    }
}

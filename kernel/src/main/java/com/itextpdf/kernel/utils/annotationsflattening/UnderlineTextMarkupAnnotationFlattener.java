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
package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link IAnnotationFlattener} for underline annotations.
 */
public class UnderlineTextMarkupAnnotationFlattener extends AbstractTextMarkupAnnotationFlattener {

    /**
     * Creates a new {@link UnderlineTextMarkupAnnotationFlattener} instance.
     */
    public UnderlineTextMarkupAnnotationFlattener() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean draw(PdfAnnotation annotation, PdfPage page) {
        final PdfCanvas under = createCanvas(page);
        final float[] quadPoints = getQuadPointsAsFloatArray(annotation);
        under.saveState()
                .setStrokeColor(getColor(annotation))
                .setLineWidth(1)
                .moveTo(quadPoints[4], quadPoints[5] + 1.25)
                .lineTo(quadPoints[6], quadPoints[7] + 1.25)
                .stroke()
                .restoreState();
        return true;
    }
}

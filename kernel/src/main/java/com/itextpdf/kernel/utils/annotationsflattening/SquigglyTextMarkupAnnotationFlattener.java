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
 * Implementation of {@link IAnnotationFlattener} for squiggly annotations.
 */
public class SquigglyTextMarkupAnnotationFlattener extends AbstractTextMarkupAnnotationFlattener {
    private static final double HEIGHT = 1;
    private static final double ADVANCE = 1;

    /**
     * Creates a new {@link SquigglyTextMarkupAnnotationFlattener} instance.
     */
    public SquigglyTextMarkupAnnotationFlattener() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean draw(PdfAnnotation annotation, PdfPage page) {
        final PdfCanvas under = createCanvas(page);
        final float[] quadPoints = getQuadPointsAsFloatArray(annotation);

        final double baseLineHeight = quadPoints[7] + 1.25;
        final double maxHeight = baseLineHeight + HEIGHT;
        final double minHeight = baseLineHeight - HEIGHT;
        final double maxWidth = page.getPageSize().getWidth();
        double xCoordinate = quadPoints[4];
        final double endX = quadPoints[6];

        under.saveState()
                .setStrokeColor(getColor(annotation));
        while (xCoordinate <= endX) {
            if (xCoordinate >= maxWidth) {
                //safety check to avoid infinite loop
                break;
            }
            under.moveTo(xCoordinate, baseLineHeight);
            xCoordinate += ADVANCE;
            under.lineTo(xCoordinate, maxHeight);
            xCoordinate += 2 * ADVANCE;
            under.lineTo(xCoordinate, minHeight);
            xCoordinate += ADVANCE;
            under.lineTo(xCoordinate, baseLineHeight);
            under.stroke();
        }
        under.restoreState();
        return true;
    }
}

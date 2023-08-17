package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link IAnnotationFlattener} for strikeout annotations.
 */
public class StrikeOutTextMarkupAnnotationFlattener extends AbstractTextMarkupAnnotationFlattener {

    /**
     * Creates a new {@link StrikeOutTextMarkupAnnotationFlattener} instance.
     */
    public StrikeOutTextMarkupAnnotationFlattener() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean draw(PdfAnnotation annotation, PdfPage page) {
        final PdfCanvas under = createCanvas(page);
        final float[] quadPoints = getQuadPointsAsFloatArray(annotation);
        final double height = quadPoints[7] + ((quadPoints[1] - quadPoints[7]) / 2);
        under.saveState().setStrokeColor(getColor(annotation)).moveTo(quadPoints[4], height)
                .lineTo(quadPoints[6], height).stroke().restoreState();
        return true;
    }
}

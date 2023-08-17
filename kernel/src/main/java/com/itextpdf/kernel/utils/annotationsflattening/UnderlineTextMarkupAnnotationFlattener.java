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

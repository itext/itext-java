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

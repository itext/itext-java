package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link IAnnotationFlattener} for highlight text markup annotations.
 */
public class HighLightTextMarkupAnnotationFlattener extends AbstractTextMarkupAnnotationFlattener {

    /**
     * Creates a new {@link HighLightTextMarkupAnnotationFlattener} instance.
     */
    public HighLightTextMarkupAnnotationFlattener() {
        super();
    }


    /**
     * Creates a canvas. It will draw below the other items on the canvas.
     *
     * @param page the page to draw the annotation on
     *
     * @return the {@link  PdfCanvas} the annotation will be drawn upon.
     */
    @Override
    protected PdfCanvas createCanvas(PdfPage page) {
        return new PdfCanvas(page.newContentStreamBefore(), page.getResources(), page.getDocument());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean draw(PdfAnnotation annotation, PdfPage page) {
        final PdfCanvas under = createCanvas(page);
        final float[] values = getQuadPointsAsFloatArray(annotation);
        under.saveState()
                .setColor(getColor(annotation), true)
                .moveTo(values[0], values[1])
                .lineTo(values[2], values[3])
                .lineTo(values[6], values[7])
                .lineTo(values[4], values[5])
                .lineTo(values[0], values[1])
                .fill()
                .restoreState();
        return true;
    }
}

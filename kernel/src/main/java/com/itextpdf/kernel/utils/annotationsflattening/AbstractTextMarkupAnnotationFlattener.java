package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

/**
 * This class is used to flatten text markup annotations.
 * <p>
 * Text markup annotations are:
 * {@link com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation#MarkupHighlight},
 * {@link com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation#MarkupUnderline},
 * {@link com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation#MarkupSquiggly},
 * {@link com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation#MarkupStrikeout}.
 */
public abstract class AbstractTextMarkupAnnotationFlattener extends DefaultAnnotationFlattener {

    private final static int AMOUNT_OF_QUAD_POINTS = 8;

    /**
     * Gets the quadpoints as a float array.
     * if the annotation has no quadpoints, returns the annotation rectangle converted to the same notation as
     * the quadpoints.
     *
     * @param annotation the annotation
     *
     * @return the quadpoints as float array
     */
    public static float[] getQuadPointsAsFloatArray(PdfAnnotation annotation) {
        final PdfArray pdfArray = annotation.getPdfObject().getAsArray(PdfName.QuadPoints);
        if (pdfArray == null) {
            return convertFloatToQuadPoints(annotation.getRectangle().toRectangle());
        }
        final float[] floats = pdfArray.toFloatArray();
        if (floats.length == AMOUNT_OF_QUAD_POINTS) {
            return pdfArray.toFloatArray();
        }
        return convertFloatToQuadPoints(annotation.getRectangle().toRectangle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean flatten(PdfAnnotation annotation, PdfPage page) {
        final boolean flattenSucceeded = super.flatten(annotation, page);
        // Try to draw the annotation if no normal appearance was defined
        if (!flattenSucceeded) {
            draw(annotation, page);
            page.removeAnnotation(annotation);
        }
        return true;
    }

    /**
     * @param annotation the annotation to extract the color from.
     *
     * @return the color or null if the colorspace is invalid
     */
    protected Color getColor(PdfAnnotation annotation) {
        return Color.createColorWithColorSpace(annotation.getColorObject().toFloatArray());
    }


    private static float[] convertFloatToQuadPoints(Rectangle rectangle) {
        final float[] quadPoints = new float[AMOUNT_OF_QUAD_POINTS];
        quadPoints[0] = rectangle.getLeft();
        quadPoints[1] = rectangle.getTop();
        quadPoints[2] = rectangle.getRight();
        quadPoints[3] = rectangle.getTop();
        quadPoints[4] = rectangle.getLeft();
        quadPoints[5] = rectangle.getBottom();
        quadPoints[6] = rectangle.getRight();
        quadPoints[7] = rectangle.getBottom();
        return quadPoints;
    }
}

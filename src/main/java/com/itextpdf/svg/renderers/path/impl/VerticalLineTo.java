package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/***
 * Implements lineTo(V) attribute of SVG's path element
 * */
public class VerticalLineTo extends OneDimensionalLineTo {
    @Override
    public void draw(PdfCanvas canvas) {
        float minY = getCoordinate(properties, MINIMUM_CHANGING_DIMENSION_VALUE);
        float maxY = getCoordinate(properties, MAXIMUM_CHANGING_DIMENSION_VALUE);
        float endY = getCoordinate(properties, ENDING_CHANGING_DIMENSION_VALUE);
        float x = getCoordinate(properties, CURRENT_NONCHANGING_DIMENSION_VALUE);

        canvas.lineTo(x, maxY);
        canvas.lineTo(x, minY);
        canvas.lineTo(x, endY);
    }

    @Override
    public Point getEndingPoint() {
        float y = getSvgCoordinate(properties, ENDING_CHANGING_DIMENSION_VALUE);
        float x = getSvgCoordinate(properties, CURRENT_NONCHANGING_DIMENSION_VALUE);
        return new Point(x, y);
    }
}

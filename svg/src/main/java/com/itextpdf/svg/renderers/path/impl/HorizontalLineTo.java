package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/***
 * Implements lineTo(H) attribute of SVG's path element
 * */
public class HorizontalLineTo extends OneDimensionalLineTo {
    @Override
    public void draw(PdfCanvas canvas) {
        float minX = getCoordinate(properties, MINIMUM_CHANGING_DIMENSION_VALUE);
        float maxX = getCoordinate(properties, MAXIMUM_CHANGING_DIMENSION_VALUE);
        float endX = getCoordinate(properties, ENDING_CHANGING_DIMENSION_VALUE);
        float y = getCoordinate(properties, CURRENT_NONCHANGING_DIMENSION_VALUE);

        canvas.lineTo(maxX, y);
        canvas.lineTo(minX, y);
        canvas.lineTo(endX, y);
    }

    @Override
    public Point getEndingPoint() {
        float y = getSvgCoordinate(properties, CURRENT_NONCHANGING_DIMENSION_VALUE);
        float x = getSvgCoordinate(properties, ENDING_CHANGING_DIMENSION_VALUE);
        return new Point(x, y);
    }
}

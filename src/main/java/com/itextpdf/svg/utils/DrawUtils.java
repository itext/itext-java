package com.itextpdf.svg.utils;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.util.List;

public class DrawUtils {

    /**
     * Draw an arc on the passed canvas,
     * enclosed by the rectangle for which two opposite corners are specified.
     * The arc starts at the passed starting angle and extends to the starting angle + extent
     * @param x1 corner-coordinate of the enclosing rectangle, first corner
     * @param y1 corner-coordinate of the enclosing rectangle, first corner
     * @param x2 corner-coordinate of the enclosing rectangle, second corner
     * @param y2 corner-coordinate of the enclosing rectangle, second corner
     * @param startAng starting angle in degrees
     * @param extent extent of the arc
     * @param cv canvas to paint on
     */
    public static void arc(final float x1, final float y1, final float x2, final float y2, final float startAng, final float extent, PdfCanvas cv) {
        List<double[]> ar = PdfCanvas.bezierArc(x1, y1, x2, y2, startAng, extent);
        if (ar.isEmpty()) {
            return;
        }
        double pt[];
        for (int k = 0; k < ar.size(); ++k) {
            pt = ar.get(k);
            cv.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
        }
    }
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.svg.utils;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.util.List;

/**
 * Utility class for drowing shapes on {@link PdfCanvas}
 */
public class DrawUtils {

    private DrawUtils() {}

    /**
     * Draw an arc on the passed canvas,
     * enclosed by the rectangle for which two opposite corners are specified.
     * The arc starts at the passed starting angle and extends to the starting angle + extent
     * @deprecated In {@link PdfCanvas} most of the path drawing methods accept {@code double}.
     * So it is preferable to use {@link DrawUtils#arc(double, double, double, double, double, double, PdfCanvas)}.
     * This method will be removed in iText 7.2
     * @param x1 corner-coordinate of the enclosing rectangle, first corner
     * @param y1 corner-coordinate of the enclosing rectangle, first corner
     * @param x2 corner-coordinate of the enclosing rectangle, second corner
     * @param y2 corner-coordinate of the enclosing rectangle, second corner
     * @param startAng starting angle in degrees
     * @param extent extent of the arc
     * @param cv canvas to paint on
     */
    @Deprecated
    public static void arc(final float x1, final float y1, final float x2, final float y2, final float startAng, final float extent, PdfCanvas cv) {
        arc((double) x1, (double) y1, (double) x2, (double) y2, (double) startAng, (double) extent, cv);
    }

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
    public static void arc(final double x1, final double y1, final double x2, final double y2, final double startAng, final double extent, PdfCanvas cv) {
        List<double[]> ar = PdfCanvas.bezierArc(x1, y1, x2, y2, startAng, extent);
        if (!ar.isEmpty()) {
            for (double[] pt : ar) {
                cv.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
            }
        }
    }
}

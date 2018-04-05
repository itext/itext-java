package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;polygon&gt; tag.
 */
public class PolygonSvgNodeRenderer extends PolylineSvgNodeRenderer {

    /**
     * Calls setPoints(String) to set {@link PolylineSvgNodeRenderer#points}
     * Then calls {@link PolygonSvgNodeRenderer#connectPoints()} to create a path between the first and last point if it doesn't already exist
     */
    @Override
    protected void setPoints(String pointsAttribute) {
        super.setPoints(pointsAttribute);
        connectPoints();
    }

    /**
     * Appends the starting point to the end of {@link PolylineSvgNodeRenderer#points} if it is not already there.
     */
    private void connectPoints() {
        if (points.size() < 2) {
            return;
        }

        Point start = points.get(0);
        Point end = points.get(points.size() - 1);
        if (start.x != end.x && start.y != end.y) {
            points.add(new Point(start.x, start.y));
        }
    }
}

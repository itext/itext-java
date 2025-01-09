/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.renderers.IMarkerCapable;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;polygon&gt; tag.
 */
public class PolygonSvgNodeRenderer extends PolylineSvgNodeRenderer implements IMarkerCapable {

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
        if (Double.compare(start.getX(), end.getX()) != 0 || Double.compare(start.getY(), end.getY()) != 0) {
            points.add(new Point(start.getX(), start.getY()));
        }
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        PolygonSvgNodeRenderer copy = new PolygonSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }
}

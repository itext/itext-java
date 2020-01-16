/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.renderers;

import com.itextpdf.svg.MarkerVertexType;
import com.itextpdf.svg.renderers.impl.MarkerSvgNodeRenderer;

/**
 * Interface implemented by elements that support marker drawing.
 * Defines methods for working with markers.
 */
public interface IMarkerCapable {
    /**
     * Draws a marker in the specified context.
     * The marker is drawn on the vertices defined according to the given marker type.
     *
     * @param context          the object that knows the place to draw this element and maintains its state
     * @param markerVertexType type of marker that determine on which vertices of the given element
     *                         marker should  be drawn
     */
    void drawMarker(SvgDrawContext context, MarkerVertexType markerVertexType);

    /**
     * Calculates marker orientation angle if {@code orient} attribute is set to {@code auto}
     *
     * @param marker  marker for which the rotation angle should be calculated
     * @param reverse indicates that the resulting angle should be rotated 180 degrees
     * @return marker orientation angle so that its positive x-axis is pointing in the direction of the path at the
     * point it is placed
     */
    double getAutoOrientAngle(MarkerSvgNodeRenderer marker, boolean reverse);
}

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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;

/**
 * Interface that describes a Path object which is defined by control points. In practice this only means Bézier curves,
 * both quadratic (one control point) and cubic (two control points). This interface is relevant in the context of
 * Smooth (Shorthand) Bézier curves, which omit a control point from their arguments list because it can be calculated
 * from the last control point of the previous curve. Therefore, the last control point of a curve must be exposed to
 * the {@link com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer} algorithm.
 */
public interface IControlPointCurve {

    /**
     * Returns coordinates of the last control point (the one closest to the ending point)
     * in the Bezier curve, in SVG space coordinates
     * @return coordinates of the last control point in SVG space coordinates
     */
    Point getLastControlPoint();
}

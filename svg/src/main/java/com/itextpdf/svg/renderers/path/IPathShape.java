/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.svg.renderers.path;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.path.impl.AbstractPathShape;

/**
 * Interface for IPathShape, which draws the Path-data's d element instructions.
 */
public interface IPathShape {
    /**
     * Draws this instruction to a canvas object.
     * <p>
     * Deprecated in favour of {@link AbstractPathShape#draw()} and later this method should be introduced
     * in this interface, along with {@link AbstractPathShape#setContext(SvgDrawContext)} method. Since
     * canvas can be got from {@link SvgDrawContext} the {@link PdfCanvas} parameter is no more needed.
     *
     * @param canvas to which this instruction is drawn
     */
    @Deprecated
    void draw(PdfCanvas canvas);

    /**
     * This method sets the coordinates for the path painting operator and does internal
     * preprocessing, if necessary
     * @param inputCoordinates an array containing point values for path coordinates
     * @param startPoint the ending point of the previous operator, or, in broader terms,
     *                   the point that the coordinates should be absolutized against, for relative operators
     */
    void setCoordinates(String[] inputCoordinates, Point startPoint);

    /**
     * Gets the ending point on the canvas after the path shape has been drawn
     * via the {@link IPathShape#draw(PdfCanvas)} method, in SVG space coordinates.
     *
     * @return The {@link Point} representing the final point in the drawn path.
     *         If the point does not exist or does not change {@code null} may be returned.
     */
    Point getEndingPoint();

    /**
     * Returns true when this shape is a relative operator. False if it is an absolute operator.
     *
     * @return true if relative, false if absolute
     */
    boolean isRelative();

    /**
     * Get bounding rectangle of the current path shape.
     *
     * @param lastPoint start point for this shape
     * @return calculated rectangle
     */
    Rectangle getPathShapeRectangle(Point lastPoint);
}

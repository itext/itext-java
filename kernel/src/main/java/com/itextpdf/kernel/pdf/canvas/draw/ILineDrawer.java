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
package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * The {@link ILineDrawer} defines a drawing operation on a {@link PdfCanvas}
 * <br>
 * This interface allows to customize the 'empty' space in a
 * {@code com.itextpdf.layout.element.TabStop} through a Strategy design
 * pattern
 */
public interface ILineDrawer {

    /**
     * Performs configurable drawing operations related to specific region
     * coordinates on a canvas.
     *
     * @param canvas   the canvas to draw on
     * @param drawArea the rectangle in relation to which to fulfill drawing
     *                 instructions
     */
    void draw(PdfCanvas canvas, Rectangle drawArea);

    /**
     * Gets the width of the line
     *
     * @return width of the line
     */
    float getLineWidth();

    /**
     * Sets line width in points
     *
     * @param lineWidth new line width
     */
    void setLineWidth(float lineWidth);

    /**
     * Gets the color of the line
     *
     * @return color of the line
     */
    Color getColor();

    /**
     * Sets line color
     *
     * @param color new line color
     */
    void setColor(Color color);


}

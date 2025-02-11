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
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link ILineDrawer} which draws a dashed horizontal line over
 * the middle of the specified rectangle.
 */
public class DashedLine implements ILineDrawer {

    private float lineWidth = 1;

    private Color color = ColorConstants.BLACK;

    public DashedLine() {
    }

    /**
     * Creates an instance of {@link DashedLine} with the specified line width.
     *
     * @param lineWidth the line width
     */
    public DashedLine(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState()
                .setLineWidth(lineWidth)
                .setStrokeColor(color)
                .setLineDash(2, 2)
                .moveTo(drawArea.getX(), drawArea.getY() + lineWidth / 2)
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY() + lineWidth / 2)
                .stroke()
                .restoreState();
    }

    /**
     * Gets line width in points.
     *
     * @return line thickness
     */
    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets line width in points.
     *
     * @param lineWidth new line width
     */
    @Override
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
       this.color = color;
    }

}

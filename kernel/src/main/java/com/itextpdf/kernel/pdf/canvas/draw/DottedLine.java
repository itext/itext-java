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
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

/**
 * Implementation of {@link ILineDrawer} which draws a dotted horizontal line along
 * the bottom edge of the specified rectangle.
 */
public class DottedLine implements ILineDrawer {

    /**
     * the gap between the dots.
     */
    protected float gap = 4;

    private float lineWidth = 1;

    private Color color = ColorConstants.BLACK;

    /**
     * Constructs a dotted horizontal line which will be drawn along the bottom edge of the specified rectangle.
     */
    public DottedLine() {
    }

    /**
     * Constructs a dotted horizontal line which will be drawn along the bottom edge of the specified rectangle.
     *
     * @param lineWidth the width of the line
     * @param gap       the gap between the center of the dots of the dotted line.
     */
    public DottedLine(float lineWidth, float gap) {
        this.lineWidth = lineWidth;
        this.gap = gap;
    }

    /**
     * Constructs a dotted horizontal line which will be drawn along the bottom edge of the specified rectangle.
     *
     * @param lineWidth the width of the line
     */
    public DottedLine(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState()
                .setLineWidth(lineWidth)
                .setStrokeColor(color)
                .setLineDash(0, gap, gap / 2)
                .setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND)
                .moveTo(drawArea.getX(), drawArea.getY() + lineWidth / 2)
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY() + lineWidth / 2)
                .stroke()
                .restoreState();
    }

    /**
     * Getter for the gap between the center of the dots of the dotted line.
     *
     * @return the gap between the center of the dots
     */
    public float getGap() {
        return gap;
    }

    /**
     * Setter for the gap between the center of the dots of the dotted line.
     *
     * @param    gap    the gap between the center of the dots
     */
    public void setGap(float gap) {
        this.gap = gap;
    }

    /**
     * Gets line width in points
     *
     * @return line thickness
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets line width in points
     *
     * @param lineWidth new line width
     */
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

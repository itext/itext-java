package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * The {@link LineDrawer} defines a drawing operation on a {@link PdfCanvas}
 *
 * This interface allows to customize the 'empty' space in a
 * {@link com.itextpdf.layout.element.Tabstop TabStop} through a Strategy design
 * pattern
 */
public interface LineDrawer {

    /**
     * Performs configurable drawing operations related to specific region
     * coordinates on a canvas.
     *
     * @param canvas the canvas to draw on
     * @param drawArea the rectangle in relation to which to fulfill drawing
     * instructions
     */
    void draw(PdfCanvas canvas, Rectangle drawArea);

    /**
     * Gets the width of the line
     * @return width of the line
     */
    float getLineWidth();

    /**
     * Sets line width in points
     * @param lineWidth new line width
     */
    void setLineWidth(float lineWidth);
}

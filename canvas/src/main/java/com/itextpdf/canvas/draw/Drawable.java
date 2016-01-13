package com.itextpdf.canvas.draw;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;

/**
 * The <code>Drawable</code> defines a drawing operation on a {@link PdfCanvas}
 *
 * This interface allows to customize the 'empty' space in a
 * {@link com.itextpdf.model.element.Tabstop TabStop} through a Strategy design
 * pattern
 */
public interface Drawable {

    /**
     * Performs configurable drawing operations related to specific region
     * coordinates on a canvas.
     *
     * @param canvas the canvas to draw on
     * @param drawArea the rectangle in relation to which to fulfill drawing
     * instructions
     */
    void draw(PdfCanvas canvas, Rectangle drawArea);
}

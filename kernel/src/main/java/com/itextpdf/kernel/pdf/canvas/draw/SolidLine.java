package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link Drawable} which draws a solid horizontal line along
 * the bottom edge of the specified rectangle.
 */
public class SolidLine implements Drawable {

    private float lineWidth = .5f;

    /**
     * Constructs an instance of solid line drawer
     */
    public SolidLine() {
    }

    /**
     * Constructs an instance of solid line drawer with the specified line thickness
     * @param lineWidth line width
     */
    public SolidLine(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState();
        canvas.setLineWidth(lineWidth);
        canvas
                .moveTo(drawArea.getX(), drawArea.getY())
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY())
                .stroke()
                .restoreState();
    }

    /**
     * Gets line width in points
     * @return line thickness
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets line width in points
     * @param lineWidth new line width
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

}

package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link LineDrawer} which draws a solid horizontal line along
 * the bottom edge of the specified rectangle.
 */
public class SolidLine implements LineDrawer {

    private float lineWidth = 1;

    private Color color = Color.BLACK;

    /**
     * Constructs an instance of solid line drawer
     */
    public SolidLine() {
    }

    /**
     * Constructs an instance of solid line drawer with the specified line thickness
     *
     * @param lineWidth line width
     */
    public SolidLine(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState()
                .setStrokeColor(color)
                .setColor(color, true)
                .moveTo(drawArea.getX(), drawArea.getY())
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY())
                .stroke()
                .restoreState();
    }

    /**
     * Gets line width in points
     *
     * @return line thickness
     */
    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets line width in points
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

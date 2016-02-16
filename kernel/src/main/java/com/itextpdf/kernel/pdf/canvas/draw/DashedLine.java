package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implementation of {@link LineDrawer} which draws a dashed horizontal line over
 * the middle of the specified rectangle.
 */
public class DashedLine implements LineDrawer {

    private float lineWidth = 1;

    public DashedLine() {
    }

    /**
     * Creates an instance of {@link DashedLine} with the specified line width.
     * @param lineWidth
     */
    public DashedLine(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState()
                .setLineWidth(lineWidth)
                .setLineDash(2, 2)
                .moveTo(drawArea.getX(), drawArea.getY() + drawArea.getHeight() / 2)
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY() + drawArea.getHeight() / 2)
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

package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

/**
 * Implementation of {@link Drawable} which draws a dotted horizontal line along
 * the bottom edge of the specified rectangle.
 */
public class DottedLine implements Drawable {

    /** the gap between the dots. */
    protected float gap = 4;

    /**
     * Constructs a dotted horizontal line which will be drawn along the bottom edge of the specified rectangle.
     */
    public DottedLine() {
    }

    /**
     * Constructs a dotted horizontal line which will be drawn along the bottom edge of the specified rectangle.
     * @param gap the gap between the center of the dots of the dotted line.
     */
    public DottedLine(float gap) {
        this.gap = gap;
    }

    @Override
    public void draw(PdfCanvas canvas, Rectangle drawArea) {
        canvas.saveState();
        canvas.setLineDash(0, gap, gap / 2);
        canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);
        canvas
                .moveTo(drawArea.getX(), drawArea.getY())
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY())
                .stroke()
                .restoreState();
    }

    /**
     * Getter for the gap between the center of the dots of the dotted line.
     * @return	the gap between the center of the dots
     */
    public float getGap() {
        return gap;
    }

    /**
     * Setter for the gap between the center of the dots of the dotted line.
     * @param	gap	the gap between the center of the dots
     */
    public void setGap(float gap) {
        this.gap = gap;
    }

}

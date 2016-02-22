package com.itextpdf.kernel.pdf.canvas.draw;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

/**
 * Implementation of {@link LineDrawer} which draws a dotted horizontal line along
 * the bottom edge of the specified rectangle.
 */
public class DottedLine implements LineDrawer {

    /**
     * the gap between the dots.
     */
    protected float gap = 4;

    private float lineWidth = 1;

    private Color color = Color.BLACK;

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
                .moveTo(drawArea.getX(), drawArea.getY())
                .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY())
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

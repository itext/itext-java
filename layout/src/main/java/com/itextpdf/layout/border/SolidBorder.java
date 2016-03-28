package com.itextpdf.layout.border;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.Color;

/**
 * Draws a solid border around the element it's set to.
 */
public class SolidBorder extends Border {

    /**
     * Creates a SolidBorder with the specified width and sets the color to black.
     *
     * @param width width of the border
     */
    public SolidBorder(float width) {
        super(width);
    }

    /**
     * Creates a SolidBorder with the specified width and the specified color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public SolidBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public int getType() {
        return Border.SOLID;
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2);
        switch (borderSide) {
            case TOP:
                x3 = x2 + borderWidthAfter;
                y3 = y2 + width;
                x4 = x1 - borderWidthBefore;
                y4 = y1 + width;
                break;
            case RIGHT:
                x3 = x2 + width;
                y3 = y2 - borderWidthAfter;
                x4 = x1 + width;
                y4 = y1 + borderWidthBefore;
                break;
            case BOTTOM:
                x3 = x2 - borderWidthAfter;
                y3 = y2 - width;
                x4 = x1 + borderWidthBefore;
                y4 = y1 - width;
                break;
            case LEFT:
                x3 = x2 - width;
                y3 = y2 + borderWidthAfter;
                x4 = x1 - width;
                y4 = y1 - borderWidthBefore;
                break;
        }

        canvas.setFillColor(color);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();
    }

    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        canvas.
                saveState().
                setStrokeColor(color).
                setLineWidth(width).
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }
}

package com.itextpdf.layout.border;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.Color;

/**
 * Draws a dotted border around the element it has been set to. Do note that this border draw square dots,
 * if you want to draw round dots, see {@link com.itextpdf.layout.border.RoundDotsBorder}.
 */
public class DottedBorder extends Border {

    private static final float gapModifier = 1.5f;

    /**
     * Creates a DotterBorder instance with the specified width. The color is set to the default: black.
     * @param width width of the border
     */
    public DottedBorder(float width) {
        super(width);
    }

    /**
     * Creates a DottedBorder instance with the specified width and color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public DottedBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public int getType() {
        return Border.DOTTED;
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * gapModifier;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = getDotsGap(borderLength, initialGap + width);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        float widthHalf = width / 2;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2);
        switch (borderSide) {
            case TOP:
                y1 += widthHalf;
                y2 += widthHalf;
                break;
            case RIGHT:
                x1 += widthHalf;
                x2 += widthHalf;
                break;
            case BOTTOM:
                y1 -= widthHalf;
                y2 -= widthHalf;
                break;
            case LEFT:
                x1 -= widthHalf;
                x2 -= widthHalf;
                break;
        }

        canvas.setLineWidth(width);
        canvas.setStrokeColor(color);
        canvas.setLineDash(width, adjustedGap, width + adjustedGap/2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke();

    }

    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        float initialGap = width * gapModifier;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = getDotsGap(borderLength, initialGap + width);
        if (adjustedGap > width) {
            adjustedGap -= width;
        }

        canvas.
                saveState().
                setLineWidth(width).
                setStrokeColor(color).
                setLineDash(width, adjustedGap, width + adjustedGap / 2).
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }

    protected float getDotsGap(double distance, float initialGap) {
        double gapsNum = Math.ceil(distance / initialGap);
        return (float) (distance / gapsNum);
    }

}

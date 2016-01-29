package com.itextpdf.model.border;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.color.Color;

/**
 * Draws a border with rounded dots aroudn the element it's been set to. For square dots see {@link com.itextpdf.model.border.DottedBorder}.
 */
public class RoundDotsBorder extends Border {
    private static final float gapModifier = 2.5f;

    /**
     * Creates a RoundDotsBorder with the specified witµdth and sets the color to black.
     *
     * @param width width of the border
     */
    public RoundDotsBorder(float width) {
        super(width);
    }

    /**
     * Creates a RoundDotsBorder with the specified witµdth and the specified color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    public RoundDotsBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public int getType() {
        return Border.ROUND_DOTS;
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * gapModifier;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);
        float adjustedGap = getDotsGap(borderLength, initialGap);

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

        canvas.setStrokeColor(color);
        canvas.setLineWidth(width);
        canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);

        canvas.setLineDash(0, adjustedGap, adjustedGap/2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke();
    }

    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        float initialGap = width * gapModifier;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);
        float adjustedGap = getDotsGap(borderLength, initialGap);
        boolean isHorizontal = false;
        if (Math.abs(y2 - y1) < 0.0005f) {
            isHorizontal = true;
        }

        if (isHorizontal) {
            x2 -= width;
        }

        canvas.setStrokeColor(color);
        canvas.setLineWidth(width);
        canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);

        canvas.setLineDash(0, adjustedGap, adjustedGap/2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke();
    }

    protected float getDotsGap(double distance, float initialGap) {
        double gapsNum = Math.ceil(distance / initialGap);
        return (float) (distance / gapsNum);
    }

}

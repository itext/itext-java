package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.PdfCanvasConstants;
import com.itextpdf.canvas.color.Color;

public class RoundDotsBorder extends Border {
    private static final float gapModifier = 2.5f;

    public RoundDotsBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float joinAreaBefore, float joinAreaAfter) {
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

    protected float getDotsGap(double distance, float initialGap) {
        double gapsNum = Math.ceil(distance / initialGap);
        return (float) (distance / gapsNum);
    }

}

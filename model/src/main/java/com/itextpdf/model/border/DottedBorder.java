package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public class DottedBorder extends Border {
    private static final float gapModifier = 1.5f;

    public DottedBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float joinAreaBefore, float joinAreaAfter) {
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

    protected float getDotsGap(double distance, float initialGap) {
        double gapsNum = Math.ceil(distance / initialGap);
        return (float) (distance / gapsNum);
    }

}

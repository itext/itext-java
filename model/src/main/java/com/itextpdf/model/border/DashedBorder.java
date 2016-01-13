package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;

public class DashedBorder extends Border {

    private static final float dashModifier = 5f;
    private static final float gapModifier = 3.5f;

    public DashedBorder(float width) {
        super(width);
    }
    
    public DashedBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public int getType() {
        return Border.DASHED;
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter) {
        float initialGap = width * gapModifier;
        float dash = width * dashModifier;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = getDotsGap(borderLength, initialGap + dash);
        if (adjustedGap > dash) {
            adjustedGap -= dash;
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

        canvas.setLineDash(dash, adjustedGap, dash + adjustedGap/2)
                .moveTo(x1, y1).lineTo(x2, y2)
                .stroke();
    }

    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        float initialGap = width * gapModifier;
        float dash = width * dashModifier;
        float dx = x2 - x1;
        float dy = y2 - y1;
        double borderLength = Math.sqrt(dx * dx + dy * dy);

        float adjustedGap = getDotsGap(borderLength, initialGap + dash);
        if (adjustedGap > dash) {
            adjustedGap -= dash;
        }

        canvas.
                saveState().
                moveTo(x1, y1).
                setStrokeColor(color).
                setLineDash(dash, adjustedGap, dash + adjustedGap / 2).
                setLineWidth(width).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }

    protected float getDotsGap(double distance, float initialGap) {
        double gapsNum = Math.ceil(distance / initialGap);
        return (float) (distance / gapsNum);
    }
}

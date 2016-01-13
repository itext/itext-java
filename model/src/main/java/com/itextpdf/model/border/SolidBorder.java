package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;

public class SolidBorder extends Border {

    public SolidBorder(float width) {
        super(width);
    }

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
                moveTo(x1, y1).
                setStrokeColor(color).
                setLineWidth(width).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }
}

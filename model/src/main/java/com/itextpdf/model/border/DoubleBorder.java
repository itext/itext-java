package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;

public class DoubleBorder extends Border{

    public DoubleBorder(float width) {
        super(width);
    }

    public DoubleBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public int getType() {
        return Border.DOUBLE;
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;
        float thirdOfWidth = width / 3;
        float thirdOfWidthBefore = borderWidthBefore / 3;
        float thirdOfWidthAfter = borderWidthAfter / 3;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2);

        switch (borderSide) {
            case TOP:
                x3 = x2 + thirdOfWidthAfter;
                y3 = y2 + thirdOfWidth;
                x4 = x1 - thirdOfWidthBefore;
                y4 = y1 + thirdOfWidth;
                break;
            case RIGHT:
                x3 = x2 + thirdOfWidth;
                y3 = y2 - thirdOfWidthAfter;
                x4 = x1 + thirdOfWidth;
                y4 = y1 + thirdOfWidthBefore;
                break;
            case BOTTOM:
                x3 = x2 - thirdOfWidthAfter;
                y3 = y2 - thirdOfWidth;
                x4 = x1 + thirdOfWidthBefore;
                y4 = y1 - thirdOfWidth;
                break;
            case LEFT:
                x3 = x2 - thirdOfWidth;
                y3 = y2 + thirdOfWidthAfter;
                x4 = x1 - thirdOfWidth;
                y4 = y1 - thirdOfWidthBefore;
                break;
        }

        canvas.setFillColor(color);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();

        switch (borderSide) {
            case TOP:
                x2 += 2*thirdOfWidthAfter;
                y2 += 2*thirdOfWidth;
                x3 += 2*thirdOfWidthAfter;
                y3 += 2*thirdOfWidth;
                x4 -= 2*thirdOfWidthBefore;
                y4 += 2*thirdOfWidth;
                x1 -= 2*thirdOfWidthBefore;
                y1 += 2*thirdOfWidth;
                break;
            case RIGHT:
                x2 += 2*thirdOfWidth;
                y2 -= 2*thirdOfWidthAfter;
                x3 += 2*thirdOfWidth;
                y3 -= 2*thirdOfWidthAfter;
                x4 += 2*thirdOfWidth;
                y4 += 2*thirdOfWidthBefore;
                x1 += 2*thirdOfWidth;
                y1 += 2*thirdOfWidthBefore;
                break;
            case BOTTOM:
                x2 -= 2*thirdOfWidthAfter;
                y2 -= 2*thirdOfWidth;
                x3 -= 2*thirdOfWidthAfter;
                y3 -= 2*thirdOfWidth;
                x4 += 2*thirdOfWidthBefore;
                y4 -= 2*thirdOfWidth;
                x1 += 2*thirdOfWidthBefore;
                y1 -= 2*thirdOfWidth;
                break;
            case LEFT:
                x2 -= 2*thirdOfWidth;
                y2 += 2*thirdOfWidthAfter;
                x3 -= 2*thirdOfWidth;
                y3 += 2*thirdOfWidthAfter;
                x4 -= 2*thirdOfWidth;
                y4 -= 2*thirdOfWidthBefore;
                x1 -= 2*thirdOfWidth;
                y1 -= 2*thirdOfWidthBefore;
                break;
        }

        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();
    }

    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
//        float x3 = 0, y3 = 0;
//        float x4 = 0, y4 = 0;
        float thirdOfWidth = width / 3;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2);

        switch (borderSide) {
            case TOP:
                //x1 += thirdOfWidth;
                //x2 += 2*thirdOfWidth;
                y1 -= thirdOfWidth;
                y2 = y1;
                break;
            case RIGHT:
                x1 -= thirdOfWidth;
                x2 -= thirdOfWidth;
                y1 += thirdOfWidth;
                y2 -= thirdOfWidth;
                break;
            case BOTTOM:
                break;
            case LEFT:
                break;
        }

        canvas.
                saveState().
                setLineWidth(thirdOfWidth).
                setStrokeColor(color).
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();

        switch (borderSide) {
            case TOP:
//                x1 -= 2*thirdOfWidth;
                y2 += 2*thirdOfWidth;
                y1 += 2*thirdOfWidth;
                break;
            case RIGHT:
                x2 += 2*thirdOfWidth;
                x1 += 2*thirdOfWidth;
//                y1 -= 2*thirdOfWidth;
                break;
            case BOTTOM:
                x2 -= 2*thirdOfWidth;
                y2 -= 2*thirdOfWidth;
                x1 += 2*thirdOfWidth;
                y1 -= 2*thirdOfWidth;
                break;
            case LEFT:
                y2 += 2*thirdOfWidth;
                x1 -= 2*thirdOfWidth;
                y1 -= 2*thirdOfWidth;
                break;
        }

        canvas.
                saveState().
                setLineWidth(thirdOfWidth).
                setStrokeColor(color).
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }
}

package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public class DoubleBorder extends Border{

    public DoubleBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float joinAreaBefore, float joinAreaAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;
        float thirdOfWidth = width / 3;
        float thirdOfJoinsBefore = joinAreaBefore / 3;
        float thirdOfJoinsAfter = joinAreaAfter / 3;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2);

        switch (borderSide) {
            case TOP:
                x3 = x2 + thirdOfJoinsAfter;
                y3 = y2 + thirdOfWidth;
                x4 = x1 - thirdOfJoinsBefore;
                y4 = y1 + thirdOfWidth;
                break;
            case RIGHT:
                x3 = x2 + thirdOfWidth;
                y3 = y2 - thirdOfJoinsAfter;
                x4 = x1 + thirdOfWidth;
                y4 = y1 + thirdOfJoinsBefore;
                break;
            case BOTTOM:
                x3 = x2 - thirdOfJoinsAfter;
                y3 = y2 - thirdOfWidth;
                x4 = x1 + thirdOfJoinsBefore;
                y4 = y1 - thirdOfWidth;
                break;
            case LEFT:
                x3 = x2 - thirdOfWidth;
                y3 = y2 + thirdOfJoinsAfter;
                x4 = x1 - thirdOfWidth;
                y4 = y1 - thirdOfJoinsBefore;
                break;
        }

        canvas.setFillColor(color);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();

        switch (borderSide) {
            case TOP:
                x2 += 2*thirdOfJoinsAfter;
                y2 += 2*thirdOfWidth;
                x3 += 2*thirdOfJoinsAfter;
                y3 += 2*thirdOfWidth;
                x4 -= 2*thirdOfJoinsBefore;
                y4 += 2*thirdOfWidth;
                x1 -= 2*thirdOfJoinsBefore;
                y1 += 2*thirdOfWidth;
                break;
            case RIGHT:
                x2 += 2*thirdOfWidth;
                y2 -= 2*thirdOfJoinsAfter;
                x3 += 2*thirdOfWidth;
                y3 -= 2*thirdOfJoinsAfter;
                x4 += 2*thirdOfWidth;
                y4 += 2*thirdOfJoinsBefore;
                x1 += 2*thirdOfWidth;
                y1 += 2*thirdOfJoinsBefore;
                break;
            case BOTTOM:
                x2 -= 2*thirdOfJoinsAfter;
                y2 -= 2*thirdOfWidth;
                x3 -= 2*thirdOfJoinsAfter;
                y3 -= 2*thirdOfWidth;
                x4 += 2*thirdOfJoinsBefore;
                y4 -= 2*thirdOfWidth;
                x1 += 2*thirdOfJoinsBefore;
                y1 -= 2*thirdOfWidth;
                break;
            case LEFT:
                x2 -= 2*thirdOfWidth;
                y2 += 2*thirdOfJoinsAfter;
                x3 -= 2*thirdOfWidth;
                y3 += 2*thirdOfJoinsAfter;
                x4 -= 2*thirdOfWidth;
                y4 -= 2*thirdOfJoinsBefore;
                x1 -= 2*thirdOfWidth;
                y1 -= 2*thirdOfJoinsBefore;
                break;
        }

        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();
    }
}

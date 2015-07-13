package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public abstract class Border3D extends Border{
    public Border3D(Color color, float width) {
        super(color, width);
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float joinAreaBefore, float joinAreaAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;
        float widthHalf = width / 2;
        float halfOfJoinsBefore = joinAreaBefore / 2;
        float halfOfJoinsAfter = joinAreaAfter / 2;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2);
        switch (borderSide) {
            case TOP:
                x3 = x2 + halfOfJoinsAfter;
                y3 = y2 + widthHalf;
                x4 = x1 - halfOfJoinsBefore;
                y4 = y1 + widthHalf;
                break;
            case RIGHT:
                x3 = x2 + widthHalf;
                y3 = y2 - halfOfJoinsAfter;
                x4 = x1 + widthHalf;
                y4 = y1 + halfOfJoinsBefore;
                break;
            case BOTTOM:
                x3 = x2 - halfOfJoinsAfter;
                y3 = y2 - widthHalf;
                x4 = x1 + halfOfJoinsBefore;
                y4 = y1 - widthHalf;
                break;
            case LEFT:
                x3 = x2 - widthHalf;
                y3 = y2 + halfOfJoinsAfter;
                x4 = x1 - widthHalf;
                y4 = y1 - halfOfJoinsBefore;
                break;
        }

        setInnerHalfColor(canvas, borderSide);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();

        switch (borderSide) {
            case TOP:
                x2 += joinAreaAfter;
                y2 += width;
                x1 -= joinAreaBefore;
                y1 += width;
                break;
            case RIGHT:
                x2 += width;
                y2 -= joinAreaAfter;
                x1 += width;
                y1 += joinAreaBefore;
                break;
            case BOTTOM:
                x2 -= joinAreaAfter;
                y2 -= width;
                x1 += joinAreaBefore;
                y1 -= width;
                break;
            case LEFT:
                x2 -= width;
                y2 += joinAreaAfter;
                x1 -= width;
                y1 -= joinAreaBefore;
                break;
        }

        setOuterHalfColor(canvas, borderSide);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();
    }

    protected abstract void setInnerHalfColor(PdfCanvas canvas, Side side);

    protected abstract void setOuterHalfColor(PdfCanvas canvas, Side side);
}

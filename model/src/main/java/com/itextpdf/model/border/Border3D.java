package com.itextpdf.model.border;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;

public abstract class Border3D extends Border{
    public static final DeviceRgb gray = new DeviceRgb(212, 208, 200);


    public Border3D(float width) {
        this(gray, width);
    }

    public Border3D(DeviceRgb color, float width) {
        super(color, width);
    }

    public Border3D(DeviceCmyk color, float width) {
        super(color, width);
    }

    public Border3D(DeviceGray color, float width) {
        super(color, width);
    }

    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;
        float widthHalf = width / 2;
        float halfOfWidthBefore = borderWidthBefore / 2;
        float halfOfWidthAfter = borderWidthAfter / 2;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2);
        switch (borderSide) {
            case TOP:
                x3 = x2 + halfOfWidthAfter;
                y3 = y2 + widthHalf;
                x4 = x1 - halfOfWidthBefore;
                y4 = y1 + widthHalf;
                break;
            case RIGHT:
                x3 = x2 + widthHalf;
                y3 = y2 - halfOfWidthAfter;
                x4 = x1 + widthHalf;
                y4 = y1 + halfOfWidthBefore;
                break;
            case BOTTOM:
                x3 = x2 - halfOfWidthAfter;
                y3 = y2 - widthHalf;
                x4 = x1 + halfOfWidthBefore;
                y4 = y1 - widthHalf;
                break;
            case LEFT:
                x3 = x2 - widthHalf;
                y3 = y2 + halfOfWidthAfter;
                x4 = x1 - widthHalf;
                y4 = y1 - halfOfWidthBefore;
                break;
        }

        setInnerHalfColor(canvas, borderSide);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();

        switch (borderSide) {
            case TOP:
                x2 += borderWidthAfter;
                y2 += width;
                x1 -= borderWidthBefore;
                y1 += width;
                break;
            case RIGHT:
                x2 += width;
                y2 -= borderWidthAfter;
                x1 += width;
                y1 += borderWidthBefore;
                break;
            case BOTTOM:
                x2 -= borderWidthAfter;
                y2 -= width;
                x1 += borderWidthBefore;
                y1 -= width;
                break;
            case LEFT:
                x2 -= width;
                y2 += borderWidthAfter;
                x1 -= width;
                y1 -= borderWidthBefore;
                break;
        }

        setOuterHalfColor(canvas, borderSide);
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

    protected Color getDarkerColor() {
        if (color instanceof DeviceRgb)
            return DeviceRgb.makeDarker((DeviceRgb)color);
        else if (color instanceof DeviceCmyk)
            return DeviceCmyk.makeDarker((DeviceCmyk)color);
        else if (color instanceof DeviceGray)
            return DeviceGray.makeDarker((DeviceGray)color);

        return color;
    }

    protected abstract void setInnerHalfColor(PdfCanvas canvas, Side side);

    protected abstract void setOuterHalfColor(PdfCanvas canvas, Side side);
}

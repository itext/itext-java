package com.itextpdf.model.border;

import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.color.DeviceCmyk;
import com.itextpdf.core.color.DeviceGray;
import com.itextpdf.core.color.DeviceRgb;

public class OutsetBorder extends Border3D {

    public OutsetBorder(float width) {
        super(width);
    }

    public OutsetBorder(DeviceRgb color, float width) {
        super(color, width);
    }

    public OutsetBorder(DeviceCmyk color, float width) {
        super(color, width);
    }

    public OutsetBorder(DeviceGray color, float width) {
        super(color, width);
    }

    @Override
    public int getType(){
        return Border._3D_OUTSET;
    }

    @Override
    protected void setInnerHalfColor(PdfCanvas canvas, Border.Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(getColor());
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(getDarkerColor());
                break;
        }
    }

    @Override
    protected void setOuterHalfColor(PdfCanvas canvas, Border.Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(getColor());
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(getDarkerColor());
                break;
        }
    }
}

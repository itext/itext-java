package com.itextpdf.model.border;

import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.color.DeviceCmyk;
import com.itextpdf.core.color.DeviceGray;
import com.itextpdf.core.color.DeviceRgb;

public class RidgeBorder extends Border3D {

    public RidgeBorder(float width) {
        super(width);
    }

    public RidgeBorder(DeviceRgb color, float width) {
        super(color, width);
    }

    public RidgeBorder(DeviceCmyk color, float width) {
        super(color, width);
    }

    public RidgeBorder(DeviceGray color, float width) {
        super(color, width);
    }

    @Override
    public int getType(){
        return Border._3D_RIDGE;
    }

    @Override
    protected void setInnerHalfColor(PdfCanvas canvas, Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(getDarkerColor());
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(getColor());
                break;
        }
    }

    @Override
    protected void setOuterHalfColor(PdfCanvas canvas, Side side) {
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

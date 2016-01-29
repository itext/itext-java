package com.itextpdf.model.border;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;

public class InsetBorder extends Border3D {

    public InsetBorder(float width) {
        super(width);
    }

    public InsetBorder(DeviceRgb color, float width) {
        super(color, width);
    }

    public InsetBorder(DeviceCmyk color, float width) {
        super(color, width);
    }

    public InsetBorder(DeviceGray color, float width) {
        super(color, width);
    }

    @Override
    public int getType(){
        return Border._3D_INSET;
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
                canvas.setFillColor(getDarkerColor());
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(getColor());
                break;
        }
    }
}

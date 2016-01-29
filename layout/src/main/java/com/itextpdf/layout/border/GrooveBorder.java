package com.itextpdf.layout.border;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;

public class GrooveBorder extends Border3D {

    public GrooveBorder(float width) {
        super(width);
    }

    public GrooveBorder(DeviceRgb color, float width) {
        super(color, width);
    }

    public GrooveBorder(DeviceCmyk color, float width) {
        super(color, width);
    }

    public GrooveBorder(DeviceGray color, float width) {
        super(color, width);
    }

    @Override
    public int getType(){
        return Border._3D_GROOVE;
    }

    @Override
    protected void setInnerHalfColor(PdfCanvas canvas, Side side) {
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

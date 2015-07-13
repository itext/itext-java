package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public class InsetBorder extends Border3D {

    public InsetBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    protected void setInnerHalfColor(PdfCanvas canvas, Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                //TODO make darker shade of color field
                canvas.setFillColor(Color.Gray);
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(color);
                break;
        }
    }

    @Override
    protected void setOuterHalfColor(PdfCanvas canvas, Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                //TODO make darker shade of color field
                canvas.setFillColor(Color.Gray);
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(color);
                break;
        }
    }
}

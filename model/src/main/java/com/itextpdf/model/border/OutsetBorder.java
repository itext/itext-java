package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public class OutsetBorder extends Border3D {

    public OutsetBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    protected void setInnerHalfColor(PdfCanvas canvas, Border.Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(color);
                break;
            case BOTTOM:
            case RIGHT:
                //TODO make darker shade of color field
                canvas.setFillColor(Color.Gray);
                break;
        }
    }

    @Override
    protected void setOuterHalfColor(PdfCanvas canvas, Border.Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(color);
                break;
            case BOTTOM:
            case RIGHT:
                //TODO make darker shade of color field
                canvas.setFillColor(Color.Gray);
                break;
        }
    }
}

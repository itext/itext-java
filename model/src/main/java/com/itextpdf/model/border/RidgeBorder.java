package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public class RidgeBorder extends Border3D {
    public RidgeBorder(Color color, float width) {
        super(color, width);
    }

    @Override
    protected void setInnerHalfColor(PdfCanvas canvas, Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                //TODO make darker shade of color field
                canvas.setFillColor(darkGray);
                break;
            case BOTTOM:
            case RIGHT:
                canvas.setFillColor(gray);
                break;
        }
    }

    @Override
    protected void setOuterHalfColor(PdfCanvas canvas, Side side) {
        switch (side) {
            case TOP:
            case LEFT:
                canvas.setFillColor(gray);
                break;
            case BOTTOM:
            case RIGHT:
                //TODO make darker shade of color field
                canvas.setFillColor(darkGray);
                break;
        }
    }
}

package com.itextpdf.canvas;

import com.itextpdf.canvas.colors.Color;
import com.itextpdf.canvas.colors.DeviceGray;

public class PdfGraphicsState {

    protected Color fillColor = DeviceGray.Black;
    protected Color strokeColor = DeviceGray.Black;

    public PdfGraphicsState() {

    }

    public PdfGraphicsState(final PdfGraphicsState source) {

    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }
}

package com.itextpdf.canvas;

import com.itextpdf.canvas.colors.Color;
import com.itextpdf.canvas.colors.DeviceGray;
import com.itextpdf.core.pdf.PdfName;

public class PdfGraphicsState {

    protected Color fillColor = DeviceGray.Black;
    protected Color strokeColor = DeviceGray.Black;

    /** This is the font size in use */
    float size;
    /** This is the font in use */
    PdfName fontName;
    /** This is the text rendering mode in use */
    protected int textRenderingMode;

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

package com.itextpdf.canvas.colors;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfDeviceCs;

public class Color {

    protected PdfColorSpace colorSpace;
    protected float[] colorValue;

    public Color(PdfObject pdfObject, float[] colorValue) throws PdfException {
        this(PdfColorSpace.makeColorSpace(pdfObject, null), colorValue);
    }

    public Color(PdfObject pdfObject) throws PdfException {
        this(pdfObject, null);
    }

    public Color(PdfColorSpace colorSpace, float[] colorValue) {
        this.colorSpace = colorSpace;
        if (colorValue == null)
            this.colorValue = new float[colorSpace.getNumOfComponents()];
        else
            this.colorValue = colorValue;
    }

    public Color(PdfColorSpace colorSpace) {
        this(colorSpace, null);
    }

    public int getNumOfComponents() {
        return colorValue.length;
    }

    public PdfColorSpace getColorSpace() {
        return colorSpace;
    }

    public float[] getColorValue() {
        return colorValue;
    }

}

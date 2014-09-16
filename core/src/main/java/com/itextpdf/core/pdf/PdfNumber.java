package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.OutputStream;

public class PdfNumber extends PdfPrimitiveObject {

    protected static final byte Int = 1;
    protected static final byte Double = 2;

    double value;
    byte valueType = -1;

    public PdfNumber(int value) {
        super(PdfObject.Number);
        this.value = value;
        this.valueType = Int;
    }

    public PdfNumber(long value) {
        super(PdfObject.Number);
        this.value = value;
        this.valueType = Double;
    }

    public PdfNumber(float value) {
        super(PdfObject.Number);
        this.value = value;
        this.valueType = Double;
    }

    public PdfNumber(double value) {
        super(PdfObject.Number);
        this.value = value;
        this.valueType = Double;
    }

    public PdfNumber(PdfDocument doc, int value) {
        super(doc, PdfObject.Number);
        this.value = value;
        this.valueType = Int;
    }

    public PdfNumber(PdfDocument doc, long value) {
        super(doc, PdfObject.Number);
        this.value = value;
        this.valueType = Int;
    }

    public PdfNumber(PdfDocument doc, float value) {
        super(doc, PdfObject.Number);
        this.value = value;
        this.valueType = Double;
    }

    public PdfNumber(PdfDocument doc, double value) {
        super(doc, PdfObject.Number);
        this.value = value;
        this.valueType = Double;
    }

    public double getValue() throws PdfException {
        if(java.lang.Double.isNaN(value))
            generateValue();
        return value;
    }

    public float getFloatValue() throws PdfException {
        return (float)getValue();
    }

    public long getLongValue() throws PdfException {
        return (long)getValue();
    }

    public int getIntValue() throws PdfException {
        return (int)getValue();
    }

    protected byte getValueType() {
        return valueType;
    }

    @Override
    public void generateContent() {
        switch (valueType) {
            case Int:
                content = OutputStream.getIsoBytes((int)value);
                break;
            case Double:
                content = OutputStream.getIsoBytes(value);
                break;
            default:
                content = new byte[0];
        }
    }

    @Override
    protected void generateValue() throws PdfException {

    }
}

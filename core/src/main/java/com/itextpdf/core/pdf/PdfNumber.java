package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.OutputStream;

public class PdfNumber extends PdfPrimitiveObject {

    protected static final byte Int = 1;
    protected static final byte Double = 2;

    double value;
    byte valueType;

    public PdfNumber(double value) {
        super();
        setValue(value);
    }

    public PdfNumber(int value) {
        super();
        setValue(value);
    }

    public PdfNumber(byte[] content) {
        super(content);
        this.valueType = Double;
        this.value = java.lang.Double.NaN;
    }

    private PdfNumber() {
        super();
    }

    @Override
    public byte getType() {
        return Number;
    }

    public double getValue() {
        if(java.lang.Double.isNaN(value))
            generateValue();
        return value;
    }

    public float getFloatValue() {
        return (float)getValue();
    }

    public long getLongValue() {
        return (long)getValue();
    }

    public int getIntValue() {
        return (int)getValue();
    }

    public void setValue(int value) {
        this.value = value;
        this.valueType = Int;
        this.content = null;
    }

    public void setValue(double value) {
        this.value = value;
        this.valueType = Double;
        this.content = null;
    }

    public void increment() {
        this.value++;
    }

    public void decrement() {
        this.value++;
    }

    @Override
    public String toString() {
        if (valueType == Int)
            return new String(OutputStream.getIsoBytes(getIntValue()));
        else
            return new String(OutputStream.getIsoBytes(getValue()));
    }

    @Override
    protected PdfNumber newInstance() {
        return new PdfNumber();
    }

    protected byte getValueType() {
        return valueType;
    }

    @Override
    protected void generateContent() {
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

    protected void generateValue() {
        try {
            value = java.lang.Double.parseDouble(new String(content));
        } catch (NumberFormatException e) {
            value = java.lang.Double.NaN;
        }
        valueType = Double;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        super.copyContent(from, document);
        PdfNumber number = (PdfNumber)from;
        value = number.value;
        valueType = number.valueType;
    }

}
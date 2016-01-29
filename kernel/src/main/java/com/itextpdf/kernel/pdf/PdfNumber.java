package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.OutputStream;

public class PdfNumber extends PdfPrimitiveObject {

    protected static final byte Int = 1;
    protected static final byte Double = 2;

    private double value;
    private byte valueType;

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
    public int getType() {
        return Number;
    }

    public double getValue() {
        if (java.lang.Double.isNaN(value))
            generateValue();
        return value;
    }

    public float getFloatValue() {
        return (float) getValue();
    }

    public long getLongValue() {
        return (long) getValue();
    }

    public int getIntValue() {
        return (int) getValue();
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
        setValue(++value);
    }

    public void decrement() {
        setValue(--value);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfNumber makeIndirect(PdfDocument document) {
        return super.makeIndirect(document);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfNumber makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        return super.makeIndirect(document, reference);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfNumber copyToDocument(PdfDocument document) {
        return super.copyToDocument(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfNumber copyToDocument(PdfDocument document, boolean allowDuplicating) {
        return super.copyToDocument(document, allowDuplicating);
    }

    @Override
    public String toString() {
        if (content != null)
            return new String(content);
        else if (valueType == Int)
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
                content = OutputStream.getIsoBytes((int) value);
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
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfNumber number = (PdfNumber) from;
        value = number.value;
        valueType = number.valueType;
    }

}
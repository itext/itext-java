package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.OutputStream;

public class PdfString extends PdfPrimitiveObject {

    protected String value = null;

    public PdfString(String value) {
        super();
        this.value = value;
    }

    public String getValue() throws PdfException {
        if (value == null)
            generateValue();
        return value;
    }

    @Override
    public byte getType() {
        return String;
    }

    @Override
    protected void generateValue() throws PdfException {

    }

    @Override
    protected void generateContent() {
        content = OutputStream.getIsoBytes((byte)'(', value, (byte)')');
    }

    @Override
    public String toString() {
        return value;
    }
}

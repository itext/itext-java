package com.itextpdf.core.pdf;

import com.itextpdf.io.streams.OutputStream;

public class PdfBoolean extends PdfPrimitiveObject {

    public static final PdfBoolean PdfTrue = new PdfBoolean(true);
    public static final PdfBoolean PdfFalse = new PdfBoolean(false);

    private static final byte[] True = OutputStream.getIsoBytes("true");
    private static final byte[] False = OutputStream.getIsoBytes("false");

    private boolean value;

    public PdfBoolean(boolean value) {
        super();
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public byte getType() {
        return Boolean;
    }

    protected void generateContent() {
        content = value ? True : False;
    }

    @Override
    public String toString() {
        return java.lang.Boolean.toString(value);
    }
}

package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.OutputStream;

public class PdfBoolean extends PdfPrimitiveObject {

    public static final PdfBoolean PdfTrue = new PdfBoolean(true);
    public static final PdfBoolean PdfFalse = new PdfBoolean(false);

    private static final byte[] True = OutputStream.getIsoBytes("true");
    private static final byte[] False = OutputStream.getIsoBytes("false");

    private byte value = -1;

    public PdfBoolean(boolean value) {
        super(PdfObject.Boolean);
        this.value = value ? (byte)1 : 0;
    }

    public PdfBoolean(PdfDocument doc, boolean value) {
        super(doc, PdfObject.Boolean);
        this.value = value ? (byte)1 : (byte)0;
    }

    public boolean getValue() throws PdfException {
        if (value == -1)
            generateValue();
        return value == 1;
    }

    @Override
    protected void generateValue() throws PdfException {

    }

    @Override
    protected void generateContent() {
        content = value == 1 ? True : False;
    }
}

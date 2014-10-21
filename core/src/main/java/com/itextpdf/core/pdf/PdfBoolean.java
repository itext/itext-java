package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.streams.OutputStream;

public class PdfBoolean extends PdfPrimitiveObject {

    public static final PdfBoolean PdfTrue = new PdfBoolean(true, true);
    public static final PdfBoolean PdfFalse = new PdfBoolean(false, true);

    private static final byte[] True = OutputStream.getIsoBytes("true");
    private static final byte[] False = OutputStream.getIsoBytes("false");

    private boolean value;

    public PdfBoolean(boolean value) {
        this(value, false);
    }

    private PdfBoolean(boolean value, boolean directOnly) {
        super(directOnly);
        this.value = value;
    }

    private PdfBoolean() {
        super();
    }

    public boolean getValue() {
        return value;
    }

    public byte getType() {
        return Boolean;
    }

    @Override
    public String toString() {
        return java.lang.Boolean.toString(value);
    }

    @Override
    protected void generateContent() {
        content = value ? True : False;
    }

    @Override
    protected PdfBoolean newInstance() {
        return new PdfBoolean();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        super.copyContent(from, document);
        PdfBoolean bool = (PdfBoolean)from;
        value = bool.value;
    }
}

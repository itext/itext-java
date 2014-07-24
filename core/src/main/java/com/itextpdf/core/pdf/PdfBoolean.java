package com.itextpdf.core.pdf;

public class PdfBoolean extends PdfObject {

    protected boolean value = false;

    public PdfBoolean(boolean value) {
        super(PdfObject.Boolean);
        this.value = value;
    }

    public PdfBoolean(PdfDocument doc, boolean value) {
        super(doc, PdfObject.Boolean);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}

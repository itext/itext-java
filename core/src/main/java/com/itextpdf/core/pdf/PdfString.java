package com.itextpdf.core.pdf;

public class PdfString extends PdfObject {

    protected String value;

    public PdfString(String value) {
        super(PdfObject.String);
        this.value = value;
    }

    public PdfString(PdfDocument doc, String value) {
        super(doc, PdfObject.String);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

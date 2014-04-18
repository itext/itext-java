package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.pdf.PdfDocument;

public class PdfBoolean extends PdfObject {

    protected boolean value = false;

    public PdfBoolean(boolean value) {
        super();
        this.value = value;
    }

    public PdfBoolean(PdfDocument doc, boolean value) {
        super(doc);
        this.value = value;
    }

}

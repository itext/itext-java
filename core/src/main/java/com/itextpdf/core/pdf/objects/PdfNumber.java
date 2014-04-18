package com.itextpdf.core.pdf.objects;

public class PdfNumber extends PdfObject {

    float value = 0;

    public PdfNumber(float value) {
        super();
        this.value = value;
    }

    public PdfNumber(int value) {
        this((float) value);
    }


}

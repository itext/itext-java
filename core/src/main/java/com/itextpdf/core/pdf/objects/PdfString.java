package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.pdf.PdfDocument;

public class PdfString extends PdfObject {

    public PdfString(String text) {
        super();
    }

    public PdfString(PdfDocument doc, String text) {
        super(doc);
    }

}

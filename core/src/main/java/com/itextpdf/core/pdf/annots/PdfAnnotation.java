package com.itextpdf.core.pdf.annots;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;

public class PdfAnnotation extends PdfDictionary {

    public PdfAnnotation() {
        super();
    }

    public PdfAnnotation(PdfDocument doc) {
        super(doc);
    }

}

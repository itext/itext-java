package com.itextpdf.core.pdf.annots;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.objects.PdfDictionary;

public class PdfAnnotation extends PdfDictionary {

    public PdfAnnotation() {
        super();
    }

    public PdfAnnotation(PdfDocument doc) {
        super(doc);
    }

}

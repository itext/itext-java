package com.itextpdf.core.pdf.navigation;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.objects.PdfString;

public class PdfStringDestination extends PdfString implements IPdfDestination {

    public PdfStringDestination(String text) {
        super(text);
    }

    public PdfStringDestination(PdfDocument doc, String text) {
        super(doc, text);
    }

}

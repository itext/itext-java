package com.itextpdf.core.pdf.navigation;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfString;

public class PdfStringDestination extends PdfString implements IPdfDestination {

    public PdfStringDestination(String text) {
        super(text);
    }

}

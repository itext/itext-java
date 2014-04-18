package com.itextpdf.core.pdf.filespec;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.objects.PdfString;

public class PdfStringFS extends PdfString implements IPdfFileSpec {

    public PdfStringFS(String text) {
        super(text);
    }

    public PdfStringFS(PdfDocument doc, String text) {
        super(doc, text);
    }

}

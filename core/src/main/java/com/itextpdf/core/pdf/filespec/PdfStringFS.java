package com.itextpdf.core.pdf.filespec;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfString;

public class PdfStringFS extends PdfString implements IPdfFileSpec {

    public PdfStringFS(String text) {
        super(text);
    }

}

package com.itextpdf.core.pdf.filespec;

import com.itextpdf.core.pdf.PdfString;

public class PdfStringFS extends PdfFileSpec<PdfString> {

    public PdfStringFS(String string) {
        super(new PdfString(string));
    }

    public PdfStringFS(PdfString pdfObject) {
        super(pdfObject);
    }

}

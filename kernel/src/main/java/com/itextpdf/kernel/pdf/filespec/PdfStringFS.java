package com.itextpdf.kernel.pdf.filespec;

import com.itextpdf.kernel.pdf.PdfString;

public class PdfStringFS extends PdfFileSpec<PdfString> {

    public PdfStringFS(String string) {
        super(new PdfString(string));
    }

    public PdfStringFS(PdfString pdfObject) {
        super(pdfObject);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

}

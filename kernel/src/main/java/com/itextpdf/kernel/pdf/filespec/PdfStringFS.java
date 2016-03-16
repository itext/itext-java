package com.itextpdf.kernel.pdf.filespec;

import com.itextpdf.kernel.pdf.PdfString;

public class PdfStringFS extends PdfFileSpec<PdfString> {

    private static final long serialVersionUID = 3440302276954369264L;

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

package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;

public class PdfMcrNumber extends PdfMcr<PdfNumber> {

    private static final long serialVersionUID = -9039654592261202430L;

	public PdfMcrNumber(PdfNumber pdfObject, PdfStructElem parent) {
        super(pdfObject, parent);
    }

    public PdfMcrNumber(PdfPage page, PdfStructElem parent) {
        super(new PdfNumber(page.getNextMcid()), parent);
    }

    @Override
    public Integer getMcid() {
        return ((PdfNumber) getPdfObject()).getIntValue();
    }

    @Override
    public PdfDictionary getPageObject() {
        PdfDictionary page = parent.getPdfObject().getAsDictionary(PdfName.Pg);
        return page;
    }
}

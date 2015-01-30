package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfPage;

public class PdfMcrNumber extends PdfMcr<PdfNumber> {

    public PdfMcrNumber(PdfNumber pdfObject, PdfStructElem parent) {
        super(pdfObject, parent);
    }

    public PdfMcrNumber(PdfPage page, PdfStructElem parent) throws PdfException {
        super(new PdfNumber(page.getNextMcid()), parent);
    }

    @Override
    public Integer getMcid() throws PdfException {
        return ((PdfNumber) getPdfObject()).getIntValue();
    }

    @Override
    protected PdfDictionary getPageObject() throws PdfException {
        PdfDictionary page = parent.getPdfObject().getAsDictionary(PdfName.Pg);
        return page;
    }
}

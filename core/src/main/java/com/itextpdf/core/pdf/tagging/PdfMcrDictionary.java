package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfPage;

public class PdfMcrDictionary extends PdfMcr<PdfDictionary> {

    public PdfMcrDictionary(PdfDictionary pdfObject, PdfStructElem parent) {
        super(pdfObject, parent);
    }

    public PdfMcrDictionary(PdfPage page, PdfStructElem parent) throws PdfException {
        super(new PdfDictionary(), parent);
        ((PdfDictionary)getPdfObject()).put(PdfName.Type, PdfName.MCR);
        ((PdfDictionary)getPdfObject()).put(PdfName.Pg, page.getPdfObject());
        ((PdfDictionary)getPdfObject()).put(PdfName.MCID, new PdfNumber(page.getNextMcid()));
    }

    @Override
    public Integer getMcid() throws PdfException {
        return ((PdfDictionary)getPdfObject()).getAsNumber(PdfName.MCID).getIntValue();
    }

    @Override
    protected PdfDictionary getPageObject() throws PdfException {
        PdfDictionary page = ((PdfDictionary)getPdfObject()).getAsDictionary(PdfName.Pg);
        if (page == null)
            page = parent.getPdfObject().getAsDictionary(PdfName.Pg);
        return page;
    }
}

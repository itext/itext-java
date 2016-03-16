package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;

public class PdfMcrDictionary extends PdfMcr<PdfDictionary> {

    private static final long serialVersionUID = 3562443854685749324L;

	public PdfMcrDictionary(PdfDictionary pdfObject, PdfStructElem parent) {
        super(pdfObject, parent);
    }

    public PdfMcrDictionary(PdfPage page, PdfStructElem parent) {
        super(new PdfDictionary(), parent);
        PdfDictionary dict = (PdfDictionary) getPdfObject();
        dict.put(PdfName.Type, PdfName.MCR);
        dict.put(PdfName.Pg, page.getPdfObject());
        dict.put(PdfName.MCID, new PdfNumber(page.getNextMcid()));
    }

    @Override
    public Integer getMcid() {
        return ((PdfDictionary)getPdfObject()).getAsNumber(PdfName.MCID).getIntValue();
    }

    @Override
    public PdfDictionary getPageObject() {
        PdfDictionary page = ((PdfDictionary)getPdfObject()).getAsDictionary(PdfName.Pg);
        if (page == null)
            page = parent.getPdfObject().getAsDictionary(PdfName.Pg);
        return page;
    }
}

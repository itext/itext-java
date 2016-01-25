package com.itextpdf.core.pdf.tagging;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.annot.PdfAnnotation;

public class PdfObjRef extends PdfMcr<PdfDictionary> {

    public PdfObjRef(PdfDictionary pdfObject, PdfStructElem parent) {
        super(pdfObject, parent);
    }

    public PdfObjRef(PdfAnnotation annot, PdfStructElem parent) {
        super(new PdfDictionary(), parent);
        PdfDictionary dict = (PdfDictionary) getPdfObject();
        dict.put(PdfName.Type, PdfName.OBJR);
        dict.put(PdfName.Obj, annot.tag(parent.getDocument()).getPdfObject());
    }

    @Override
    public Integer getMcid() {
        return null;
    }

    @Override
    public PdfDictionary getPageObject() {
        PdfDictionary page = ((PdfDictionary)getPdfObject()).getAsDictionary(PdfName.Pg);
        if (page == null)
            page = parent.getPdfObject().getAsDictionary(PdfName.Pg);
        return page;
    }

}

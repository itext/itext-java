package com.itextpdf.core.pdf.tagging;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;

public class PdfObjRef extends PdfMcr<PdfDictionary> {

    public PdfObjRef(PdfDictionary pdfObject, PdfStructElem parent) {
        super(pdfObject, parent);
        parent.getDocument().getStructTreeRoot().registerObjRef(this);
    }

    public PdfObjRef(PdfAnnotation annot, PdfStructElem parent) {
        super(new PdfDictionary(), parent);
        ((PdfDictionary) getPdfObject()).put(PdfName.Type, PdfName.OBJR);
        ((PdfDictionary) getPdfObject()).put(PdfName.Obj, annot.tag().getPdfObject());
        parent.getDocument().getStructTreeRoot().registerObjRef(this);
    }

    @Override
    public Integer getMcid() {
        return 0;
    }

    @Override
    protected PdfDictionary getPageObject() {
        PdfDictionary page = parent.getPdfObject().getAsDictionary(PdfName.Pg);
        return page;
    }


}

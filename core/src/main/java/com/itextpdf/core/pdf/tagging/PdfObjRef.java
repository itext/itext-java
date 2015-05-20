package com.itextpdf.core.pdf.tagging;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfAnnotation;

public class PdfObjRef extends PdfMcr<PdfDictionary> {

    public PdfObjRef(PdfDictionary pdfObject, PdfStructElem parent) throws PdfException {
        super(pdfObject, parent);
        parent.getDocument().getStructTreeRoot().registerObjRef(this);
    }

    public PdfObjRef(PdfAnnotation annot, PdfStructElem parent) throws PdfException {
        super(new PdfDictionary(), parent);
        ((PdfDictionary) getPdfObject()).put(PdfName.Type, PdfName.OBJR);
        ((PdfDictionary) getPdfObject()).put(PdfName.Obj, annot.tag().getPdfObject());
        parent.getDocument().getStructTreeRoot().registerObjRef(this);
    }

    @Override
    public Integer getMcid() throws PdfException {
        return 0;
    }

    @Override
    protected PdfDictionary getPageObject() throws PdfException {
        PdfDictionary page = parent.getPdfObject().getAsDictionary(PdfName.Pg);
        return page;
    }


}

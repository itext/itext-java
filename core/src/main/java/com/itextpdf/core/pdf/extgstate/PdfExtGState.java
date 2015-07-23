package com.itextpdf.core.pdf.extgstate;

import com.itextpdf.core.pdf.*;

public class PdfExtGState extends PdfObjectWrapper<PdfDictionary> {

    public PdfExtGState(PdfDictionary pdfObject) {
        super(pdfObject);
        mustBeIndirect();
    }

    public PdfExtGState() {
        this(new PdfDictionary());
    }

//    @Override
//    public PdfExtGState copy(PdfDocument document) {
//        return new PdfExtGState((PdfDictionary)getPdfObject().copy(document), document);
//    }

}

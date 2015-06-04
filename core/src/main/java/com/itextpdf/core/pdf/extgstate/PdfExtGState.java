package com.itextpdf.core.pdf.extgstate;

import com.itextpdf.core.pdf.*;

public class PdfExtGState extends PdfObjectWrapper<PdfDictionary> {

    public PdfExtGState(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    public PdfExtGState(PdfDocument document) {
        this(new PdfDictionary(), document);
    }

//    @Override
//    public PdfExtGState copy(PdfDocument document) {
//        return new PdfExtGState((PdfDictionary)getPdfObject().copy(document), document);
//    }

}

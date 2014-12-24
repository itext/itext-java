package com.itextpdf.core.pdf.extgstate;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

public class PdfExtGState extends PdfObjectWrapper<PdfDictionary> {

    public PdfExtGState(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    public PdfExtGState(PdfDocument document) throws PdfException {
        this(new PdfDictionary(), document);
    }

//    @Override
//    public PdfExtGState copy(PdfDocument document) throws PdfException {
//        return new PdfExtGState((PdfDictionary)getPdfObject().copy(document), document);
//    }

}

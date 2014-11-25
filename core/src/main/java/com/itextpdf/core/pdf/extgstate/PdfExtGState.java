package com.itextpdf.core.pdf.extgstate;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

public class PdfExtGState extends PdfObjectWrapper<PdfDictionary> {

    public PdfExtGState(PdfDocument document) {
        this(new PdfDictionary(), document);
    }

    public PdfExtGState(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    @Override
    public PdfExtGState copy(PdfDocument document) throws PdfException {
        return new PdfExtGState((PdfDictionary)getPdfObject().copy(document), document);
    }

}

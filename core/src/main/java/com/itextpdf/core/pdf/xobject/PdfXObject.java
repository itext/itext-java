package com.itextpdf.core.pdf.xobject;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfStream;

public class PdfXObject extends PdfObjectWrapper<PdfStream> {

    public PdfXObject(PdfDocument document) throws PdfException {
        this(new PdfStream(document), document);
    }

    public PdfXObject(PdfStream pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    static public PdfXObject makeXObject(PdfStream stream, PdfDocument document) {
        return null;
    }

}

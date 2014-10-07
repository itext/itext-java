package com.itextpdf.core.pdf;

public class PdfImageXObject extends PdfObjectWrapper<PdfStream> implements IPdfXObject {

    public PdfImageXObject(PdfDocument document) {
        this(new PdfStream(document), document);
    }

    public PdfImageXObject(PdfStream pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

}

package com.itextpdf.core.pdf.xobject;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfStream;

public class PdfImageXObject extends PdfXObject {

    public PdfImageXObject(PdfDocument document) {
        this(new PdfStream(document), document);
    }

    public PdfImageXObject(PdfStream pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

}

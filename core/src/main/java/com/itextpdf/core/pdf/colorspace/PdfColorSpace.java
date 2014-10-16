package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfStream;

abstract public class PdfColorSpace<T extends PdfObject> extends PdfObjectWrapper {

    public PdfColorSpace(T pdfObject) {
        super(pdfObject);
    }

    public PdfColorSpace(T pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    abstract public int getNumOfComponents();

    static public PdfColorSpace makeColorSpace(PdfObject object, PdfDocument document) {
        return null;
    }

}

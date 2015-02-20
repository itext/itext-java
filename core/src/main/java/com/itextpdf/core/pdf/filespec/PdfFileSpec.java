package com.itextpdf.core.pdf.filespec;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfObjectWrapper;

public class PdfFileSpec<T extends PdfObject> extends PdfObjectWrapper<T>  {

    public PdfFileSpec(T pdfObject) {
        super(pdfObject);
    }

    public PdfFileSpec(T pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }
}

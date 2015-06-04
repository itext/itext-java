package com.itextpdf.core.pdf.navigation;

import com.itextpdf.core.pdf.*;

import java.util.HashMap;

public abstract class PdfDestination<T extends PdfObject> extends PdfObjectWrapper<T> {

    public PdfDestination(T pdfObject) {
        super(pdfObject);
    }

    public PdfDestination(T pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    public abstract PdfObject getDestinationPage(HashMap<Object, PdfObject> names);

    public abstract PdfDestination replaceNamedDestination(HashMap<Object, PdfObject> names);

    public static PdfDestination makeDestination(PdfObject pdfObject) {

        if (pdfObject.getType() == PdfObject.String)
            return  new PdfStringDestination((PdfString) pdfObject);
        else if (pdfObject.getType() == PdfObject.Name)
            return new PdfNamedDestination((PdfName) pdfObject);
        else if (pdfObject.getType() == PdfObject.Array)
            return new PdfExplicitDestination((PdfArray) pdfObject);
        else
            throw new UnsupportedOperationException();
    }
}

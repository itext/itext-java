package com.itextpdf.core.pdf.navigation;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

import java.util.HashMap;

public class PdfNamedDestination extends PdfDestination<PdfName> {

    public PdfNamedDestination(String name) {
        this(new PdfName(name));
    }

    public PdfNamedDestination(PdfName pdfObject) {
        super(pdfObject);
    }

    public PdfNamedDestination(PdfName pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    @Override
    public PdfObject getDestinationPage(final HashMap<Object, PdfObject> names) throws PdfException {
        PdfArray array = (PdfArray) names.get(getPdfObject());

        return array != null ? array.get(0, false) : null;
    }

    @Override
     public PdfDestination replaceNamedDestination(final HashMap<Object, PdfObject> names){

        PdfArray array = (PdfArray) names.get(getPdfObject());
        if (array != null){
            return PdfDestination.makeDestination(array);
        }
        return null;
    }
}

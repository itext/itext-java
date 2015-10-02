package com.itextpdf.core.pdf.navigation;

import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfString;

import java.util.HashMap;

public class PdfStringDestination extends PdfDestination<PdfString> {

    public PdfStringDestination(String string) {
        this(new PdfString(string));
    }

    public PdfStringDestination(PdfString pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfObject getDestinationPage(HashMap<Object, PdfObject> names) {
        PdfArray array = (PdfArray) names.get(getPdfObject().toUnicodeString());

        return array != null ? array.get(0, false) : null;
    }

    @Override
    public PdfDestination replaceNamedDestination(final HashMap<Object, PdfObject> names) {

        PdfArray array = (PdfArray) names.get(getPdfObject().toUnicodeString());
        if (array != null){
            return PdfDestination.makeDestination(array);
        }
        return null;
    }
}

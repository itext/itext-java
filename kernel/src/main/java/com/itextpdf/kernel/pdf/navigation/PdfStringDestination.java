package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.Map;

public class PdfStringDestination extends PdfDestination<PdfString> {

    public PdfStringDestination(String string) {
        this(new PdfString(string));
    }

    public PdfStringDestination(PdfString pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfObject getDestinationPage(Map<Object, PdfObject> names) {
        PdfArray array = (PdfArray) names.get(getPdfObject().toUnicodeString());

        return array != null ? array.get(0, false) : null;
    }

    @Override
    public PdfDestination replaceNamedDestination(final Map<Object, PdfObject> names) {

        PdfArray array = (PdfArray) names.get(getPdfObject().toUnicodeString());
        if (array != null){
            return PdfDestination.makeDestination(array);
        }
        return null;
    }
}

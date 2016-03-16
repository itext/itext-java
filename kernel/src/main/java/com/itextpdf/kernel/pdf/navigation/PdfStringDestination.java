package com.itextpdf.kernel.pdf.navigation;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.Map;

public class PdfStringDestination extends PdfDestination<PdfString> {

    private static final long serialVersionUID = -5949596673571485743L;

	public PdfStringDestination(String string) {
        this(new PdfString(string));
    }

    public PdfStringDestination(PdfString pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfObject getDestinationPage(Map<String, PdfObject> names) {
        PdfArray array = (PdfArray) names.get(getPdfObject().toUnicodeString());

        return array != null ? array.get(0) : null;
    }

    @Override
    public PdfDestination replaceNamedDestination(final Map<Object, PdfObject> names) {
        PdfArray array = (PdfArray) names.get(getPdfObject().toUnicodeString());
        if (array != null){
            return PdfDestination.makeDestination(array);
        }
        return null;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}

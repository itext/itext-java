package com.itextpdf.forms.formfields;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

public class PdfChoiceFormField extends PdfFormField {

    public PdfChoiceFormField(PdfDocument pdfDocument, PdfWidgetAnnotation widget) {
        super(pdfDocument, widget);
    }

    protected PdfChoiceFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfChoiceFormField(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    @Override
    public PdfName getFormType() {
        return PdfName.Ch;
    }

    @Override
    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }
}

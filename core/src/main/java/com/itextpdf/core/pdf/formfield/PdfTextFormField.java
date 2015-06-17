package com.itextpdf.core.pdf.formfield;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

public class PdfTextFormField extends PdfFormField {

    protected PdfTextFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfTextFormField(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    public PdfTextFormField(PdfDocument pdfDocument, PdfWidgetAnnotation widget) {
        super(pdfDocument, widget);
    }

    @Override
    public PdfName getFormType() {
        return PdfName.Tx;
    }

    @Override
    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }
}

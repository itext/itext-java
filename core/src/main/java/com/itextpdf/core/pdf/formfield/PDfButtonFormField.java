package com.itextpdf.core.pdf.formfield;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

public class PdfButtonFormField extends PdfFormField {

    protected PdfButtonFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfButtonFormField(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    public PdfButtonFormField(PdfDocument pdfDocument, PdfWidgetAnnotation widget) {
        super(pdfDocument, widget);
    }

    @Override
    public PdfName getFormType() {
        return PdfName.Btn;
    }

    @Override
    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }
}

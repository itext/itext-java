package com.itextpdf.forms.formfields;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

public class PdfSignatureFormField extends PdfFormField {

    public PdfSignatureFormField() {
        super();
    }

    public PdfSignatureFormField(PdfWidgetAnnotation widget) {
        super(widget);
    }

    protected PdfSignatureFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getFormType() {
        return PdfName.Sig;
    }

    @Override
    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }
}

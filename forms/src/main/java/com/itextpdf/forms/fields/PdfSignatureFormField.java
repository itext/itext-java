package com.itextpdf.forms.fields;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.forms.PdfSigFieldLockDictionary;

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

    public <T extends PdfFormField> T setValue(PdfObject value) {
        return put(PdfName.V, value);
    }

    public PdfSigFieldLockDictionary getSigFieldLockDictionary() {
        PdfDictionary sigLockDict = (PdfDictionary) getPdfObject().get(PdfName.Lock);
        return sigLockDict == null? null : new PdfSigFieldLockDictionary(sigLockDict);
    }
}

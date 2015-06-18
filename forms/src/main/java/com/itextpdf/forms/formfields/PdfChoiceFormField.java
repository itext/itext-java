package com.itextpdf.forms.formfields;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.annot.PdfWidgetAnnotation;

public class PdfChoiceFormField extends PdfFormField {

    public static final int COMBO = 1 << 17;
    public static final int EDIT = 1 << 18;
    public static final int SORT = 1 << 19;
    public static final int MULTI_SELECT = 1 << 21;
    public static final int DO_NOT_SPELL_CHECK = 1 << 22;
    public static final int COMMIT_ON_SEL_CHANGE = 1 << 26;

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
    public PdfChoiceFormField setValue(PdfObject value) {
        return put(PdfName.V, value);
    }

    public PdfChoiceFormField setTopIndex(int index) {
        return put(PdfName.TI, new PdfNumber(index));
    }

    public PdfNumber getTopIndex() {
        return getPdfObject().getAsNumber(PdfName.TI);
    }

    public PdfChoiceFormField setIndices(PdfArray indices) {
        return put(PdfName.I, indices);
    }

    public PdfArray getIndices() {
        return getPdfObject().getAsArray(PdfName.I);
    }
}

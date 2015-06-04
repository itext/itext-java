package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

import java.util.Set;

public class PdfAnnotationAppearance extends PdfObjectWrapper<PdfDictionary> {

    public PdfAnnotationAppearance(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfAnnotationAppearance(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    public PdfAnnotationAppearance() {
        this(new PdfDictionary());
    }

    public PdfAnnotationAppearance(PdfDocument pdfDocument) {
        this(new PdfDictionary(), pdfDocument);
    }

    public PdfAnnotationAppearance setState(PdfName stateName, PdfFormXObject state) {
        return put(stateName, state);
    }

    public PdfAnnotationAppearance setStateObject(PdfName stateName, PdfStream state) {
        return put(stateName, state);
    }

    public PdfStream getStateObject(PdfName stateName) {
        return getPdfObject().getAsStream(stateName);
    }

    public Set<PdfName> getStates() {
        return getPdfObject().keySet();
    }


}

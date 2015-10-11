package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

import java.util.Set;

public class PdfAnnotationAppearance extends PdfObjectWrapper<PdfDictionary> {

    public PdfAnnotationAppearance(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfAnnotationAppearance() {
        this(new PdfDictionary());
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

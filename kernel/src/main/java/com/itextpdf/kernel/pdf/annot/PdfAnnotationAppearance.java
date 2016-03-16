package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.util.Set;

public class PdfAnnotationAppearance extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 6989855812604521083L;

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

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}

package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

public class PdfFixedPrint extends PdfObjectWrapper<PdfDictionary> {

    public PdfFixedPrint() {
        this(new PdfDictionary());
    }

    public PdfFixedPrint(PdfDictionary pdfObject) {
        super(pdfObject);
        pdfObject.put(PdfName.Type, PdfName.FixedPrint);
    }

    public PdfFixedPrint setMatrix(PdfArray matrix){
        return put(PdfName.Matrix, matrix);
    }

    public PdfFixedPrint setMatrix(float[] matrix) {
        return put(PdfName.Matrix, new PdfArray(matrix));
    }

    public PdfFixedPrint setHorizontalTranslation(float horizontal){
        return put(PdfName.H, new PdfNumber(horizontal));
    }

    public PdfFixedPrint setVerticalTranslation(float vertical){
        return put(PdfName.V, new PdfNumber(vertical));
    }

    public PdfArray getMatrix() {
        return getPdfObject().getAsArray(PdfName.Matrix);
    }

    public PdfNumber getHorizontalTranslation() {
        return getPdfObject().getAsNumber(PdfName.H);
    }

    public PdfNumber getVerticalTranslation() {
        return getPdfObject().getAsNumber(PdfName.V);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}

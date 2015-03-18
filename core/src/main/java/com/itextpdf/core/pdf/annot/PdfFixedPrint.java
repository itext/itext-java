package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;

public class PdfFixedPrint extends PdfObjectWrapper<PdfDictionary> {

    public PdfFixedPrint(PdfDocument pdfDocument) throws PdfException {
        this(new PdfDictionary(), pdfDocument);
    }

    public PdfFixedPrint(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
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

    public PdfArray getMatrix() throws PdfException {
        return getPdfObject().getAsArray(PdfName.Matrix);
    }

    public PdfNumber getHorizontalTranslation() throws PdfException {
        return getPdfObject().getAsNumber(PdfName.H);
    }

    public PdfNumber getVerticalTranslation() throws PdfException {
        return getPdfObject().getAsNumber(PdfName.V);
    }
}

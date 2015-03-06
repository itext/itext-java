package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfCaretAnnotation extends PdfMarkupAnnotation {

    public PdfCaretAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfCaretAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Caret;
    }

    public PdfCaretAnnotation setRectangleDifferences(PdfArray rect){
        return put(PdfName.RD, rect);
    }

    public PdfCaretAnnotation setSymbol(PdfString symbol){
        return put(PdfName.Sy, symbol);
    }

    public PdfArray getRectangleDifferences() throws PdfException {
        return getPdfObject().getAsArray(PdfName.RD);
    }

    public PdfString getSymbol() throws PdfException {
        return getPdfObject().getAsString(PdfName.Sy);
    }
}

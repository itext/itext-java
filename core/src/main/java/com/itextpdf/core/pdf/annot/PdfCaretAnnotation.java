package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfString;

public class PdfCaretAnnotation extends PdfMarkupAnnotation {

    public PdfCaretAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfCaretAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Caret;
    }

    public PdfCaretAnnotation setSymbol(PdfString symbol) {
        return put(PdfName.Sy, symbol);
    }

    public PdfString getSymbol() {
        return getPdfObject().getAsString(PdfName.Sy);
    }
}

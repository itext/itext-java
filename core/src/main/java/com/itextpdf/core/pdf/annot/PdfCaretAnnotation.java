package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfString;

public class PdfCaretAnnotation extends PdfMarkupAnnotation {

    public PdfCaretAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfCaretAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
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

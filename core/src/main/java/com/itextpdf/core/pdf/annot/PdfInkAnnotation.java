package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfInkAnnotation extends PdfMarkupAnnotation {

    public PdfInkAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfInkAnnotation(PdfDocument document, Rectangle rect, PdfArray inkList) {
        this(document, rect);
        put(PdfName.InkList, inkList);
    }

    public PdfInkAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Ink;
    }


}

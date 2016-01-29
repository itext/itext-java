package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

public class PdfInkAnnotation extends PdfMarkupAnnotation {

    public PdfInkAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfInkAnnotation(Rectangle rect, PdfArray inkList) {
        this(rect);
        put(PdfName.InkList, inkList);
    }

    public PdfInkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Ink;
    }


}

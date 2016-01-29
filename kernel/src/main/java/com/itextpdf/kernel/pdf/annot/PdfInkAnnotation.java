package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

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

package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfInkAnnotation extends PdfMarkupAnnotation {

    public PdfInkAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfInkAnnotation(PdfDocument document, Rectangle rect, PdfArray inkList) throws PdfException {
        this(document, rect);
        put(PdfName.InkList, inkList);
    }

    public PdfInkAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Ink;
    }


}

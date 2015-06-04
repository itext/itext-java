package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

public class PdfPrinterMarkAnnotation extends PdfAnnotation {

    public PdfPrinterMarkAnnotation(PdfDocument document, Rectangle rect, PdfFormXObject appearanceStream) {
        super(document, rect);
        setNormalAppearance(appearanceStream.getPdfObject());
        setFlags(PdfAnnotation.Print | PdfAnnotation.ReadOnly);
    }

    public PdfPrinterMarkAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    public PdfName getSubtype() {
        return PdfName.PrinterMark;
    }

    public PdfMarkupAnnotation setArbitraryTypeName(PdfName arbitraryTypeName) {
        return put(PdfName.MN, arbitraryTypeName);
    }

    public PdfName getArbitraryTypeName() {
        return getPdfObject().getAsName(PdfName.MN);
    }
}

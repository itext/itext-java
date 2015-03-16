package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

public class PdfPrinterMarkAnnotation extends PdfAnnotation {

    public PdfPrinterMarkAnnotation(PdfDocument document, Rectangle rect, PdfFormXObject appearanceStream) throws PdfException {
        super(document, rect);
        setNormalAppearance(appearanceStream.getPdfObject());
        setFlags(PdfAnnotation.Print | PdfAnnotation.ReadOnly);
    }

    public PdfPrinterMarkAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    public PdfName getSubtype() throws PdfException {
        return PdfName.PrinterMark;
    }

    public PdfMarkupAnnotation setArbitraryTypeName(PdfName arbitraryTypeName) {
        return put(PdfName.MN, arbitraryTypeName);
    }

    public PdfName getArbitraryTypeName() throws PdfException {
        return getPdfObject().getAsName(PdfName.MN);
    }
}

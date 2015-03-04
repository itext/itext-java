package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.navigation.PdfDestination;

public class PdfLinkAnnotation extends PdfAnnotation {

    public PdfLinkAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    public PdfLinkAnnotation(PdfDictionary pdfObject, PdfDocument document, PdfPage page) throws PdfException {
        super(pdfObject, document, page);
    }

    public PdfLinkAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
        put(PdfName.Subtype, PdfName.Link);
    }

    public PdfLinkAnnotation(PdfDocument document, Rectangle rect, PdfPage page) throws PdfException {
        super(document, rect, page);
        put(PdfName.Subtype, PdfName.Link);
    }

    public PdfLinkAnnotation setDestination(PdfDestination destination) {
        return this;
    }

}

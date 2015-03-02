package com.itextpdf.core.pdf.annots;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.navigation.PdfDestination;

public class PdfLinkAnnotation extends PdfAnnotation {

    public PdfLinkAnnotation(PdfDocument document) throws PdfException {
        super(document);
    }

    public PdfLinkAnnotation setDestination(PdfDestination destination) {
        return this;
    }

    public PdfLinkAnnotation setAction(PdfAction action) {
        return this;
    }

}

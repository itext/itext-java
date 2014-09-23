package com.itextpdf.core.pdf.annots;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.actions.PdfAction;
import com.itextpdf.core.pdf.navigation.IPdfDestination;

public class PdfLinkAnnotation extends PdfAnnotation {

    public PdfLinkAnnotation() {
        super();
    }

    public PdfLinkAnnotation setDestination(IPdfDestination destination) {
        return this;
    }

    public PdfLinkAnnotation setAction(PdfAction action) {
        return this;
    }

}

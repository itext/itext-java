package com.itextpdf.core.pdf.annots;

import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.navigation.PdfDestination;

public class PdfLinkAnnotation extends PdfAnnotation {

    public PdfLinkAnnotation() {
        super();
    }

    public PdfLinkAnnotation setDestination(PdfDestination destination) {
        return this;
    }

    public PdfLinkAnnotation setAction(PdfAction action) {
        return this;
    }

}

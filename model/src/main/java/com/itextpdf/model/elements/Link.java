package com.itextpdf.model.elements;

import com.itextpdf.core.pdf.actions.PdfAction;
import com.itextpdf.core.pdf.annots.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.IPdfDestination;

public class Link extends Span {

    protected PdfLinkAnnotation linkAnnotation;

    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        this.linkAnnotation = linkAnnotation;
    }

    public Link(String text, PdfAction action) {
        this(text, new PdfLinkAnnotation().setAction(action));
    }

    public Link(String text, IPdfDestination destination) {
        this(text, new PdfLinkAnnotation().setDestination(destination));
    }


}

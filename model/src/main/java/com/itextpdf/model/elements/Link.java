package com.itextpdf.model.elements;

import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annots.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.PdfDestination;

public class Link extends Span {

    protected PdfLinkAnnotation linkAnnotation;

    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        this.linkAnnotation = linkAnnotation;
    }

    public Link(String text, PdfAction action) {
        this(text, new PdfLinkAnnotation().setAction(action));
    }

    public Link(String text, PdfDestination destination) {
        this(text, new PdfLinkAnnotation().setDestination(destination));
    }


}

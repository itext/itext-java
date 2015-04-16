package com.itextpdf.model.element;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.PdfDestination;

public class Link extends Span {

    protected PdfLinkAnnotation linkAnnotation;

    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        this.linkAnnotation = linkAnnotation;
    }

    public Link(String text, PdfAction action) throws PdfException {
        this(text, new PdfLinkAnnotation(action.getDocument(), new Rectangle(0, 0, 0, 0)).setAction(action));
    }

    public Link(String text, PdfDestination destination) throws PdfException {
        this(text, new PdfLinkAnnotation(destination.getDocument(), new Rectangle(0, 0, 0, 0)).setDestination(destination));
    }


}

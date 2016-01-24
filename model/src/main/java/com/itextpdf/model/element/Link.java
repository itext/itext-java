package com.itextpdf.model.element;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.PdfDestination;
import com.itextpdf.model.renderer.LinkRenderer;

public class Link extends Text {

    protected PdfLinkAnnotation linkAnnotation;

    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        this.linkAnnotation = linkAnnotation;
        setRole(PdfName.Link);
    }

    public Link(String text, PdfAction action) {
        this(text, new PdfLinkAnnotation(action.getDocument(), new Rectangle(0, 0, 0, 0)).setAction(action));
    }

    public Link(String text, PdfDestination destination) {
        this(text, new PdfLinkAnnotation(destination.getDocument(), new Rectangle(0, 0, 0, 0)).setDestination(destination));
    }

    public PdfLinkAnnotation getLinkAnnotation(){
        return linkAnnotation;
    }

    @Override
    protected LinkRenderer makeNewRenderer() {
        return new LinkRenderer(this, text);
    }
}

package com.itextpdf.layout.element;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.layout.renderer.LinkRenderer;

public class Link extends Text {

    protected PdfLinkAnnotation linkAnnotation;

    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        this.linkAnnotation = linkAnnotation;
        setRole(PdfName.Link);
    }

    public Link(String text, PdfAction action) {
        this(text, new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)).setAction(action));
    }

    public Link(String text, PdfDestination destination) {
        this(text, new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)).setDestination(destination));
    }

    public PdfLinkAnnotation getLinkAnnotation() {
        return linkAnnotation;
    }

    @Override
    protected LinkRenderer makeNewRenderer() {
        return new LinkRenderer(this, text);
    }
}

package com.itextpdf.model.element;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.PdfDestination;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.LinkRenderer;

public class Link extends Text {

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

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new LinkRenderer(this, text);
    }

    public PdfLinkAnnotation getLinkAnnotation(){
        return linkAnnotation;
    }
}

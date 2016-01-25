package com.itextpdf.model.element;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.core.pdf.navigation.PdfDestination;
import com.itextpdf.model.renderer.LinkRenderer;

public class Link extends Text {

    private PdfLinkAnnotation linkAnnotation;
    private PdfAction pdfAction;
    private PdfDestination pdfDestination;

    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        this.linkAnnotation = linkAnnotation;
        setRole(PdfName.Link);
    }

    public Link(String text, PdfAction action) {
        super(text);
        this.pdfAction = action;
        setRole(PdfName.Link);
    }

    public Link(String text, PdfDestination destination) {
        super(text);
        this.pdfDestination = destination;
        setRole(PdfName.Link);
    }

    public PdfLinkAnnotation getLinkAnnotation(PdfDocument pdfDocument) {
        if (linkAnnotation == null) {
            linkAnnotation = new PdfLinkAnnotation(pdfDocument, new Rectangle(0, 0, 0, 0));
            if (pdfAction != null) {
                linkAnnotation.setAction(pdfAction);
            } else if (pdfDestination != null) {
                linkAnnotation.setDestination(pdfDestination);
            }
        }
        return linkAnnotation;
    }

    @Override
    protected LinkRenderer makeNewRenderer() {
        return new LinkRenderer(this, text);
    }
}

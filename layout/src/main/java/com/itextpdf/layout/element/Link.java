package com.itextpdf.layout.element;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.layout.renderer.LinkRenderer;

/**
 * A clickable piece of {@link Text} which contains a {@link PdfLinkAnnotation
 * link annotation dictionary}. The concept is largely similar to that of the
 * HTML anchor tag.
 */
public class Link extends Text {

    protected PdfLinkAnnotation linkAnnotation;

    /**
     * Creates a Link with a fully constructed link annotation dictionary.
     * 
     * @param text the textual contents of the link
     * @param linkAnnotation a {@link PdfLinkAnnotation}
     */
    public Link(String text, PdfLinkAnnotation linkAnnotation) {
        super(text);
        this.linkAnnotation = linkAnnotation;
        setRole(PdfName.Link);
    }

    /**
     * Creates a Link which can execute an action.
     * 
     * @param text the textual contents of the link
     * @param action a {@link PdfAction}
     */
    public Link(String text, PdfAction action) {
        this(text, new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)).setAction(action));
    }

    /**
     * Creates a Link to another location in the document.
     * 
     * @param text the textual contents of the link
     * @param destination a {@link PdfDestination}
     */
    public Link(String text, PdfDestination destination) {
        this(text, new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)).setDestination(destination));
    }

    /**
     * Gets the link annotation dictionary associated with this link.
     * @return a {@link PdfLinkAnnotation}
     */
    public PdfLinkAnnotation getLinkAnnotation() {
        return linkAnnotation;
    }

    @Override
    protected LinkRenderer makeNewRenderer() {
        return new LinkRenderer(this, text);
    }
}

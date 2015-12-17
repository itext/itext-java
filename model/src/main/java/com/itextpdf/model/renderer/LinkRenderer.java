package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.Border;
import com.itextpdf.model.element.Link;
import com.itextpdf.model.layout.LayoutPosition;

public class LinkRenderer extends TextRenderer {

    public LinkRenderer(Link link) {
        this (link, link.getText());
    }

    public LinkRenderer(Link linkElement, String text) {
        super(linkElement, text);
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas){
        super.draw(document, canvas);

        int position = getPropertyAsInteger(Property.POSITION);
        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(false);
        }

        PdfLinkAnnotation linkAnnotation = ((Link)modelElement).getLinkAnnotation();
        linkAnnotation.setRectangle(new PdfArray(occupiedArea.getBBox()));

        Border border = getProperty(Property.BORDER);
        if (border != null) {
            linkAnnotation.setBorder(new PdfArray(new float[]{0, 0, border.getWidth()}));
        } else {
            linkAnnotation.setBorder(new PdfArray(new float[]{0, 0, 0}));
        }

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }

        PdfPage page = document.getPage(occupiedArea.getPageNumber());
        page.addAnnotation(linkAnnotation);
    }

    @Override
    public LinkRenderer getNextRenderer() {
        return null;
    }
}

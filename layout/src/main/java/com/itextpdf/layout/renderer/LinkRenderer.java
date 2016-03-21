package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.layout.LayoutPosition;

public class LinkRenderer extends TextRenderer {

    public LinkRenderer(Link link) {
        this (link, link.getText());
    }

    public LinkRenderer(Link linkElement, String text) {
        super(linkElement, text);
    }

    @Override
    public void draw(DrawContext drawContext){
        super.draw(drawContext);

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

        PdfPage page = drawContext.getDocument().getPage(occupiedArea.getPageNumber());
        page.addAnnotation(linkAnnotation);
    }

    @Override
    public LinkRenderer getNextRenderer() {
        return new LinkRenderer((Link) modelElement, null);
    }
}

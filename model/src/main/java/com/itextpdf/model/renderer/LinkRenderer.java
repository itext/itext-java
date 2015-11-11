package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Link;
import com.itextpdf.model.layout.LayoutPosition;

public class LinkRenderer extends TextRenderer{

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

        if (position == LayoutPosition.RELATIVE) {
            applyAbsolutePositioningTranslation(true);
        }

        PdfPage page = document.getPage(occupiedArea.getPageNumber());
        page.addAnnotation(linkAnnotation);
    }
}

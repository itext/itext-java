package com.itextpdf.model.renderer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.model.element.Link;

public class LinkRenderer extends TextRenderer{

    public LinkRenderer(Link linkElement, String text) {
        super(linkElement, text);
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas){
        super.draw(document, canvas);

        PdfLinkAnnotation linkAnnotation = ((Link)modelElement).getLinkAnnotation();
        linkAnnotation.setRectangle(new PdfArray(occupiedArea.getBBox()));

        try{
            PdfPage page = linkAnnotation.getDocument().getPage(occupiedArea.getPageNumber());
            page.addAnnotation(linkAnnotation);
        } catch (PdfException e){
            throw new RuntimeException(e);
        }
    }
}

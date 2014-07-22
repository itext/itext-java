package com.itextpdf.model.layout;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.model.Document;
import com.itextpdf.model.elements.IElement;
import com.itextpdf.model.elements.Paragraph;
import com.itextpdf.model.layout.shapes.ILayoutShape;

import java.io.IOException;
import java.util.List;

/**
 * Basic implementation of layout manager.
 * <p/>
 * We suggest it will be used by Document class by default and it will emulate the iText 5 ColumnText behaviour.
 */
public class DefaultLayoutMgr implements ILayoutMgr {

    protected Document document;

    public DefaultLayoutMgr(Document document) {
        this.document = document;
    }

    @Override
    public void setCanvas(PdfCanvas canvas) {

    }

    @Override
    public void setShapes(List<ILayoutShape> shapes) {

    }

    @Override
    public List<ILayoutShape> getShapes() {
        return null;
    }

    @Override
    public IPlaceElementResult placeElement(IElement element) {
        return null;
    }

    @Override
    public IPlaceElementResult overflow(IElement element) throws IOException {
        document.newPage();
        return placeElement(element);
    }

    public static void showTextAligned(final PdfCanvas canvas, final int alignment, final Paragraph paragraph, final float x, final float y, final float rotation) {

    }

}

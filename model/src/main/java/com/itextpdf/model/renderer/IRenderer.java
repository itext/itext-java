package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

public interface IRenderer {

    void addChild(IRenderer renderer);
    LayoutResult layout(LayoutContext layoutContext);
    // TODO do we need it?
    LayoutArea getNextArea();
    void draw(PdfDocument document, PdfCanvas canvas);
    LayoutArea getOccupiedArea();
    <T> T getProperty(int key);
    IRenderer setParent(IRenderer parent);

}

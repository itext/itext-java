package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

public interface IRenderer {

    void addChild(IRenderer renderer);
    LayoutResult layout(LayoutContext layoutContext);
    IRenderer split();
    LayoutArea getNextArea();
    void draw(PdfCanvas canvas);
    LayoutArea getOccupiedArea();

}

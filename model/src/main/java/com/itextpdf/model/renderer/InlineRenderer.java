package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

public class InlineRenderer extends AbstractRenderer {


    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        throw new RuntimeException();
    }

    @Override
    public IRenderer split() {
        throw new RuntimeException();
    }

    @Override
    public LayoutArea getNextArea() {
        throw new RuntimeException();
    }

    @Override
    public void draw(PdfCanvas canvas) {
        throw new RuntimeException();
    }

}

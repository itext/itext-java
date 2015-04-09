package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.model.element.PageBreak;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

public class PageBreakRenderer implements IRenderer {

    protected PageBreak pageBreak;

    public PageBreakRenderer(PageBreak pageBreak) {
        this.pageBreak = pageBreak;
    }

    @Override
    public void addChild(IRenderer renderer) {
        throw new RuntimeException();
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        return new LayoutResult(LayoutResult.NOTHING, layoutContext.getArea(), null).setNewPageSize(pageBreak.getPageSize());
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

    @Override
    public LayoutArea getOccupiedArea() {
        return null;
    }
}

package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
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
        LayoutArea occupiedArea = layoutContext.getArea().clone();
        occupiedArea.getBBox().setHeight(0);
        occupiedArea.getBBox().setWidth(0);
        return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, null).setNewPageSize(pageBreak.getPageSize());
    }

    @Override
    public LayoutArea getNextArea() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LayoutArea getOccupiedArea() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getProperty(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRenderer setParent(IRenderer parent) {
        return this;
    }
}

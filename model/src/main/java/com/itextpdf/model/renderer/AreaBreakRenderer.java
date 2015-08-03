package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.AreaBreak;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.List;

public class AreaBreakRenderer implements IRenderer {

    protected AreaBreak areaBreak;

    public AreaBreakRenderer(AreaBreak areaBreak) {
        this.areaBreak = areaBreak;
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
        return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, null).setNewPageSize(areaBreak.getPageSize());
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
    public boolean hasProperty(Property property) {
        return false;
    }

    @Override
    public <T> T getProperty(Property key) {
        return null;
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        return null;
    }

    @Override
    public <T> T getProperty(Property property, T defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends IRenderer> T setProperty(Property property, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteProperty(Property property) {

    }

    @Override
    public IRenderer setParent(IRenderer parent) {
        return this;
    }

    @Override
    public IPropertyContainer getModelElement() {
        return null;
    }

    @Override
    public List<IRenderer> getChildRenderers() {
        return null;
    }

    @Override
    public boolean isFlushed() {
        return false;
    }

    @Override
    public void move(float dx, float dy) {
        throw new UnsupportedOperationException();
    }
}

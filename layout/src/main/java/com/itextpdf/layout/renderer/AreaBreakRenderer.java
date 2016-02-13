package com.itextpdf.layout.renderer;

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;

import java.util.List;

/**
 * Renderer object for the {@link AreaBreak} layout element. Will terminate the
 * current content area and initialize a new one.
 */
public class AreaBreakRenderer implements IRenderer {

    protected AreaBreak areaBreak;

    /**
     * Creates an AreaBreakRenderer.
     * @param areaBreak the {@link AreaBreak} that will be rendered by this object
     */
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
        return new LayoutResult(LayoutResult.NOTHING, occupiedArea, null, null).setAreaBreak(areaBreak);
    }

    @Override
    public void draw(DrawContext drawContext) {
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
    public boolean hasOwnProperty(Property property) {
        return false;
    }

    @Override
    public <T> T getProperty(Property key) {
        return null;
    }

    @Override
    public <T> T getOwnProperty(Property property) {
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
    public void deleteOwnProperty(Property property) {

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

    @Override
    public AreaBreakRenderer getNextRenderer() {
        return null;
    }
}

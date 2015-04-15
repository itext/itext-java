package com.itextpdf.model.element;

import com.itextpdf.core.font.PdfFont;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.renderer.IRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElement implements IElement {

    protected IRenderer nextRenderer;
    protected Map<Integer, Object> properties = new HashMap<>();
    protected List<IElement> childElements = new ArrayList<>();

    @Override
    public void setNextRenderer(IRenderer renderer) {
        this.nextRenderer = renderer;
    }

    @Override
    public IRenderer createRendererSubTree() {
        IRenderer rendererRoot = makeRenderer();
        for (IElement child : childElements) {
            rendererRoot.addChild(child.createRendererSubTree());
        }
        return rendererRoot;
    }

    @Override
    public <T extends IPropertyContainer> T setProperty(Integer propertyKey, Object value) {
        properties.put(propertyKey, value);
        return (T) this;
    }

    @Override
    public <T> T getProperty(Integer propertyKey) {
        return (T) properties.get(propertyKey);
    }

    @Override
    public <T> T getDefaultProperty(Integer propertyKey) {
        return null;
    }

    public Float getWidth() {
        return getProperty(Property.WIDTH);
    }

    public <T extends AbstractElement> T setWidth(float width) {
        return setProperty(Property.WIDTH, width);
    }

    public Float getHeight() {
        return getProperty(Property.HEIGHT);
    }

    public <T extends AbstractElement> T setHeight(float height) {
        return setProperty(Property.HEIGHT, height);
    }

    public <T extends AbstractElement> T setRelativePosition(float left, float top, float right, float bottom) {
        return setProperty(Property.POSITION, LayoutPosition.RELATIVE).
            setProperty(Property.LEFT, left).
            setProperty(Property.RIGHT, right).
            setProperty(Property.TOP, top).
            setProperty(Property.BOTTOM, bottom);
    }

    public <T extends AbstractElement> T setFixedPosition(float x, float y) {
        return setProperty(Property.POSITION, LayoutPosition.FIXED).
            setProperty(Property.X, x).
            setProperty(Property.Y, y);
    }

    public <T extends AbstractElement> T setAbsolutePosition(float x, float y) {
        return setProperty(Property.POSITION, LayoutPosition.ABSOLUTE).
            setProperty(Property.X, x).
            setProperty(Property.Y, y);
    }

    public <T extends AbstractElement> T setFont(PdfFont font) {
        return setProperty(Property.FONT, font);
    }

    @Override
    public boolean isBreakable() {
        return true;
    }
}

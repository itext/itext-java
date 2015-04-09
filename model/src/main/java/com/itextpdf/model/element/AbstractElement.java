package com.itextpdf.model.element;

import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.renderer.IRenderer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractElement implements IElement {

    protected IRenderer renderer;
    protected Map<Integer, Object> properties = new HashMap<Integer, Object>();

    @Override
    public void setRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }

    public <T extends AbstractElement> T setProperty(Integer propertyKey, Object value) {
        properties.put(propertyKey, value);
        return (T) this;
    }

    public <T> T getProperty(Integer propertyKey) {
        return (T)properties.get(propertyKey);
    }

    public <T extends AbstractElement> T setWidth(float width) {
        return setProperty(Property.WIDTH, width);
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

}

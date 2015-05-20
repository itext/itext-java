package com.itextpdf.model.element;

import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.model.IPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.renderer.IRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElement<T extends AbstractElement> implements IElement {

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
        switch (propertyKey) {
            case Property.MARGIN_TOP:
            case Property.MARGIN_RIGHT:
            case Property.MARGIN_BOTTOM:
            case Property.MARGIN_LEFT:
                return (T) Float.valueOf(0);
            default:
                return null;
        }
    }

    public Float getWidth() {
        return getProperty(Property.WIDTH);
    }

    public T setWidth(float width) {
        return setProperty(Property.WIDTH, width);
    }

    public Float getHeight() {
        return getProperty(Property.HEIGHT);
    }

    public T setHeight(float height) {
        return setProperty(Property.HEIGHT, height);
    }

    public T setRelativePosition(float left, float top, float right, float bottom) {
        return setProperty(Property.POSITION, LayoutPosition.RELATIVE).
            setProperty(Property.LEFT, left).
            setProperty(Property.RIGHT, right).
            setProperty(Property.TOP, top).
            setProperty(Property.BOTTOM, bottom);
    }

    public T setFixedPosition(float x, float y) {
        return setProperty(Property.POSITION, LayoutPosition.FIXED).
            setProperty(Property.X, x).
            setProperty(Property.Y, y);
    }

    public T setAbsolutePosition(float x, float y) {
        return setProperty(Property.POSITION, LayoutPosition.ABSOLUTE).
            setProperty(Property.X, x).
            setProperty(Property.Y, y);
    }

    public T setFont(PdfFont font) {
        return setProperty(Property.FONT, font);
    }

    public T setFontColor(Color fontColor) {
        return setProperty(Property.FONT_COLOR, fontColor);
    }

    public T setFontSize(float fontSize) {
        return setProperty(Property.FONT_SIZE, fontSize);
    }

    public T setBackgroundColor(Color backgroundColor) {
        return setBackgroundColor(backgroundColor, 0, 0, 0, 0);
    }

    public T setBackgroundColor(Color backgroundColor, float extraLeft, final float extraTop, final float extraRight, float extraBottom) {
        return setProperty(Property.BACKGROUND, new Property.Background(backgroundColor, extraLeft, extraTop, extraRight, extraBottom));
    }

    @Override
    public boolean isBreakable() {
        return true;
    }
}

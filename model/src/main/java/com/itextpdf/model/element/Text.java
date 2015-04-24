package com.itextpdf.model.element;

import com.itextpdf.canvas.color.Color;
import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.TextRenderer;

public class Text extends AbstractElement<Text> implements ILeafElement, IAccessibleElement {

    protected String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new TextRenderer(this, text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getTextRise() {
        return getProperty(Property.TEXT_RISE);
    }

    public Text setTextRise(float textRise) {
        return setProperty(Property.TEXT_RISE, textRise);
    }

    public int getTextRenderingMode() {
        return getProperty(Property.TEXT_RENDERING_MODE);
    }

    public Text setTextRenderingMode(int textRenderingMode) {
        return setProperty(Property.TEXT_RENDERING_MODE, textRenderingMode);
    }

    public Color getStrokeColor() {
        return getProperty(Property.STROKE_COLOR);
    }

    public Text setStrokeColor(Color strokeColor) {
        return setProperty(Property.STROKE_COLOR, strokeColor);
    }

    public Float getStrokeWidth() {
        return getProperty(Property.STROKE_WIDTH);
    }

    public Text setStrokeWidth(float strokeWidth) {
        return setProperty(Property.STROKE_WIDTH, strokeWidth);
    }
}

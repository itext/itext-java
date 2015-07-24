package com.itextpdf.model.element;

import com.itextpdf.canvas.color.Color;
import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.TextRenderer;

public class Text extends AbstractElement<Text> implements ILeafElement<Text>, IAccessibleElement<Text> {

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

    @Override
    public <T> T getDefaultProperty(int propertyKey) {
        switch (propertyKey) {
            case Property.HORIZONTAL_SCALING:
            case Property.VERTICAL_SCALING:
                return (T) new Float(1);
            default:
                return super.getDefaultProperty(propertyKey);
        }
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

    public Float getHorizontalScaling() {
        return getProperty(Property.HORIZONTAL_SCALING);
    }

    /**
     * The horizontal scaling parameter adjusts the width of glyphs by stretching or
     * compressing them in the horizontal direction.
     * @param horizontalScaling the scaling parameter. 1 means no scaling will be applied,
     *                          0.5 means the text will be scaled by half.
     *                          2 means the text will be twice as wide as normal one.
     */
    public Text setHorizontalScaling(float horizontalScaling) {
        return setProperty(Property.HORIZONTAL_SCALING, horizontalScaling);
    }
}

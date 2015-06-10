package com.itextpdf.model.element;

import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.BlockRenderer;
import com.itextpdf.model.renderer.IRenderer;

public abstract class BlockElement<T extends BlockElement> extends AbstractElement<T> implements IAccessibleElement<T> {

    public BlockElement() {
    }

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new BlockRenderer(this);
    }

    public Float getMarginLeft() {
        return getProperty(Property.MARGIN_LEFT);
    }

    public T setMarginLeft(float value) {
        return setProperty(Property.MARGIN_LEFT, value);
    }

    public Float getMarginRight() {
        return getProperty(Property.MARGIN_RIGHT);
    }

    public T setMarginRight(float value) {
        return setProperty(Property.MARGIN_RIGHT, value);
    }

    public Float getMarginTop() {
        return getProperty(Property.MARGIN_TOP);
    }

    public T setMarginTop(float value) {
        return setProperty(Property.MARGIN_TOP, value);
    }

    public Float getMarginBottom() {
        return getProperty(Property.MARGIN_BOTTOM);
    }

    public T setMarginBottom(float value) {
        return setProperty(Property.MARGIN_BOTTOM, value);
    }

    public T setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        return (T) setMarginTop(marginTop).setMarginRight(marginRight).setMarginBottom(marginBottom).setMarginLeft(marginLeft);
    }

    public Float getPaddingLeft() {
        return getProperty(Property.PADDING_LEFT);
    }

    public T setPaddingLeft(float value) {
        return setProperty(Property.PADDING_LEFT, value);
    }

    public Float getPaddingRight() {
        return getProperty(Property.PADDING_RIGHT);
    }

    public T setPaddingRight(float value) {
        return setProperty(Property.PADDING_RIGHT, value);
    }

    public Float getPaddingTop() {
        return getProperty(Property.PADDING_TOP);
    }

    public T setPaddingTop(float value) {
        return setProperty(Property.PADDING_TOP, value);
    }

    public Float getPaddingBottom() {
        return getProperty(Property.PADDING_BOTTOM);
    }

    public T setPaddingBottom(float value) {
        return setProperty(Property.PADDING_BOTTOM, value);
    }

    public T setPaddings(float paddingTop, float paddingRight, float paddingBottom, float paddingLeft) {
        return (T) setPaddingTop(paddingTop).setPaddingRight(paddingRight).setPaddingBottom(paddingBottom).setPaddingLeft(paddingLeft);
    }

    public T setAlignment(Property.Alignment alignment) {
        return setProperty(Property.ALIGNMENT, alignment);
    }

    @Override
    public <T> T getDefaultProperty(int propertyKey) {
        switch (propertyKey) {
            case Property.KEEP_TOGETHER:
                return (T) Boolean.valueOf(false);
            default:
                return super.getDefaultProperty(propertyKey);
        }
    }

    public Boolean isKeepTogether() {
        return getProperty(Property.KEEP_TOGETHER);
    }

    public T setKeepTogether(boolean keepTogether) {
        return setProperty(Property.KEEP_TOGETHER, keepTogether);
    }
}

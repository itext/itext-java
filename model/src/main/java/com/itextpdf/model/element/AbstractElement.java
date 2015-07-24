package com.itextpdf.model.element;

import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.Border;
import com.itextpdf.model.layout.LayoutPosition;
import com.itextpdf.model.renderer.IRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractElement<Type extends AbstractElement> implements IElement<Type> {

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
    public <T extends Type> T setProperty(int propertyKey, Object value) {
        properties.put(propertyKey, value);
        return (T) this;
    }

    @Override
    public <T> T getProperty(int propertyKey) {
        return (T) properties.get(propertyKey);
    }

    @Override
    public <T> T getDefaultProperty(int propertyKey) {
        switch (propertyKey) {
            case Property.MARGIN_TOP:
            case Property.MARGIN_RIGHT:
            case Property.MARGIN_BOTTOM:
            case Property.MARGIN_LEFT:
            case Property.PADDING_TOP:
            case Property.PADDING_RIGHT:
            case Property.PADDING_BOTTOM:
            case Property.PADDING_LEFT:
                return (T) Float.valueOf(0);
            case Property.POSITION:
                return (T)Integer.valueOf(LayoutPosition.STATIC);
            default:
                return null;
        }
    }

    public Float getWidth() {
        return getProperty(Property.WIDTH);
    }

    public Type setWidth(float width) {
        return setProperty(Property.WIDTH, width);
    }

    public Float getHeight() {
        return getProperty(Property.HEIGHT);
    }

    public Type setHeight(float height) {
        return setProperty(Property.HEIGHT, height);
    }

    public Type setRelativePosition(float left, float top, float right, float bottom) {
        return (Type) setProperty(Property.POSITION, LayoutPosition.RELATIVE).
            setProperty(Property.LEFT, left).
            setProperty(Property.RIGHT, right).
            setProperty(Property.TOP, top).
            setProperty(Property.BOTTOM, bottom);
    }

    public Type setFixedPosition(float x, float y) {
        return (Type) setProperty(Property.POSITION, LayoutPosition.FIXED).
            setProperty(Property.X, x).
            setProperty(Property.Y, y);
    }

    public Type setFixedPosition(int pageNumber, float x, float y) {
        return (Type) setFixedPosition(x, y).
               setProperty(Property.PAGE_NUMBER, pageNumber);
    }

//    public Type setAbsolutePosition(float x, float y) {
//        return (Type) setProperty(Property.POSITION, LayoutPosition.ABSOLUTE).
//            setProperty(Property.X, x).
//            setProperty(Property.Y, y);
//    }

    public Type setFont(PdfFont font) {
        return setProperty(Property.FONT, font);
    }

    public Type setFontColor(Color fontColor) {
        return setProperty(Property.FONT_COLOR, fontColor);
    }

    public Type setFontSize(float fontSize) {
        return setProperty(Property.FONT_SIZE, fontSize);
    }

    public Type setCharacterSpacing(float charSpacing) {
        return setProperty(Property.CHARACTER_SPACING, charSpacing);
    }

    /**
     * The word-spacing parameter is added to the glyph’s horizontal or vertical displacement (depending on the writing mode).
     */
    public Type setWordSpacing(float wordSpacing) {
        return setProperty(Property.WORD_SPACING, wordSpacing);
    }

    /**
     * Enable or disable kerning.
     * Some fonts may specify kern pairs, i.e. pair of glyphs, between which the amount of horizontal space is adjusted.
     * This adjustment is typically negative, e.g. in "AV" pair the glyphs will typically be moved closer to each other.
     */
    public Type setFontKerning(Property.FontKerning fontKerning) {
        return setProperty(Property.FONT_KERNING, fontKerning);
    }

    public Type setBackgroundColor(Color backgroundColor) {
        return setBackgroundColor(backgroundColor, 0, 0, 0, 0);
    }

    public Type setBackgroundColor(Color backgroundColor, float extraLeft, final float extraTop, final float extraRight, float extraBottom) {
        return setProperty(Property.BACKGROUND, new Property.Background(backgroundColor, extraLeft, extraTop, extraRight, extraBottom));
    }

    public Type setBorder(Border border) {
        return setProperty(Property.BORDER, border);
    }

    public Type setBorderTop(Border border) {
        return setProperty(Property.BORDER_TOP, border);
    }

    public Type setBorderRight(Border border) {
        return setProperty(Property.BORDER_RIGHT, border);
    }

    public Type setBorderBottom(Border border) {
        return setProperty(Property.BORDER_BOTTOM, border);
    }

    public Type setBorderLeft(Border border) {
        return setProperty(Property.BORDER_LEFT, border);
    }

    @Override
    public boolean isBreakable() {
        return true;
    }
}

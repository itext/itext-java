package com.itextpdf.model.element;

import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.tagutils.AccessibleAttributes;
import com.itextpdf.core.pdf.tagutils.IAccessibleElement;
import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.TextRenderer;

public class Text extends AbstractElement<Text> implements ILeafElement<Text>, IElement<Text>, IAccessibleElement {

    protected String text;
    protected PdfName role = PdfName.Span;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        switch (property) {
            case HORIZONTAL_SCALING:
            case VERTICAL_SCALING:
                return (T) new Float(1);
            default:
                return super.getDefaultProperty(property);
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

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
    }

    @Override
    public AccessibleAttributes getAccessibleAttributes() {
        return null;
    }

    @Override
    protected TextRenderer makeNewRenderer() {
        return new TextRenderer(this, text);
    }
}

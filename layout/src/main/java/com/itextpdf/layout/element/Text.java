package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.renderer.TextRenderer;

/**
 * A {@link Text} is a piece of text of any length. As a {@link ILeafElement leaf element},
 * it is the smallest piece of content that may bear specific layout attributes.
 */
public class Text extends AbstractElement<Text> implements ILeafElement<Text>, IElement<Text>, IAccessibleElement {

    protected String text;
    protected PdfName role = PdfName.Span;
    protected AccessibilityProperties tagProperties;

    /**
     * Constructs a Text with its role initialized.
     * @param text the contents, as a {@link String}
     */
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

    /**
     * Gets the text rise.
     * @return the vertical distance from the text's default base line, as a float.
     */
    public float getTextRise() {
        return getProperty(Property.TEXT_RISE);
    }

    /**
     * Sets the text rise.
     * @param textRise a vertical distance from the text's default base line.
     * @return this Text
     */
    public Text setTextRise(float textRise) {
        return setProperty(Property.TEXT_RISE, textRise);
    }

    /**
     * Gets the horizontal scaling property, which determines how wide the text
     * should be stretched.
     * @return the horizontal spacing, as a <code>float</code>
     */
    public Float getHorizontalScaling() {
        return getProperty(Property.HORIZONTAL_SCALING);
    }


    /**
     * Skews the text to simulate italic and other effects. Try <CODE>alpha=0
     * </CODE> and <CODE>beta=12</CODE>.
     *
     * @param alpha the first angle in degrees
     * @param beta  the second angle in degrees
     * @return this <CODE>Text</CODE>
     */
    public Text setSkew(float alpha, float beta){
        alpha = (float) Math.tan(alpha * Math.PI / 180);
        beta = (float) Math.tan(beta * Math.PI / 180);
        return setProperty(Property.SKEW, new Float[]{alpha, beta});
    }

    /**
     * The horizontal scaling parameter adjusts the width of glyphs by stretching or
     * compressing them in the horizontal direction.
     * @param horizontalScaling the scaling parameter. 1 means no scaling will be applied,
     *                          0.5 means the text will be scaled by half.
     *                          2 means the text will be twice as wide as normal one.
     * @return this Text
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
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibilityProperties();
        }
        return tagProperties;
    }

    @Override
    protected TextRenderer makeNewRenderer() {
        return new TextRenderer(this, text);
    }
}

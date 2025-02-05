/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TextRenderer;

/**
 * A {@link Text} is a piece of text of any length. As a {@link ILeafElement leaf element},
 * it is the smallest piece of content that may bear specific layout attributes.
 */
public class Text extends AbstractElement<Text> implements ILeafElement, IAccessibleElement {

    protected String text;
    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Constructs a Text with its role initialized.
     * @param text the contents, as a {@link String}
     */
    public Text(String text) {
        if (null == text) {
            throw new IllegalArgumentException(LayoutExceptionMessageConstant.TEXT_CONTENT_CANNOT_BE_NULL);
        }
        this.text = text;
    }

    /**
     * Gets the contents of the Text object that will be rendered.
     *
     * @return the string with the contents
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the contents of the Text object.
     * @param text the new contents
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the text rise.
     * @return the vertical distance from the text's default base line, as a float.
     */
    public float getTextRise() {
        return (float)this.<Float>getProperty(Property.TEXT_RISE);
    }

    /**
     * Sets the text rise.
     * @param textRise a vertical distance from the text's default base line.
     * @return this Text
     */
    public Text setTextRise(float textRise) {
        setProperty(Property.TEXT_RISE, textRise);
        return (Text) (Object) this;
    }

    /**
     * Gets the horizontal scaling property, which determines how wide the text
     * should be stretched.
     * @return the horizontal spacing, as a <code>float</code>
     */
    public Float getHorizontalScaling() {
        return this.<Float>getProperty(Property.HORIZONTAL_SCALING);
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
        setProperty(Property.SKEW, new float[]{alpha, beta});
        return this;
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
        setProperty(Property.HORIZONTAL_SCALING, horizontalScaling);
        return (Text) (Object) this;
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.SPAN);
        }
        return tagProperties;
    }

    /**
     * Give this element a neutral role. See also {@link AccessibilityProperties#setRole(String)}.
     *
     * @return this Element
     */
    public Text setNeutralRole() {
        this.getAccessibilityProperties().setRole(null);
        return this;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new TextRenderer(this, text);
    }
}

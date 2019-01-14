/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.layout.property.Property;
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
            throw new IllegalArgumentException();
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

    @Override
    protected IRenderer makeNewRenderer() {
        return new TextRenderer(this, text);
    }
}

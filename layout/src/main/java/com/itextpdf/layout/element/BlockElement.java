/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.BlockRenderer;

/**
 * A {@link BlockElement} will try to take up as much horizontal space as
 * available to it on the canvas or page. The concept is comparable to the block
 * element in HTML. Also like in HTML, the visual representation of the object
 * can be delimited by padding, a border, and/or a margin.
 * 
 * @param <T> the type of the implementation
 */
public abstract class BlockElement<T extends IElement> extends AbstractElement<T> implements IAccessibleElement {

    /**
     * Creates a BlockElement.
     */
    public BlockElement() {
    }

    /**
     * Gets the current left margin width of the element.
     * @return the left margin width, as a <code>float</code>
     */
    public Float getMarginLeft() {
        return getProperty(Property.MARGIN_LEFT);
    }

    /**
     * Sets the left margin width of the element.
     * @param value the new left margin width
     * @return this element
     */
    public T setMarginLeft(float value) {
        setProperty(Property.MARGIN_LEFT, value);
        return (T) this;
    }

    /**
     * Gets the current right margin width of the element.
     * @return the right margin width, as a <code>float</code>
     */
    public Float getMarginRight() {
        return getProperty(Property.MARGIN_RIGHT);
    }

    /**
     * Sets the right margin width of the element.
     * @param value the new right margin width
     * @return this element
     */
    public T setMarginRight(float value) {
        setProperty(Property.MARGIN_RIGHT, value);
        return (T) this;
    }

    /**
     * Gets the current top margin width of the element.
     * @return the top margin width, as a <code>float</code>
     */
    public Float getMarginTop() {
        return getProperty(Property.MARGIN_TOP);
    }

    /**
     * Sets the top margin width of the element.
     * @param value the new top margin width
     * @return this element
     */
    public T setMarginTop(float value) {
        setProperty(Property.MARGIN_TOP, value);
        return (T) this;
    }

    /**
     * Gets the current bottom margin width of the element.
     * @return the bottom margin width, as a <code>float</code>
     */
    public Float getMarginBottom() {
        return getProperty(Property.MARGIN_BOTTOM);
    }

    /**
     * Sets the bottom margin width of the element.
     * @param value the new bottom margin width
     * @return this element
     */
    public T setMarginBottom(float value) {
        setProperty(Property.MARGIN_BOTTOM, value);
        return (T) this;
    }

    /**
     * Sets all margins around the element to the same width.
     *
     * @param commonMargin the new margin width
     * @return this element
     */
    public T setMargin(float commonMargin) {
        return setMargins(commonMargin, commonMargin, commonMargin, commonMargin);
    }

    /**
     * Sets the margins around the element to a series of new widths.
     *
     * @param marginTop the new margin top width
     * @param marginRight the new margin right width
     * @param marginBottom the new margin bottom width
     * @param marginLeft the new margin left width
     * @return this element
     */
    public T setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        setMarginTop(marginTop);
        setMarginRight(marginRight);
        setMarginBottom(marginBottom);
        setMarginLeft(marginLeft);
        return (T) this;
    }

    /**
     * Gets the current left padding width of the element.
     * @return the left padding width, as a <code>float</code>
     */
    public Float getPaddingLeft() {
        return getProperty(Property.PADDING_LEFT);
    }

    /**
     * Sets the left padding width of the element.
     * @param value the new left padding width
     * @return this element
     */
    public T setPaddingLeft(float value) {
        setProperty(Property.PADDING_LEFT, value);
        return (T) this;
    }

    /**
     * Gets the current right padding width of the element.
     * @return the right padding width, as a <code>float</code>
     */
    public Float getPaddingRight() {
        return getProperty(Property.PADDING_RIGHT);
    }

    /**
     * Sets the right padding width of the element.
     * @param value the new right padding width
     * @return this element
     */
    public T setPaddingRight(float value) {
        setProperty(Property.PADDING_RIGHT, value);
        return (T) this;
    }

    /**
     * Gets the current top padding width of the element.
     * @return the top padding width, as a <code>float</code>
     */
    public Float getPaddingTop() {
        return getProperty(Property.PADDING_TOP);
    }

    /**
     * Sets the top padding width of the element.
     * @param value the new top padding width
     * @return this element
     */
    public T setPaddingTop(float value) {
        setProperty(Property.PADDING_TOP, value);
        return (T) this;
    }

    /**
     * Gets the current bottom padding width of the element.
     * @return the bottom padding width, as a <code>float</code>
     */
    public Float getPaddingBottom() {
        return getProperty(Property.PADDING_BOTTOM);
    }

    /**
     * Sets the bottom padding width of the element.
     * @param value the new bottom padding width
     * @return this element
     */
    public T setPaddingBottom(float value) {
        setProperty(Property.PADDING_BOTTOM, value);
        return (T) this;
    }

    /**
     * Sets all paddings around the element to the same width.
     *
     * @param commonPadding the new padding width
     * @return this element
     */
    public T setPadding(float commonPadding) {
        return setPaddings(commonPadding, commonPadding, commonPadding, commonPadding);
    }

    /**
     * Sets the paddings around the element to a series of new widths.
     *
     * @param paddingTop the new padding top width
     * @param paddingRight the new padding right width
     * @param paddingBottom the new padding bottom width
     * @param paddingLeft the new padding left width
     * @return this element
     */
    public T setPaddings(float paddingTop, float paddingRight, float paddingBottom, float paddingLeft) {
        setPaddingTop(paddingTop);
        setPaddingRight(paddingRight);
        setPaddingBottom(paddingBottom);
        setPaddingLeft(paddingLeft);
        return (T) this;
    }

    /**
     * Sets the vertical alignment of the element.
     *
     * @param verticalAlignment the vertical alignment setting
     * @return this element
     */
    public T setVerticalAlignment(VerticalAlignment verticalAlignment) {
        setProperty(Property.VERTICAL_ALIGNMENT, verticalAlignment);
        return (T) this;
    }

    /**
     * Sets a ratio which determines in which proportion will word spacing and character spacing
     * be applied when horizontal alignment is justified.
     * @param ratio the ratio coefficient. It must be between 0 and 1, inclusive.
     *              It means that <b>ratio</b> part of the free space will
     *              be compensated by word spacing, and <b>1-ratio</b> part of the free space will
     *              be compensated by character spacing.
     *              If <b>ratio</b> is 1, additional character spacing will not be applied.
     *              If <b>ratio</b> is 0, additional word spacing will not be applied.
     */
    public T setSpacingRatio(float ratio) {
        setProperty(Property.SPACING_RATIO, ratio);
        return (T) this;
    }

    @Override
    public <T1> T1 getDefaultProperty(Property property) {
        switch (property) {
            case KEEP_TOGETHER:
                return (T1) Boolean.valueOf(false);
            default:
                return super.getDefaultProperty(property);
        }
    }

    /**
     * Returns whether the {@link BlockElement} should be kept together as much
     * as possible.
     * @return the current value of the {@link Property#KEEP_TOGETHER} property
     */
    public Boolean isKeepTogether() {
        return getProperty(Property.KEEP_TOGETHER);
    }

    /**
     * Sets whether the {@link BlockElement} should be kept together as much
     * as possible.
     * @param keepTogether the new value of the {@link Property#KEEP_TOGETHER} property
     * @return this element
     */
    public T setKeepTogether(boolean keepTogether) {
        setProperty(Property.KEEP_TOGETHER, keepTogether);
        return (T) this;
    }

    /**
     * Sets the rotation radAngle.
     * 
     * @param radAngle the new rotation radAngle, as a <code>float</code>
     * @return this element
     */
    public T setRotationAngle(float radAngle) {
        setProperty(Property.ROTATION_ANGLE, radAngle);
        return (T) this;
    }

    /**
     * Sets the rotation angle.
     * 
     * @param angle the new rotation angle, as a <code>double</code>
     * @return this element
     */
    public T setRotationAngle(double angle) {
        setProperty(Property.ROTATION_ANGLE, (float) angle);
        return (T) this;
    }

    @Override
    protected BlockRenderer makeNewRenderer() {
        return new BlockRenderer(this);
    }
}

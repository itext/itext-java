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

import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.tagging.IAccessibleElement;

/**
 * A {@link BlockElement} will try to take up as much horizontal space as
 * available to it on the canvas or page. The concept is comparable to the block
 * element in HTML. Also like in HTML, the visual representation of the object
 * can be delimited by padding, a border, and/or a margin.
 *
 * @param <T> the type of the implementation
 */
public abstract class BlockElement<T extends IElement> extends AbstractElement<T> implements IAccessibleElement, IBlockElement {

    /**
     * Creates a BlockElement.
     */
    protected BlockElement() {
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.OVERFLOW_X:
            case Property.OVERFLOW_Y:
                return (T1) (Object) OverflowPropertyValue.FIT;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * Gets the current left margin width of the element.
     *
     * @return the left margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginLeft() {
        return this.<UnitValue>getProperty(Property.MARGIN_LEFT);
    }

    /**
     * Sets the left margin width of the element.
     *
     * @param value the new left margin width
     * @return this element
     */
    public T setMarginLeft(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_LEFT, marginUV);
        return (T) (Object) this;
    }

    /**
     * Gets the current right margin width of the element.
     *
     * @return the right margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginRight() {
        return this.<UnitValue>getProperty(Property.MARGIN_RIGHT);
    }

    /**
     * Sets the right margin width of the element.
     *
     * @param value the new right margin width
     * @return this element
     */
    public T setMarginRight(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_RIGHT, marginUV);
        return (T) (Object) this;
    }

    /**
     * Gets the current top margin width of the element.
     *
     * @return the top margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginTop() {
        return this.<UnitValue>getProperty(Property.MARGIN_TOP);
    }

    /**
     * Sets the top margin width of the element.
     *
     * @param value the new top margin width
     * @return this element
     */
    public T setMarginTop(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_TOP, marginUV);
        return (T) (Object) this;
    }

    /**
     * Gets the current bottom margin width of the element.
     *
     * @return the bottom margin width, as a {@link UnitValue} object
     */
    public UnitValue getMarginBottom() {
        return this.<UnitValue>getProperty(Property.MARGIN_BOTTOM);
    }

    /**
     * Sets the bottom margin width of the element.
     *
     * @param value the new bottom margin width
     * @return this element
     */
    public T setMarginBottom(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_BOTTOM, marginUV);
        return (T) (Object) this;
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
     * @param marginTop    the new margin top width
     * @param marginRight  the new margin right width
     * @param marginBottom the new margin bottom width
     * @param marginLeft   the new margin left width
     * @return this element
     */
    public T setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        setMarginTop(marginTop);
        setMarginRight(marginRight);
        setMarginBottom(marginBottom);
        setMarginLeft(marginLeft);
        return (T) (Object) this;
    }

    /**
     * Gets the current left padding width of the element.
     *
     * @return the left padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingLeft() {
        return this.<UnitValue>getProperty(Property.PADDING_LEFT);
    }

    /**
     * Sets the left padding width of the element.
     *
     * @param value the new left padding width
     * @return this element
     */
    public T setPaddingLeft(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_LEFT, paddingUV);
        return (T) (Object) this;
    }

    /**
     * Gets the current right padding width of the element.
     *
     * @return the right padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingRight() {
        return this.<UnitValue>getProperty(Property.PADDING_RIGHT);
    }

    /**
     * Sets the right padding width of the element.
     *
     * @param value the new right padding width
     * @return this element
     */
    public T setPaddingRight(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_RIGHT, paddingUV);
        return (T) (Object) this;
    }

    /**
     * Gets the current top padding width of the element.
     *
     * @return the top padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingTop() {
        return this.<UnitValue>getProperty(Property.PADDING_TOP);
    }

    /**
     * Sets the top padding width of the element.
     *
     * @param value the new top padding width
     * @return this element
     */
    public T setPaddingTop(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_TOP, paddingUV);
        return (T) (Object) this;
    }

    /**
     * Gets the current bottom padding width of the element.
     *
     * @return the bottom padding width, as a {@link UnitValue} object
     */
    public UnitValue getPaddingBottom() {
        return this.<UnitValue>getProperty(Property.PADDING_BOTTOM);
    }

    /**
     * Sets the bottom padding width of the element.
     *
     * @param value the new bottom padding width
     * @return this element
     */
    public T setPaddingBottom(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_BOTTOM, paddingUV);
        return (T) (Object) this;
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
     * @param paddingTop    the new padding top width
     * @param paddingRight  the new padding right width
     * @param paddingBottom the new padding bottom width
     * @param paddingLeft   the new padding left width
     * @return this element
     */
    public T setPaddings(float paddingTop, float paddingRight, float paddingBottom, float paddingLeft) {
        setPaddingTop(paddingTop);
        setPaddingRight(paddingRight);
        setPaddingBottom(paddingBottom);
        setPaddingLeft(paddingLeft);
        return (T) (Object) this;
    }

    /**
     * Sets the vertical alignment of the element.
     *
     * @param verticalAlignment the vertical alignment setting
     * @return this element
     */
    public T setVerticalAlignment(VerticalAlignment verticalAlignment) {
        setProperty(Property.VERTICAL_ALIGNMENT, verticalAlignment);
        return (T) (Object) this;
    }

    /**
     * Sets a ratio which determines in which proportion will word spacing and character spacing
     * be applied when horizontal alignment is justified.
     *
     * @param ratio the ratio coefficient. It must be between 0 and 1, inclusive.
     *              It means that <strong>ratio</strong> part of the free space will
     *              be compensated by word spacing, and <strong>1-ratio</strong> part of the free space will
     *              be compensated by character spacing.
     *              If <strong>ratio</strong> is 1, additional character spacing will not be applied.
     *              If <strong>ratio</strong> is 0, additional word spacing will not be applied.
     * @return this element
     */
    public T setSpacingRatio(float ratio) {
        setProperty(Property.SPACING_RATIO, ratio);
        return (T) (Object) this;
    }

    /**
     * Returns whether the {@link BlockElement} should be kept together as much
     * as possible.
     *
     * @return the current value of the {@link Property#KEEP_TOGETHER} property
     */
    public Boolean isKeepTogether() {
        return this.<Boolean>getProperty(Property.KEEP_TOGETHER);
    }

    /**
     * Sets whether the {@link BlockElement} should be kept together as much
     * as possible.
     *
     * @param keepTogether the new value of the {@link Property#KEEP_TOGETHER} property
     * @return this element
     */
    public T setKeepTogether(boolean keepTogether) {
        setProperty(Property.KEEP_TOGETHER, keepTogether);
        return (T) (Object) this;
    }

    /**
     * Returns whether the end of this {@link BlockElement} and the start of the next sibling of this element
     * should be placed in the same area.
     *
     * @return the current value of the {@link Property#KEEP_WITH_NEXT} property
     */
    public Boolean isKeepWithNext() {
        return this.<Boolean>getProperty(Property.KEEP_WITH_NEXT);
    }

    /**
     * Sets whether the end of this {@link BlockElement} and the start of the next sibling of this element
     * should be placed in the same area.
     * Note that this will only work for high-level elements, i.e. elements added to the {@link com.itextpdf.layout.RootElement}.
     *
     * @param keepWithNext the new value of the {@link Property#KEEP_WITH_NEXT} property
     * @return this element
     */
    public T setKeepWithNext(boolean keepWithNext) {
        setProperty(Property.KEEP_WITH_NEXT, keepWithNext);
        return (T) (Object) this;
    }

    /**
     * Sets the rotation radAngle.
     *
     * @param angleInRadians the new rotation radAngle, as a <code>float</code>, in radians
     * @return this element
     */
    public T setRotationAngle(float angleInRadians) {
        setProperty(Property.ROTATION_ANGLE, angleInRadians);
        return (T) (Object) this;
    }

    /**
     * Sets the rotation angle.
     *
     * @param angleInRadians the new rotation angle, as a <code>double</code>, in radians
     * @return this element
     */
    public T setRotationAngle(double angleInRadians) {
        setProperty(Property.ROTATION_ANGLE, (float) angleInRadians);
        return (T) (Object) this;
    }

    /**
     * Sets the width property of a block element, measured in points.
     *
     * @param width a value measured in points.
     * @return this Element.
     */
    public T setWidth(float width) {
        setProperty(Property.WIDTH, UnitValue.createPointValue(width));
        return (T) (Object) this;
    }

    /**
     * Sets the width property of a block element with a {@link UnitValue}.
     *
     * @param width a {@link UnitValue} object
     * @return this Element.
     */
    public T setWidth(UnitValue width) {
        setProperty(Property.WIDTH, width);
        return (T) (Object) this;
    }

    /**
     * Gets the width property of a block element.
     *
     * @return the width of the element, with a value and a measurement unit.
     * @see UnitValue
     */
    public UnitValue getWidth() {
        return (UnitValue) this.<UnitValue>getProperty(Property.WIDTH);
    }

    /**
     * Sets the height property of a block element with a {@link UnitValue}.
     *
     * @param height a {@link UnitValue} object
     * @return this Element.
     */
    public T setHeight(UnitValue height) {
        setProperty(Property.HEIGHT, height);
        return (T) (Object) this;
    }

    /**
     * Sets the height property a block element as a point-value.
     *
     * @param height a floating point value for the new height
     * @return the block element itself.
     */
    public T setHeight(float height) {
        UnitValue heightAsUV = UnitValue.createPointValue(height);
        setProperty(Property.HEIGHT, heightAsUV);
        return (T) (Object) this;
    }

    /**
     * Gets the height property of a block element.
     *
     * @return the height of the element, as a floating point value. Null if the property is not present
     */
    public UnitValue getHeight() {
        return (UnitValue) this.<UnitValue>getProperty(Property.HEIGHT);
    }

    /**
     * Sets the max-height of a block element as point-unit value.
     *
     * @param maxHeight a floating point value for the new max-height
     * @return the block element itself
     */
    public T setMaxHeight(float maxHeight) {
        UnitValue maxHeightAsUV = UnitValue.createPointValue(maxHeight);
        setProperty(Property.MAX_HEIGHT, maxHeightAsUV);
        return (T) (Object) this;
    }

    /**
     * Sets the max-height property of a block element with a {@link UnitValue}.
     *
     * @param maxHeight a {@link UnitValue} object
     * @return the block element itself
     */
    public T setMaxHeight(UnitValue maxHeight) {
        setProperty(Property.MAX_HEIGHT, maxHeight);
        return (T) (Object) this;
    }

    /**
     * Sets the min-height property of a block element with a {@link UnitValue}.
     *
     * @param minHeight a {@link UnitValue} object
     * @return the block element itself
     */
    public T setMinHeight(UnitValue minHeight) {
        setProperty(Property.MIN_HEIGHT, minHeight);
        return (T) (Object) this;
    }

    /**
     * Sets the min-height of a block element as point-unit value.
     *
     * @param minHeight a floating point value for the new min-height
     * @return the block element itself
     */
    public T setMinHeight(float minHeight) {
        UnitValue minHeightAsUV = UnitValue.createPointValue(minHeight);
        setProperty(Property.MIN_HEIGHT, minHeightAsUV);
        return (T) (Object) this;
    }

    /**
     * Sets the max-width property of a block element with a {@link UnitValue}.
     *
     * @param maxWidth a {@link UnitValue} object
     * @return the block element itself
     */
    public T setMaxWidth(UnitValue maxWidth) {
        setProperty(Property.MAX_WIDTH, maxWidth);
        return (T) (Object) this;
    }

    /**
     * Sets the max-width of a block element as point-unit value.
     *
     * @param maxWidth a floating point value for the new max-width
     * @return the block element itself
     */
    public T setMaxWidth(float maxWidth) {
        setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(maxWidth));
        return (T) (Object) this;
    }

    /**
     * Sets the min-width property of a block element with a {@link UnitValue}.
     *
     * @param minWidth a {@link UnitValue} object
     * @return the block element itself
     */
    public T setMinWidth(UnitValue minWidth) {
        setProperty(Property.MIN_WIDTH, minWidth);
        return (T) (Object) this;
    }

    /**
     * Sets the min-width of a block element as point-unit value.
     *
     * @param minWidth a floating point value for the new min-width
     * @return the block element itself
     */
    public T setMinWidth(float minWidth) {
        setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(minWidth));
        return (T) (Object) this;
    }

    /**
     * Give this element a neutral role. See also {@link AccessibilityProperties#setRole(String)}.
     *
     * @return this Element
     */
    public T setNeutralRole() {
        this.getAccessibilityProperties().setRole(null);
        // cast to Object is necessary for autoporting reasons
        return (T) (Object) this;
    }
}

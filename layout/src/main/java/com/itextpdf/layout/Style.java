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
package com.itextpdf.layout;

import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

/**
 * Container object for style properties of an element. A style can be used as
 * an effective way to define multiple equal properties to several elements.
 * Used in {@link AbstractElement}.
 *
 * The properties set via Style have a lower priority than directly set properties.
 * For example, if the same property is set directly and added via Style, then,
 * no matter in which order they are set, the one set directly will be chosen.
 */
public class Style extends ElementPropertyContainer<Style> {

    public Style() {
    }

    public Style(Style style) {
        properties.putAll(style.properties);
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
    public Style setMarginLeft(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_LEFT, marginUV);
        return (Style) (Object) this;
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
    public Style setMarginRight(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_RIGHT, marginUV);
        return (Style) (Object) this;
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
    public Style setMarginTop(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_TOP, marginUV);
        return (Style) (Object) this;
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
    public Style setMarginBottom(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_BOTTOM, marginUV);
        return (Style) (Object) this;
    }

    /**
     * Sets all margins around the element to the same width.
     *
     * @param commonMargin the new margin width
     * @return this element
     */
    public Style setMargin(float commonMargin) {
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
    public Style setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        setMarginTop(marginTop);
        setMarginRight(marginRight);
        setMarginBottom(marginBottom);
        setMarginLeft(marginLeft);
        return (Style) (Object) this;
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
    public Style setPaddingLeft(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_LEFT, paddingUV);
        return (Style) (Object) this;
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
    public Style setPaddingRight(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_RIGHT, paddingUV);
        return (Style) (Object) this;
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
    public Style setPaddingTop(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_TOP, paddingUV);
        return (Style) (Object) this;
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
    public Style setPaddingBottom(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_BOTTOM, paddingUV);
        return (Style) (Object) this;
    }

    /**
     * Sets all paddings around the element to the same width.
     *
     * @param commonPadding the new padding width
     * @return this element
     */
    public Style setPadding(float commonPadding) {
        return (Style) (Object) setPaddings(commonPadding, commonPadding, commonPadding, commonPadding);
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
    public Style setPaddings(float paddingTop, float paddingRight, float paddingBottom, float paddingLeft) {
        setPaddingTop(paddingTop);
        setPaddingRight(paddingRight);
        setPaddingBottom(paddingBottom);
        setPaddingLeft(paddingLeft);
        return (Style) (Object) this;
    }

    /**
     * Sets the vertical alignment of the element.
     *
     * @param verticalAlignment the vertical alignment setting
     * @return this element
     */
    public Style setVerticalAlignment(VerticalAlignment verticalAlignment) {
        setProperty(Property.VERTICAL_ALIGNMENT, verticalAlignment);
        return (Style) (Object) this;
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
    public Style setSpacingRatio(float ratio) {
        setProperty(Property.SPACING_RATIO, ratio);
        return (Style) (Object) this;
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
    public Style setKeepTogether(boolean keepTogether) {
        setProperty(Property.KEEP_TOGETHER, keepTogether);
        return (Style) (Object) this;
    }

    /**
     * Sets the rotation radAngle.
     *
     * @param radAngle the new rotation radAngle, as a <code>float</code>
     * @return this element
     */
    public Style setRotationAngle(float radAngle) {
        setProperty(Property.ROTATION_ANGLE, radAngle);
        return (Style) (Object) this;
    }

    /**
     * Sets the rotation angle.
     *
     * @param angle the new rotation angle, as a <code>double</code>
     * @return this element
     */
    public Style setRotationAngle(double angle) {
        setProperty(Property.ROTATION_ANGLE, (float) angle);
        return (Style) (Object) this;
    }

    /**
     * Sets the width property of the element, measured in points.
     *
     * @param width a value measured in points.
     * @return this Element.
     */
    public Style setWidth(float width) {
        setProperty(Property.WIDTH, UnitValue.createPointValue(width));
        return (Style) (Object) this;
    }

    /**
     * Sets the width property of the element with a {@link UnitValue}.
     *
     * @param width a {@link UnitValue} object
     * @return this Element.
     */
    public Style setWidth(UnitValue width) {
        setProperty(Property.WIDTH, width);
        return (Style) (Object) this;
    }

    /**
     * Gets the width property of the element.
     *
     * @return the width of the element, with a value and a measurement unit.
     * @see UnitValue
     */
    public UnitValue getWidth() {
        return (UnitValue) this.<UnitValue>getProperty(Property.WIDTH);
    }

    /**
     * Sets the height property of the element with a {@link UnitValue}.
     *
     * @param height a {@link UnitValue} object
     * @return this Element.
     */
    public Style setHeight(UnitValue height) {
        setProperty(Property.HEIGHT, height);
        return (Style) (Object) this;
    }

    /**
     * Sets the height property the element as a point-value.
     *
     * @param height a floating point value for the new height
     * @return the block element itself.
     */
    public Style setHeight(float height) {
        UnitValue heightAsUV = UnitValue.createPointValue(height);
        setProperty(Property.HEIGHT, heightAsUV);
        return (Style) (Object) this;
    }

    /**
     * Gets the height property of the element.
     *
     * @return the height of the element, as a floating point value. Null if the property is not present
     */
    public UnitValue getHeight() {
        return (UnitValue) this.<UnitValue>getProperty(Property.HEIGHT);
    }

    /**
     * Sets the max-height of the element as point-unit value.
     *
     * @param maxHeight a floating point value for the new max-height
     * @return the block element itself
     */
    public Style setMaxHeight(float maxHeight) {
        UnitValue maxHeightAsUV = UnitValue.createPointValue(maxHeight);
        setProperty(Property.MAX_HEIGHT, maxHeightAsUV);
        return (Style) (Object) this;
    }

    /**
     * Sets the max-height property of the element with a {@link UnitValue}.
     *
     * @param maxHeight a {@link UnitValue} object
     * @return the block element itself
     */
    public Style setMaxHeight(UnitValue maxHeight) {
        setProperty(Property.MAX_HEIGHT, maxHeight);
        return (Style) (Object) this;
    }

    /**
     * Sets the min-height property of the element with a {@link UnitValue}.
     *
     * @param minHeight a {@link UnitValue} object
     * @return the block element itself
     */
    public Style setMinHeight(UnitValue minHeight) {
        setProperty(Property.MIN_HEIGHT, minHeight);
        return (Style) (Object) this;
    }

    /**
     * Sets the min-height of the element as point-unit value.
     *
     * @param minHeight a floating point value for the new min-height
     * @return the block element itself
     */
    public Style setMinHeight(float minHeight) {
        UnitValue minHeightAsUV = UnitValue.createPointValue(minHeight);
        setProperty(Property.MIN_HEIGHT, minHeightAsUV);
        return (Style) (Object) this;
    }

    /**
     * Sets the max-width property of the element with a {@link UnitValue}.
     *
     * @param maxWidth a {@link UnitValue} object
     * @return the block element itself
     */
    public Style setMaxWidth(UnitValue maxWidth) {
        setProperty(Property.MAX_WIDTH, maxWidth);
        return (Style) (Object) this;
    }

    /**
     * Sets the max-width of the element as point-unit value.
     *
     * @param maxWidth a floating point value for the new max-width
     * @return the block element itself
     */
    public Style setMaxWidth(float maxWidth) {
        setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(maxWidth));
        return (Style) (Object) this;
    }

    /**
     * Sets the min-width property of the element with a {@link UnitValue}.
     *
     * @param minWidth a {@link UnitValue} object
     * @return the block element itself
     */
    public Style setMinWidth(UnitValue minWidth) {
        setProperty(Property.MIN_WIDTH, minWidth);
        return (Style) (Object) this;
    }

    /**
     * Sets the min-width of the element as point-unit value.
     *
     * @param minWidth a floating point value for the new min-width
     * @return the block element itself
     */
    public Style setMinWidth(float minWidth) {
        setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(minWidth));
        return (Style) (Object) this;
    }
}

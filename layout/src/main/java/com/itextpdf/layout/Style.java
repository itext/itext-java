/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.layout.properties.Transform;
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
     *
     * @return this {@link Style} instance
     */
    public Style setMarginLeft(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_LEFT, marginUV);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setMarginRight(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_RIGHT, marginUV);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setMarginTop(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_TOP, marginUV);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setMarginBottom(float value) {
        UnitValue marginUV = UnitValue.createPointValue(value);
        setProperty(Property.MARGIN_BOTTOM, marginUV);
        return this;
    }

    /**
     * Sets all margins around the element to the same width.
     *
     * @param commonMargin the new margin width
     *
     * @return this {@link Style} instance
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
     *
     * @return this {@link Style} instance
     */
    public Style setMargins(float marginTop, float marginRight, float marginBottom, float marginLeft) {
        setMarginTop(marginTop);
        setMarginRight(marginRight);
        setMarginBottom(marginBottom);
        setMarginLeft(marginLeft);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setPaddingLeft(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_LEFT, paddingUV);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setPaddingRight(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_RIGHT, paddingUV);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setPaddingTop(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_TOP, paddingUV);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setPaddingBottom(float value) {
        UnitValue paddingUV = UnitValue.createPointValue(value);
        setProperty(Property.PADDING_BOTTOM, paddingUV);
        return this;
    }

    /**
     * Sets all paddings around the element to the same width.
     *
     * @param commonPadding the new padding width
     *
     * @return this {@link Style} instance
     */
    public Style setPadding(float commonPadding) {
        return setPaddings(commonPadding, commonPadding, commonPadding, commonPadding);
    }

    /**
     * Sets the paddings around the element to a series of new widths.
     *
     * @param paddingTop    the new padding top width
     * @param paddingRight  the new padding right width
     * @param paddingBottom the new padding bottom width
     * @param paddingLeft   the new padding left width
     *
     * @return this {@link Style} instance
     */
    public Style setPaddings(float paddingTop, float paddingRight, float paddingBottom, float paddingLeft) {
        setPaddingTop(paddingTop);
        setPaddingRight(paddingRight);
        setPaddingBottom(paddingBottom);
        setPaddingLeft(paddingLeft);
        return this;
    }

    /**
     * Sets the vertical alignment of the element.
     *
     * @param verticalAlignment the vertical alignment setting
     *
     * @return this {@link Style} instance
     */
    public Style setVerticalAlignment(VerticalAlignment verticalAlignment) {
        setProperty(Property.VERTICAL_ALIGNMENT, verticalAlignment);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setSpacingRatio(float ratio) {
        setProperty(Property.SPACING_RATIO, ratio);
        return this;
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
     *
     * @return this {@link Style} instance
     */
    public Style setKeepTogether(boolean keepTogether) {
        setProperty(Property.KEEP_TOGETHER, keepTogether);
        return this;
    }


    /**
     * Sets the rotation angle in this style.
     *
     * <p>
     * The angle is specified in radians. Positive values rotate counter-clockwise,
     * negative values rotate clockwise.
     *
     * <p>
     * When this style is applied to an element, rotation is performed during rendering,
     * and layout uses a bounding box that encloses the rotated content.
     *
     * @param radAngle the rotation angle, in radians
     *
     * @return this {@link Style} instance
     */
    public Style setRotationAngle(float radAngle) {
        setProperty(Property.ROTATION_ANGLE, radAngle);
        return this;
    }

    /**
     * Sets the rotation angle in this style.
     *
     * <p>
     * Convenience overload of {@link #setRotationAngle(float)}.
     *
     * @param angle the rotation angle, in radians
     *
     * @return this {@link Style} instance
     */
    public Style setRotationAngle(double angle) {
        setProperty(Property.ROTATION_ANGLE, (float) angle);
        return this;
    }

    /**
     * Sets a transformation to be applied during rendering.
     *
     * @param transform a {@link Transform} describing the sequence of transform operations
     *                  (for example, translate, scale, rotate, skew)
     *
     * @return this {@link Style} instance
     */
    public Style setTransform(Transform transform) {
        setProperty(Property.TRANSFORM, transform);
        return this;
    }

    /**
     * Sets the width property of the element, measured in points.
     *
     * @param width a value measured in points
     *
     * @return this {@link Style} instance
     */
    public Style setWidth(float width) {
        setProperty(Property.WIDTH, UnitValue.createPointValue(width));
        return this;
    }

    /**
     * Sets the width property of the element with a {@link UnitValue}.
     *
     * @param width a {@link UnitValue} object
     *
     * @return this {@link Style} instance
     */
    public Style setWidth(UnitValue width) {
        setProperty(Property.WIDTH, width);
        return this;
    }

    /**
     * Gets the width property of the element.
     *
     * @return the width of the element, with a value and a measurement unit.
     * @see UnitValue
     */
    public UnitValue getWidth() {
        return this.<UnitValue>getProperty(Property.WIDTH);
    }

    /**
     * Sets the height property of the element with a {@link UnitValue}.
     *
     * @param height a {@link UnitValue} object
     *
     * @return this {@link Style} instance
     */
    public Style setHeight(UnitValue height) {
        setProperty(Property.HEIGHT, height);
        return this;
    }

    /**
     * Sets the height property the element as a point-value.
     *
     * @param height a floating point value for the new height
     *
     * @return this {@link Style} instance
     */
    public Style setHeight(float height) {
        UnitValue heightAsUV = UnitValue.createPointValue(height);
        setProperty(Property.HEIGHT, heightAsUV);
        return this;
    }

    /**
     * Gets the height property of the element.
     *
     * @return the height of the element, as a floating point value. Null if the property is not present
     */
    public UnitValue getHeight() {
        return this.<UnitValue>getProperty(Property.HEIGHT);
    }

    /**
     * Sets the max height of the element as point-unit value.
     *
     * @param maxHeight a floating point value for the new max height
     *
     * @return this {@link Style} instance
     */
    public Style setMaxHeight(float maxHeight) {
        UnitValue maxHeightAsUV = UnitValue.createPointValue(maxHeight);
        setProperty(Property.MAX_HEIGHT, maxHeightAsUV);
        return this;
    }

    /**
     * Sets the max height property of the element with a {@link UnitValue}.
     *
     * @param maxHeight a {@link UnitValue} object
     *
     * @return this {@link Style} instance
     */
    public Style setMaxHeight(UnitValue maxHeight) {
        setProperty(Property.MAX_HEIGHT, maxHeight);
        return this;
    }

    /**
     * Sets the min height property of the element with a {@link UnitValue}.
     *
     * @param minHeight a {@link UnitValue} object
     *
     * @return this {@link Style} instance
     */
    public Style setMinHeight(UnitValue minHeight) {
        setProperty(Property.MIN_HEIGHT, minHeight);
        return this;
    }

    /**
     * Sets the min height of the element as point-unit value.
     *
     * @param minHeight a floating point value for the new min-height
     *
     * @return this {@link Style} instance
     */
    public Style setMinHeight(float minHeight) {
        UnitValue minHeightAsUV = UnitValue.createPointValue(minHeight);
        setProperty(Property.MIN_HEIGHT, minHeightAsUV);
        return this;
    }

    /**
     * Sets the max width property of the element with a {@link UnitValue}.
     *
     * @param maxWidth a {@link UnitValue} object
     *
     * @return this {@link Style} instance
     */
    public Style setMaxWidth(UnitValue maxWidth) {
        setProperty(Property.MAX_WIDTH, maxWidth);
        return this;
    }

    /**
     * Sets the max width of the element as point-unit value.
     *
     * @param maxWidth a floating point value for the new max-width
     *
     * @return this {@link Style} instance
     */
    public Style setMaxWidth(float maxWidth) {
        setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(maxWidth));
        return this;
    }

    /**
     * Sets the min width property of the element with a {@link UnitValue}.
     *
     * @param minWidth a {@link UnitValue} object
     *
     * @return this {@link Style} instance
     */
    public Style setMinWidth(UnitValue minWidth) {
        setProperty(Property.MIN_WIDTH, minWidth);
        return this;
    }

    /**
     * Sets the min width of the element as point-unit value.
     *
     * @param minWidth a floating point value for the new min-width
     *
     * @return this {@link Style} instance
     */
    public Style setMinWidth(float minWidth) {
        setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(minWidth));
        return this;
    }

    /**
     * Sets the text rise of the element.
     *
     * @param textRise the new text rise in points
     *
     * @return this style
     */
    public Style setTextRise(float textRise) {
        setProperty(Property.TEXT_RISE, textRise);
        return (Style) (Object) this;
    }
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.FormDefaultAccessibilityProperties;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.InputFieldRenderer;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Extension of the {@link FormField} class representing a button so that
 * a {@link InputFieldRenderer} is used.
 */
public class InputField extends FormField<InputField> implements IPlaceholderable {

    /**
     * Default padding X offset.
     */
    private static final float X_OFFSET = 2;

    /**
     * The placeholder paragraph.
     */
    private Paragraph placeholder;

    /**
     * Field rotation, counterclockwise. Must be a multiple of 90 degrees.
     */
    private int rotation = 0;

    /**
     * Creates a new input field.
     *
     * @param id the id
     */
    public InputField(String id) {
        super(id);
        setProperties();
    }

    /**
     * Determines, whether the input field will be password.
     * 
     * <p>
     * Usually means that instead of glyphs '*' will be shown in case of flatten field.
     * 
     * <p>
     * If the field is not flatten, value will be ignored.
     * 
     * @param isPassword {@code true} is this field shall be considered as password, {@code false} otherwise
     * 
     * @return this input field
     */
    public InputField useAsPassword(boolean isPassword) {
        setProperty(FormProperty.FORM_FIELD_PASSWORD_FLAG, isPassword);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Paragraph getPlaceholder() {
        return placeholder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlaceholder(Paragraph placeholder) {
        this.placeholder = placeholder;
    }

    /* (non-Javadoc)
     * @see FormField#getDefaultProperty(int)
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case FormProperty.FORM_FIELD_PASSWORD_FLAG:
                return (T1) (Object) false;
            case FormProperty.FORM_FIELD_SIZE:
                return (T1) (Object) 20;
            case FormProperty.TEXT_FIELD_COMB_FLAG:
                return (T1) (Object) false;
            case FormProperty.TEXT_FIELD_MAX_LEN:
                return (T1) (Object) 0;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * Get rotation.
     *
     * @return rotation value.
     */
    public int getRotation() {
        return this.rotation;
    }

    /**
     * Set rotation of the input field.
     *
     * @param rotation new rotation value, counterclockwise. Must be a multiple of 90 degrees.
     *                 It has sense only in interactive mode, see {@link FormField#setInteractive}
     *
     * @return the edited {@link InputField}
     */
    public InputField setRotation(int rotation) {
        if (rotation % 90 != 0) {
            throw new IllegalArgumentException(FormsExceptionMessageConstant.INVALID_ROTATION_VALUE);
        }

        this.rotation = rotation;
        return this;
    }

    /**
     * Sets {@code Comb} flag for the text field. Meaningful only if the MaxLen entry is present in the text field
     * dictionary and if the Multiline, Password and FileSelect flags are clear.
     *
     * <p>
     * If true, the field is automatically divided into as many equally spaced positions, or combs,
     * as the value of MaxLen, and the text is laid out into those combs.
     *
     * @param isComb boolean value specifying whether to enable combing
     *
     * @return this {@link InputField} instance
     */
    public InputField setComb(boolean isComb) {
        setProperty(FormProperty.TEXT_FIELD_COMB_FLAG, isComb);
        return this;
    }

    /**
     * Sets the maximum length of the field's text, in characters.
     *
     * @param maxLen the current maximum text length
     *
     * @return this {@link InputField} instance
     */
    public InputField setMaxLen(int maxLen) {
        setProperty(FormProperty.TEXT_FIELD_MAX_LEN, maxLen);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new FormDefaultAccessibilityProperties(FormDefaultAccessibilityProperties.FORM_FIELD_TEXT);
        }

        return tagProperties;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.element.AbstractElement#makeNewRenderer()
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new InputFieldRenderer(this);
    }

    private void setProperties() {
        setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(X_OFFSET));
        setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(X_OFFSET));
        setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
    }
}

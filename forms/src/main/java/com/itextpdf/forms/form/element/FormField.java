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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.tagging.IAccessibleElement;

/**
 * Implementation of the {@link AbstractElement} class for form fields.
 *
 * @param <T> the generic type of the form field (e.g. input field, button, text area)
 */
public abstract class FormField<T extends IFormField> extends AbstractElement<T> implements IFormField,
        IAccessibleElement {

    /** The id. */
    private final String id;


    /** The tag properties. */
    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Instantiates a new {@link FormField} instance.
     *
     * @param id the id
     */
    FormField(String id) {
        if (id == null || id.contains(".")) {
            throw new IllegalArgumentException("id should not contain '.'");
        }
        this.id = id;
    }

    /**
     * Sets the form field's width and height.
     *
     * @param size form field's width and height.
     *
     * @return this same {@link FormField} element.
     */
    public T setSize(float size) {
        setProperty(Property.WIDTH, UnitValue.createPointValue(size));
        setProperty(Property.HEIGHT, UnitValue.createPointValue(size));

        return (T) (Object) this;
    }

    /**
     * {@inheritDoc}
     *
     * @param width {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IFormField setWidth(float width) {
        setProperty(Property.WIDTH, UnitValue.createPointValue(width));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param height {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IFormField setHeight(float height) {
        setProperty(Property.HEIGHT, UnitValue.createPointValue(height));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param value {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IFormField setValue(String value) {
        setProperty(FormProperty.FORM_FIELD_VALUE, value);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @param property {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case FormProperty.FORM_FIELD_FLATTEN:
                return (T1) (Object) true;
            case FormProperty.FORM_FIELD_VALUE:
                return (T1) (Object) "";
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param interactive {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IFormField setInteractive(boolean interactive) {
        setProperty(FormProperty.FORM_FIELD_FLATTEN, !interactive);
        return this;
    }
}

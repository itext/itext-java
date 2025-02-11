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

import com.itextpdf.forms.FormDefaultAccessibilityProperties;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.RadioRenderer;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Extension of the {@link FormField} class representing a radio button so that
 * a {@link RadioRenderer} is used instead of the default renderer for fields.
 */
public class Radio extends FormField<Radio> {

    /**
     * Creates a new {@link Radio} instance.
     *
     * @param id the id.
     */
    public Radio(String id) {
        super(id);
        // Draw the borders inside the element by default
        setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        // Rounded border
        setBorderRadius(new BorderRadius(UnitValue.createPercentValue(50)));
        // Draw border as a circle by default
        setProperty(FormProperty.FORM_FIELD_RADIO_BORDER_CIRCLE, true);
    }

    /**
     * Creates a new {@link Radio} instance.
     *
     * @param id the id.
     * @param radioGroupName the name of the radio group the radio button belongs to. It has sense only in case
     *                       this Radio element will not be rendered but Acroform field will be created instead.
     */
    public Radio(String id, String radioGroupName) {
        this(id);
        setProperty(FormProperty.FORM_FIELD_RADIO_GROUP_NAME, radioGroupName);
    }

    /**
     * Sets the state of the radio button.
     *
     * @param checked {@code true} if the radio button shall be checked, {@code false} otherwise.
     *                By default, the radio button is unchecked.
     * @return this same {@link Radio} button.
     */
    public Radio setChecked(boolean checked) {
        setProperty(FormProperty.FORM_FIELD_CHECKED, checked);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getProperty(int property) {
        // Paddings do not make sense for radio buttons
        if (property == Property.PADDING_LEFT || property == Property.PADDING_RIGHT ||
                property == Property.PADDING_TOP || property == Property.PADDING_BOTTOM) {
            return (T1)(Object)UnitValue.createPointValue(0);
        }
        return super.<T1>getProperty(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null){
            tagProperties = new FormDefaultAccessibilityProperties(FormDefaultAccessibilityProperties.FORM_FIELD_RADIO);
        }
        if (tagProperties instanceof FormDefaultAccessibilityProperties){
            ((FormDefaultAccessibilityProperties)tagProperties).updateCheckedValue(this);
        }
        return tagProperties;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.element.AbstractElement#makeNewRenderer()
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new RadioRenderer(this);
    }
}

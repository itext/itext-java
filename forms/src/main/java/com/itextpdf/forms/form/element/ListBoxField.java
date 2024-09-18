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
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.SelectFieldListBoxRenderer;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * A field that represents a control for selecting one or several of the provided options.
 */
public class ListBoxField extends AbstractSelectField {

    /**
     * Create a new list box field.
     *
     * @param size the size of the list box, which will define the height of visible properties,
     *             shall be greater than zero
     * @param allowMultipleSelection a boolean flag that defines whether multiple options are allowed
     *                              to be selected at once
     * @param id the id
     */
    public ListBoxField(String id, int size, boolean allowMultipleSelection) {
        super(id);
        setProperty(FormProperty.FORM_FIELD_SIZE, size);
        setProperty(FormProperty.FORM_FIELD_MULTIPLE, allowMultipleSelection);
        setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(1));
        setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(1));
        setProperty(Property.PADDING_TOP, UnitValue.createPointValue(1));
        setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(1));

        // This property allows to show selected item if height is smaller than the size of all items
        setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.HIDDEN);
    }

    /* (non-Javadoc)
     * @see FormField#getDefaultProperty(int)
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case FormProperty.FORM_FIELD_MULTIPLE:
                return (T1) (Object) false;
            case FormProperty.FORM_FIELD_SIZE:
                return (T1) (Object) 4;
            case FormProperty.LIST_BOX_TOP_INDEX:
                return (T1) (Object) 0;
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    /**
     * Add an option for {@link ListBoxField}. The option is not selected.
     *
     * @param option string representation of the option.
     * @return this {@link ListBoxField}.
     */
    public ListBoxField addOption(String option) {
        return addOption(option, false);
    }

    /**
     * Add an option for {@link ListBoxField}.
     *
     * @param option string representation of the option.
     * @param selected {@code true} is the option if selected, {@code false} otherwise.
     * @return this {@link ListBoxField}.
     */
    public ListBoxField addOption(String option, boolean selected) {
        SelectFieldItem item = new SelectFieldItem(option);
        addOption(item);
        item.getElement().setProperty(FormProperty.FORM_FIELD_SELECTED, selected);

        return this;
    }

    /**
     * Get a list of selected options.
     *
     * @return a list of display values of selected options.
     */
    public List<String> getSelectedStrings() {
        List<String> selectedStrings = new ArrayList<String>();
        for (SelectFieldItem option : options) {
            if (Boolean.TRUE.equals(option.getElement().<Boolean>getProperty(FormProperty.FORM_FIELD_SELECTED))) {
                selectedStrings.add(option.getDisplayValue());
            }
        }

        return selectedStrings;
    }

    /**
     * Sets the index of the first visible option in a scrollable list.
     *
     * @param topIndex the index of the first option
     *
     * @return this {@link ListBoxField} instance
     */
    public ListBoxField setTopIndex(int topIndex) {
        setProperty(FormProperty.LIST_BOX_TOP_INDEX, topIndex);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new FormDefaultAccessibilityProperties(
                    FormDefaultAccessibilityProperties.FORM_FIELD_LIST_BOX);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new SelectFieldListBoxRenderer(this);
    }
}

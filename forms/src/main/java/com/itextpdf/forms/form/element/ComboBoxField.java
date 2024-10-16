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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.FormDefaultAccessibilityProperties;
import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.renderer.SelectFieldComboBoxRenderer;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.renderer.IRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A field that represents a control for selecting one of the provided options.
 */
public class ComboBoxField extends AbstractSelectField {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComboBoxField.class);

    private String selectedExportValue;

    /**
     * Creates a new select field box.
     *
     * @param id the id
     */
    public ComboBoxField(String id) {
        super(id);
    }

    /**
     * Gets the export value of the selected option.
     *
     * @return the export value of the selected option. This may be null if no value has been selected.
     */
    public String getSelectedExportValue() {
        return selectedExportValue;
    }


    /**
     * Selects an option by its index. The index is zero-based.
     *
     * @param index the index of the option to select.
     *
     * @return this {@link ComboBoxField} instance.
     *
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public ComboBoxField setSelected(int index) {
        if (index < 0 || index >= this.getOptions().size()) {
            String message = MessageFormatUtil.format(FormsExceptionMessageConstant.INDEX_OUT_OF_BOUNDS, index,
                    this.getOptions().size());
            throw new IndexOutOfBoundsException(message);
        }
        setSelected(this.getOptions().get(index));
        return this;
    }

    /**
     * Selects an option by its export value.
     *
     * @param value the export value of the option to select.
     *
     * @return this {@link ComboBoxField} instance.
     */
    public ComboBoxField setSelected(String value) {
        clearSelected();
        selectedExportValue = value;
        boolean found = false;
        for (SelectFieldItem option : this.getOptions()) {
            if (option.getExportValue().equals(value)) {
                if (!found) {
                    option.getElement().setProperty(FormProperty.FORM_FIELD_SELECTED, true);
                    found = true;
                } else {
                    LOGGER.warn(FormsLogMessageConstants.DUPLICATE_EXPORT_VALUE);
                }
            }
        }
        return this;
    }

    /**
     * Selects an option by its value. This will use the export value of the
     * option to match it to existing options.
     *
     * @param item the option to select.
     *
     * @return this {@link ComboBoxField} instance.
     */
    public ComboBoxField setSelected(SelectFieldItem item) {
        if (item == null) {
            return this;
        }
        setSelected(item.getExportValue());
        return this;
    }

    /**
     * Add an option to the element.
     *
     * @param option a {@link SelectFieldItem}.
     */
    @Override
    public void addOption(SelectFieldItem option) {
        boolean found = false;
        for (SelectFieldItem item : this.getOptions()) {
            if (item.getExportValue().equals(option.getExportValue())) {
                found = true;
                break;
            }
        }
        if (found) {
            LOGGER.warn(FormsLogMessageConstants.DUPLICATE_EXPORT_VALUE);
        }
        super.addOption(option);
    }


    /**
     * Gets the selected option.
     *
     * @return the selected option. This may be null if no option has been selected.
     */
    public SelectFieldItem getSelectedOption() {
        if (selectedExportValue == null) {
            return null;
        }
        for (SelectFieldItem option : this.getOptions()) {
            if (option.getExportValue().equals(selectedExportValue)) {
                return option;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null){
            tagProperties = new FormDefaultAccessibilityProperties(
                    FormDefaultAccessibilityProperties.FORM_FIELD_LIST_BOX);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new SelectFieldComboBoxRenderer(this);
    }

    private void clearSelected() {
        this.selectedExportValue = null;
        for (SelectFieldItem option : this.getOptions()) {
            option.getElement().deleteOwnProperty(FormProperty.FORM_FIELD_SELECTED);
        }
    }
}

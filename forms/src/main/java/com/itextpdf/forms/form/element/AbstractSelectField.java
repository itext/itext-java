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

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class for fields that represents a control for selecting one or several of the provided options.
 */
public abstract class AbstractSelectField extends FormField<AbstractSelectField> {

    protected List<SelectFieldItem> options = new ArrayList<>();

    /**
     * Instantiates a new {@link AbstractSelectField} instance.
     *
     * @param id the id of the field
     */
    protected AbstractSelectField(String id) {
        super(id);
    }

    /**
     * Add an option to the element.
     *
     * @param option a {@link SelectFieldItem}
     */
    public void addOption(SelectFieldItem option) {
        options.add(option);
    }

    /**
     * Add an option to the element.
     *
     * @param option   a {@link SelectFieldItem}
     * @param selected {@code true} is the option if selected, {@code false} otherwise
     */
    public void addOption(SelectFieldItem option, boolean selected) {
        option.getElement().setProperty(FormProperty.FORM_FIELD_SELECTED, selected);
        options.add(option);
    }

    /**
     * Get a list of {@link SelectFieldItem}.
     *
     * @return a list of options.
     */
    public List<SelectFieldItem> getOptions() {
        return options;
    }

    /**
     * Gets the total amount of options available.
     *
     * @return the number of options in the element.
     */
    public int optionsCount() {
        return this.getOptions().size();
    }

    /**
     * Checks if the element has any options.
     *
     * @return true if the element has options, false otherwise.
     */
    public boolean hasOptions() {
        return optionsCount() > 0;
    }

    /**
     * Get an option {@link SelectFieldItem} by its string value.
     *
     * @param value string value to find an option by
     *
     * @return a {@link SelectFieldItem}.
     */
    public SelectFieldItem getOption(String value) {
        for (SelectFieldItem option : options) {
            if (option.getExportValue().equals(value)) {
                return option;
            }
        }

        return null;
    }

    /**
     * Checks if the field has options with export and display values.
     *
     * @return {@code true} if the field has options with export and display values, {@code false} otherwise.
     */
    public boolean hasExportAndDisplayValues() {
        for (SelectFieldItem option : options) {
            if (option.hasExportAndDisplayValues()) {
                return true;
            }
        }
        return false;
    }
}

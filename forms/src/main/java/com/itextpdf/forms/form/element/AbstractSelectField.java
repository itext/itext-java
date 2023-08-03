/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class for fields that represents a control for selecting one or several of the provided options.
 */
public abstract class AbstractSelectField extends FormField<AbstractSelectField> {

    protected List<SelectFieldItem> options = new ArrayList<>();

    protected AbstractSelectField(String id) {
        super(id);
    }

    /**
     * Add a container with options. This might be a container for options group.
     *
     * @param optionElement a container with options.
     *
     * @deprecated starting from 8.0.1.
     */
    @Deprecated
    public void addOption(IBlockElement optionElement) {
        String value = tryAndExtractText(optionElement);
        addOption(new SelectFieldItem(value, optionElement));
    }

    /**
     * Add an option to the element.
     *
     * @param option a {@link SelectFieldItem}.
     */
    public void addOption(SelectFieldItem option) {
        options.add(option);
    }

    /**
     * Add an option to the element.
     *
     * @param option   a {@link SelectFieldItem}.
     * @param selected {@code true} is the option if selected, {@code false} otherwise.
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
    public List<SelectFieldItem> getItems() {
        return options;
    }


    /**
     * Gets the total amount of options available.
     *
     * @return the number of options in the element.
     */
    public int optionsCount() {
        return this.getItems().size();
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
     * @param value string value to find an option by.
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
     * Gets a list of containers with option(s). Every container might be a container for options group.
     *
     * @return a list of containers with options.
     *
     * @deprecated starting from 8.0.1.
     */
    @Deprecated
    public List<IBlockElement> getOptions() {
        List<IBlockElement> blockElements = new ArrayList<>();
        for (SelectFieldItem option : options) {
            blockElements.add(option.getElement());
        }
        return blockElements;
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

    private String tryAndExtractText(IBlockElement optionElement) {
        String label = optionElement.<String>getProperty(FormProperty.FORM_FIELD_LABEL);
        if (label != null) {
            return label;
        }

        for (IElement child : optionElement.getChildren()) {
            if (child instanceof Text) {
                return ((Text) child).getText();
            } else if (child instanceof IBlockElement) {
                return tryAndExtractText((IBlockElement) child);
            }
        }
        return "";
    }
}


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
import com.itextpdf.layout.element.IBlockElement;

/**
 * Common interface for HTML form elements.
 */
public interface IFormField extends IBlockElement {
    /**
     * Sets the {@link FormProperty#FORM_FIELD_VALUE} property.
     *
     * @param value string value of the property to be set
     *
     * @return this same {@link IFormField} instance
     */
    IFormField setValue(String value);

    /**
     * Set the form field to be interactive and added into Acroform instead of drawing it on a page.
     *
     * @param interactive {@code true} if the form field element shall be added into Acroform, {@code false} otherwise.
     *                    By default, the form field element is not interactive and drawn on a page
     *
     * @return this same {@link IFormField} instance
     */
    IFormField setInteractive(boolean interactive);

    /**
     * Gets the id.
     *
     * @return the id
     */
    String getId();

    /**
     * Set the form field's width.
     *
     * @param width form field's width
     *
     * @return this {@link FormField} element
     */
    IFormField setWidth(float width);

    /**
     * Set the form field's height.
     *
     * @param height form field's height
     *
     * @return this {@link FormField} element
     */
    IFormField setHeight(float height);
}

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
import com.itextpdf.forms.form.renderer.SelectFieldListBoxRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * A field that represents a control for selecting one or several of the provided options.
 */
public class ListBoxField extends AbstractSelectField {

    /**
     * Creates a new list box field.
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
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new SelectFieldListBoxRenderer(this);
    }
}

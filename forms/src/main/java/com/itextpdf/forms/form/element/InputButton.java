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

import com.itextpdf.forms.form.renderer.InputButtonRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Extension of the {@link FormField} class representing a input with type button or submit in html.
 */
public class InputButton extends FormField<InputButton> {

    /**
     * Creates a new {@link InputButton} instance.
     *
     * @param id the id
     */
    public InputButton(String id) {
        super(id);
    }

    /* (non-Javadoc)
     * @see com.itextpdf.layout.element.AbstractElement#makeNewRenderer()
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new InputButtonRenderer(this);
    }
}

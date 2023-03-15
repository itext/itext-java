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

import com.itextpdf.forms.form.renderer.ButtonRenderer;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Extension of the {@link FormField} class representing a button in html.
 */
public class Button extends FormField<Button> {

    public Button(String id) {
        super(id);
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new ButtonRenderer(this);
    }


    /**
     * Adds any block element to the div's contents.
     *
     * @param element a {@link BlockElement}
     * @return this Element
     */
    public Button add(IBlockElement element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds an image to the div's contents.
     *
     * @param element an {@link Image}
     * @return this Element
     */
    public Button add(Image element) {
        childElements.add(element);
        return this;
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        if (property == Property.KEEP_TOGETHER) {
            return (T1) (Object) true;
        }
        return super.<T1>getDefaultProperty(property);
    }
}

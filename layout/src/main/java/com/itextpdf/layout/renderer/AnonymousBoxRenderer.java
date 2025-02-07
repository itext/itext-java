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
package com.itextpdf.layout.renderer;

import com.itextpdf.layout.element.AnonymousBox;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

/**
 * This class represents the {@link IRenderer} object for a {@link AnonymousBox} object.
 */
public class AnonymousBoxRenderer extends ParagraphRenderer {

    /**
     * Creates an {@link AnonymousBoxRenderer} from its corresponding layout model element.
     *
     * @param modelElement the {@link AnonymousBox} layout model element to render
     */
    public AnonymousBoxRenderer(AnonymousBox modelElement) {
        super(modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(AnonymousBoxRenderer.class, this.getClass());
        return new AnonymousBoxRenderer((AnonymousBox) modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Float retrieveResolvedDeclaredHeight() {
        return ((AbstractRenderer) parent).retrieveResolvedDeclaredHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        if (property == Property.MARGIN_TOP || property == Property.MARGIN_BOTTOM || property == Property.MARGIN_LEFT ||
                property == Property.MARGIN_RIGHT) {
            return (T1) (Object) UnitValue.createPointValue(0f);
        }
        return super.<T1>getDefaultProperty(property);
    }
}

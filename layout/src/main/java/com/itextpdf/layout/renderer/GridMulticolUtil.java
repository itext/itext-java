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

import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.ContinuousContainer;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.List;

/**
 * The class stores common logic for multicol and grid layout.
 */
final class GridMulticolUtil {
    private GridMulticolUtil() {
        // do nothing
    }

    /**
     * Creates a split renderer.
     *
     * @param children children of the split renderer
     * @param renderer parent renderer
     *
     * @return a new {@link AbstractRenderer} instance
     */
    static AbstractRenderer createSplitRenderer(List<IRenderer> children, AbstractRenderer renderer) {
        AbstractRenderer splitRenderer = (AbstractRenderer) renderer.getNextRenderer();
        splitRenderer.parent = renderer.parent;
        splitRenderer.modelElement = renderer.modelElement;
        splitRenderer.occupiedArea = renderer.occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.setChildRenderers(children);
        splitRenderer.addAllProperties(renderer.getOwnProperties());
        ContinuousContainer.setupContinuousContainerIfNeeded(splitRenderer);
        return splitRenderer;
    }

    static float updateOccupiedWidth(float initialWidth, AbstractRenderer renderer) {
        float result = initialWidth;
        result += safelyRetrieveFloatProperty(Property.PADDING_LEFT, renderer);
        result += safelyRetrieveFloatProperty(Property.PADDING_RIGHT, renderer);

        result += safelyRetrieveFloatProperty(Property.MARGIN_LEFT, renderer);
        result += safelyRetrieveFloatProperty(Property.MARGIN_RIGHT, renderer);

        result += safelyRetrieveFloatProperty(Property.BORDER_LEFT, renderer);
        result += safelyRetrieveFloatProperty(Property.BORDER_RIGHT, renderer);

        return result;
    }

    static float updateOccupiedHeight(float initialHeight, boolean isFull, AbstractRenderer renderer) {
        float result = initialHeight;
        if (isFull) {
            result += safelyRetrieveFloatProperty(Property.PADDING_BOTTOM, renderer);
            result += safelyRetrieveFloatProperty(Property.MARGIN_BOTTOM, renderer);
            result += safelyRetrieveFloatProperty(Property.BORDER_BOTTOM, renderer);
        }
        result += safelyRetrieveFloatProperty(Property.PADDING_TOP, renderer);
        result += safelyRetrieveFloatProperty(Property.MARGIN_TOP, renderer);
        result += safelyRetrieveFloatProperty(Property.BORDER_TOP, renderer);

        return result;
    }

    private static float safelyRetrieveFloatProperty(int property, AbstractRenderer renderer) {
        final Object value = renderer.<Object>getProperty(property);
        if (value instanceof UnitValue) {
            return ((UnitValue) value).getValue();
        }
        if (value instanceof Border) {
            return ((Border) value).getWidth();
        }
        return 0F;
    }
}

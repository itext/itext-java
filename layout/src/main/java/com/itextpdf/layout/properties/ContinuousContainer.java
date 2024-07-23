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
package com.itextpdf.layout.properties;

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.HashMap;

/**
 * This class is used to store properties of the renderer that are needed to be removed/reapplied.
 * THis is used for processing continuous container property.
 * This behavior is used when we want to simulate a continuous appearance over multiple pages.
 * This means that only for the first and last page the margins, paddings and borders are applied.
 * On the first page the top properties are applied and on the last page the bottom properties are applied.
 */
public final class ContinuousContainer {

    /**
     * Properties needed to be removed/added for continuous container.
     */
    private static final int[] PROPERTIES_NEEDED_FOR_CONTINUOUS_CONTAINER = {Property.MARGIN_BOTTOM,
            Property.BORDER_BOTTOM,
            Property.PADDING_BOTTOM, Property.BORDER};
    private final HashMap<Integer, Object> properties = new HashMap<>();


    /**
     * Creates a new {@link ContinuousContainer} instance.
     *
     * @param renderer the renderer that is used to get properties from.
     */
    private ContinuousContainer(IRenderer renderer) {
        for (int property : PROPERTIES_NEEDED_FOR_CONTINUOUS_CONTAINER) {
            properties.put(property, renderer.<Object>getProperty(property));
        }
    }

    /**
     * Removes properties from the overflow renderer that are not needed for continuous container.
     *
     * @param overFlowRenderer the renderer that is used to remove properties from.
     */
    public static void clearPropertiesFromOverFlowRenderer(IPropertyContainer overFlowRenderer) {
        if (overFlowRenderer == null) {
            return;
        }
        if (Boolean.TRUE.equals(overFlowRenderer.<Boolean>getProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER))) {
            overFlowRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(0));
            overFlowRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(0));
            overFlowRenderer.setProperty(Property.BORDER_TOP, null);
        }
    }

    /**
     * Sets up the needed values in the model element of the renderer.
     *
     * @param blockRenderer the renderer that is used to set up continuous container.
     */
    public static void setupContinuousContainerIfNeeded(AbstractRenderer blockRenderer) {
        if (Boolean.TRUE.equals(blockRenderer.<Boolean>getProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER))) {
            if (!blockRenderer.hasProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER_RESULT)) {
                final ContinuousContainer continuousContainer = new ContinuousContainer(blockRenderer);
                blockRenderer
                        .setProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER_RESULT, continuousContainer);
            }
            clearPropertiesFromSplitRenderer(blockRenderer);
        }
    }

    private static void clearPropertiesFromSplitRenderer(AbstractRenderer blockRenderer) {
        if (blockRenderer == null) {
            return;
        }
        blockRenderer.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(0));
        blockRenderer.setProperty(Property.BORDER_BOTTOM, null);
        blockRenderer.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(0));

    }

    /**
     * Re adds the properties that were removed from the overflow renderer.
     *
     * @param blockRenderer the renderer that is used to reapply properties.
     */
    public void reApplyProperties(AbstractRenderer blockRenderer) {
        for (int property : PROPERTIES_NEEDED_FOR_CONTINUOUS_CONTAINER) {
            blockRenderer.setProperty(property, properties.get(property));
        }
        final Border allBorders = (Border) properties.get(Property.BORDER);
        final Border bottomBorder = (Border) properties.get(Property.BORDER_BOTTOM);
        if (allBorders != null && bottomBorder == null) {
            blockRenderer.setProperty(Property.BORDER_BOTTOM, allBorders);
        }
    }

    /**
     * Updates values of the saved property.
     *
     * @param property the property to be updated
     * @param value the new value
     */
    public void updateValueOfSavedProperty(int property, Object value) {
        properties.put(property, value);
    }
}

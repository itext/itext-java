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
package com.itextpdf.forms.util;

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.properties.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class for centralized logic related to form field rendering.
 */
public final class FormFieldRendererUtil {

    //These properties are related to the outer box of the element.
    private static final int[] PROPERTIES_THAT_IMPACT_LAYOUT = new int[] {
            Property.MARGIN_TOP, Property.MARGIN_BOTTOM, Property.MARGIN_LEFT, Property.MARGIN_RIGHT,
            Property.WIDTH, Property.BOTTOM, Property.LEFT, Property.POSITION
    };

    /**
     * Creates a new instance of {@link FormFieldRendererUtil}.
     */
    private FormFieldRendererUtil() {
        // empty constructor
    }

    /**
     * Removes properties that impact the lay outing of interactive form fields.
     *
     * @param modelElement The model element to remove the properties from.
     *
     * @return A map containing the removed properties.
     */
    public static Map<Integer, Object> removeProperties(IPropertyContainer modelElement) {
        final Map<Integer, Object> properties = new HashMap<>(PROPERTIES_THAT_IMPACT_LAYOUT.length);
        for (int i : PROPERTIES_THAT_IMPACT_LAYOUT) {
            properties.put(i, modelElement.<Object>getOwnProperty(i));
            modelElement.deleteOwnProperty(i);
        }

        return properties;
    }

    /**
     * Reapplies the properties {@link  IPropertyContainer}.
     *
     * @param modelElement The model element to reapply the properties to.
     * @param properties   The properties to reapply.
     */
    public static void reapplyProperties(IPropertyContainer modelElement, Map<Integer, Object> properties) {
        for (Entry<Integer, Object> integerObjectEntry : properties.entrySet()) {
            if (integerObjectEntry.getValue() != null) {
                modelElement.setProperty(integerObjectEntry.getKey(), integerObjectEntry.getValue());
            } else {
                modelElement.deleteOwnProperty(integerObjectEntry.getKey());
            }
        }
    }
}

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

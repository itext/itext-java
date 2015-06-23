package com.itextpdf.model;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public class PropertyTest {
    @Test
    public void testPropertiyValues() {
        Field[] fields = Property.class.getDeclaredFields();
        HashSet properties = new HashSet(fields.length);
        final int flags = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
        try {
            for (Field field : fields) {
                if ((field.getModifiers() & flags) == flags && field.getType().equals(int.class)) {
                    Assert.assertTrue("Duplicate property found: " + field.getName(),
                            properties.add(field.get(null)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInheritedProperties() throws IllegalAccessException {
        Field[] fields = Property.class.getDeclaredFields();
        int[] inheritedProperties = null;
        for (Field field: fields) {
            if (field.getName().equals("inheritedProperties")) {
                field.setAccessible(true);
                inheritedProperties = (int[])field.get(null);
            }
        }
        int m = Integer.MIN_VALUE;
        for (int property: inheritedProperties) {
            Assert.assertTrue("iheritedPrperties will be used with binarySearch, but values are not sorted", m < property);
            m = property;
        }

        Assert.assertTrue("FONT must be inherited", Property.isPropertyInherited(Property.FONT, null, null));
        Assert.assertTrue("CHARACTER_SPACING must be inherited", Property.isPropertyInherited(Property.CHARACTER_SPACING, null, null));
        Assert.assertTrue("SPACING_RATIO must be inherited", Property.isPropertyInherited(Property.SPACING_RATIO, null, null));
    }
}

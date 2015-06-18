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
}

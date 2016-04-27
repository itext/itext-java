package com.itextpdf.layout.property;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PropertyTest extends ExtendedITextTest {

    @Test
    public void propertyUniquenessTest() throws IllegalAccessException {
        Set<Integer> fieldValues = new HashSet<>();
        int maxFieldValue = 1;
        for (Field field : Property.class.getFields()) {
            if (field.getType() == int.class) {
                int value = (int) field.get(null);
                maxFieldValue = Math.max(maxFieldValue, value);
                if (fieldValues.contains(value)) {
                    Assert.fail("Multiple fields with same value");
                }
                fieldValues.add(value);
            }
        }

        for (int i = 1; i <= maxFieldValue; i++) {
            if (!fieldValues.contains(i)) {
                Assert.fail(MessageFormat.format("Missing value: {0}", i));
            }
        }

        System.out.println(MessageFormat.format("Max field value: {0}", maxFieldValue));
    }

}

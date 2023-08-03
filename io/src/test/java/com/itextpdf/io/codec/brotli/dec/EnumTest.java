/* Copyright 2017 Google Inc. All Rights Reserved.

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package com.itextpdf.io.codec.brotli.dec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.TreeSet;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for Enum-like classes.
 */
@Category(UnitTest.class)
@RunWith(JUnit4.class)
public class EnumTest extends ExtendedITextTest {

    @Test
    public void testRunningState() {
        checkEnumClass(RunningState.class);
    }

    @Test
    public void testWordTransformType() {
        checkEnumClass(WordTransformType.class);
    }

    private static void checkEnumClass(Class<?> clazz) {
        TreeSet<Integer> values = new TreeSet<Integer>();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isSynthetic()) {
                assertEquals("int", f.getType().getName());
                assertEquals(Modifier.FINAL | Modifier.STATIC, f.getModifiers());
                Integer value = null;
                try {
                    value = f.getInt(null);
                } catch (IllegalAccessException ex) {
                    fail("Inaccessible field");
                }
                assertFalse(values.contains(value));
                values.add(value);
            }
        }
        assertEquals(0, values.first().intValue());
        assertEquals(values.size(), values.last() + 1);
    }
}
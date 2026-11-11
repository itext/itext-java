/* Copyright 2017 Google Inc. All Rights Reserved.

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package com.itextpdf.io.codec.brotli.dec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.TreeSet;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Tests for Enum-like classes.
 */
@Tag("UnitTest")
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
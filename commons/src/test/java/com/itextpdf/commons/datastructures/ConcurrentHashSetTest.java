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
package com.itextpdf.commons.datastructures;

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

@Tag("UnitTest")
public class ConcurrentHashSetTest extends ExtendedITextTest {

    @Test
    public void sizeTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        set.add("2");
        Assertions.assertEquals(2, set.size());
    }

    @Test
    public void containsKeyTrueTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        Assertions.assertTrue(set.contains("1"));
        Assertions.assertTrue(set.contains("2"));
        Assertions.assertTrue(set.contains("3"));
    }

    @Test
    public void containsKeyFalseTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        Assertions.assertFalse(set.contains("5"));
    }

    @Test
    public void clearTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        set.clear();
        Assertions.assertFalse(set.contains("1"));
    }

    @Test
    public void addTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        set.add("1");
        Assertions.assertEquals(1, set.size());
    }

    @Test
    public void removeTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        set.add("2");
        set.remove("1");
        Assertions.assertFalse(set.contains("1"));
    }

    @Test
    public void forEachTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        Set<String> anotherSet = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        set.forEach((String str) -> {
            anotherSet.add(str);
        });

        Assertions.assertEquals(3, anotherSet.size());
    }

    @Test
    public void equalsTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        HashSet<String> anotherSet = new HashSet<>();
        Assertions.assertFalse(set.equals(anotherSet));
    }

    @Test
    public void addAllTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        HashSet<String> anotherSet = new HashSet<>();
        anotherSet.add("1");
        anotherSet.add("2");
        set.addAll(anotherSet);
        Assertions.assertEquals(2, set.size());
    }

    @Test
    public void removeAllTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        HashSet<String> anotherSet = new HashSet<>();
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> set.removeAll(anotherSet));
        Assertions.assertEquals(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION, e.getMessage());
    }

    @Test
    public void retainAllTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        HashSet<String> anotherSet = new HashSet<>();
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> set.retainAll(anotherSet));
        Assertions.assertEquals(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION, e.getMessage());
    }

    @Test
    public void containsAllTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        HashSet<String> anotherSet = new HashSet<>();
        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> set.containsAll(anotherSet));
        Assertions.assertEquals(CommonsExceptionMessageConstant.UNSUPPORTED_OPERATION, e.getMessage());
    }

    @Test
    public void hashCodeTest() {
        ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
        set.add("1");
        HashSet<String> anotherSet = new HashSet<>();
        anotherSet.add("2");
        Assertions.assertNotEquals(set.hashCode(), anotherSet.hashCode());
    }
}
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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AbstractIdentifiableObjectTest extends ExtendedITextTest {

    @Test
    public void testIdentifiersAreUnique() {
        AbstractIdentifiableObject sut1 = new TestIdentifiableObject("A");
        AbstractIdentifiableObject sut2 = new TestIdentifiableObject("A");
        Assertions.assertNotEquals(sut1.getIdentifier().getId(), sut2.getIdentifier().getId());
    }

    @Test
    public void testEqualsForEqualIdentity() {
        AbstractIdentifiableObject sut1 = getIdentifiableObjectUnderTest();
        AbstractIdentifiableObject sut2 = sut1;
        // Equals is being tested here.
        Assertions.assertTrue(sut1.equals(sut2));
    }

    @Test
    public void testEqualsForNull() {
        AbstractIdentifiableObject sut = getIdentifiableObjectUnderTest();
        // Equals is being tested here.
        Assertions.assertFalse(sut.equals(null));
    }

    @Test
    public void testEqualsForSomeObject() {
        AbstractIdentifiableObject sut = getIdentifiableObjectUnderTest();
        // Equals is being tested here.
        Assertions.assertFalse(sut.equals("Test"));
    }

    @Test
    public void testEqualsForEqualInstances() {
        performTestEqualsForEqualInstances();
    }

    @Test
    public void testHashForEqualInstances() {
        performTestHashForEqualInstances();
    }

    @Test
    public void testEqualsForDifferentInstances() {
        performTestEqualsForDifferentInstances();
    }

    @Test
    public void testHashForDifferentInstances() {
        performTestHashForDifferentInstances();
    }

    protected abstract void performTestHashForEqualInstances();

    protected abstract void performTestEqualsForEqualInstances();

    protected abstract void performTestEqualsForDifferentInstances();

    protected abstract void performTestHashForDifferentInstances();

    abstract AbstractIdentifiableObject getIdentifiableObjectUnderTest();

    private static class TestIdentifiableObject extends AbstractIdentifiableObject {
        protected TestIdentifiableObject(String prefix) {
            super(prefix);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}

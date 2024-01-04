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
package com.itextpdf.layout.font;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import java.util.Random;

@Category(UnitTest.class)
public class RangeTest extends ExtendedITextTest {

    @Test
    public void testWrongRange() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new RangeBuilder().addRange(11, 10));
    }

    @Test
    public void testWrongRangeSize() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new RangeBuilder().create());
    }

    @Test
    public void testFullRange() {
        Assert.assertTrue(RangeBuilder.getFullRange().contains(new Random().nextInt()));

        Assert.assertTrue(RangeBuilder.getFullRange().equals(RangeBuilder.getFullRange()));

        Assert.assertTrue(RangeBuilder.getFullRange() == RangeBuilder.getFullRange());

        Assert.assertFalse(RangeBuilder.getFullRange().equals(new RangeBuilder().addRange(1).create()));
    }

    @Test
    public void testHashCodeAndEquals() {

        Range range = new RangeBuilder((char) 25, (char) 26)
                .addRange(1, 5)
                .addRange(4, 7)
                .create();

        Range range2 = new RangeBuilder(25, 26)
                .addRange((char) 1, (char) 7)
                .create();


        Assert.assertTrue(range.hashCode() == range2.hashCode());
        Assert.assertTrue(range.equals(range2));
        Assert.assertEquals(range.toString(), range2.toString());

        Range range3 = new RangeBuilder(25)
                .addRange((char) 26)
                .addRange((char) 1, (char) 7)
                .create();

        Assert.assertFalse(range2.hashCode() == range3.hashCode());
        Assert.assertFalse(range2.equals(range3));
        Assert.assertNotEquals(range2.toString(), range3.toString());

        Range range4 = new RangeBuilder(26)
                .addRange((char) 25)
                .addRange((char) 1, (char) 4)
                .addRange((char) 3, (char) 7)
                .create();

        Assert.assertTrue(range3.hashCode() == range4.hashCode());
        Assert.assertTrue(range3.equals(range4));
        Assert.assertEquals(range3.toString(), range4.toString());
    }

    @Test
    public void testUnionAndContains() {
        Range range = new RangeBuilder((char) 25, (char) 27)
                .addRange(2, 10)
                .addRange(0, 20)
                .addRange(1, 19)
                .addRange(33, 40)
                .addRange(0, 5)
                .addRange(20, 22)
                .addRange(8, 15)
                .addRange(25, 30)
                .create();
        Assert.assertEquals("[(0; 22), (25; 30), (33; 40)]", range.toString());

        Assert.assertTrue(range.contains(0));
        Assert.assertTrue(range.contains(10));
        Assert.assertTrue(range.contains(22));
        Assert.assertTrue(range.contains(25));
        Assert.assertTrue(range.contains(27));
        Assert.assertTrue(range.contains(30));
        Assert.assertTrue(range.contains(33));
        Assert.assertTrue(range.contains(34));
        Assert.assertTrue(range.contains(40));

        Assert.assertFalse(range.contains(-1));
        Assert.assertFalse(range.contains(23));
        Assert.assertFalse(range.contains(31));
        Assert.assertFalse(range.contains(32));
        Assert.assertFalse(range.contains(41));
    }

    @Test
    public void testSingles() {
        Range range = new RangeBuilder((char) 1)
                .addRange(2)
                .addRange(3)
                .addRange(6)
                .create();
        Assert.assertEquals("[(1; 1), (2; 2), (3; 3), (6; 6)]", range.toString());

        Assert.assertTrue(range.contains(1));
        Assert.assertTrue(range.contains(2));
        Assert.assertTrue(range.contains(3));
        Assert.assertTrue(range.contains(6));

        Assert.assertFalse(range.contains(0));
        Assert.assertFalse(range.contains(5));
        Assert.assertFalse(range.contains(7));
    }
}

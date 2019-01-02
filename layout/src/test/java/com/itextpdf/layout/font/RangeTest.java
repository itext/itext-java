/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.font;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.util.Random;

@Category(UnitTest.class)
public class RangeTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();


    @Test
    public void testWrongRange() {
        junitExpectedException.expect(IllegalArgumentException.class);
        new RangeBuilder().addRange(11, 10);
    }

    @Test
    public void testWrongRangeSize() {
        junitExpectedException.expect(IllegalArgumentException.class);
        new RangeBuilder().create();
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

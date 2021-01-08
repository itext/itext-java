/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.pdf.canvas.parser.clipper;

import com.itextpdf.kernel.pdf.canvas.parser.clipper.Point.DoublePoint;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.Point.LongPoint;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PointTest extends ExtendedITextTest {
    private static final double DOUBLE_EPSILON_COMPARISON = 1E-6;

    // DoublePoint tests block

    @Test
    public void doublePointDefaultConstructorTest() {
        DoublePoint dp = new DoublePoint();
        PointTest.assertDoublePointFields(dp, 0, 0 ,0);
    }

    @Test
    public void doublePointTwoParamConstructorTest() {
        DoublePoint dp = new DoublePoint(1.23, 5.34);
        PointTest.assertDoublePointFields(dp, 1.23, 5.34, 0);
    }

    @Test
    public void doublePointThreeParamConstructorTest() {
        DoublePoint dp = new DoublePoint(1.23, 5.34, 234.23);
        PointTest.assertDoublePointFields(dp, 1.23, 5.34, 234.23);
    }

    @Test
    public void doublePointCopyConstructorTest() {
        DoublePoint dp = new DoublePoint(1.23, 5.34, 234.23);
        DoublePoint copy = new DoublePoint(dp);
        PointTest.assertDoublePointFields(copy, 1.23, 5.34, 234.23);
    }

    @Test
    public void doublePointEqualsAndHashCodeItselfTest() {
        DoublePoint dp = new DoublePoint(1.23, 5.34, 234.23);

        Assert.assertTrue(dp.equals(dp));
        Assert.assertEquals(dp.hashCode(), dp.hashCode());
    }

    @Test
    public void doublePointEqualsAndHashCodeToAnotherEqualPointTest() {
        DoublePoint first = new DoublePoint(1.23, 5.34, 234.23);
        DoublePoint second = new DoublePoint(1.23, 5.34, 13);

        Assert.assertTrue(first.equals(second));
        Assert.assertTrue(second.equals(first));
        Assert.assertNotEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void doublePointEqualsAndHashCodeToAnotherNotEqualPointTest() {
        DoublePoint first = new DoublePoint(1.23, 5.34, 234.23);

        DoublePoint second = new DoublePoint(0, 5.34, 234.23);
        Assert.assertFalse(first.equals(second));
        Assert.assertFalse(second.equals(first));
        Assert.assertNotEquals(first.hashCode(), second.hashCode());

        second = new DoublePoint(1.23, 0, 234.23);
        Assert.assertFalse(first.equals(second));
        Assert.assertFalse(second.equals(first));
        Assert.assertNotEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void doublePointEqualsToNullTest() {
        DoublePoint dp = new DoublePoint(1.23, 5.34, 234.23);

        Assert.assertFalse(dp.equals(null));
    }

    @Test
    public void doublePointEqualsToAnotherClassTest() {
        DoublePoint dp = new DoublePoint(1.23, 5.34, 234.23);

        Assert.assertFalse(dp.equals(new String()));
    }

    @Test
    public void doublePointSetTest() {
        DoublePoint temp = new DoublePoint(1.23, 5.34, 234.23);
        DoublePoint dp = new DoublePoint();
        dp.set(temp);
        PointTest.assertDoublePointFields(dp, 1.23, 5.34, 234.23);
    }

    @Test
    public void doublePointSetFieldsTest() {
        DoublePoint dp = new DoublePoint();

        dp.setX(1.23);
        PointTest.assertDoublePointFields(dp, 1.23, 0, 0);

        dp.setY(345.34);
        PointTest.assertDoublePointFields(dp, 1.23, 345.34, 0);

        dp.setZ(4213.34);
        PointTest.assertDoublePointFields(dp, 1.23, 345.34, 4213.34);
    }

    @Test
    public void doublePointToStringTest() {
        DoublePoint dp = new DoublePoint(1.23, 5.34, 234.23);
        Assert.assertEquals("Point [x=1.23, y=5.34, z=234.23]", dp.toString());
    }

    // LongPoint tests block

    @Test
    public void longPointDefaultConstructorTest() {
        LongPoint lp = new LongPoint();
        PointTest.assertLongPointFields(lp, 0, 0 ,0);
    }

    @Test
    public void longPointTwoLongParamConstructorTest() {
        LongPoint lp = new LongPoint(1, 5);
        PointTest.assertLongPointFields(lp, 1, 5, 0);
    }

    @Test
    public void longPointTwoDoubleParamConstructorTest() {
        LongPoint lp = new LongPoint(1.23, 5.34);
        PointTest.assertLongPointFields(lp, 1, 5, 0);
    }

    @Test
    public void longPointThreeParamConstructorTest() {
        LongPoint lp = new LongPoint(1, 5, 234);
        PointTest.assertLongPointFields(lp, 1, 5, 234);
    }

    @Test
    public void longPointCopyConstructorTest() {
        LongPoint lp = new LongPoint(1, 5, 234);
        LongPoint copy = new LongPoint(lp);
        PointTest.assertLongPointFields(copy, 1, 5, 234);
    }

    @Test
    public void longPointEqualsAndHashCodeItselfTest() {
        LongPoint lp = new LongPoint(1, 5, 234);

        Assert.assertTrue(lp.equals(lp));
        Assert.assertEquals(lp.hashCode(), lp.hashCode());
    }

    @Test
    public void longPointEqualsAndHashCodeToAnotherEqualPointTest() {
        LongPoint first = new LongPoint(1, 5, 234);
        LongPoint second = new LongPoint(1, 5, 13);

        Assert.assertTrue(first.equals(second));
        Assert.assertTrue(second.equals(first));
        Assert.assertNotEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void longPointEqualsAndHashCodeToAnotherNotEqualPointTest() {
        LongPoint first = new LongPoint(1, 5, 234);

        LongPoint second = new LongPoint(0, 5, 234);
        Assert.assertFalse(first.equals(second));
        Assert.assertFalse(second.equals(first));
        Assert.assertNotEquals(first.hashCode(), second.hashCode());

        second = new LongPoint(1, 0, 234);
        Assert.assertFalse(first.equals(second));
        Assert.assertFalse(second.equals(first));
        Assert.assertNotEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void longPointEqualsToNullTest() {
        LongPoint lp = new LongPoint(1, 5, 234);

        Assert.assertFalse(lp.equals(null));
    }

    @Test
    public void longPointEqualsToAnotherClassTest() {
        LongPoint lp = new LongPoint(1, 5, 234);

        Assert.assertFalse(lp.equals(new String()));
    }

    @Test
    public void longPointSetTest() {
        LongPoint temp = new LongPoint(1, 5, 234);
        LongPoint lp = new LongPoint();
        lp.set(temp);
        PointTest.assertLongPointFields(lp, 1, 5, 234);
    }

    @Test
    public void longPointSetFieldsTest() {
        LongPoint lp = new LongPoint();

        lp.setX(1L);
        PointTest.assertLongPointFields(lp, 1, 0, 0);

        lp.setY(345L);
        PointTest.assertLongPointFields(lp, 1, 345, 0);

        lp.setZ(4213L);
        PointTest.assertLongPointFields(lp, 1, 345, 4213);
    }

    @Test
    public void longPointToStringTest() {
        LongPoint lp = new LongPoint(1, 5, 234);
        Assert.assertEquals("Point [x=1, y=5, z=234]", lp.toString());
    }

    @Test
    public void longPointGetDeltaXEqualsYTest() {
        LongPoint lp1 = new LongPoint(1, 5);
        LongPoint lp2 = new LongPoint(3, 5);
        Assert.assertEquals(Edge.HORIZONTAL, LongPoint.getDeltaX(lp1, lp2), DOUBLE_EPSILON_COMPARISON);
    }

    @Test
    public void longPointGetDeltaXDifferentYTest() {
        LongPoint lp1 = new LongPoint(1, 5);
        LongPoint lp2 = new LongPoint(3, 6);
        Assert.assertEquals(2, LongPoint.getDeltaX(lp1, lp2), DOUBLE_EPSILON_COMPARISON);
    }

    // Point tests block

    @Test
    public void isPt2BetweenPt1AndPt3Test() {
        LongPoint pt1 = new LongPoint();
        LongPoint pt2 = new LongPoint();
        LongPoint pt3 = new LongPoint();
        Assert.assertFalse(Point.isPt2BetweenPt1AndPt3(pt1, pt2, pt3));

        pt3.setX(10L);
        pt3.setY(10L);
        Assert.assertFalse(Point.isPt2BetweenPt1AndPt3(pt1, pt2, pt3));

        pt2.setX(10L);
        pt2.setY(10L);
        Assert.assertFalse(Point.isPt2BetweenPt1AndPt3(pt1, pt2, pt3));

        pt2.setX(5L);
        pt2.setY(10L);
        Assert.assertTrue(Point.isPt2BetweenPt1AndPt3(pt1, pt2, pt3));

        pt1.setX(10L);
        pt1.setY(0L);
        pt2.setX(10L);
        pt2.setY(5L);
        Assert.assertTrue(Point.isPt2BetweenPt1AndPt3(pt1, pt2, pt3));
    }

    @Test
    public void slopesEqualThreePointTest() {
        LongPoint pt1 = new LongPoint(9, 0);
        LongPoint pt2 = new LongPoint(3, 2);
        LongPoint pt3 = new LongPoint(0, 3);

        Assert.assertTrue(Point.slopesEqual(pt1, pt2, pt3, false));
        Assert.assertTrue(Point.slopesEqual(pt1, pt2, pt3, true));

        pt1.setX(10L);
        Assert.assertFalse(Point.slopesEqual(pt1, pt2, pt3, false));
        Assert.assertFalse(Point.slopesEqual(pt1, pt2, pt3, true));
    }

    @Test
    public void slopesEqualFourPointTest() {
        LongPoint pt1 = new LongPoint(6, 0);
        LongPoint pt2 = new LongPoint(3, 3);
        LongPoint pt3 = new LongPoint(3, 2);
        LongPoint pt4 = new LongPoint(0, 5);

        Assert.assertTrue(Point.slopesEqual(pt1, pt2, pt3, pt4, false));
        Assert.assertTrue(Point.slopesEqual(pt1, pt2, pt3, pt4, true));

        pt1.setX(10L);
        Assert.assertFalse(Point.slopesEqual(pt1, pt2, pt3, pt4, false));
        Assert.assertFalse(Point.slopesEqual(pt1, pt2, pt3, pt4, true));
    }

    @Test
    public void arePointsCloseTest() {
        LongPoint pt1 = new LongPoint(1, 2);
        LongPoint pt2 = new LongPoint(0, 0);

        Assert.assertFalse(Point.arePointsClose(pt1, pt2, 4));
        Assert.assertTrue(Point.arePointsClose(pt1, pt2, 5));
    }

    @Test
    public void distanceFromLineSqrdTest() {
        DoublePoint pt = new DoublePoint(9.2342, 2);
        DoublePoint ln1 = new DoublePoint(0, 10);
        DoublePoint ln2 = new DoublePoint(0, 0);

        // 9.2342 * 9.2342 ~ 85.27044964
        Assert.assertEquals(85.27044964, Point.distanceFromLineSqrd(pt, ln1, ln2), DOUBLE_EPSILON_COMPARISON);
    }

    @Test
    public void getUnitNormalTest() {
        LongPoint pt1 = new LongPoint(1, 1);
        LongPoint pt2 = new LongPoint(1, 1);

        Assert.assertEquals(new DoublePoint(), Point.getUnitNormal(pt1, pt2));

        pt2.setX(5L);
        Assert.assertEquals(new DoublePoint(0, -1), Point.getUnitNormal(pt1, pt2));

        pt2.setY(4L);
        DoublePoint dp = Point.getUnitNormal(pt1, pt2);
        Assert.assertEquals(0.6, dp.getX(), DOUBLE_EPSILON_COMPARISON);
        Assert.assertEquals(-0.8, dp.getY(), DOUBLE_EPSILON_COMPARISON);
    }

    @Test
    public void slopesNearCollinearTest01() {
        LongPoint pt1 = new LongPoint(5, 2);
        LongPoint pt2 = new LongPoint(1, 1);
        LongPoint pt3 = new LongPoint(6, 2);

        Assert.assertTrue(Point.slopesNearCollinear(pt1, pt2, pt3, 0.04));
        Assert.assertFalse(Point.slopesNearCollinear(pt1, pt2, pt3, 0.03));

        pt1 = new LongPoint(2, 5);
        pt2 = new LongPoint(1, 1);
        pt3 = new LongPoint(2, 6);
        Assert.assertTrue(Point.slopesNearCollinear(pt1, pt2, pt3, 0.04));
        Assert.assertFalse(Point.slopesNearCollinear(pt1, pt2, pt3, 0.03));
    }

    @Test
    public void slopesNearCollinearTest02() {
        LongPoint pt1 = new LongPoint(1, 1);
        LongPoint pt2 = new LongPoint(5, 2);
        LongPoint pt3 = new LongPoint(6, 2);

        Assert.assertTrue(Point.slopesNearCollinear(pt1, pt2, pt3, 0.04));
        Assert.assertFalse(Point.slopesNearCollinear(pt1, pt2, pt3, 0.03));

        pt1 = new LongPoint(1, 1);
        pt2 = new LongPoint(2, 5);
        pt3 = new LongPoint(2, 6);
        Assert.assertTrue(Point.slopesNearCollinear(pt1, pt2, pt3, 0.04));
        Assert.assertFalse(Point.slopesNearCollinear(pt1, pt2, pt3, 0.03));
    }

    @Test
    public void slopesNearCollinearTest03() {
        LongPoint pt1 = new LongPoint(1, 1);
        LongPoint pt2 = new LongPoint(6, 2);
        LongPoint pt3 = new LongPoint(5, 2);

        Assert.assertTrue(Point.slopesNearCollinear(pt1, pt2, pt3, 0.04));
        Assert.assertFalse(Point.slopesNearCollinear(pt1, pt2, pt3, 0.03));

        pt1 = new LongPoint(1, 1);
        pt2 = new LongPoint(2, 6);
        pt3 = new LongPoint(2, 5);
        Assert.assertTrue(Point.slopesNearCollinear(pt1, pt2, pt3, 0.04));
        Assert.assertFalse(Point.slopesNearCollinear(pt1, pt2, pt3, 0.03));
    }

    private static void assertLongPointFields(LongPoint lp, long x, long y, long z) {
        Assert.assertEquals(x, lp.getX());
        Assert.assertEquals(y, lp.getY());
        Assert.assertEquals(z, lp.getZ());
    }

    private static void assertDoublePointFields(DoublePoint dp, double x, double y, double z) {
        Assert.assertEquals(x, dp.getX(), DOUBLE_EPSILON_COMPARISON);
        Assert.assertEquals(y, dp.getY(), DOUBLE_EPSILON_COMPARISON);
        Assert.assertEquals(z, dp.getZ(), DOUBLE_EPSILON_COMPARISON);
    }
}

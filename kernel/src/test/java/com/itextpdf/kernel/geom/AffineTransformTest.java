/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.geom;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AffineTransformTest extends ExtendedITextTest {

    @Test
    public void selfTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assert.assertTrue(affineTransform.equals(affineTransform));
    }

    @Test
    public void nullTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assert.assertFalse(affineTransform.equals(null));
    }

    @Test
    public void otherClassTest() {
        AffineTransform affineTransform = new AffineTransform();
        String string = "Test";

        Assert.assertFalse(affineTransform.equals(string));
    }

    @Test
    public void sameValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assert.assertFalse(affineTransform1 == affineTransform2);
        Assert.assertEquals(hash1, hash2);
        Assert.assertTrue(affineTransform1.equals(affineTransform2));
    }

    @Test
    public void differentValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(5d, 4d, 3d, 2d, 1d, 1d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assert.assertFalse(affineTransform1 == affineTransform2);
        Assert.assertNotEquals(hash1, hash2);
        Assert.assertFalse(affineTransform1.equals(affineTransform2));
    }

    @Test
    public void getRotateInstanceTest() {
        AffineTransform rotateOne = AffineTransform.getRotateInstance(Math.PI / 2);
        AffineTransform expected = new AffineTransform(0, 1, -1, 0, 0, 0);

        Assert.assertEquals(rotateOne, expected);
    }

    @Test
    public void getRotateInstanceTranslateTest() {
        AffineTransform rotateTranslate = AffineTransform.getRotateInstance(Math.PI / 2, 10, 5);
        AffineTransform expected = new AffineTransform(0, 1, -1, 0, 15, -5);

        Assert.assertEquals(rotateTranslate, expected);
    }

    @Test
    public void cloneTest() throws CloneNotSupportedException {
        AffineTransform original = new AffineTransform();
        AffineTransform clone = original.clone();

        Assert.assertTrue(original != clone);
        Assert.assertTrue(original.equals(clone));
    }

    @Test
    public void getTransformValuesTest() {
        float[] matrix = new float[]{0f, 1f, 2f, 3f, 4f, 5f};
        AffineTransform affineTransform = new AffineTransform(matrix);

        Assert.assertEquals(matrix[0], affineTransform.getScaleX(), 0.0);
        Assert.assertEquals(matrix[3], affineTransform.getScaleY(), 0.0);
        Assert.assertEquals(matrix[2], affineTransform.getShearX(), 0.0);
        Assert.assertEquals(matrix[1], affineTransform.getShearY(), 0.0);
        Assert.assertEquals(matrix[4], affineTransform.getTranslateX(), 0.0);
        Assert.assertEquals(matrix[5], affineTransform.getTranslateY(), 0.0);
        Assert.assertEquals(32, affineTransform.getType(), 0.0);
    }

    @Test
    public void createAffineTransformFromOtherATTest() {
        AffineTransform template = new AffineTransform(0, 1, 2, 3, 4, 5);
        AffineTransform result = new AffineTransform(template);

        Assert.assertNotSame(template, result);
        Assert.assertEquals(template, result);
    }

    @Test
    public void createAffineTransformFromFloatArrayTest() {
        float[] matrix = new float[]{0f, 1f, 2f, 3f, 4f, 5f};
        AffineTransform expected = new AffineTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
        AffineTransform result = new AffineTransform(matrix);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void createAffineTransformFromDoubleArrayTest() {
        double[] matrix = new double[]{0d, 1d, 2d, 3d, 4d, 5d};
        AffineTransform expected = new AffineTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
        AffineTransform result = new AffineTransform(matrix);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void setTransformTest() {
        float[] matrix = new float[]{0f, 1f, 2f, 3f, 4f, 5f};
        AffineTransform expected = new AffineTransform(matrix);
        AffineTransform result = new AffineTransform();

        result.setTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void setToIdentityTest() {
        AffineTransform expected = new AffineTransform(1, 0, 0, 1, 0, 0);
        AffineTransform result = new AffineTransform();

        result.setToIdentity();

        Assert.assertEquals(expected, result);
    }

    @Test
    public void setToShearTypeIdentityTest() {
        double shx = 0d;
        double shy = 0d;
        AffineTransform expected = new AffineTransform(1, shx, shy, 1, 0, 0);
        AffineTransform result = new AffineTransform();

        result.setToShear(shx, shy);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void setToShearTypeUnknownTest() {
        double shx = 1d;
        double shy = 1d;
        AffineTransform expected = new AffineTransform(1, shx, shy, 1, 0, 0);
        AffineTransform result = new AffineTransform();

        result.setToShear(shx, shy);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void getShearInstanceTest() {
        double shx = 1d;
        double shy = 1d;
        AffineTransform expected = new AffineTransform(1, shx, shy, 1, 0, 0);
        AffineTransform result = AffineTransform.getShearInstance(shx, shy);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void shearTest() {
        double shx = 1d;
        double shy = 1d;
        AffineTransform expected = new AffineTransform(4d, 6d, 4d, 6d, 5d, 6d);
        AffineTransform result = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);

        result.shear(shx, shy);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void rotateTest() {
        double angle = Math.PI / 2;
        AffineTransform expected = new AffineTransform(3d, 4d, -1d, -2d, 5d, 6d);
        AffineTransform result = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);

        result.rotate(angle);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void preConcatenateTest() {
        AffineTransform expected = new AffineTransform(6d, 6d, 14d, 14d, 24d, 24d);
        AffineTransform result = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);
        AffineTransform template = new AffineTransform(2d, 2d, 2d, 2d, 2d, 2d);

        result.preConcatenate(template);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void transformDoubleArrayTest() {
        AffineTransform affineTransform = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);
        double[] expected = new double[]{0d, 13d, 18d, 13d, 18d, 0d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        double[] dest = new double[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assert.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void transformDoubleArraySourceDestEqualsTest() {
        AffineTransform affineTransform = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);
        double[] expected = new double[]{2d, 2d, 13d, 18d, 13d, 18d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};

        affineTransform.transform(src, 1, src, 2, 2);

        Assert.assertArrayEquals(expected, src, 0);
    }

    @Test
    public void transformFloatArrayTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{0f, 13f, 18f, 13f, 18f, 0f};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};
        float[] dest = new float[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assert.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void transformFloatArraySourceDestEqualsTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{2f, 2f, 13f, 18f, 13f, 18f};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};

        affineTransform.transform(src, 1, src, 2, 2);

        Assert.assertArrayEquals(expected, src, 0);
    }

    @Test
    public void transformFloatToDoubleTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        double[] expected = new double[]{0d, 13d, 18d, 13d, 18d, 0d};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};
        double[] dest = new double[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assert.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void transformDoubleToFloatTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{0f, 13f, 18f, 13f, 18f, 0f};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        float[] dest = new float[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assert.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void deltaTransformPointTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point dest = new Point();
        Point expected = new Point(8, 12);

        affineTransform.deltaTransform(src, dest);

        Assert.assertEquals(expected, dest);
    }

    @Test
    public void deltaTransformPointNullDestTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point expected = new Point(8, 12);

        Point dest = affineTransform.deltaTransform(src, null);

        Assert.assertEquals(expected, dest);
    }

    @Test
    public void deltaTransformDoubleArrayTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        double[] expected = new double[]{0d, 8d, 12d, 8d, 12d, 0d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        double[] dest = new double[6];

        affineTransform.deltaTransform(src, 1, dest, 1, 2);

        Assert.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void inverseTransformPointTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point dest = new Point();
        Point expected = new Point(0, -1);

        affineTransform.inverseTransform(src, dest);

        Assert.assertEquals(expected, dest);
    }

    @Test
    public void inverseTransformPointNullTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point expected = new Point(0, -1);

        Point dest = affineTransform.inverseTransform(src, null);

        Assert.assertEquals(expected, dest);
    }

    @Test
    public void inverseTransformDoubleArrayTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        double[] expected = new double[]{0d, -0d, -1d, -0d, -1d, 0d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        double[] dest = new double[6];

        affineTransform.inverseTransform(src, 1, dest, 1, 2);

        Assert.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void inverseTransformFloatArrayTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{0f, -0f, -1f, -0f, -1f, 0f};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};
        float[] dest = new float[6];

        affineTransform.inverseTransform(src, 1, dest, 1, 2);

        Assert.assertArrayEquals(expected, dest, 0);
    }
}

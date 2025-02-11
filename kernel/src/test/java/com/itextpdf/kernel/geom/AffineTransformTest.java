/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.geom;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class AffineTransformTest extends ExtendedITextTest {

    @Test
    public void selfTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assertions.assertTrue(affineTransform.equals(affineTransform));
    }

    @Test
    public void nullTest() {
        AffineTransform affineTransform = new AffineTransform();

        Assertions.assertFalse(affineTransform.equals(null));
    }

    @Test
    public void otherClassTest() {
        AffineTransform affineTransform = new AffineTransform();
        String string = "Test";

        Assertions.assertFalse(affineTransform.equals(string));
    }

    @Test
    public void sameValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assertions.assertFalse(affineTransform1 == affineTransform2);
        Assertions.assertEquals(hash1, hash2);
        Assertions.assertTrue(affineTransform1.equals(affineTransform2));
    }

    @Test
    public void differentValuesTest() {
        AffineTransform affineTransform1 = new AffineTransform(0d, 1d, 2d, 3d, 4d, 5d);
        AffineTransform affineTransform2 = new AffineTransform(5d, 4d, 3d, 2d, 1d, 1d);
        int hash1 = affineTransform1.hashCode();
        int hash2 = affineTransform2.hashCode();

        Assertions.assertFalse(affineTransform1 == affineTransform2);
        Assertions.assertNotEquals(hash1, hash2);
        Assertions.assertFalse(affineTransform1.equals(affineTransform2));
    }

    @Test
    public void getRotateInstanceTest() {
        AffineTransform rotateOne = AffineTransform.getRotateInstance(Math.PI / 2);
        AffineTransform expected = new AffineTransform(0, 1, -1, 0, 0, 0);

        Assertions.assertEquals(rotateOne, expected);
    }

    @Test
    public void getRotateInstanceTranslateTest() {
        AffineTransform rotateTranslate = AffineTransform.getRotateInstance(Math.PI / 2, 10, 5);
        AffineTransform expected = new AffineTransform(0, 1, -1, 0, 15, -5);

        Assertions.assertEquals(rotateTranslate, expected);
    }

    @Test
    public void cloneTest() throws CloneNotSupportedException {
        AffineTransform original = new AffineTransform();
        AffineTransform clone = original.clone();

        Assertions.assertTrue(original != clone);
        Assertions.assertTrue(original.equals(clone));
    }

    @Test
    public void getTransformValuesTest() {
        float[] matrix = new float[]{0f, 1f, 2f, 3f, 4f, 5f};
        AffineTransform affineTransform = new AffineTransform(matrix);

        Assertions.assertEquals(matrix[0], affineTransform.getScaleX(), 0.0);
        Assertions.assertEquals(matrix[3], affineTransform.getScaleY(), 0.0);
        Assertions.assertEquals(matrix[2], affineTransform.getShearX(), 0.0);
        Assertions.assertEquals(matrix[1], affineTransform.getShearY(), 0.0);
        Assertions.assertEquals(matrix[4], affineTransform.getTranslateX(), 0.0);
        Assertions.assertEquals(matrix[5], affineTransform.getTranslateY(), 0.0);
        Assertions.assertEquals(32, affineTransform.getType(), 0.0);
    }

    @Test
    public void createAffineTransformFromOtherATTest() {
        AffineTransform template = new AffineTransform(0, 1, 2, 3, 4, 5);
        AffineTransform result = new AffineTransform(template);

        Assertions.assertNotSame(template, result);
        Assertions.assertEquals(template, result);
    }

    @Test
    public void createAffineTransformFromFloatArrayTest() {
        float[] matrix = new float[]{0f, 1f, 2f, 3f, 4f, 5f};
        AffineTransform expected = new AffineTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
        AffineTransform result = new AffineTransform(matrix);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void createAffineTransformFromDoubleArrayTest() {
        double[] matrix = new double[]{0d, 1d, 2d, 3d, 4d, 5d};
        AffineTransform expected = new AffineTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
        AffineTransform result = new AffineTransform(matrix);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void setTransformTest() {
        float[] matrix = new float[]{0f, 1f, 2f, 3f, 4f, 5f};
        AffineTransform expected = new AffineTransform(matrix);
        AffineTransform result = new AffineTransform();

        result.setTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void setToIdentityTest() {
        AffineTransform expected = new AffineTransform(1, 0, 0, 1, 0, 0);
        AffineTransform result = new AffineTransform();

        result.setToIdentity();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void setToShearTypeIdentityTest() {
        double shx = 0d;
        double shy = 0d;
        AffineTransform expected = new AffineTransform(1, shx, shy, 1, 0, 0);
        AffineTransform result = new AffineTransform();

        result.setToShear(shx, shy);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void setToShearTypeUnknownTest() {
        double shx = 1d;
        double shy = 1d;
        AffineTransform expected = new AffineTransform(1, shx, shy, 1, 0, 0);
        AffineTransform result = new AffineTransform();

        result.setToShear(shx, shy);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getShearInstanceTest() {
        double shx = 1d;
        double shy = 1d;
        AffineTransform expected = new AffineTransform(1, shx, shy, 1, 0, 0);
        AffineTransform result = AffineTransform.getShearInstance(shx, shy);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void shearTest() {
        double shx = 1d;
        double shy = 1d;
        AffineTransform expected = new AffineTransform(4d, 6d, 4d, 6d, 5d, 6d);
        AffineTransform result = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);

        result.shear(shx, shy);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void rotateTest() {
        double angle = Math.PI / 2;
        AffineTransform expected = new AffineTransform(3d, 4d, -1d, -2d, 5d, 6d);
        AffineTransform result = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);

        result.rotate(angle);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void preConcatenateTest() {
        AffineTransform expected = new AffineTransform(6d, 6d, 14d, 14d, 24d, 24d);
        AffineTransform result = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);
        AffineTransform template = new AffineTransform(2d, 2d, 2d, 2d, 2d, 2d);

        result.preConcatenate(template);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void transformDoubleArrayTest() {
        AffineTransform affineTransform = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);
        double[] expected = new double[]{0d, 13d, 18d, 13d, 18d, 0d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        double[] dest = new double[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assertions.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void transformDoubleArraySourceDestEqualsTest() {
        AffineTransform affineTransform = new AffineTransform(1d, 2d, 3d, 4d, 5d, 6d);
        double[] expected = new double[]{2d, 2d, 13d, 18d, 13d, 18d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};

        affineTransform.transform(src, 1, src, 2, 2);

        Assertions.assertArrayEquals(expected, src, 0);
    }

    @Test
    public void transformFloatArrayTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{0f, 13f, 18f, 13f, 18f, 0f};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};
        float[] dest = new float[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assertions.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void transformFloatArraySourceDestEqualsTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{2f, 2f, 13f, 18f, 13f, 18f};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};

        affineTransform.transform(src, 1, src, 2, 2);

        Assertions.assertArrayEquals(expected, src, 0);
    }

    @Test
    public void transformFloatToDoubleTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        double[] expected = new double[]{0d, 13d, 18d, 13d, 18d, 0d};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};
        double[] dest = new double[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assertions.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void transformDoubleToFloatTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{0f, 13f, 18f, 13f, 18f, 0f};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        float[] dest = new float[6];

        affineTransform.transform(src, 1, dest, 1, 2);

        Assertions.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void deltaTransformPointTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point dest = new Point();
        Point expected = new Point(8, 12);

        affineTransform.deltaTransform(src, dest);

        Assertions.assertEquals(expected, dest);
    }

    @Test
    public void deltaTransformPointNullDestTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point expected = new Point(8, 12);

        Point dest = affineTransform.deltaTransform(src, null);

        Assertions.assertEquals(expected, dest);
    }

    @Test
    public void deltaTransformDoubleArrayTest() {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        double[] expected = new double[]{0d, 8d, 12d, 8d, 12d, 0d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        double[] dest = new double[6];

        affineTransform.deltaTransform(src, 1, dest, 1, 2);

        Assertions.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void inverseTransformPointTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point dest = new Point();
        Point expected = new Point(0, -1);

        affineTransform.inverseTransform(src, dest);

        Assertions.assertEquals(expected, dest);
    }

    @Test
    public void inverseTransformPointNullTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        Point src = new Point(2, 2);
        Point expected = new Point(0, -1);

        Point dest = affineTransform.inverseTransform(src, null);

        Assertions.assertEquals(expected, dest);
    }

    @Test
    public void inverseTransformDoubleArrayTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        double[] expected = new double[]{0d, -0d, -1d, -0d, -1d, 0d};
        double[] src = new double[]{2d, 2d, 2d, 2d, 2d, 2d};
        double[] dest = new double[6];

        affineTransform.inverseTransform(src, 1, dest, 1, 2);

        Assertions.assertArrayEquals(expected, dest, 0);
    }

    @Test
    public void inverseTransformFloatArrayTest() throws NoninvertibleTransformException {
        AffineTransform affineTransform = new AffineTransform(1f, 2f, 3f, 4f, 5f, 6f);
        float[] expected = new float[]{0f, -0f, -1f, -0f, -1f, 0f};
        float[] src = new float[]{2f, 2f, 2f, 2f, 2f, 2f};
        float[] dest = new float[6];

        affineTransform.inverseTransform(src, 1, dest, 1, 2);

        Assertions.assertArrayEquals(expected, dest, 0);
    }
}

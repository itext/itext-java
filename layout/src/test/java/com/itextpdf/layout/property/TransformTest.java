/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.property;

import com.itextpdf.kernel.geom.AffineTransform;

import org.junit.Assert;
import org.junit.Test;

public class TransformTest {
    // AffineTransform.TYPE_UNKNOWN
    private static final float type = -1;

    @Test
    public void createDefaultSingleTransformTest() {
        Transform.SingleTransform defaultSingleTransform = new Transform.SingleTransform();
        UnitValue[] defaultUnitValues = defaultSingleTransform.getUnitValues();
        Assert.assertArrayEquals(new float[] {1f, 0f, 0f, 1f}, defaultSingleTransform.getFloats(), 0);
        Assert.assertEquals(2, defaultUnitValues.length);
        Assert.assertEquals(UnitValue.createPointValue(0), defaultUnitValues[0]);
        Assert.assertEquals(UnitValue.createPointValue(0), defaultUnitValues[1]);
    }

    @Test
    public void getAffineTransformPercentPointUnitValuesTest() {
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, 12f, 30f, type}),
                getAffineTransform(UnitValue.PERCENT, UnitValue.POINT));
    }

    @Test
    public void getAffineTransformPointPercentUnitValuesTest() {
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, 20f, 24f, type}),
                getAffineTransform(UnitValue.POINT, UnitValue.PERCENT));
    }

    @Test
    public void getAffineTransformPercentPercentUnitValuesTest() {
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, 12f, 24f, type}),
                getAffineTransform(UnitValue.PERCENT, UnitValue.PERCENT));
    }

    @Test
    public void getAffineTransformPointPointUnitValuesTest() {
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, 20f, 30f, type}),
                getAffineTransform(UnitValue.POINT, UnitValue.POINT));
    }

    @Test
    public void getAffineTransformDiffSingleTransformTest() {
        final float txUnitValue = 20f;
        final float tyUnitValue2 = 30f;
        Transform transform = new Transform(4);
        transform.addSingleTransform(createSingleTransform(UnitValue.createPercentValue(txUnitValue),
                UnitValue.createPointValue(tyUnitValue2)));
        transform.addSingleTransform(createSingleTransform(UnitValue.createPointValue(txUnitValue),
                UnitValue.createPercentValue(tyUnitValue2)));
        transform.addSingleTransform(
                createSingleTransform(UnitValue.createPercentValue(txUnitValue),
                        UnitValue.createPercentValue(tyUnitValue2)));
        transform.addSingleTransform(createSingleTransform(UnitValue.createPointValue(txUnitValue),
                UnitValue.createPointValue(tyUnitValue2)));
        Assert.assertEquals(new AffineTransform(new float[] {-524f, -105f, 140f, -419f, -788f, 2220f, type}),
                Transform.getAffineTransform(transform, 60f, 80f));
    }

    @Test
    public void getAffineTransformOneSingleTransformFewTimesTest() {
        Transform transform = new Transform(4);
        Transform.SingleTransform singleTransform = createSingleTransform(UnitValue.createPointValue(20f),
                UnitValue.createPointValue(30f));
        transform.addSingleTransform(singleTransform);
        transform.addSingleTransform(singleTransform);
        transform.addSingleTransform(singleTransform);
        transform.addSingleTransform(singleTransform);
        Assert.assertEquals(new AffineTransform(new float[] {-524f, -105f, 140f, -419f, -700f, 2100f, type}),
                Transform.getAffineTransform(transform, 60f, 60f));
    }

    @Test
    public void getAffineTransformDifferentWidthHeightTest() {
        Transform transform = new Transform(1);
        transform.addSingleTransform(createSingleTransform(UnitValue.createPercentValue(20f),
                UnitValue.createPercentValue(30f)));
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, -10f, -6f, type}),
                Transform.getAffineTransform(transform, -50f, -20f));
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, 10f, -6f, type}),
                Transform.getAffineTransform(transform, 50f, -20f));
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, -10f, 6f, type}),
                Transform.getAffineTransform(transform, -50f, 20f));
        Assert.assertEquals(new AffineTransform(new float[] {-2f, 3f, -4f, -5f, 10f, 6f, type}),
                Transform.getAffineTransform(transform, 50f, 20f));
    }

    private static AffineTransform getAffineTransform(int txUnitValueType, int tyUnitValueType) {
        final float txUnitValue = 20f;
        final float tyUnitValue = 30f;
        final float width = 60f;
        final float height = 80f;

        // create Transform
        Transform transform = new Transform(1);
        transform.addSingleTransform(createSingleTransform(new UnitValue(txUnitValueType, txUnitValue),
                new UnitValue(tyUnitValueType, tyUnitValue)));

        // get AffineTransform
        return Transform.getAffineTransform(transform, width, height);
    }

    private static Transform.SingleTransform createSingleTransform(UnitValue xUnitVal, UnitValue yUnitVal) {
        final float a = -2f;
        final float b = 3f;
        final float c = -4f;
        final float d = -5f;
        return new Transform.SingleTransform(a, b, c, d, xUnitVal, yUnitVal);
    }
}

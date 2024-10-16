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
package com.itextpdf.kernel.geom;

import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BezierCurveTest extends ExtendedITextTest {

    @Test
    public void approximationTest() {
        List<Point> controlPoints = Arrays.asList(new Point(0f, 0f),
                new Point(-5f, 20f), new Point(7f, -15f), new Point(20f, 5f));
        BezierCurve curve = new BezierCurve(controlPoints);
        List<Point> approximation = curve.getPiecewiseLinearApproximation();

        List<Point> expectedApproximation = Arrays.asList(new Point(0f, 0f),
                new Point(-0.419434f, 1.717224f), new Point(-0.971191f, 4.265442f),
                new Point(-1.159668f, 5.766296f), new Point(-1.076721f, 6.301537f),
                new Point(-0.920105f, 6.414986f), new Point(-0.540527f, 6.270447f),
                new Point(0.220215f, 5.596008f), new Point(1.859375f, 3.879395f),
                new Point(4.025879f, 1.800232f), new Point(5.724121f, 0.481262f),
                new Point(7.598145f, -0.596008f), new Point(9.104919f, -1.148415f),
                new Point(10.152161f, -1.357231f), new Point(11.230164f, -1.414986f),
                new Point(12.335999f, -1.301537f), new Point(13.466736f, -0.996742f),
                new Point(14.619446f, -0.480461f), new Point(15.791199f, 0.267448f),
                new Point(16.979065f, 1.267128f), new Point(18.180115f, 2.538719f),
                new Point(19.391418f, 4.102364f), new Point(20f, 5f));

        Assertions.assertEquals(expectedApproximation.size(), approximation.size());

        for (int i = 0; i < expectedApproximation.size(); ++i) {
            Assertions.assertEquals(expectedApproximation.get(i).getX(), approximation.get(i).getX(), 0.001f);
            Assertions.assertEquals(expectedApproximation.get(i).getY(), approximation.get(i).getY(), 0.001f);
        }
    }

    @Test
    public void approximationWithEqualStartAndEndTest() {
        List<Point> controlPoints = Arrays.asList(new Point(0f, 0f),
                new Point(-5f, 20f), new Point(7f, -15f), new Point(0f, 0f));
        BezierCurve curve = new BezierCurve(controlPoints);
        List<Point> approximation = curve.getPiecewiseLinearApproximation();

        List<Point> expectedApproximation = Arrays.asList(new Point(0f, 0f),
                new Point(-0.747070f, 3.131104f), new Point(-1.235962f, 5.747223f),
                new Point(-1.244339f, 6.259632f), new Point(-1.177597f, 6.350613f),
                new Point(-0.985474f, 6.159210f), new Point(-0.592163f, 5.392914f),
                new Point(0.184570f, 3.460693f), new Point(1.291992f, 0.230713f),
                new Point(2.094727f, -2.618408f), new Point(2.243042f, -3.765106f),
                new Point(2.026978f, -3.769684f), new Point(1.497437f, -2.986908f),
                new Point(0.601685f, -1.262970f), new Point(0f, 0f));

        Assertions.assertEquals(expectedApproximation.size(), approximation.size());

        for (int i = 0; i < expectedApproximation.size(); ++i) {
            Assertions.assertEquals(expectedApproximation.get(i).getX(), approximation.get(i).getX(), 0.001f);
            Assertions.assertEquals(expectedApproximation.get(i).getY(), approximation.get(i).getY(), 0.001f);
        }
    }

    @Test
    public void approximationWithEqualSecondAndEndTest() {
        List<Point> controlPoints = Arrays.asList(new Point(0f, 0f),
                new Point(20f, 5f), new Point(7f, -15f), new Point(20f, 5f));
        BezierCurve curve = new BezierCurve(controlPoints);
        List<Point> approximation = curve.getPiecewiseLinearApproximation();

        List<Point> expectedApproximation = Arrays.asList(new Point(0f, 0f),
                new Point(1.780121f, 0.397491f), new Point(4.803497f, 0.800629f),
                new Point(7.183075f, 0.760651f), new Point(9.005280f, 0.372772f),
                new Point(10.356537f, -0.267792f), new Point(11.323273f, -1.065826f),
                new Point(12.241455f, -2.349854f), new Point(12.780609f, -3.452606f),
                new Point(13.073517f, -3.928375f), new Point(13.414032f, -4.085541f),
                new Point(13.888580f, -3.828888f), new Point(15.040771f, -2.459717f),
                new Point(17.852783f, 1.702881f), new Point(20f, 5f));

        Assertions.assertEquals(expectedApproximation.size(), approximation.size());

        for (int i = 0; i < expectedApproximation.size(); ++i) {
            Assertions.assertEquals(expectedApproximation.get(i).getX(), approximation.get(i).getX(), 0.001f);
            Assertions.assertEquals(expectedApproximation.get(i).getY(), approximation.get(i).getY(), 0.001f);
        }
    }

    @Test
    public void approximationWithEqualStartAndEndAndHugeDistanceToleranceManhattanTest() {
        List<Point> controlPoints = Arrays.asList(new Point(0f, 0f),
                new Point(-5f, 20f), new Point(7f, -15f), new Point(0f, 0f));
        BezierCurve curve = new BezierCurve(controlPoints);

        double oldDistanceToleranceManhattan = BezierCurve.distanceToleranceManhattan;
        try {
            BezierCurve.distanceToleranceManhattan = 142d;
            List<Point> approximation = curve.getPiecewiseLinearApproximation();

            List<Point> expectedApproximation = Arrays.asList(new Point(0.000000f, 0.000000f),
                    new Point(0.75f, 1.875f), new Point(0.000000f, 0.000000f));

            Assertions.assertEquals(expectedApproximation.size(), approximation.size());

            for (int i = 0; i < expectedApproximation.size(); ++i) {
                Assertions.assertEquals(expectedApproximation.get(i).getX(), approximation.get(i).getX(), 0.001f);
                Assertions.assertEquals(expectedApproximation.get(i).getY(), approximation.get(i).getY(), 0.001f);
            }
        } finally {
            BezierCurve.distanceToleranceManhattan = oldDistanceToleranceManhattan;
        }
    }
}

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
package com.itextpdf.kernel.pdf.canvas.parser.clipper;

import com.itextpdf.kernel.geom.IShape;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineCapStyle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineJoinStyle;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.ClipType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.EndType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.JoinType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.PolyType;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ClipperBridgeTest extends ExtendedITextTest {

    @Test
    public void squareClippingTest() {
        Subpath squareSubpath = new Subpath(new com.itextpdf.kernel.geom.Point(10, 10));
        squareSubpath.addSegment(new Line(10, 10, 10, 30));
        squareSubpath.addSegment(new Line(10, 30, 30, 30));
        squareSubpath.addSegment(new Line(30, 30, 30, 10));
        squareSubpath.addSegment(new Line(30, 10, 10, 10));
        squareSubpath.setClosed(true);
        Path squarePath = new Path();
        squarePath.addSubpath(squareSubpath);

        Subpath rectangleSubpath = new Subpath(new com.itextpdf.kernel.geom.Point(20, 20));
        rectangleSubpath.addSegment(new Line(20, 20, 20, 40));
        rectangleSubpath.addSegment(new Line(20, 40, 30, 40));
        rectangleSubpath.addSegment(new Line(30, 40, 30, 20));
        rectangleSubpath.addSegment(new Line(30, 20, 20, 20));
        rectangleSubpath.setClosed(true);
        Path rectanglePath = new Path();
        rectanglePath.addSubpath(rectangleSubpath);

        DefaultClipper clipper = new DefaultClipper();
        ClipperBridge clipperBridge = new ClipperBridge(squarePath, rectanglePath);
        clipperBridge.addPath(clipper, squarePath, PolyType.SUBJECT);
        clipperBridge.addPath(clipper, rectanglePath, PolyType.CLIP);

        PolyTree polyTree = new PolyTree();
        clipper.execute(ClipType.UNION, polyTree);
        Path result = clipperBridge.convertToPath(polyTree);

        Assertions.assertEquals(new com.itextpdf.kernel.geom.Point(20, 40), result.getCurrentPoint());
        Assertions.assertEquals(2, result.getSubpaths().size());

        Subpath closedPath = result.getSubpaths().get(0);
        Assertions.assertEquals(new com.itextpdf.kernel.geom.Point(20, 40), closedPath.getStartPoint());
        List<IShape> closedPartSegments = closedPath.getSegments();
        Assertions.assertEquals(5, closedPartSegments.size());
        Assertions.assertTrue(areShapesEqual(new Line(20, 40, 20, 30), closedPartSegments.get(0)));
        Assertions.assertTrue(areShapesEqual(new Line(20, 30, 10, 30), closedPartSegments.get(1)));
        Assertions.assertTrue(areShapesEqual(new Line(10, 30, 10, 10), closedPartSegments.get(2)));
        Assertions.assertTrue(areShapesEqual(new Line(10, 10, 30, 10), closedPartSegments.get(3)));
        Assertions.assertTrue(areShapesEqual(new Line(30, 10, 30, 40), closedPartSegments.get(4)));
        Assertions.assertTrue(closedPath.isClosed());

        Subpath openPart = result.getSubpaths().get(1);
        Assertions.assertEquals(new com.itextpdf.kernel.geom.Point(20, 40), openPart.getStartPoint());
        Assertions.assertEquals(0, openPart.getSegments().size());
        Assertions.assertFalse(openPart.isClosed());
    }

    @Test
    public void getJoinTypeTest() {
        Assertions.assertEquals(JoinType.BEVEL, ClipperBridge.getJoinType(LineJoinStyle.BEVEL));
        Assertions.assertEquals(JoinType.MITER, ClipperBridge.getJoinType(LineJoinStyle.MITER));
        Assertions.assertEquals(JoinType.ROUND, ClipperBridge.getJoinType(LineJoinStyle.ROUND));
    }

    @Test
    public void getEndTypeTest() {
        Assertions.assertEquals(EndType.OPEN_BUTT, ClipperBridge.getEndType(LineCapStyle.BUTT));
        Assertions.assertEquals(EndType.OPEN_SQUARE, ClipperBridge.getEndType(LineCapStyle.PROJECTING_SQUARE));
        Assertions.assertEquals(EndType.OPEN_ROUND, ClipperBridge.getEndType(LineCapStyle.ROUND));
    }

    @Test
    public void longRectWidthTest() {
        LongRect longRect = new LongRect(14900000000000000L, 21275000000000000L, 71065802001953128L, 71075000000000000L);
        Assertions.assertEquals(561.658, new ClipperBridge().longRectCalculateWidth(longRect), 0.001f);
    }


    @Test
    public void longRectHeightTest() {
        LongRect longRect = new LongRect(14900000000000000L, 21275000000000000L, 71065802001953128L, 71075000000000000L);
        Assertions.assertEquals(498, new ClipperBridge().longRectCalculateHeight(longRect), 0.001f);
    }

    @Test
    public void dynamicFloatMultiplierCalculationsSmallValuesTest() {
        Point[] points = new Point[]{
                new Point(1e-10, 0),
                new Point(0, 1e-13)
        };
        Assertions.assertEquals(1.8014398509481984e26, new ClipperBridge(points).getFloatMultiplier(), 0e+10);
    }

    @Test
    public void dynamicFloatMultiplierCalculationsBigValuesTest() {
        Point[] points = new Point[]{
                new Point(1e+11, 10),
                new Point(10, 1e+10)
        };
        Assertions.assertEquals(180143, new ClipperBridge(points).getFloatMultiplier(), 0.001f);
    }

    @Test
    public void smallFloatMultiplierCoefficientTest() {
        Point[] points = new Point[]{new Point(1e-10, 1e+10)};
        Assertions.assertEquals(
                new com.itextpdf.kernel.pdf.canvas.parser.clipper.Point.LongPoint(0, 18014390000000000L),
                new ClipperBridge(points).convertToLongPoints(Arrays.asList(points)).get(0));
    }

    private boolean areShapesEqual(IShape expected, IShape actual) {
        if (expected == actual) {
            return true;
        }
        if (actual == null || expected.getClass() != actual.getClass()) {
            return false;
        }
        return expected.getBasePoints().equals(actual.getBasePoints());
    }
}

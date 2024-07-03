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
package com.itextpdf.kernel.pdf.canvas.parser.clipper;

import com.itextpdf.kernel.geom.IShape;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.geom.Subpath;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineCapStyle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineJoinStyle;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.ClipType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.EndType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.JoinType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.PolyType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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
        ClipperBridge.addPath(clipper, squarePath, PolyType.SUBJECT);
        ClipperBridge.addPath(clipper, rectanglePath, PolyType.CLIP);

        PolyTree polyTree = new PolyTree();
        clipper.execute(ClipType.UNION, polyTree);
        Path result = ClipperBridge.convertToPath(polyTree);

        Assert.assertEquals(new com.itextpdf.kernel.geom.Point(20, 40), result.getCurrentPoint());
        Assert.assertEquals(2, result.getSubpaths().size());

        Subpath closedPath = result.getSubpaths().get(0);
        Assert.assertEquals(new com.itextpdf.kernel.geom.Point(20, 40), closedPath.getStartPoint());
        List<IShape> closedPartSegments = closedPath.getSegments();
        Assert.assertEquals(5, closedPartSegments.size());
        Assert.assertTrue(areShapesEqual(new Line(20, 40, 20, 30), closedPartSegments.get(0)));
        Assert.assertTrue(areShapesEqual(new Line(20, 30, 10, 30), closedPartSegments.get(1)));
        Assert.assertTrue(areShapesEqual(new Line(10, 30, 10, 10), closedPartSegments.get(2)));
        Assert.assertTrue(areShapesEqual(new Line(10, 10, 30, 10), closedPartSegments.get(3)));
        Assert.assertTrue(areShapesEqual(new Line(30, 10, 30, 40), closedPartSegments.get(4)));
        Assert.assertTrue(closedPath.isClosed());

        Subpath openPart = result.getSubpaths().get(1);
        Assert.assertEquals(new com.itextpdf.kernel.geom.Point(20, 40), openPart.getStartPoint());
        Assert.assertEquals(0, openPart.getSegments().size());
        Assert.assertFalse(openPart.isClosed());
    }

    @Test
    public void getJoinTypeTest() {
        Assert.assertEquals(JoinType.BEVEL, ClipperBridge.getJoinType(LineJoinStyle.BEVEL));
        Assert.assertEquals(JoinType.MITER, ClipperBridge.getJoinType(LineJoinStyle.MITER));
        Assert.assertEquals(JoinType.ROUND, ClipperBridge.getJoinType(LineJoinStyle.ROUND));
    }

    @Test
    public void getEndTypeTest() {
        Assert.assertEquals(EndType.OPEN_BUTT, ClipperBridge.getEndType(LineCapStyle.BUTT));
        Assert.assertEquals(EndType.OPEN_SQUARE, ClipperBridge.getEndType(LineCapStyle.PROJECTING_SQUARE));
        Assert.assertEquals(EndType.OPEN_ROUND, ClipperBridge.getEndType(LineCapStyle.ROUND));
    }

    @Test
    public void longRectWidthTest() {
        LongRect longRect = new LongRect(14900000000000000L, 21275000000000000L, 71065802001953128L, 71075000000000000L);
        Assert.assertEquals(561.658, ClipperBridge.longRectCalculateWidth(longRect), 0.001f);
    }


    @Test
    public void longRectHeightTest() {
        LongRect longRect = new LongRect(14900000000000000L, 21275000000000000L, 71065802001953128L, 71075000000000000L);
        Assert.assertEquals(498, ClipperBridge.longRectCalculateHeight(longRect), 0.001f);
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

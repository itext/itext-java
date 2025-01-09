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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.impl.ClosePath;
import com.itextpdf.svg.renderers.path.impl.EllipticalCurveTo;
import com.itextpdf.svg.renderers.path.impl.MoveTo;
import com.itextpdf.svg.renderers.path.impl.SmoothSCurveTo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.util.List;

@Tag("IntegrationTest")
public class PathSvgNodeRendererLowLevelIntegrationTest extends SvgIntegrationTest {

    @Test
    public void testRelativeArcOperatorShapes() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        List<IPathShape> segments = (List<IPathShape>) path.getShapes();
        Assertions.assertEquals(2, segments.size());
        Assertions.assertTrue(segments.get(0) instanceof MoveTo);
        Assertions.assertTrue(segments.get(1) instanceof EllipticalCurveTo);
    }

    @Test
    public void testRelativeArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(1);
        Point end = arc.getEndingPoint();
        Assertions.assertEquals(new Point(210, 310), end);
    }

    @Test
    public void testMultipleRelativeArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(2);
        Point end = arc.getEndingPoint();
        Assertions.assertEquals(new Point(220, 320), end);
    }

    @Test
    public void testAbsoluteArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 A 10 10 0 0 0 210 310";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(1);
        Point end = arc.getEndingPoint();
        Assertions.assertEquals(new Point(210, 310), end);
    }

    @Test
    public void testMultipleAbsoluteArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 A 10 10 0 0 0 210 310 A 10 10 0 0 0 220 320";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(2);
        Point end = arc.getEndingPoint();
        Assertions.assertEquals(new Point(220, 320), end);
    }

    // tests resulting in empty path
    @Test
    public void testEmptyPath() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testNonsensePathNoOperators() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "200";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testNonsensePathNotExistingOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "F";
        path.setAttribute(SvgConstants.Attributes.D, instructions);

        Assertions.assertThrows(SvgProcessingException.class, () -> path.getShapes());
    }

    @Test
    public void testClosePathNoPrecedingPathsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "z";
        path.setAttribute(SvgConstants.Attributes.D, instructions);

        Assertions.assertThrows(SvgProcessingException.class, () -> path.getShapes());
    }

    @Test
    public void testMoveNoArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testMoveOddArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testAddMultipleArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500 500 200 200 300 300";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertEquals(3, path.getShapes().size());
    }

    @Test
    public void testAddMultipleOddArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "L 500 500 200 200 300";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertEquals(2, path.getShapes().size());
    }

    @Test
    public void testAddMultipleOddArgsOperatorThenOtherStuff() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500 500 200 200 300 z";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertEquals(3, path.getShapes().size());
        Assertions.assertTrue(((List<IPathShape>) path.getShapes()).get(2) instanceof ClosePath);
    }

    @Test
    public void testAddDoubleArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500 500 S 200 100 100 200 300 300 400 400";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assertions.assertEquals(3, path.getShapes().size());
        Assertions.assertTrue(((List<IPathShape>) path.getShapes()).get(2) instanceof SmoothSCurveTo);
    }

    @Test
    public void smoothCurveAsFirstShapeTest1() {
        String instructions = "S 100 200 300 400";
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, instructions);

        Exception e = Assertions.assertThrows(SvgProcessingException.class, () -> path.getShapes());
        Assertions.assertEquals(SvgExceptionMessageConstant.INVALID_SMOOTH_CURVE_USE, e.getMessage());
    }

    @Test
    public void smoothCurveAsFirstShapeTest2() {
        String instructions = "T 100,200";
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        path.setAttribute(SvgConstants.Attributes.D, instructions);

        Exception e = Assertions.assertThrows(SvgProcessingException.class, () -> path.getShapes());
        Assertions.assertEquals(SvgExceptionMessageConstant.INVALID_SMOOTH_CURVE_USE, e.getMessage());
    }
}

package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.impl.EllipticalCurveTo;
import com.itextpdf.svg.renderers.path.impl.MoveTo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

public class PathSvgNodeRendererLowLevelIntegrationTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void testRelativeArcOperatorShapes() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        List<IPathShape> segments = (List<IPathShape>) path.getShapes();
        Assert.assertEquals(2, segments.size());
        Assert.assertTrue(segments.get(0) instanceof MoveTo);
        Assert.assertTrue(segments.get(1) instanceof EllipticalCurveTo);
    }

    @Test
    public void testRelativeArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(1);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(210, 310), end);
    }

    @Test
    public void testMultipleRelativeArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(2);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(220, 320), end);
    }

    @Test
    public void testAbsoluteArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 A 10 10 0 0 0 210 310";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(1);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(210, 310), end);
    }

    @Test
    public void testMultipleAbsoluteArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 A 10 10 0 0 0 210 310 A 10 10 0 0 0 220 320";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(2);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(220, 320), end);
    }

    // tests resulting in empty path
    @Test
    public void testEmptyPath() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testNonsensePathNoOperators() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "200";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testNonsensePathNotExistingOperator() {
        junitExpectedException.expect(SvgProcessingException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "F";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testClosePathNoPrecedingPathsOperator() {
        junitExpectedException.expect(SvgProcessingException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "z";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testMoveNoArgsOperator() {
        junitExpectedException.expect(IllegalArgumentException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testMoveOddArgsOperator() {
        junitExpectedException.expect(IllegalArgumentException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }
}

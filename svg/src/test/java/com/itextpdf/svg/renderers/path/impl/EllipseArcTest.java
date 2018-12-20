package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;
import org.junit.Assert;
import org.junit.Test;

import static com.itextpdf.svg.renderers.path.impl.EllipticalCurveTo.EllipseArc;

public class EllipseArcTest {

    private static final double DELTA = 0.00001;

    private void assertPointEqual(Point expected, Point actual) {
        Assert.assertEquals(expected.x, actual.x, DELTA);
        Assert.assertEquals(expected.y, actual.y, DELTA);
    }

    @Test
    public void testCircleSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, true, true);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assert.assertEquals(180, arc.extent, DELTA);
        Assert.assertEquals(180, arc.startAng, DELTA);
    }

    @Test
    public void testCircleSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, true, false);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assert.assertEquals(180, arc.extent, DELTA);
        Assert.assertEquals(180, arc.startAng, DELTA);
    }

    @Test
    public void testCircleNotSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, false, true);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assert.assertEquals(180, arc.extent, DELTA);
        Assert.assertEquals(0, arc.startAng, DELTA);
    }

    @Test
    public void testCircleNotSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, false, false);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assert.assertEquals(180, arc.extent, DELTA);
        Assert.assertEquals(0, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, true, true);
        assertPointEqual(new Point(-20, -19.428090), arc.ll);
        assertPointEqual(new Point(40, 0.571909), arc.ur);
        Assert.assertEquals(321.057558, arc.extent, DELTA);
        Assert.assertEquals(109.471220, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, true, false);
        assertPointEqual(new Point(-20, -0.571909), arc.ll);
        assertPointEqual(new Point(40, 19.428090), arc.ur);
        Assert.assertEquals(38.942441, arc.extent, DELTA);
        Assert.assertEquals(250.528779, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseNotSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, false, true);
        assertPointEqual(new Point(-20, -0.571909), arc.ll);
        assertPointEqual(new Point(40, 19.428090), arc.ur);
        Assert.assertEquals(321.057558, arc.extent, DELTA);
        Assert.assertEquals(289.4712206344907, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseNotSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, false, false);
        assertPointEqual(new Point(-20, -19.428090), arc.ll);
        assertPointEqual(new Point(40, 0.5719095), arc.ur);
        Assert.assertEquals(38.942441, arc.extent, DELTA);
        Assert.assertEquals(70.528779, arc.startAng, DELTA);
    }
}

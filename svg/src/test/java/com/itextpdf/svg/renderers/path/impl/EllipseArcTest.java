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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static com.itextpdf.svg.renderers.path.impl.EllipticalCurveTo.EllipseArc;

@Tag("UnitTest")
public class EllipseArcTest extends ExtendedITextTest {

    private static final double DELTA = 0.00001;

    private void assertPointEqual(Point expected, Point actual) {
        Assertions.assertEquals(expected.getX(), actual.getX(), DELTA);
        Assertions.assertEquals(expected.getY(), actual.getY(), DELTA);
    }

    @Test
    public void testCircleSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, true, true);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assertions.assertEquals(180, arc.extent, DELTA);
        Assertions.assertEquals(180, arc.startAng, DELTA);
    }

    @Test
    public void testCircleSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, true, false);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assertions.assertEquals(180, arc.extent, DELTA);
        Assertions.assertEquals(180, arc.startAng, DELTA);
    }

    @Test
    public void testCircleNotSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, false, true);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assertions.assertEquals(180, arc.extent, DELTA);
        Assertions.assertEquals(0, arc.startAng, DELTA);
    }

    @Test
    public void testCircleNotSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 10, 10, false, false);
        assertPointEqual(new Point(0, -10), arc.ll);
        assertPointEqual(new Point(20, 10), arc.ur);
        Assertions.assertEquals(180, arc.extent, DELTA);
        Assertions.assertEquals(0, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, true, true);
        assertPointEqual(new Point(-20, -19.428090), arc.ll);
        assertPointEqual(new Point(40, 0.571909), arc.ur);
        Assertions.assertEquals(321.057558, arc.extent, DELTA);
        Assertions.assertEquals(109.471220, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, true, false);
        assertPointEqual(new Point(-20, -0.571909), arc.ll);
        assertPointEqual(new Point(40, 19.428090), arc.ur);
        Assertions.assertEquals(38.942441, arc.extent, DELTA);
        Assertions.assertEquals(250.528779, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseNotSweepLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, false, true);
        assertPointEqual(new Point(-20, -0.571909), arc.ll);
        assertPointEqual(new Point(40, 19.428090), arc.ur);
        Assertions.assertEquals(321.057558, arc.extent, DELTA);
        Assertions.assertEquals(289.4712206344907, arc.startAng, DELTA);
    }

    @Test
    public void testEllipseNotSweepNotLarge() {
        EllipseArc arc = EllipseArc.getEllipse(new Point(0, 0), new Point(20, 0), 30, 10, false, false);
        assertPointEqual(new Point(-20, -19.428090), arc.ll);
        assertPointEqual(new Point(40, 0.5719095), arc.ur);
        Assertions.assertEquals(38.942441, arc.extent, DELTA);
        Assertions.assertEquals(70.528779, arc.startAng, DELTA);
    }
}

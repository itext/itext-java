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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class LineSegmentTest extends ExtendedITextTest {
    @Test
    public void containsPointNullTest() {
        LineSegment lineSegment = new LineSegment(new Vector(20.3246f, 769.4974f, 1.0f),
                new Vector(151.22923f, 769.4974f, 1.0f));

        Assertions.assertFalse(lineSegment.containsPoint(null));
    }

    @Test
    public void containsPointTest() {
        Vector pointToCheck = new Vector(20.3246f, 769.4974f, 1.0f);
        LineSegment lineSegment = new LineSegment(pointToCheck,
                new Vector(151.22923f, 769.4974f, 1.0f));

        Assertions.assertTrue(lineSegment.containsPoint(pointToCheck));
    }

    @Test
    public void notContainsPointLeftTest() {
        Vector pointToCheck = new Vector(100.3246f, 769.4974f, 1.0f);
        LineSegment lineSegment = new LineSegment(new Vector(120.3246f, 769.4974f, 1.0f),
                new Vector(151.22923f, 769.4974f, 1.0f));

        Assertions.assertFalse(lineSegment.containsPoint(pointToCheck));
    }

    @Test
    public void notContainsPointRightTest() {
        Vector pointToCheck = new Vector(160.3246f, 769.4974f, 1.0f);
        LineSegment lineSegment = new LineSegment(new Vector(120.3246f, 769.4974f, 1.0f),
                new Vector(151.22923f, 769.4974f, 1.0f));

        Assertions.assertFalse(lineSegment.containsPoint(pointToCheck));
    }

    @Test
    public void containsSegmentNullTest() {
        LineSegment lineSegment = new LineSegment(new Vector(100.3246f, 769.4974f, 1.0f),
                new Vector(151.22923f, 769.4974f, 1.0f));

        Assertions.assertFalse(lineSegment.containsSegment(null));
    }

    @Test
    public void containsSegmentTest() {
        LineSegment lineSegment = new LineSegment(new Vector(100.3246f, 769.4974f, 1.0f),
                new Vector(151.22923f, 769.4974f, 1.0f));

        LineSegment segmentToCheck = new LineSegment(new Vector(110.3246f, 769.4974f, 1.0f),
                new Vector(140.22923f, 769.4974f, 1.0f));

        Assertions.assertTrue(lineSegment.containsSegment(segmentToCheck));
    }

    @Test
    public void notContainsSegmentTest() {
        LineSegment lineSegment = new LineSegment(new Vector(120.3246f, 769.4974f, 1.0f),
                new Vector(151.22923f, 769.4974f, 1.0f));

        LineSegment segmentToCheck = new LineSegment(new Vector(110.3246f, 769.4974f, 1.0f),
                new Vector(115.22923f, 769.4974f, 1.0f));

        Assertions.assertFalse(lineSegment.containsSegment(segmentToCheck));
    }
}

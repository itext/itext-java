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

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class RectangleSvgNodeRendererUnitTest extends ExtendedITextTest {

    private static final float EPSILON = 0.00001f;
    RectangleSvgNodeRenderer renderer;

    @BeforeEach
    public void setup() {
        renderer = new RectangleSvgNodeRenderer();
    }

    @Test
    public void checkRadiusTest() {
        float rad = renderer.checkRadius(0f, 20f);
        Assertions.assertEquals(0f, rad, EPSILON);
    }

    @Test
    public void checkRadiusNegativeTest() {
        float rad = renderer.checkRadius(-1f, 20f);
        Assertions.assertEquals(0f, rad, EPSILON);
    }

    @Test
    public void checkRadiusTooLargeTest() {
        float rad = renderer.checkRadius(30f, 20f);
        Assertions.assertEquals(10f, rad, EPSILON);
    }

    @Test
    public void checkRadiusTooLargeNegativeTest() {
        float rad = renderer.checkRadius(-100f, 20f);
        Assertions.assertEquals(0f, rad, EPSILON);
    }

    @Test
    public void checkRadiusHalfLengthTest() {
        float rad = renderer.checkRadius(10f, 20f);
        Assertions.assertEquals(10f, rad, EPSILON);
    }

    @Test
    public void findCircularRadiusTest() {
        float rad = renderer.findCircularRadius(0f, 20f, 100f, 200f);
        Assertions.assertEquals(20f, rad, EPSILON);
    }

    @Test
    public void findCircularRadiusHalfLengthTest() {
        float rad = renderer.findCircularRadius(0f, 200f, 100f, 200f);
        Assertions.assertEquals(50f, rad, EPSILON);
    }

    @Test
    public void findCircularRadiusSmallWidthTest() {
        float rad = renderer.findCircularRadius(0f, 20f, 5f, 200f);
        Assertions.assertEquals(2.5f, rad, EPSILON);
    }
}

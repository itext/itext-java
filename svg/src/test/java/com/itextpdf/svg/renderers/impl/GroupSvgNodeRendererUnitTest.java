/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class GroupSvgNodeRendererUnitTest extends ExtendedITextTest {

    private static final float EPSILON = 0.00001f;

    @Test
    public void noObjectBoundingBoxTest() {
        GroupSvgNodeRenderer renderer = new GroupSvgNodeRenderer();
        Assertions.assertNull(renderer.getObjectBoundingBox(null));
    }

    @Test
    public void translatedChildObjectBoundingBoxTest() {
        GroupSvgNodeRenderer group = new GroupSvgNodeRenderer();
        RectangleSvgNodeRenderer child = createRectangle("10pt", "20pt", "30pt", "40pt");
        child.setAttribute(SvgConstants.Attributes.TRANSFORM, "translate(5pt 7pt)");
        group.addChild(child);

        Rectangle objectBoundingBox = group.getObjectBoundingBox(createContext());

        Assertions.assertNotNull(objectBoundingBox);
        Assertions.assertTrue(new Rectangle(15, 27, 30, 40).equalsWithEpsilon(objectBoundingBox, EPSILON));
    }

    @Test
    public void rotatedChildObjectBoundingBoxTest() {
        GroupSvgNodeRenderer group = new GroupSvgNodeRenderer();
        RectangleSvgNodeRenderer child = createRectangle("10pt", "20pt", "30pt", "40pt");
        child.setAttribute(SvgConstants.Attributes.TRANSFORM, "rotate(90)");
        group.addChild(child);

        Rectangle objectBoundingBox = group.getObjectBoundingBox(createContext());

        Assertions.assertNotNull(objectBoundingBox);
        Assertions.assertTrue(new Rectangle(-60, 10, 40, 30).equalsWithEpsilon(objectBoundingBox, EPSILON));
    }

    private static RectangleSvgNodeRenderer createRectangle(String x, String y, String width, String height) {
        RectangleSvgNodeRenderer rectangle = new RectangleSvgNodeRenderer();
        rectangle.setAttribute(SvgConstants.Attributes.X, x);
        rectangle.setAttribute(SvgConstants.Attributes.Y, y);
        rectangle.setAttribute(SvgConstants.Attributes.WIDTH, width);
        rectangle.setAttribute(SvgConstants.Attributes.HEIGHT, height);
        return rectangle;
    }

    private static SvgDrawContext createContext() {
        SvgDrawContext context = new SvgDrawContext(null, null);
        context.addViewPort(new Rectangle(0, 0, 200, 200));
        return context;
    }
}

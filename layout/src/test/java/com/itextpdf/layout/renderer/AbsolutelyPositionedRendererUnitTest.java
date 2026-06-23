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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class AbsolutelyPositionedRendererUnitTest extends ExtendedITextTest {
    private static final LayoutArea DUMMY_AREA = new LayoutArea(1, new Rectangle(0, 0));

    @Test
    public void layoutForElementWhichReturnsNothingTest() {
        CustomRenderer customRenderer = new CustomRenderer(new Div());
        AbsolutelyPositionedRenderer absolutelyPositionedRenderer = new AbsolutelyPositionedRenderer(customRenderer, false, false);
        absolutelyPositionedRenderer.layout(new LayoutContext(DUMMY_AREA));

        Assertions.assertEquals(2, customRenderer.counter);
    }

    @Test
    public void getNextRendererTest() {
        AbsolutelyPositionedRenderer absolutelyPositionedRenderer = new AbsolutelyPositionedRenderer(new DivRenderer(new Div()), false, false);
        IRenderer nextRenderer = absolutelyPositionedRenderer.getNextRenderer();

        Assertions.assertTrue(nextRenderer instanceof AbsolutelyPositionedRenderer);
        Assertions.assertTrue(((AbsolutelyPositionedRenderer)nextRenderer).getWrappedRenderer() instanceof DivRenderer);
    }

    @Test
    public void getPropertyTest() {
        DivRenderer wrappedRenderer = new DivRenderer(new Div());
        wrappedRenderer.setProperty(Property.POSITION, LayoutPosition.ABSOLUTE);
        wrappedRenderer.setProperty(Property.LEFT, 50);
        AbsolutelyPositionedRenderer absolutelyPositionedRenderer = new AbsolutelyPositionedRenderer(wrappedRenderer, false, false);

        Assertions.assertEquals(LayoutPosition.STATIC, absolutelyPositionedRenderer.<Integer>getProperty(Property.POSITION));
        Assertions.assertEquals(50, absolutelyPositionedRenderer.<Integer>getProperty(Property.LEFT));
    }

    static class CustomRenderer extends DivRenderer {
        public int counter = 0;

        public CustomRenderer(Div modelElement) {
            super(modelElement);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            counter++;
            return new LayoutResult(LayoutResult.NOTHING, DUMMY_AREA, null, null);
        }
    }
}

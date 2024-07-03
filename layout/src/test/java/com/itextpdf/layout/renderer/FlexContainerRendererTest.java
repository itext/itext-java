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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.FlexWrapPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class FlexContainerRendererTest extends ExtendedITextTest {
    private static float EPS = 0.0001F;

    @Test
    public void widthNotSetTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        DivRenderer divRenderer = new DivRenderer(new Div());

        flexRenderer.addChild(divRenderer);

        Assertions.assertEquals(0F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(0F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToChildOneChildTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        DivRenderer divRenderer = new DivRenderer(new Div());
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(50));

        flexRenderer.addChild(divRenderer);

        Assertions.assertEquals(50F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(50F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToChildManyChildrenTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(5));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(125F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(125F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToChildManyChildrenWithBordersMarginsPaddingsTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        divRenderer1.setProperty(Property.BORDER, new SolidBorder(5));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        divRenderer2.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(10));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        divRenderer3.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(15));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(10));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(165F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(165F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToFlexRendererAndToChildManyChildrenWithBordersMarginsPaddingsTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(50));

        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        divRenderer1.setProperty(Property.BORDER, new SolidBorder(5));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        divRenderer2.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(10));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        divRenderer3.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(15));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(10));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(50F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(50F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToChildManyChildrenFlexRendererWithRotationAngleTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.ROTATION_ANGLE, 10f);

        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(5));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(104.892334F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(104.892334F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToChildManyChildrenFlexRendererWithMinWidthTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(71));

        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(5));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(71F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(125F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }


    @Test
    public void decreaseLayoutBoxAfterChildPlacementResultsOccupiedAreaNull() {
        FlexContainerRenderer splitRenderer = new FlexContainerRenderer(new Div());
        splitRenderer.occupiedArea = new LayoutArea(0, new Rectangle(0, 0));

        LayoutResult nothing = new LayoutResult(LayoutResult.NOTHING, null, splitRenderer, null);
        Assertions.assertNotNull(new FlexContainerRenderer(new Div())
                .getOccupiedAreaInCaseNothingWasWrappedWithFull(nothing, splitRenderer));
    }

    @Test
    public void widthSetToChildManyChildrenFlexRendererWithMinWidthBiggerThanMaxWidthTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(150));

        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(5));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(150F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(150F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToChildManyChildrenFlexRendererWithMaxWidthTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(150));

        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(5));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(125F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(150F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    public void widthSetToChildManyChildrenFlexRendererWithMaxWidthLowerThanMinWidthTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(100));

        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(5));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);

        Assertions.assertEquals(100F, flexRenderer.getMinMaxWidth().getMinWidth(), EPS);
        Assertions.assertEquals(100F, flexRenderer.getMinMaxWidth().getMaxWidth(), EPS);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.GET_NEXT_RENDERER_SHOULD_BE_OVERRIDDEN)
    })
    public void getNextRendererShouldBeOverriddenTest() {
        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(new Div()) {
            // Nothing is overridden
        };

        Assertions.assertEquals(FlexContainerRenderer.class, flexContainerRenderer.getNextRenderer().getClass());
    }

    @Test
    public void hypotheticalCrossSizeCacheTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(150));

        FlexContainerRenderer flexRendererChild = new FlexContainerRenderer(new Div());
        flexRendererChild.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(150));

        DivRenderer divRenderer = new DivRenderer(new Div());
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(150));

        flexRendererChild.addChild(divRenderer);
        flexRenderer.addChild(flexRendererChild);

        // In general, it's possible that we might call layout more than once for 1 renderer
        flexRenderer.layout(new LayoutContext(
                new LayoutArea(0, new Rectangle(100, 0))));
        flexRendererChild.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(125));
        flexRenderer.layout(new LayoutContext(
                new LayoutArea(0, new Rectangle(200, 0))));

        // Test that hypotheticalCrossSizes can contain more than 1 value
        Assertions.assertNotNull(flexRendererChild.getHypotheticalCrossSize(125F));
        Assertions.assertNotNull(flexRendererChild.getHypotheticalCrossSize(150F));
    }

    @Test
    public void minMaxWidthForFlexRendererWithWrapTest() {
        FlexContainerRenderer flexRenderer = new FlexContainerRenderer(new Div());
        flexRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
        flexRenderer.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(100));
        flexRenderer.setProperty(Property.BORDER, new SolidBorder(5));

        // line 1
        DivRenderer divRenderer1 = new DivRenderer(new Div());
        divRenderer1.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(30));
        divRenderer1.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(50));
        DivRenderer divRenderer2 = new DivRenderer(new Div());
        divRenderer2.setProperty(Property.WIDTH, UnitValue.createPointValue(40));
        // line 2
        DivRenderer divRenderer3 = new DivRenderer(new Div());
        divRenderer3.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        DivRenderer divRenderer4 = new DivRenderer(new Div());
        divRenderer4.setProperty(Property.WIDTH, UnitValue.createPointValue(5));
        // line 3
        DivRenderer divRenderer5 = new DivRenderer(new Div());
        divRenderer5.setProperty(Property.WIDTH, UnitValue.createPointValue(75));

        flexRenderer.addChild(divRenderer1);
        flexRenderer.addChild(divRenderer2);
        flexRenderer.addChild(divRenderer3);
        flexRenderer.addChild(divRenderer4);
        flexRenderer.addChild(divRenderer5);

        flexRenderer.layout(new LayoutContext(new LayoutArea(0, new Rectangle(100, 100))));

        MinMaxWidth minMaxWidth = flexRenderer.getMinMaxWidth();
        Assertions.assertEquals(75F, minMaxWidth.getChildrenMinWidth(), EPS);
        Assertions.assertEquals(85F, minMaxWidth.getMinWidth(), EPS);
        Assertions.assertEquals(100F, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assertions.assertEquals(110F, minMaxWidth.getMaxWidth(), EPS);

        flexRenderer.deleteOwnProperty(Property.MAX_WIDTH);
        minMaxWidth = flexRenderer.getMinMaxWidth();
        Assertions.assertEquals(90F, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assertions.assertEquals(100F, minMaxWidth.getMaxWidth(), EPS);
    }
}

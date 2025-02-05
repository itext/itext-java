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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class TextAreaRendererTest extends ExtendedITextTest {
    
    private static final double EPS = 0.0001;
    
    @Test
    public void colsPropertyIsSetToNullTest() {
        TextAreaRenderer areaRenderer = new TextAreaRenderer(new TextArea(""));
        areaRenderer.setProperty(FormProperty.FORM_FIELD_COLS, null);
        
        Assertions.assertEquals(20, areaRenderer.getCols());
    }

    @Test
    public void colsPropertyIsSetToZeroTest() {
        TextAreaRenderer areaRenderer = new TextAreaRenderer(new TextArea(""));
        areaRenderer.setProperty(FormProperty.FORM_FIELD_COLS, 0);

        Assertions.assertEquals(20, areaRenderer.getCols());
    }

    @Test
    public void rowsPropertyIsSetToNullTest() {
        TextAreaRenderer areaRenderer = new TextAreaRenderer(new TextArea(""));
        areaRenderer.setProperty(FormProperty.FORM_FIELD_ROWS, null);

        Assertions.assertEquals(2, areaRenderer.getRows());
    }

    @Test
    public void rowsPropertyIsSetToZeroTest() {
        TextAreaRenderer areaRenderer = new TextAreaRenderer(new TextArea(""));
        areaRenderer.setProperty(FormProperty.FORM_FIELD_ROWS, 0);

        Assertions.assertEquals(2, areaRenderer.getRows());
    }
    
    @Test
    public void getRendererTest() {
        TextAreaRenderer areaRenderer = new TextAreaRenderer(new TextArea(""));
        
        IRenderer nextRenderer = areaRenderer.getNextRenderer();
        Assertions.assertTrue(nextRenderer instanceof TextAreaRenderer);
    }
    
    @Test
    public void setMinMaxWidthBasedOnFixedWidthWithAbsoluteWidthTest() {
        CustomTextAreaRenderer areaRenderer = new CustomTextAreaRenderer(new TextArea(""));
        areaRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(10));
        MinMaxWidth minMaxWidth = new MinMaxWidth();
        
        Assertions.assertTrue(areaRenderer.callSetMinMaxWidthBasedOnFixedWidth(minMaxWidth));
        Assertions.assertEquals(122, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assertions.assertEquals(122, minMaxWidth.getChildrenMinWidth(), EPS);
    }

    @Test
    public void setMinMaxWidthBasedOnFixedWidthWithoutAbsoluteWidthTest() {
        CustomTextAreaRenderer areaRenderer = new CustomTextAreaRenderer(new TextArea(""));
        areaRenderer.setProperty(Property.WIDTH, UnitValue.createPercentValue(10));
        areaRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(10));
        MinMaxWidth minMaxWidth = new MinMaxWidth();

        Assertions.assertTrue(areaRenderer.callSetMinMaxWidthBasedOnFixedWidth(minMaxWidth));
        Assertions.assertEquals(122, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assertions.assertEquals(122, minMaxWidth.getChildrenMinWidth(), EPS);
    }

    @Test
    public void setMinMaxWidthBasedOnFixedWidthWithoutAbsoluteWidthOnElementTest() {
        CustomTextAreaRenderer areaRenderer = new CustomTextAreaRenderer(new TextArea(""));
        areaRenderer.getModelElement().setProperty(Property.WIDTH, UnitValue.createPercentValue(10));
        areaRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(10));
        MinMaxWidth minMaxWidth = new MinMaxWidth();

        Assertions.assertTrue(areaRenderer.callSetMinMaxWidthBasedOnFixedWidth(minMaxWidth));
        Assertions.assertEquals(122, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assertions.assertEquals(122, minMaxWidth.getChildrenMinWidth(), EPS);
    }

    @Test
    public void getLastYLineRecursivelyNoOccupiedAreaTest() {
        CustomTextAreaRenderer areaRenderer = new CustomTextAreaRenderer(new TextArea(""));
        Float lastY = areaRenderer.callGetLastYLineRecursively();

        Assertions.assertNull(lastY);
    }

    @Test
    public void getLastYLineRecursivelyEmptyOccupiedAreaTest() {
        CustomTextAreaRenderer areaRenderer = new CustomTextAreaRenderer(new TextArea(""));
        areaRenderer.setOccupiedArea(new LayoutArea(1, null));
        Float lastY = areaRenderer.callGetLastYLineRecursively();

        Assertions.assertNull(lastY);
    }

    @Test
    public void getLastYLineRecursivelyWithOccupiedAreaTest() {
        CustomTextAreaRenderer areaRenderer = new CustomTextAreaRenderer(new TextArea(""));
        areaRenderer.setOccupiedArea(new LayoutArea(1, new Rectangle(100, 100, 100, 100)));
        Float lastY = areaRenderer.callGetLastYLineRecursively();

        Assertions.assertEquals(100, lastY, EPS);
    }

    private static class CustomTextAreaRenderer extends TextAreaRenderer {

        public CustomTextAreaRenderer(TextArea modelElement) {
            super(modelElement);
        }

        public void setOccupiedArea(LayoutArea occupiedArea) {
            this.occupiedArea = occupiedArea;
        }
        
        public boolean callSetMinMaxWidthBasedOnFixedWidth(MinMaxWidth minMaxWidth) {
            return this.setMinMaxWidthBasedOnFixedWidth(minMaxWidth);
        }
        
        public Float callGetLastYLineRecursively() {
            return this.getLastYLineRecursively();
        }
    }
}

/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class InputFieldRendererTest extends ExtendedITextTest {

    private static final double EPS = 0.0001;
    
    @Test
    public void nullPasswordTest() {
        InputFieldRenderer inputFieldRenderer = new InputFieldRenderer(new InputField(""));
        inputFieldRenderer.setProperty(FormProperty.FORM_FIELD_PASSWORD_FLAG, null);

        Assert.assertFalse(inputFieldRenderer.isPassword());
    }
    
    @Test
    public void nullSizeTest() {
        InputFieldRenderer inputFieldRenderer = new InputFieldRenderer(new InputField(""));
        inputFieldRenderer.setProperty(FormProperty.FORM_FIELD_SIZE, null);
        
        Assert.assertEquals(20, inputFieldRenderer.getSize());
    }

    @Test
    public void setMinMaxWidthBasedOnFixedWidthWithAbsoluteWidthTest() {
        CustomInputFieldRenderer areaRenderer = new CustomInputFieldRenderer(new InputField(""));
        areaRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(10));
        MinMaxWidth minMaxWidth = new MinMaxWidth();

        Assert.assertTrue(areaRenderer.callSetMinMaxWidthBasedOnFixedWidth(minMaxWidth));
        Assert.assertEquals(122, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assert.assertEquals(122, minMaxWidth.getChildrenMinWidth(), EPS);
    }

    @Test
    public void setMinMaxWidthBasedOnFixedWidthWithoutAbsoluteWidthTest() {
        CustomInputFieldRenderer areaRenderer = new CustomInputFieldRenderer(new InputField(""));
        areaRenderer.setProperty(Property.WIDTH, UnitValue.createPercentValue(10));
        areaRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(10));
        MinMaxWidth minMaxWidth = new MinMaxWidth();

        Assert.assertTrue(areaRenderer.callSetMinMaxWidthBasedOnFixedWidth(minMaxWidth));
        Assert.assertEquals(122, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assert.assertEquals(0, minMaxWidth.getChildrenMinWidth(), EPS);
    }

    @Test
    public void setMinMaxWidthBasedOnFixedWidthWithoutAbsoluteWidthOnElementTest() {
        CustomInputFieldRenderer areaRenderer = new CustomInputFieldRenderer(new InputField(""));
        areaRenderer.getModelElement().setProperty(Property.WIDTH, UnitValue.createPercentValue(10));
        areaRenderer.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(10));
        MinMaxWidth minMaxWidth = new MinMaxWidth();

        Assert.assertTrue(areaRenderer.callSetMinMaxWidthBasedOnFixedWidth(minMaxWidth));
        Assert.assertEquals(122, minMaxWidth.getChildrenMaxWidth(), EPS);
        Assert.assertEquals(0, minMaxWidth.getChildrenMinWidth(), EPS);
    }
    
    @Test
    public void createParagraphRendererTest() {
        InputFieldRenderer inputFieldRendererWithoutPlaceholder = new InputFieldRenderer(new InputField(""));

        IRenderer paragraphRender = inputFieldRendererWithoutPlaceholder.createParagraphRenderer("");
        Assert.assertTrue(paragraphRender instanceof ParagraphRenderer);

        InputField inputFieldWithEmptyPlaceholder = new InputField("");
        inputFieldWithEmptyPlaceholder.setPlaceholder(new Paragraph() {
            @Override
            public IRenderer createRendererSubTree() {
                return new CustomParagraphRenderer(this);
            }
        });
        InputFieldRenderer inputFieldRendererWithEmptyPlaceholder =
                new InputFieldRenderer(inputFieldWithEmptyPlaceholder);
        paragraphRender = inputFieldRendererWithEmptyPlaceholder.createParagraphRenderer("");
        Assert.assertTrue(paragraphRender instanceof ParagraphRenderer);
        Assert.assertFalse(paragraphRender instanceof CustomParagraphRenderer);

        InputField inputFieldWithPlaceholder = new InputField("");
        inputFieldWithPlaceholder.setPlaceholder(new Paragraph() {
            @Override
            public boolean isEmpty() {
                return false;
            }
            
            @Override
            public IRenderer createRendererSubTree() {
                return new CustomParagraphRenderer(this);
            }
        });
        InputFieldRenderer inputFieldRendererWithPlaceholder =
                new InputFieldRenderer(inputFieldWithPlaceholder);
        paragraphRender = inputFieldRendererWithPlaceholder.createParagraphRenderer("");
        Assert.assertTrue(paragraphRender instanceof CustomParagraphRenderer);
    }
    
    private static class CustomParagraphRenderer extends ParagraphRenderer {
        
        public CustomParagraphRenderer(Paragraph modelElement) {
            super(modelElement);
        }
    }
    
    private static class CustomInputFieldRenderer extends InputFieldRenderer {
        public CustomInputFieldRenderer(InputField modelElement) {
            super(modelElement);
        }

        public boolean callSetMinMaxWidthBasedOnFixedWidth(MinMaxWidth minMaxWidth) {
            return this.setMinMaxWidthBasedOnFixedWidth(minMaxWidth);
        }
    }
}

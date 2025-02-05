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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.SvgIntegrationTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TextLeafSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    @Test
    public void getContentLengthBaseTest() throws Exception {
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        toTest.setAttribute(SvgConstants.Attributes.FONT_SIZE, "10");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 17.085f;
        Assertions.assertEquals(expected, actual, 1e-6f);
    }

    @Test
    public void getContentLengthNoValueTest() throws Exception {
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 27.336f;
        Assertions.assertEquals(expected, actual,1e-6f);
    }

    @Test
    public void getContentLengthNaNTest() throws Exception {
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        toTest.setAttribute(SvgConstants.Attributes.FONT_SIZE, "spice");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 0.0f;
        Assertions.assertEquals(expected, actual, 1e-6f);
    }

    @Test
    public void getContentLengthNegativeTest() throws Exception {
        TextLeafSvgNodeRenderer toTest = new TextLeafSvgNodeRenderer();
        toTest.setAttribute(SvgConstants.Attributes.TEXT_CONTENT, "Hello");
        toTest.setAttribute(SvgConstants.Attributes.FONT_SIZE, "-10");
        PdfFont font = PdfFontFactory.createFont();
        float actual = toTest.getTextContentLength(12, font);
        float expected = 27.336f;
        Assertions.assertEquals(expected, actual,1e-6f);
    }
}

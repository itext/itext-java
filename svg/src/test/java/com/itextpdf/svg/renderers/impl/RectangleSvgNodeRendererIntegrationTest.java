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

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class RectangleSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/RectangleSvgNodeRendererTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/RectangleSvgNodeRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicRectangle");
    }

    @Test
    public void basicRectangleRxRyZeroTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicRectangleRxRyZero");
    }

    @Test
    public void basicCircularRoundedRectangleRyZeroTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicCircularRoundedRectangleRyZero");}

    @Test
    public void basicCircularRoundedRectangleRxZeroTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicCircularRoundedRectangleRxZero");
    }

    @Test
    public void basicCircularRoundedRxRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicCircularRoundedRxRectangle");
    }

    @Test
    public void basicCircularRoundedRyRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicCircularRoundedRyRectangle");
    }

    @Test
    public void basicEllipticalRoundedRectangleXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicEllipticalRoundedRectangleX");
    }

    @Test
    public void basicEllipticalRoundedRectangleYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicEllipticalRoundedRectangleY");
    }

    @Test
    public void basicEllipticalWidthCappedRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicEllipticalWidthCappedRoundedRectangle");
    }

    @Test
    public void basicEllipticalHeightCappedRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicEllipticalHeightCappedRoundedRectangle");
    }

    @Test
    public void basicEllipticalNegativeWidthRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicEllipticalNegativeWidthRoundedRectangle");
    }

    @Test
    public void basicEllipticalNegativeHeightRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicEllipticalNegativeHeightRoundedRectangle");}

    @Test
    public void complexRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "complexRectangle");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 1))
    public void noFillRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER,DESTINATION_FOLDER,"noFillRectangle");
    }

    @Test
    public void rectangleNoWidthNoHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleNoWidthNoHeight");
    }
}

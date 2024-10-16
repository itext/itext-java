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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class RectangleSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/RectangleSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/RectangleSvgNodeRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void basicRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicRectangle");
    }

    @Test
    public void basicRectangleRxRyZeroTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicRectangleRxRyZero");
    }

    @Test
    public void basicCircularRoundedRectangleRyZeroTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicCircularRoundedRectangleRyZero");}

    @Test
    public void basicCircularRoundedRectangleRxZeroTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicCircularRoundedRectangleRxZero");
    }

    @Test
    public void basicCircularRoundedRxRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicCircularRoundedRxRectangle");
    }

    @Test
    public void basicCircularRoundedRyRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicCircularRoundedRyRectangle");
    }

    @Test
    public void basicEllipticalRoundedRectangleXTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicEllipticalRoundedRectangleX");
    }

    @Test
    public void basicEllipticalRoundedRectangleYTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicEllipticalRoundedRectangleY");
    }

    @Test
    public void basicEllipticalWidthCappedRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicEllipticalWidthCappedRoundedRectangle");
    }

    @Test
    public void basicEllipticalHeightCappedRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicEllipticalHeightCappedRoundedRectangle");
    }

    @Test
    //TODO change cmp-file after DEVSIX-3121 fixed
    public void basicEllipticalNegativeWidthRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicEllipticalNegativeWidthRoundedRectangle");
    }

    @Test
    //TODO change cmp-file after DEVSIX-3121 fixed
    public void basicEllipticalNegativeHeightRoundedRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "basicEllipticalNegativeHeightRoundedRectangle");}

    @Test
    public void complexRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "complexRectangle");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 1))
    public void noFillRectangleTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder,destinationFolder,"noFillRectangle");
    }
}

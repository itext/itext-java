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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SymbolTest extends SvgIntegrationTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/SymbolTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/SymbolTest/";

    private ISvgConverterProperties properties;

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void before() {
        properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
    }

    @Test
    public void simpleSymbolTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleSymbolTest");
    }

    @Test
    public void useTagFirstSymbolAfterTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useTagFirstSymbolAfterTest");
    }

    @Test
    public void heightPxAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "heightPxAttrTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED),
    })
    // TODO DEVSIX-4388 The handling of width and height attributes with percentages is not currently supported
    public void heightPercentsAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "heightPercentsAttrTest");
    }

    @Test
    public void widthPxAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthPxAttrTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED),
    })
    // TODO DEVSIX-4388 The handling of width and height attributes with percentages is not currently supported
    public void widthPercentsAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthPercentsAttrTest");
    }

    @Test
    public void widthHeightAttrPxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrPxTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 2),
    })
    // TODO DEVSIX-4388 The handling of width and height attributes with percentages is not currently supported
    public void widthHeightAttrPercentsPxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrPercentsPxTest");
    }

    @Test
    // TODO DEVSIX-3537 Processing of preserveAspectRatio attribute with offsets x and y is not currently supported
    public void preserveAspectRatioViewBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioViewBoxTest");
    }

    @Test
    public void xYInUseWithDefsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "xYInUseWithDefsTest");
    }

    @Test
    // TODO DEVSIX-4563 Processing of attributes from an external CSS is not currently supported
    public void classAttributeTestWithCssTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "classAttrTest");
    }

    @Test
    public void styleAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "styleAttrTest");
    }

    @Test
    public void styleAttrInUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "styleAttrInUseTest");
    }

    @Test
    public void bothStyleAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "bothStyleAttrTest");
    }

    @Test
    // TODO DEVSIX-2258 Processing of stroke attribute is not currently correct supported
    public void opacityAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacityAttrTest");
    }

    @Test
    // TODO DEVSIX-2254 Processing of visibility attribute is not currently supported
    public void visibilityAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "visibilityAttrTest");
    }

    @Test
    // TODO DEVSIX-4564 Processing of display attribute is not currently supported
    public void displayNoneAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "displayNoneAttrTest");
    }

    @Test
    // TODO DEVSIX-4564 Processing of display attribute is not currently supported
    public void displayAttributeWithNoUseTagTest() throws IOException, InterruptedException {
        //Expects that nothing will be displayed on the page as it's done in Chrome browser
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "displayAttrWithNoUseTagTest");
    }

    @Test
    public void simpleImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleImageTest", properties);
    }

    @Test
    public void linearGradientSymbolTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradientSymbolTest", properties);
    }

    @Test
    // TODO DEVSIX-4566 Processing of width&height attributes in use tag are not currently supported
    public void useHeightWidthAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useHeightWidthAllUnitsTest", properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED),
    })
    // TODO DEVSIX-4388 The handling of width and height attributes with percentages is not currently supported
    // TODO DEVSIX-4566 Processing of width&height attributes in use tag are not currently supported
    public void useSymbolHeightWidthAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolHeightWidthAllUnitsTest",
                properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 3),
    })
    // TODO DEVSIX-4388 The handling of x and y attributes with percentages is not currently supported
    public void useSymbolXYContrudictionAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolXYContrudictionAllUnitsTest",
                properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 6)
    })
    // TODO DEVSIX-2654 Percent values are not correctly processed
    public void useSymbolCoordinatesContrudictionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolCoordinatesContrudiction",
                properties);
    }

    @Test
    // TODO DEVSIX-4566 Processing of width&height attributes in use tag are not currently supported
    public void widthHeightAttrInteractionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrInteraction",
                properties);
    }
}

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

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class XLinkDataTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/XLinkDataTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/XLinkDataTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void correctImageWithDataTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"correctImageWithData");
    }
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_DATA_URI))
    public void incorrectImageWithDataTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "incorrectImageWithData");
    }

    @Test
    public void linearGradXlinkTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHref");
    }

    @Test
    public void linearGradXlink3StopsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHref3Stops");
    }

    @Test
    public void linearGradXlinkGradientTransformTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefGradientTransform");
    }

    @Test
    public void linearGradXlinkNegativeOffsetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefNegativeOffset");
    }

    @Test
    public void linearGradXlinkNegativeOpacityTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefNegativeOpacity");
    }

    @Test
    public void linearGradXlinkOpacityTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefOpacity");
    }

    @Test
    public void linearGradXlinkOpacity2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefOpacity2");
    }

    @Test
    public void linearGradXlinkSpreadMethodTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefSpreadMethod1");
    }

    @Test
    public void linearGradXlinkSpreadMethod2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefSpreadMethod2");
    }

    @Test
    public void linearGradXlinkSpreadMethod3Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefSpreadMethod3");
    }

    @Test
    public void linearGradXlinkHrefXYvalsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefXYvals1");
    }

    @Test
    public void linearGradXlinkHrefXYvals2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefXYvals2");
    }

    @Test
    public void linearGradXlinkHrefXYvals3Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHrefXYvals3");
    }

    @Test
    public void linearGradXlinkHreOffsetSwapTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradXlinkHreOffsetSwap");
    }

    @Test
    public void patternXlinkTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternXlinkHref");
    }

    @Test
    public void patternXlinkHrefPatternContentUnits1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternXlinkHrefPatternContentUnits1");
    }

    @Test
    public void patternXlinkHrefPatternContentUnits2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternXlinkHrefPatternContentUnits2");
    }

    @Test
    public void patternXlinkHrefPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternXlinkHrefPatternUnits");
    }

    @Test
    public void patternXlinkHrefPreserveAR1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternXlinkHrefPreserveAR1");
    }

    @Test
    public void patternXlinkHrefPreserveAR2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternXlinkHrefPreserveAR2");
    }

    //TODO DEVSIX-2255: Update cmp file after supporting
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG),
    })
    public void textPathXlinkTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textPathXrefHref");
    }
}

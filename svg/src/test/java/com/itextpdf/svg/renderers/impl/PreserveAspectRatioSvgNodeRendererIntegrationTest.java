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
public class PreserveAspectRatioSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/PreserveAspectRatioSvgNodeRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/PreserveAspectRatioSvgNodeRendererIntegrationTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void aspectRatioPreservationMidXMidYMeetMinimalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "MidXMidYMeetMinimalTest");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectDefaultAll() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"PreserveAspectDefaultAll");
    }

    @Test
    public void viewBoxWithoutSetPreserveAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatio");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, count = 19)
    })
    public void differentAspectRatiosTest() throws IOException, InterruptedException {
        //TODO: update cmp_ when DEVSIX-2250 fixed
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "differentAspectRatios");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectDefaultAllGroup() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectDefaultAllGroup");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectMin() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectMin");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectAll() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectAll");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectMetricDimensionsMin() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectMetricDimensionsMin");
    }

    @Test
    public void viewBoxScalingTestDoNotPreserveAspectMetricDimensionsAll() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "DoNotPreserveAspectMetricDimensionsAll");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMinYMinMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMinYMinMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMinYMidMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMinYMidMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMinYMaxMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMinYMaxMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMidYMinMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMidYMinMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMidYMaxMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMidYMaxMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMaxYMinMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMaxYMinMeetScaling");
    }

    @Test
    public void viewBoxScalingTestPreserveAspectRatioXMaxYMidMeetScaling() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "PreserveAspectRatioXMaxYMidMeetScaling");
    }

    @Test
    public void viewBoxTranslationTestInnerZeroCoordinatesViewBox() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "InnerZeroCoordinatesViewBox");
    }

    @Test
    public void viewBoxTranslationTestOuterZeroCoordinatesViewBox() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "OuterZeroCoordinatesViewBox");
    }

    @Test
    public void viewBoxTranslationTestMultipleViewBoxes() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "MultipleViewBoxes");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed 
    public void svgTranslationYMinMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMinMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMidMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMaxMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationXMinMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMinMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationXMidMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMidMeet");
    }

    @Test
    //TODO (DEVSIX-3537) change cmp files after the ticket will be fixed
    public void svgTranslationXMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMaxMeet");
    }
}

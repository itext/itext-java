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

import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PreserveAspectRatioSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/PreserveAspectRatioSvgNodeRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/PreserveAspectRatioSvgNodeRendererIntegrationTest/";

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
    public void differentAspectRatiosTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "differentAspectRatios");
    }

    @Test
    public void imagePreserveAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imagePreserveAspectRatio");
    }

    @Test
    public void imageNegativeWidthHeightTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "imageNegativeWidthHeight");
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
    public void svgTranslationYMinMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMinMeet");
    }

    @Test
    public void svgTranslationYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMidMeet");
    }

    @Test
    public void svgTranslationYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationYMaxMeet");
    }

    @Test
    public void svgTranslationXMinMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMinMeet");
    }

    @Test
    public void svgTranslationXMidMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMidMeet");
    }

    @Test
    public void svgTranslationXMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgTranslationXMaxMeet");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.VIEWBOX_WIDTH_OR_HEIGHT_IS_ZERO,
            logLevel = LogLevelConstants.INFO))
    public void svgZeroWidthRatioTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgZeroWidthRatio");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SvgLogMessageConstant.VIEWBOX_WIDTH_OR_HEIGHT_IS_ZERO,
            logLevel = LogLevelConstants.INFO))
    public void svgZeroHeightRatioTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "svgZeroHeightRatio");
    }
}

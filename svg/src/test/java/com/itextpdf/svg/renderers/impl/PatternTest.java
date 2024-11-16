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

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PatternTest extends SvgIntegrationTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/PatternTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/PatternTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void widthHeightXYInCmUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInCmUnitDiffPatternUnits");
    }

    @Test
    public void widthHeightXYInInchUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInInchUnitDiffPatternUnits");
    }

    @Test
    public void widthHeightXYInEmUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInEmUnitDiffPatternUnits");
    }

    @Test
    public void widthHeightXYInExUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInExUnitDiffPatternUnits");
    }

    @Test
    public void widthHeightXYInPercentsDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInPercentsDiffPatternUnits");
    }

    @Test
    public void widthHeightXYInPxUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInPxUnitDiffPatternUnits");
    }

    @Test
    public void widthHeightXYInMmUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInMmUnitDiffPatternUnits");
    }

    @Test
    public void widthHeightXYInPtUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInPtUnitDiffPatternUnits");
    }

    @Test
    public void widthHeightXYNoMeasureUnitTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYNoMeasureUnit");
    }

    @Test
    public void hrefAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hrefAttribute");
    }

    @Test
    public void patternUnitsObjectBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternUnitsObjectBoundingBox");
    }

    @Test
    public void patternUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternUnitsUserSpaceOnUse");
    }

    @Test
    public void preserveAspectRatioObjBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioObjBoundingBox", PageSize.A8);
    }

    @Test
    public void preserveAspectRatioUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioUserSpaceOnUse", PageSize.A8);
    }

    @Test
    public void objectBoundingBoxXMinYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMinYMidMeet", PageSize.A8);
    }

    @Test
    public void objectBoundingBoxXMidYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMidYMidMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMaxYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMaxYMidMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMidYMinMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMidYMinMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMidYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMidYMaxMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMidYMidMeetVerticalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMidYMidMeetVertical", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMinYMinMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMinYMinMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMinYMinMeetVerticalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMinYMinMeetVertical", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMinYMaxMeetVerticalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMinYMaxMeetVertical", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMinYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMinYMaxMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMaxYMinMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMaxYMinMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMaxYMinMeetVerticalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMaxYMinMeetVertical", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMaxYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMaxYMaxMeet", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxXMaxYMaxMeetVerticalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxXMaxYMaxMeetVertical", PageSize.A10);
    }

    @Test
    public void objectBoundingBoxNoneTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxNone", PageSize.A10);
    }

    @Test
    public void patternContentUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternContentUnitsUserSpaceOnUse");
    }

    @Test
    public void patternContentUnitsObjBoundBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternContentUnitsObjBoundBox");
    }

    @Test
    public void patternContentUnitsObjBoundBoxAbsoluteCoordTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternContentUnitsObjBoundBoxAbsoluteCoord");
    }

    @Test
    public void viewBoxAndAbsoluteCoordinatesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewBoxAndAbsoluteCoordinates");
    }

    @Test
    public void patternTransformSimpleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformSimple");
    }

    @Test
    public void patternTransformUnitsObjectBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformUnitsObjectBoundingBox");
    }

    @Test
    public void patternTransformUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformUnitsUserSpaceOnUse");
    }

    @Test
    public void patternTransformObjBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformObjBoundingBox");
    }

    @Test
    public void patternTransformUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformUserSpaceOnUse");
    }

    @Test
    public void patternTransformMixed1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformMixed1");
    }

    @Test
    public void patternTransformMixed2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformMixed2");
    }

    @Test
    public void patternTransformViewBoxUsrSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformViewBoxUsrSpaceOnUse");
    }

    @Test
    public void patternTransformViewBoxObjBoundBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformViewBoxObjBoundBox");
    }

    @Test
    public void patternTransformElementTransformTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformElementTransform", PageSize.A8);
    }

    @Test
    public void patternTransformTranslateTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformTranslate");
    }

    @Test
    public void preserveAspectRatioXMaxYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMaxYMidMeet");
    }

    @Test
    public void preserveAspectRatioXMaxYMidSliceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMaxYMidSlice");
    }

    @Test
    public void preserveAspectRatioXMidYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMidYMaxMeet");
    }

    @Test
    public void preserveAspectRatioXMidYMaxSliceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMidYMaxSlice");
    }

    @Test
    public void relativeUnitsResolveFromDefsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativeUnitsResolveFromDefs");
    }

    @Test
    public void relativeUnitsResolveFromPatternTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativeUnitsResolveFromPattern");
    }

    @Test
    public void linearGradientInsidePatternTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradientInsidePattern");
    }

    @Test
    public void nestedPatternsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedPatterns");
    }

    @Test
    public void severalComplexElementsInsidePatternTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "severalComplexElementsInsidePattern");
    }

    @Test
    public void nestedPatternsWithComplexElementsInsideTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedPatternsWithComplexElementsInside");
    }

    @Test
    // Behavior differs from browser. In our implementation we use default color for element with cycled pattern.
    public void patternUseItselfTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternUseItself");
    }

    @Test
    // Behavior differs from browser. In our implementation we use default color for element with cycled pattern.
    public void nestedPatternsLinkedToEachOtherTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedPatternsLinkedToEachOther");
    }

    @Test
    public void simplePatternTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simplePatternTest");
    }

    @Test
    public void simplePatternInheritStylesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simplePatternInheritStylesTest");
    }

    @Test
    public void simplePatternNestedTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simplePatternNestedTest");
    }

    @Test
    public void simplePatternStrokeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simplePatternStrokeTest");
    }

    @Test
    public void simplePatternNestedFillInheritanceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simplePatternNestedFillInheritanceTest");
    }

    @Test
    public void patternContentUnitsObjectBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternContentUnitsObjectBoundingBox");
    }

    @Test
    // Behavior differs from browser. We use default color instead cycled pattern.
    public void cycledPatternsUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cycledPatternsUserSpaceOnUse");
    }

    @Test
    public void objBoundingBoxWithMarginsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objBoundingBoxWithMargins");
    }

    @Test
    public void objBoundingBoxUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objBoundingBoxUserSpaceOnUse");
    }

    @Test
    public void userSpaceOnUseObjBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "userSpaceOnUseObjBoundingBox");
    }

    @Test
    public void patternDefaultWidthTest() throws IOException, InterruptedException {
        // we print the default color that is black
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternDefaultWidth");
    }

    @Test
    public void patternDefaultHeightTest() throws IOException, InterruptedException {
        // we print the default color that is black
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternDefaultHeight");
    }

    @Test
    public void viewBoxPatternXYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewBoxPatternXY");
    }

    @Test
    public void viewBoxClippedTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewBoxClipped");
    }

    @Test
    public void coordSystemTransformUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "coordSystemTransformUserSpaceOnUse");
    }

    @Test
    public void coordSystemTransformObjBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "coordSystemTransformObjBoundingBox");
    }

    @Test
    public void coordSystemTransformMixed1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "coordSystemTransformMixed1");
    }

    @Test
    public void coordSystemTransformMixed2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "coordSystemTransformMixed2");
    }

    @Test
    public void coordSystemTransform() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "coordSystemTransform");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.VIEWBOX_VALUE_MUST_BE_FOUR_NUMBERS, count = 1)})
    public void incorrectViewBoxValuesNumberTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "incorrectViewBoxValuesNumber");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.VIEWBOX_WIDTH_AND_HEIGHT_CANNOT_BE_NEGATIVE)})
    public void incorrectViewBoxNegativeWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "incorrectViewBoxNegativeWidth");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.VIEWBOX_WIDTH_AND_HEIGHT_CANNOT_BE_NEGATIVE)})
    public void incorrectViewBoxNegativeHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "incorrectViewBoxNegativeHeight");
    }

    @Test
    public void viewBoxZeroWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewBoxZeroWidth");
    }

    @Test
    public void viewBoxZeroHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewBoxZeroHeight");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.PATTERN_WIDTH_OR_HEIGHT_IS_NEGATIVE)})
    public void patternNegativeWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternNegativeWidth");
    }
}

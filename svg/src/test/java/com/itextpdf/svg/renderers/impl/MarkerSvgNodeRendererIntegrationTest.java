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
public class MarkerSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/MarkerSvgNodeRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/MarkerSvgNodeRendererIntegrationTest/";

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
    public void markerPathAutoOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathAutoOrient");
    }

    @Test
    public void markerPathAngleOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathAngleOrient");
    }

    @Test
    public void markerPathRefXRefYNoAspectRatioPreservationTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathRefXRefYNoAspectRatioPreservation");
    }

    @Test
    public void markerPathRefXAndRefYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathAutoOrient");
    }

    @Test
    public void markerPathViewboxRightOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxRightOrient");
    }

    @Test
    // TODO (DEVSIX-3621) fix cmp after fixing
    public void markerPathViewboxRightOrientNoAspectRatioPreservationTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,
                "markerPathViewboxRightOrientNoAspectRatioPreservation");
    }

    @Test
    public void markerPathViewboxLeftOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxLeftOrient");
    }

    @Test
    public void markerPathViewboxUpOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxUpOrient");
    }

    @Test
    public void markerPathViewboxDownOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxDownOrient");
    }


    @Test
    public void markerPathViewboxAngledOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxAngledOrient");
    }

    @Test
    // TODO (DEVSIX-3621) fix cmp after fixing
    public void markerPathPreserveAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathPreserveAspectRatio");
    }

    // Markers in different elements
    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "marker");
    }

    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerInLineElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInLineElement");
    }

    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerInPolylineElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInPolylineElement");
    }

    @Test
    // TODO: update when DEVSIX-3397, DEVSIX-2719 will be closed
    public void markerInPolygonElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInPolygonElement");
    }

    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerInPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInPath");
    }

    @Test
    public void markerUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerUnits");
    }

    @Test
    public void markerRefXYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerRefXY");
    }

    // orient attribute tests
    @Test
    public void markerOrientTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOrient");
    }

    @Test
    public void orientAutoLineInDifferentPositionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "orientAutoLineInDifferentPosition");
    }

    @Test
    public void orientAutoPolylineInDifferentPositionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "orientAutoPolylineInDifferentPosition");
    }

    @Test
    public void orientAutoPolygonInDifferentPositionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "orientAutoPolygonInDifferentPosition");
    }

    @Test
    // TODO DEVSIX-3432 relative values doesn't support correctly for stroke-width attribute
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 3))
    public void markerUnitsStrokeWidthWhenParentStrokeWidthIsFontRelativeValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "parentStrokeWidthIsFontRelativeValues");
    }

    @Test
    public void markerUnitsStrokeWidthWhenParentStrokeWidthIsMetricValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "parentStrokeWidthIsMetricValues");
    }

    @Test
    // TODO DEVSIX-3432 relative values doesn't support correctly for stroke-width attribute
    @LogMessages(messages = @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 3))
    public void markerUnitsStrokeWidthWhenParentStrokeWidthIsPercentageValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "parentStrokeWidthIsPercentageValues");
    }

    @Test
    public void markerDefaultValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerDefaultValues");
    }

    // Style inheritance
    @Test
    public void markerInheritFillAttributeTest0() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttribute0");
    }

    @Test
    public void markerInheritFillAttributeTest1() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttribute1");
    }

    @Test
    public void markerInheritFillAttributeTest2() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttribute2");
    }

    @Test
    public void markerInheritFillAttributeNestedMarkerTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttributeNestedMarker");
    }

    @Test
    public void fontRelativeValueInRefXTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "fontRelativeValueInRefX");
    }

    @Test
    public void fontRelativeValueInRefXDefaultTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "fontRelativeValueInRefXDefault");
    }

    @Test
    public void markerAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerAspectRatio");
    }

    // Overflow attribute
    @Test
    public void markerOverflowVisibleIncreaseViewBoxScaleRootElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleIncreaseViewBoxScaleRootElement");
    }

    @Test
    public void markerOverflowVisibleIncreaseViewBoxScaleSvgElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleIncreaseViewBoxScaleSvgElement");
    }

    @Test
    public void markerOverflowVisibleTransformScaleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleTransformScale");
    }

    @Test
    public void markerOverflowVisibleTransformTranslateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleTransformTranslate");
    }

    @Test
    public void markerOverflowVisibleNestedSvgViewBoxesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleNestedSvgViewBoxes");
    }

    @Test
    public void markerOverflowVisibleNestedSvgViewBoxes2Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleNestedSvgViewBoxes2");
    }

    @Test
    public void markerOverflowVisibleTransformRotateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleTransformRotate");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "markerWidth has zero value. Marker will not be rendered."),
            @LogMessage(messageTemplate = "markerHeight has zero value. Marker will not be rendered."),
            @LogMessage(messageTemplate = "markerWidth has negative value. Marker will not be rendered."),
            @LogMessage(messageTemplate = "markerHeight has negative value. Marker will not be rendered.")
    })
    public void markerEspecialMarkerWidthHeightValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerEspecialMarkerWidthHeightValues");
    }

    @Test
    // TODO (DEVSIX-3621) change cmp after fixing
    public void deformationWhenRotationAndPreserveAspectRationNoneTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "deformationWhenRotationAndPreserveAspectRationNone");
    }

    @Test
    // TODO DEVSIX-4130 fix after ticket will be completed
    // Compare with Chrome browser
    public void markerParentElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerParentElement");
    }
}

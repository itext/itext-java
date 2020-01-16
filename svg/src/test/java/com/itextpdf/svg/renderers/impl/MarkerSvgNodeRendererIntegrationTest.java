/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

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
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathAutoOrientTest");
    }

    @Test
    public void markerPathAngleOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathAngleOrientTest");
    }

    @Test
    public void markerPathRefXRefYNoAspectRatioPreservationTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathRefXRefYNoAspectRatioPreservationTest");
    }

    @Test
    public void markerPathRefXAndRefYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathAutoOrientTest");
    }

    @Test
    public void markerPathViewboxRightOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxRightOrientTest");
    }

    @Test
    // TODO (DEVSIX-3621) fix cmp after fixing
    public void markerPathViewboxRightOrientNoAspectRatioPreservationTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,
                "markerPathViewboxRightOrientNoAspectRatioPreservationTest");
    }

    @Test
    public void markerPathViewboxLeftOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxLeftOrientTest");
    }

    @Test
    public void markerPathViewboxUpOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxUpOrientTest");
    }

    @Test
    public void markerPathViewboxDownOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxDownOrientTest");
    }


    @Test
    public void markerPathViewboxAngledOrientTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathViewboxAngledOrientTest");
    }

    @Test
    // TODO (DEVSIX-3621) fix cmp after fixing
    public void markerPathPreserveAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "markerPathPreserveAspectRatioTest");
    }

    // Markers in different elements
    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerTest");
    }

    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerInLineElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInLineElementTest");
    }

    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerInPolylineElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInPolylineElementTest");
    }

    @Test
    // TODO: update when DEVSIX-3397, DEVSIX-2719 will be closed
    public void markerInPolygonElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInPolygonElementTest");
    }

    @Test
    // TODO: update when DEVSIX-3397 will be closed
    public void markerInPathTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInPathTest");
    }

    @Test
    public void markerUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerUnitsTest");
    }

    @Test
    public void markerRefXYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerRefXYTest");
    }

    // orient attribute tests
    @Test
    public void markerOrientTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOrientTest");
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
    // TODO: update when DEVSIX-3432 will be fixed
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 3))
    public void testMarkerUnitsStrokeWidthWhenParentStrokeWidthIsFontRelativeValues() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "testMarkerUnitsStrokeWidthWhenParentStrokeWidthIsFontRelativeValues");
    }

    @Test
    public void testMarkerUnitsStrokeWidthWhenParentStrokeWidthIsMetricValues() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "testMarkerUnitsStrokeWidthWhenParentStrokeWidthIsMetricValues");
    }

    @Test
    // TODO: update when DEVSIX-3432 will be fixed
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 3))
    public void testMarkerUnitsStrokeWidthWhenParentStrokeWidthIsPercentageValues() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "testMarkerUnitsStrokeWidthWhenParentStrokeWidthIsPercentageValues");
    }

    @Test
    public void markerDefaultValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerDefaultValuesTest");
    }

    // Style inheritance
    @Test
    public void markerInheritFillAttributeTest0() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttributeTest0");
    }

    @Test
    public void markerInheritFillAttributeTest1() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttributeTest1");
    }

    @Test
    public void markerInheritFillAttributeTest2() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttributeTest2");
    }

    @Test
    public void markerInheritFillAttributeNestedMarker() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerInheritFillAttributeNestedMarker");
    }

    @Test
    public void fontRelativeValueInRefX() throws IOException, InterruptedException {
        // This file is rendered differently in different browsers.
        // Look at MarkerSvgNOdeRenderer#parseFontRelativeOrAbsoluteLengthOnMarker to see the processing of font-relative unit .
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "fontRelativeValueInRefX");
    }

    @Test
    public void fontRelativeValueInRefXDefault() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "fontRelativeValueInRefXDefault");
    }

    @Test
    public void markerAspectRatioTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerAspectRatioTest");
    }

    // Overflow attribute
    @Test
    public void markerOverflowVisibleIncreaseViewBoxScaleRootElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleIncreaseViewBoxScaleRootElementTest");
    }

    @Test
    public void markerOverflowVisibleIncreaseViewBoxScaleSvgElementTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleIncreaseViewBoxScaleSvgElementTest");
    }

    @Test
    public void markerOverflowVisibleTransformScaleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleTransformScaleTest");
    }

    @Test
    public void markerOverflowVisibleTransformTranslateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleTransformTranslateTest");
    }

    @Test
    public void markerOverflowVisibleNestedSvgViewBoxesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleNestedSvgViewBoxesTest");
    }

    @Test
    public void markerOverflowVisibleNestedSvgViewBoxes2Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleNestedSvgViewBoxes2Test");
    }

    @Test
    public void markerOverflowVisibleTransformRotateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerOverflowVisibleTransformRotateTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "markerWidth has zero value. Marker will not be rendered."),
            @LogMessage(messageTemplate = "markerHeight has zero value. Marker will not be rendered."),
            @LogMessage(messageTemplate = "markerWidth has negative value. Marker will not be rendered."),
            @LogMessage(messageTemplate = "markerHeight has negative value. Marker will not be rendered.")
    })
    public void markerEspecialMarkerWidthHeightValuesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "markerEspecialMarkerWidthHeightValuesTest");
    }

    @Test
    // TODO (DEVSIX-3621) change cmp after fixing
    public void deformationWhenRotationAndPreserveAspectRationNone() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "deformationWhenRotationAndPreserveAspectRationNone");
    }
}

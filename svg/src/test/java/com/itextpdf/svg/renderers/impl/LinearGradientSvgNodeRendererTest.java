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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class LinearGradientSvgNodeRendererTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/LinearGradientSvgNodeRendererTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/LinearGradientSvgNodeRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void circleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circle");
    }

    @Test
    public void ellipseTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipse");
    }

    @Test
    public void lineTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "line");
    }
    
    @Test
    public void pathLinesBasedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathLinesBased");
    }

    @Test
    public void pathLinesBasedTransformedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathLinesBasedTransformed");
    }
    
    @Test
    public void pathLinesBasedWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathLinesBasedWithMove");
    }
    
    @Test
    public void pathLinesBasedWithTwoFiguresTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathLinesBasedWithTwoFigures");
    }

    @Test
    public void cubicBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cubicBezier");
    }

    @Test
    public void cubicBezier2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cubicBezier2");
    }

    @Test
    public void cubicBezier3Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cubicBezier3");
    }

    @Test
    public void cubicBezier4Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cubicBezier4");
    }

    @Test
    // See CurveTo#calculateTValues to see which discriminant is mentioned.
    public void cubicBezierZeroDiscriminantTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cubicBezierZeroDiscriminant");
    }

    @Test
    // See CurveTo#calculateTValues to see which discriminant is mentioned.
    public void cubicBezierNegativeDiscriminantTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cubicBezierNegativeDiscriminant");
    }

    @Test
    public void cubicBezierInsideOtherCubicBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "cubicBezierInsideOtherCubicBezier");
    }

    @Test
    public void smoothCubicBezierWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothCubicBezierWithAbsoluteCoordinates");
    }

    @Test
    public void smoothCubicBezierWithRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothCubicBezierWithRelativeCoordinates");
    }

    @Test
    public void smoothCubicBezierRelativeAndAbsoluteCoordWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothCubicBezierRelativeAndAbsoluteCoordWithMove");
    }

    @Test
    public void smoothCubicBezierRelativeAndAbsoluteCoordNoZOperatorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothCubicBezierRelativeAndAbsoluteCoordNoZOperator");
    }

    @Test
    public void quadraticBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "quadraticBezier");
    }

    @Test
    public void quadraticBezier2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "quadraticBezier2");
    }

    @Test
    public void quadraticBezier3Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "quadraticBezier3");
    }

    @Test
    public void quadraticBezierInsideOtherQuadraticBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "quadraticBezierInsideOtherQuadraticBezier");
    }

    @Test
    public void smoothQuadraticBezierWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothQuadraticBezierWithAbsoluteCoordinates");
    }

    @Test
    public void smoothQuadraticBezierWithRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothQuadraticBezierWithRelativeCoordinates");
    }

    @Test
    public void smoothQuadraticBezierAbsoluteAndRelativeCoordWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothQuadraticBezierAbsoluteAndRelativeCoordWithMove");
    }

    @Test
    public void smoothQuadraticBezierRelativeAndAbsoluteCoordNoZOperatorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "smoothQuadraticBezierRelativeAndAbsoluteCoordNoZOperator");
    }

    @Test
    public void ellipticalArcsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcs");
    }

    @Test
    public void ellipticalArcsNegativeRxRyTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsNegativeRxRy");
    }

    @Test
    public void ellipticalArcZeroRxRyTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcZeroRxRy");
    }

    @Test
    public void ellipticalArcsWithPhiTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsWithPhi", PageSize.A3.rotate());
    }

    @Test
    public void ellipticalArcsWithPhi0Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsWithPhi0");
    }

    @Test
    public void ellipticalArcsWithPhi90Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsWithPhi90");
    }

    @Test
    public void ellipticalArcsWithPhi180Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsWithPhi180");
    }
    @Test
    public void ellipticalArcsWithPhi270Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsWithPhi270");
    }

    @Test
    public void ellipticalArcsWithPhiRelativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsWithPhiRelative");
    }

    @Test
    public void ellipticalArcsWithPhiAbsoluteTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsWithPhiAbsolute");
    }

    @Test
    public void ellipticalArcsRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "ellipticalArcsRelativeCoordinates");
    }

    @Test
    public void arcInsideOtherEllipticalArcTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "arcInsideOtherEllipticalArc");
    }

    @Test
    public void polygonTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "polygon");
    }

    @Test
    public void polylineTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "polyline");
    }

    @Test
    public void rectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rect");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.GRADIENT_INVALID_SPREAD_METHOD_LOG, logLevel = LogLevelConstants.WARN)
    })
    public void rectWithInvalidSpreadMethodValueTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectWithInvalidSpreadMethodValue");
    }

    @Test
    public void rectsWithFallBackColorsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectsWithFallBackColors");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetPadTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithSameOffsetPad");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetReflectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithSameOffsetReflect");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithSameOffsetRepeat");
    }

    // TODO: DEVSIX-4136 update cmp_ after fix
    //  (opacity is not implemented. No stops defines no color, i.e. transparent color or black with 100% opacity)
    @Test
    public void rectNoStopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectNoStops");
    }

    @Test
    public void rectSingle0StopTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectSingle0Stop");
    }

    @Test
    public void rectSingle1StopTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectSingle1Stop");
    }

    @Test
    public void rectStopWithoutColorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectStopWithoutColor");
    }

    @Test
    public void rectTransformedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectTransformed");
    }

    @Test
    public void rectWithGradientTransformTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectWithGradientTransform");
    }

    @Test
    public void rectWithMultipleTransformsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectWithMultipleTransforms");
    }

    @Test
    public void textTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text");
    }

    @Test
    public void tspanTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "tspan");
    }
    
    @Test
    public void textNestedTSpansTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textNestedTSpansTest");
    }

    @Test
    public void textRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textRotatedTest");
    }

    @Test
    public void textDxTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textDxTest");
    }

    @Test
    public void chineseTextDxTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "chineseTextDxTest");
    }

    @Test
    public void chineseTextDxVerticalTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "chineseTextDxVerticalTest");
    }

    @Test
    public void textAnchorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textAnchorTest");
    }

    @Test
    public void textDyTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textDyTest");
    }

    @Test
    public void textXYOffset() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textXYOffset");
    }

    @Test
    public void textXOffset() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textXOffset");
    }

    @Test
    public void textXYDxDyOffset() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textXYDxDyOffset");
    }

    @Test
    public void textGradientEmUnits() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textGradientEmUnits");
    }

    @Test
    public void textGradientEmUnitsRelated() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textGradientEmUnitsRelated");
    }

    @Test
    public void textGradientEmUnitsRelatedNotDefs() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textGradientEmUnitsRelatedNotDefs");
    }

    @Test
    public void textGradientEmUnitsRelatedDefault() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textGradientEmUnitsRelatedDefault");
    }

    @Test
    public void textGradientExUnits() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textGradientExUnits");
    }

    @Test
    public void textGradientRemUnits() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textGradientRemUnits");
    }

    @Test
    public void textGradientRemUnitsNestedSvg() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textGradientRemUnitsNestedSvg");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetAndZeroCoordLengthPadTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithSameOffsetAndZeroCoordLengthPad");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetAndZeroCoordLengthReflectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithSameOffsetAndZeroCoordLengthReflect");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetAndZeroCoordLengthRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithDiffOffsetAndZeroCoordLengthRepeat");
    }

    @Test
    public void rectMultipleStopsWithDiffOffsetAndZeroCoordLengthPadTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithDiffOffsetAndZeroCoordLengthPad");
    }

    @Test
    public void rectMultipleStopsWithDiffOffsetAndZeroCoordLengthReflectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithDiffOffsetAndZeroCoordLengthReflect");
    }

    @Test
    public void rectMultipleStopsWithDiffOffsetAndZeroCoordLengthRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectMultipleStopsWithSameOffsetAndZeroCoordLengthRepeat");
    }

    @Test
    public void rectInvalidStopsSequenceTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectInvalidStopsSequence");
    }

    @Test
    public void rectInvalidCoordinatesMetricsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectInvalidCoordinatesMetrics");
    }

    @Test
    public void rectInvalidStopsSequenceWithoutBoundingStopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectInvalidStopsSequenceWithoutBoundingStops");
    }

    @Test
    public void userSpaceOnUseWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "userSpaceOnUseWithAbsoluteCoordinates");
    }

    @Test
    public void userSpaceOnUseDiffAbsoluteUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "userSpaceOnUseDiffAbsoluteUnitsInGradient");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" unit is not implemented yet)
    public void userSpaceOnUseWithChUnitTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "userSpaceOnUseWithChUnit");
    }

    @Test
    public void userSpaceOnUseWithUnitsRelativeToFontTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "userSpaceOnUseWithUnitsRelativeToFont");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("vh" "vw" "vmin" "vmax" units are not implemented yet)
    public void userSpaceOnUseWithUnitsRelativeToViewportTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "userSpaceOnUseWithUnitsRelativeToViewport");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" "vmin"+"vmax"+"vw"+"vh" not implemented yet)
    public void userSpaceOnUseDiffRelativeUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "userSpaceOnUseDiffRelativeUnitsInGradient");
    }

    @Test
    public void objectBoundingBoxWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxWithAbsoluteCoordinates");
    }

    @Test
    public void objectBoundingBoxDifferentAbsoluteUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxDifferentAbsoluteUnitsInGradient");
    }

    @Test
    public void objectBoundingBoxWithUnitsRelativeToFontTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxWithUnitsRelativeToFont");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    public void objectBoundingBoxWithChUnitTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxWithChUnit");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("vmin", "vmax", "vw", "vh" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    public void objectBoundingBoxWithUnitsRelativeToViewportTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxWithUnitsRelativeToViewport");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" "vmin"+"vmax"+"vw"+"vh" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    public void objectBoundingBoxDifferentRelativeUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "objectBoundingBoxDifferentRelativeUnitsInGradient");
    }

    @Test
    public void translateTransformInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "translateTransformInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void matrixTransformInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "matrixTransformInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void severalTransformsInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "severalTransformsInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void hrefBasicReferenceTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hrefBasicReference");
    }

    @Test
    public void transitiveHrefBasicReferenceTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "transitiveHrefBasicReference");
    }

    @Test
    public void linearGradXlinkTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHref");
    }

    @Test
    public void linearGradXlink3StopsTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHref3Stops");
    }

    @Test
    public void linearGradXlinkGradientTransformTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefGradientTransform");
    }

    @Test
    public void linearGradXlinkNegativeOffsetTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefNegativeOffset");
    }

    @Test
    public void linearGradXlinkNegativeOpacityTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefNegativeOpacity");
    }

    @Test
    public void linearGradXlinkOpacityTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefOpacity");
    }

    @Test
    public void linearGradXlinkOpacity2Test() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefOpacity2");
    }

    @Test
    public void linearGradXlinkSpreadMethodTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefSpreadMethod1");
    }

    @Test
    public void linearGradXlinkSpreadMethod2Test() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefSpreadMethod2");
    }

    @Test
    public void linearGradXlinkSpreadMethod3Test() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefSpreadMethod3");
    }

    @Test
    public void linearGradXlinkHrefXYvalsTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefXYvals1");
    }

    @Test
    public void linearGradXlinkHrefXYvals2Test() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefXYvals2");
    }

    @Test
    public void linearGradXlinkHrefXYvals3Test() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefXYvals3");
    }

    @Test
    public void linearGradXlinkHreOffsetSwapTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHreOffsetSwap");
    }

    @Test
    public void linearGradTransitiveHrefOpacityTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradTransitiveHrefOpacity");
    }

    @Test
    public void linearGradTransitiveHrefNegativeOpacityTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradTransitiveHrefNegativeOpacity");
    }

    @Test
    public void linearGradTransitiveHrefNegativeOffsetTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradTransitiveHrefNegativeOffset");
    }

    @Test
    public void linearGradTransitiveHref3stopsTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradTransitiveHref3stops");
    }

    @Test
    public void linearGradHrefTransitiveSpreadMethodTopLayerTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefTransitiveSpreadMethodTopLayer");
    }

    @Test
    public void linearGradHrefTransitiveSpreadMethodBottomLayerTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefTransitiveSpreadMethodBottomLayer");
    }

    @Test
    public void linearGradHrefTransitiveSpreadMethod3Test() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefTransitiveSpreadMethod3");
    }

    @Test
    public void linearGradHrefTransitiveSpreadMethod2Test() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradHrefTransitiveSpreadMethod2");
    }

    @Test
    public void lowerCaseGradientUnitsTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "lowerCaseGradientUnits");
    }
}

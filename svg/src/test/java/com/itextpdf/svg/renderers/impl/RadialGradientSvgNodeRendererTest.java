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
public class RadialGradientSvgNodeRendererTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/RadialGradientSvgNodeRendererTest/";
    public static final String destinationFolder = TestUtil.getOutputPath() + "/svg/renderers/impl/RadialGradientSvgNodeRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void radialGradientBasicTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradientBasic");
    }

    @Test
    public void circleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "circle");
    }

    @Test
    public void ellipseTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipse");
    }

    @Test
    public void rectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rect");
    }

    @Test
    public void polygonTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "polygon");
    }

    @Test
    public void pathTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "path");
    }

    @Test
    public void textTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "text");
    }

    @Test
    public void spreadMethodPadTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "spreadMethodPad");
    }

    @Test
    public void spreadMethodReflectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "spreadMethodReflect");
    }

    @Test
    public void spreadMethodRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "spreadMethodRepeat");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.GRADIENT_INVALID_SPREAD_METHOD_LOG,
                    logLevel = LogLevelConstants.WARN)
    })
    public void invalidSpreadMethodTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "invalidSpreadMethod");
    }

    // TODO: DEVSIX-4136 update cmp_ after fix
    //  (opacity is not implemented. No stops defines no color, i.e. transparent color or black with 100% opacity)
    @Test
    public void noStopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "noStops");
    }

    @Test
    public void singleStop0Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "singleStop0");
    }

    @Test
    public void singleStop1Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "singleStop1");
    }

    @Test
    public void stopWithoutColorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "stopWithoutColor");
    }

    @Test
    public void transformedTargetTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "transformedTarget");
    }

    @Test
    public void rectWithGradientTransformTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectWithGradientTransform");
    }

    @Test
    public void rectWithMultipleTransformsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectWithMultipleTransforms");
    }

    @Test
    public void userSpaceOnUseAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseAbsoluteCoordinates");
    }

    @Test
    public void userSpaceOnUseRelativeUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseRelativeUnits");
    }

    @Test
    public void objectBoundingBoxAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxAbsoluteCoordinates");
    }

    @Test
    public void objectBoundingBoxRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxRelativeCoordinates");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.GRADIENT_INVALID_GRADIENT_UNITS_LOG,
                    logLevel = LogLevelConstants.WARN)
    })
    public void invalidGradientUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "invalidGradientUnits");
    }

    @Test
    public void lowerCaseGradientUnitsAttributeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "lowerCaseGradientUnitsAttribute");
    }

    @Test
    public void hrefBasicReferenceTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "hrefBasicReference");
    }

    @Test
    public void transitiveHrefReferenceTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "transitiveHrefReference");
    }

    @Test
    public void xlinkHrefReferenceTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "xlinkHrefReference");
    }

    @Test
    public void hrefOverrideSpreadMethodTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "hrefOverrideSpreadMethod");
    }

    @Test
    public void hrefOverrideGeometryTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "hrefOverrideGeometry");
    }

    @Test
    public void explicitFocalPointTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "explicitFocalPoint");
    }

    @Test
    public void negativeFrTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "negativeFr");
    }

    @Test
    public void endRadiusEqualZeroTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "endRadiusEqualZero");
    }

    @Test
    public void startRadiusEqualZeroTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "startRadiusEqualZero");
    }

    @Test
    public void endRadiusLessThanZeroTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "endRadiusLessThanZero");
    }

    @Test
    public void startRadiusLessThanZeroTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "startRadiusLessThanZero");
    }

    @Test
    public void equalRadiiTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "equalRadii");
    }

    @Test
    public void similarSizeRadiiTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "similarSizeRadii");
    }

    @Test
    public void secondCircleMuchBiggerTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "secondCircleMuchBigger");
    }

    @Test
    public void planeToRightManyCirclesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "planeToRightManyCircles");
    }

    @Test
    public void planeToRightLargeSecondCircleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "planeToRightLargeSecondCircle");
    }

    @Test
    public void planeToLeftManyCirclesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "planeToLeftManyCircles");
    }

    @Test
    public void planeToLeftLargeSecondCircleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "planeToLeftLargeSecondCircle");
    }

    @Test
    public void halfPlaneToRightManyCirclesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "halfPlaneToRightManyCircles");
    }

    @Test
    public void halfPlaneToRightLargeSecondCircleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "halfPlaneToRightLargeSecondCircle");
    }

    @Test
    public void halfPlaneToLeftManyCirclesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "halfPlaneToLeftManyCircles");
    }

    @Test
    public void halfPlaneToLeftLargeSecondCircleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "halfPlaneToLeftLargeSecondCircle");
    }

    @Test
    public void lowerCaseRadialGradientTagTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "lowerCaseRadialGradientTag");
    }

    @Test
    public void multipleGradientsAndTargetsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "multipleGradientsAndTargets");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetPadTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithSameOffsetPad");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetReflectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithSameOffsetReflect");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithSameOffsetRepeat");
    }

    @Test
    public void rectInvalidStopsSequenceTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectInvalidStopsSequence");
    }

    @Test
    public void rectInvalidStopsSequenceWithoutBoundingStopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectInvalidStopsSequenceWithoutBoundingStops");
    }

    @Test
    public void userSpaceOnUseDiffAbsoluteUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseDiffAbsoluteUnitsInGradient");
    }

    @Test
    public void userSpaceOnUseWithUnitsRelativeToFontTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseWithUnitsRelativeToFont");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("vh" "vw" "vmin" "vmax" units are not implemented yet)
    public void userSpaceOnUseWithUnitsRelativeToViewportTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseWithUnitsRelativeToViewport");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" "vmin"+"vmax"+"vw"+"vh" not implemented yet)
    public void userSpaceOnUseDiffRelativeUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseDiffRelativeUnitsInGradient");
    }

    @Test
    public void objectBoundingBoxDifferentAbsoluteUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxDifferentAbsoluteUnitsInGradient");
    }

    @Test
    public void objectBoundingBoxWithUnitsRelativeToFontTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxWithUnitsRelativeToFont");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("vmin", "vmax", "vw", "vh" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    public void objectBoundingBoxWithUnitsRelativeToViewportTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxWithUnitsRelativeToViewport");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" "vmin"+"vmax"+"vw"+"vh" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    public void objectBoundingBoxDifferentRelativeUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxDifferentRelativeUnitsInGradient");
    }

    @Test
    public void translateTransformInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "translateTransformInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void matrixTransformInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "matrixTransformInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void severalTransformsInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "severalTransformsInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void radialGradXlink3StopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlink3Stops");
    }

    @Test
    public void radialGradXlinkGradientTransformTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkGradientTransform");
    }

    @Test
    public void radialGradHrefGradientTransformTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefGradientTransform");
    }

    @Test
    public void radialGradXlinkNegativeOffsetTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkNegativeOffset");
    }

    // TODO: DEVSIX-4136 change cmp when gradient opacity is added
    @Test
    public void radialGradXlinkNegativeOpacityTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkNegativeOpacity");
    }

    // TODO: DEVSIX-4136 change cmp when gradient opacity is added
    @Test
    public void radialGradXlinkOpacityTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkOpacity");
    }

    // TODO: DEVSIX-4136 change cmp when gradient opacity is added
    @Test
    public void radialGradXlinkOpacity2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkOpacity2");
    }

    @Test
    public void radialGradXlinkSpreadMethodTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkSpreadMethod");
    }

    @Test
    public void radialGradXlinkSpreadMethod2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkSpreadMethod2");
    }

    @Test
    public void radialGradXlinkSpreadMethod3Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkSpreadMethod3");
    }

    @Test
    public void radialGradXlinkHreOffsetSwapTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradXlinkHreOffsetSwap");
    }

    // TODO: DEVSIX-4136 change cmp when gradient opacity is added
    @Test
    public void radialGradTransitiveHrefOpacityTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradTransitiveHrefOpacity");
    }

    // TODO: DEVSIX-4136 change cmp when gradient opacity is added
    @Test
    public void radialGradTransitiveHrefNegativeOpacityTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradTransitiveHrefNegativeOpacity");
    }

    @Test
    public void radialGradTransitiveHrefNegativeOffsetTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradTransitiveHrefNegativeOffset");
    }

    @Test
    public void radialGradTransitiveHref3stopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradTransitiveHref3stops");
    }

    @Test
    public void radialGradHrefTransitiveSpreadMethodTopLayerTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefTransitiveSpreadMethodTopLayer");
    }

    @Test
    public void radialGradHrefTransitiveSpreadMethodBottomLayerTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefTransitiveSpreadMethodBottomLayer");
    }

    @Test
    public void radialGradHrefTransitiveSpreadMethod3Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefTransitiveSpreadMethod3");
    }

    @Test
    public void radialGradHrefTransitiveSpreadMethod2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefTransitiveSpreadMethod2");
    }

    @Test
    public void tspanTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "tspan");
    }

    @Test
    public void textNestedTSpansTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textNestedTSpansTest");
    }

    @Test
    public void textRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textRotatedTest");
    }

    @Test
    public void textDxTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textDxTest");
    }

    @Test
    public void chineseTextDxTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "chineseTextDxTest");
    }

    @Test
    public void chineseTextDxVerticalTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "chineseTextDxVerticalTest");
    }

    @Test
    public void textAnchorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textAnchorTest");
    }

    @Test
    public void textDyTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textDyTest");
    }

    @Test
    public void textXYOffsetTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textXYOffset");
    }

    @Test
    public void textXOffsetTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textXOffset");
    }

    @Test
    public void textXYDxDyOffsetTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textXYDxDyOffset");
    }

    @Test
    public void textGradientEmUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnits");
    }

    @Test
    public void textGradientEmUnitsRelatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnitsRelated");
    }

    @Test
    public void textGradientEmUnitsRelatedNotDefsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnitsRelatedNotDefs");
    }

    @Test
    public void textGradientEmUnitsRelatedDefaultTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnitsRelatedDefault");
    }

    @Test
    public void textGradientExUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientExUnits");
    }

    @Test
    public void textGradientRemUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientRemUnits");
    }

    @Test
    public void textGradientRemUnitsNestedSvgTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientRemUnitsNestedSvg");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetAndZeroCoordLengthPadTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithSameOffsetAndZeroCoordLengthPad");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetAndZeroCoordLengthReflectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithSameOffsetAndZeroCoordLengthReflect");
    }

    @Test
    public void rectMultipleStopsWithDiffOffsetAndZeroCoordLengthRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithDiffOffsetAndZeroCoordLengthRepeat");
    }

    @Test
    public void rectMultipleStopsWithDiffOffsetAndZeroCoordLengthPadTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithDiffOffsetAndZeroCoordLengthPad");
    }

    @Test
    public void rectMultipleStopsWithDiffOffsetAndZeroCoordLengthReflectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithDiffOffsetAndZeroCoordLengthReflect");
    }

    @Test
    public void rectMultipleStopsWithSameOffsetAndZeroCoordLengthRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithSameOffsetAndZeroCoordLengthRepeat");
    }

    @Test
    public void rectInvalidCoordinatesMetricsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectInvalidCoordinatesMetrics");
    }

    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    @Test
    public void userSpaceOnUseWithChUnitTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseWithChUnit");
    }

    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    @Test
    public void objectBoundingBoxWithChUnitTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxWithChUnit");
    }

    @Test
    public void radialGradHrefXYvals1Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefXYvals1");
    }

    @Test
    public void radialGradHrefXYvals2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefXYvals2");
    }

    @Test
    public void radialGradHrefXYvals3Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "radialGradHrefXYvals3");
    }

    @Test
    public void lowerCaseGradientUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "lowerCaseGradientUnits");
    }
}

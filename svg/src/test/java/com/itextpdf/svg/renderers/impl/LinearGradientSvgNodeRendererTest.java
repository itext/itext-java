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

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LinearGradientSvgNodeRendererTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/LinearGradientSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/LinearGradientSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createOrClearDestinationFolder(destinationFolder);
    }

    // TODO: DEVSIX-3932 update cmp_ after fix
    @Test
    public void circleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "circle");
    }

    // TODO: DEVSIX-3932 update cmp_ after fix
    @Test
    public void ellipseTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipse");
    }

    @Test
    public void lineTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "line");
    }
    
    @Test
    public void pathLinesBasedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "pathLinesBased");
    }

    @Test
    public void pathLinesBasedTransformedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "pathLinesBasedTransformed");
    }
    
    @Test
    public void pathLinesBasedWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "pathLinesBasedWithMove");
    }
    
    @Test
    public void pathLinesBasedWithTwoFiguresTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "pathLinesBasedWithTwoFigures");
    }

    @Test
    public void cubicBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezier");
    }

    @Test
    public void cubicBezier2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezier2");
    }

    @Test
    public void cubicBezier3Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezier3");
    }

    @Test
    public void cubicBezier4Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezier4");
    }

    @Test
    // See CurveTo#calculateTValues to see which discriminant is mentioned.
    public void cubicBezierZeroDiscriminantTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezierZeroDiscriminant");
    }

    @Test
    // See CurveTo#calculateTValues to see which discriminant is mentioned.
    public void cubicBezierNegativeDiscriminantTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezierNegativeDiscriminant");
    }

    @Test
    public void cubicBezierInsideOtherCubicBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezierInsideOtherCubicBezier");
    }

    @Test
    public void smoothCubicBezierWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierWithAbsoluteCoordinates");
    }

    @Test
    public void smoothCubicBezierWithRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierWithRelativeCoordinates");
    }

    @Test
    public void smoothCubicBezierRelativeAndAbsoluteCoordWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierRelativeAndAbsoluteCoordWithMove");
    }

    @Test
    public void smoothCubicBezierRelativeAndAbsoluteCoordNoZOperatorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierRelativeAndAbsoluteCoordNoZOperator");
    }

    @Test
    public void quadraticBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "quadraticBezier");
    }

    @Test
    public void quadraticBezier2Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "quadraticBezier2");
    }

    @Test
    public void quadraticBezier3Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "quadraticBezier3");
    }

    @Test
    public void quadraticBezierInsideOtherQuadraticBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "quadraticBezierInsideOtherQuadraticBezier");
    }

    @Test
    public void smoothQuadraticBezierWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierWithAbsoluteCoordinates");
    }

    @Test
    public void smoothQuadraticBezierWithRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierWithRelativeCoordinates");
    }

    @Test
    public void smoothQuadraticBezierAbsoluteAndRelativeCoordWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierAbsoluteAndRelativeCoordWithMove");
    }

    @Test
    public void smoothQuadraticBezierRelativeAndAbsoluteCoordNoZOperatorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierRelativeAndAbsoluteCoordNoZOperator");
    }

    @Test
    public void ellipticalArcsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcs");
    }

    @Test
    public void ellipticalArcsNegativeRxRyTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsNegativeRxRy");
    }

    @Test
    public void ellipticalArcZeroRxRyTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcZeroRxRy");
    }

    @Test
    public void ellipticalArcsWithPhiTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsWithPhi", PageSize.A3.rotate());
    }

    @Test
    public void ellipticalArcsWithPhi0Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsWithPhi0");
    }

    @Test
    public void ellipticalArcsWithPhi90Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsWithPhi90");
    }

    @Test
    public void ellipticalArcsWithPhi180Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsWithPhi180");
    }
    @Test
    public void ellipticalArcsWithPhi270Test() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsWithPhi270");
    }

    @Test
    public void ellipticalArcsWithPhiRelativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsWithPhiRelative");
    }

    @Test
    public void ellipticalArcsWithPhiAbsoluteTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsWithPhiAbsolute");
    }


    @Test
    public void ellipticalArcsRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsRelativeCoordinates");
    }

    @Test
    public void arcInsideOtherEllipticalArcTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "arcInsideOtherEllipticalArc");
    }

    // TODO: DEVSIX-3932 update cmp_ after fix
    @Test
    public void polygonTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "polygon");
    }

    @Test
    public void polylineTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "polyline");
    }

    @Test
    public void rectTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rect");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.GRADIENT_INVALID_SPREAD_METHOD_LOG, logLevel = LogLevelConstants.WARN)
    })
    public void rectWithInvalidSpreadMethodValueTest() throws java.io.IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "rectWithInvalidSpreadMethodValue");
    }

    @Test
    public void rectsWithFallBackColorsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectsWithFallBackColors");
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

    // TODO: DEVSIX-4136 update cmp_ after fix
    //  (opacity is not implemented. No stops defines no color, i.e. transparent color or black with 100% opacity)
    @Test
    public void rectNoStopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectNoStops");
    }

    @Test
    public void rectSingle0StopTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectSingle0Stop");
    }

    @Test
    public void rectSingle1StopTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectSingle1Stop");
    }

    @Test
    public void rectStopWithoutColorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectStopWithoutColor");
    }

    @Test
    public void rectTransformedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectTransformed");
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
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED)
    })
    // TODO DEVSIX-4140 font-relative values doesn't support
    public void textTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "text");
    }
    
    @Test
    public void textNestedTSpansTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textNestedTSpansTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED)
    })
    public void textRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textRotatedTest");
    }

    @Test
    public void textDxTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textDxTest");
    }

    @Test
    public void textDyTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textDyTest");
    }

    @Test
    public void textXYOffset() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textXYOffset");
    }

    @Test
    // TODO DEVSIX-4143 change cmp file after fixing
    public void textXOffset() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textXOffset");
    }

    @Test
    public void textXYDxDyOffset() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textXYDxDyOffset");
    }

    @Test
    public void textGradientEmUnits() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnits");
    }

    @Test
    public void textGradientEmUnitsRelated() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnitsRelated");
    }

    @Test
    public void textGradientEmUnitsRelatedNotDefs() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnitsRelatedNotDefs");
    }

    @Test
    public void textGradientEmUnitsRelatedDefault() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientEmUnitsRelatedDefault");
    }

    @Test
    public void textGradientExUnits() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientExUnits");
    }

    @Test
    public void textGradientRemUnits() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "textGradientRemUnits");
    }

    @Test
    public void textGradientRemUnitsNestedSvg() throws IOException, InterruptedException, java.io.IOException {
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
    public void rectMultipleStopsWithSameOffsetAndZeroCoordLengthRepeatTest() throws IOException, InterruptedException, java.io.IOException {
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
    public void rectMultipleStopsWithDiffOffsetAndZeroCoordLengthRepeatTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectMultipleStopsWithSameOffsetAndZeroCoordLengthRepeat");
    }

    @Test
    public void rectInvalidStopsSequenceTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectInvalidStopsSequence");
    }

    @Test
    public void rectInvalidCoordinatesMetricsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectInvalidCoordinatesMetrics");
    }

    @Test
    public void rectInvalidStopsSequenceWithoutBoundingStopsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "rectInvalidStopsSequenceWithoutBoundingStops");
    }

    @Test
    public void userSpaceOnUseWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseWithAbsoluteCoordinates");
    }

    @Test
    public void userSpaceOnUseDiffAbsoluteUnitsInGradientTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseDiffAbsoluteUnitsInGradient");
    }

    @Test
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" unit is not implemented yet)
    public void userSpaceOnUseWithChUnitTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "userSpaceOnUseWithChUnit");
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
    public void objectBoundingBoxWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxWithAbsoluteCoordinates");
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
    // TODO: DEVSIX-3596 update cmp_ after fix ("ch" not implemented yet)
    //  actually the value type should not affect on the objectBoundingBox coordinate, but as
    //  we are not recognize these values as valid relative type,
    //  we get the the resulted coordinate uses defaults
    public void objectBoundingBoxWithChUnitTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "objectBoundingBoxWithChUnit");
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
    //TODO change cmp after DEVSIX-4143 is fixed (bug with only one absolute coordinate in tspan)
    public void matrixTransformInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "matrixTransformInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void severalTransformsInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "severalTransformsInGradientWithObjectBoundingBoxUnits");
    }
}

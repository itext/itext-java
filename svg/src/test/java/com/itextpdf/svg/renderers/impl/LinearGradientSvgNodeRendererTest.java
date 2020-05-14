/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.IOException;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.BeforeClass;
import org.junit.Ignore;
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

    // TODO: DEVSIX-4018 add tests for all other types of path components

    // TODO: DEVSIX-4018 update cmp_ after fix (box for path is not implemented)
    @Test
    public void pathLinesBasedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "pathLinesBased");
    }

    // TODO: DEVSIX-4018 update cmp_ after fix (box for path is not implemented)
    @Test
    public void pathLinesBasedWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "pathLinesBasedWithMove");
    }

    // TODO: DEVSIX-4018 update cmp_ after fix (box for path is not implemented)
    @Test
    public void pathLinesBasedWithTwoFiguresTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "pathLinesBasedWithTwoFigures");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void cubicBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezier");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void cubicBezierInsideOtherCubicBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "cubicBezierInsideOtherCubicBezier");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothCubicBezierWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierWithAbsoluteCoordinates");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothCubicBezierWithRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierWithRelativeCoordinates");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothCubicBezierRelativeAndAbsoluteCoordWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierRelativeAndAbsoluteCoordWithMove");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothCubicBezierRelativeAndAbsoluteCoordNoZOperatorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothCubicBezierRelativeAndAbsoluteCoordNoZOperator");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void quadraticBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "quadraticBezier");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void quadraticBezierInsideOtherQuadraticBezierTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "quadraticBezierInsideOtherQuadraticBezier");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothQuadraticBezierWithAbsoluteCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierWithAbsoluteCoordinates");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothQuadraticBezierWithRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierWithRelativeCoordinates");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothQuadraticBezierAbsoluteAndRelativeCoordWithMoveTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierAbsoluteAndRelativeCoordWithMove");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void smoothQuadraticBezierRelativeAndAbsoluteCoordNoZOperatorTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "smoothQuadraticBezierRelativeAndAbsoluteCoordNoZOperator");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void ellipticalArcsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcs");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
    public void ellipticalArcsRelativeCoordinatesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "ellipticalArcsRelativeCoordinates");
    }

    @Test
    // TODO: update cmp-file after DEVSIX-4018 will be fixed
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

    // TODO: DEVSIX-4018 update cmp_ after fix (box for text is not implemented)
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED)
    })
    public void textTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "text");
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
    public void matrixTransformInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "matrixTransformInGradientWithObjectBoundingBoxUnits");
    }

    @Test
    public void severalTransformsInGradientWithObjectBoundingBoxUnitsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(sourceFolder, destinationFolder, "severalTransformsInGradientWithObjectBoundingBoxUnits");
    }
}

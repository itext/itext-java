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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.StrategyBasedRadialGradientBuilder.GradientStrategy;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * This test class focuses on different types of radial gradient strategies.
 * Tests related to stop colors work omitted here as they would be equivalent to tests in
 * {@link RadialGradientBuilderTest}.
 */
@Tag("IntegrationTest")
public class StrategyBasedRadialGradientBuilderTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/kernel/colors/gradients/StrategyBasedRadialGradientBuilderTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/kernel/colors/gradients/StrategyBasedRadialGradientBuilderTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void noSettersTest() {
        Assertions.assertNull(new StrategyBasedRadialGradientBuilder()
                .buildColor(new Rectangle(50f, 450f, 500f, 300f), null, null));
    }

    @Test
    public void noRectangleTest() {
        Assertions.assertNull(new StrategyBasedRadialGradientBuilder()
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .buildColor(null, null, null));
    }

    @Test
    public void noStrategyProvidedTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("noStrategyProvided.pdf", null, gradientBuilder);
    }

    @Test
    public void absoluteCenterFromLeftBottomTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 120, false, true, 50, false)
                .setRadiusRelativeToBoundingBoxSize(100, false, 100, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("absoluteCenterFromLeftBottom.pdf", null, gradientBuilder);
    }

    @Test
    public void absoluteCenterFromRightTopTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(false, 120, false, false, 50, false)
                .setRadiusRelativeToBoundingBoxSize(100, false, 100, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("absoluteCenterFromRightTop.pdf", null, gradientBuilder);
    }

    @Test
    public void absoluteNegativeCenterFromLeftBottomTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, -120, false, true, -50, false)
                .setRadiusRelativeToBoundingBoxSize(200, false, 200, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("absoluteNegativeCenterFromLeftBottom.pdf", null, gradientBuilder);
    }

    @Test
    public void absoluteNegativeCenterFromRightTopTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(false, -120, false, false, -50, false)
                .setRadiusRelativeToBoundingBoxSize(200, false, 200, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("absoluteNegativeCenterFromRightTop.pdf", null, gradientBuilder);
    }

    @Test
    public void relativeCenterFromLeftBottomTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.1d, true, true, 0.1d, true)
                .setRadiusRelativeToBoundingBoxSize(100, false, 100, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("relativeCenterFromLeftBottom.pdf", null, gradientBuilder);
    }

    @Test
    public void relativeCenterFromRightTopTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(false, 0.1d, true, false, 0.1d, true)
                .setRadiusRelativeToBoundingBoxSize(100, false, 100, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("relativeCenterFromRightTop.pdf", null, gradientBuilder);
    }

    @Test
    public void builderClosestSideEllipseTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(false, GradientStrategy.CLOSEST_SIDE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderClosestSideEllipse.pdf", null, gradientBuilder);
    }

    @Test
    public void builderClosestCornerEllipseTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(false, GradientStrategy.CLOSEST_CORNER)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderClosestCornerEllipse.pdf", null, gradientBuilder);
    }

    @Test
    public void builderFarthestSideEllipseTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(false, GradientStrategy.FARTHEST_SIDE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderFarthestSideEllipse.pdf", null, gradientBuilder);
    }

    @Test
    public void builderFarthestCornerEllipseTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(false, GradientStrategy.FARTHEST_CORNER)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderFarthestCornerEllipse.pdf", null, gradientBuilder);
    }

    @Test
    public void builderClosestSideCircleTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(true, GradientStrategy.CLOSEST_SIDE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderClosestSideCircle.pdf", null, gradientBuilder);
    }

    @Test
    public void builderClosestCornerCircleTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(true, GradientStrategy.CLOSEST_CORNER)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderClosestCornerCircle.pdf", null, gradientBuilder);
    }

    @Test
    public void builderFarthestSideCircleTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(true, GradientStrategy.FARTHEST_SIDE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderFarthestSideCircle.pdf", null, gradientBuilder);
    }

    @Test
    public void builderFarthestCornerCircleTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setCenterStrategy(true, 0.3, true, true, 0.2, true)
                .setRadiusFromCenterStrategy(true, GradientStrategy.FARTHEST_CORNER)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderFarthestCornerCircle.pdf", null, gradientBuilder);
    }

    @Test
    public void builderZeroManualRadiusWithPadTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setRadiusRelativeToBoundingBoxSize(0, false, 0, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .setSpread(GradientSpreadMethod.PAD);

        generateAndComparePdfs("builderZeroManualRadiusWithPad.pdf", null, gradientBuilder);
    }

    @Test
    public void builderManualRadiusTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setRadiusRelativeToBoundingBoxSize(0.3d, true, 20, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderManualRadius.pdf", null, gradientBuilder);
    }

    @Test
    public void builderNegativeRadiusTest() {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .setRadiusRelativeToBoundingBoxSize(-0.3d, true, -20, false)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        Assertions.assertNull(gradientBuilder.buildColor(
                new Rectangle(50f, 450f, 500f, 300f), null, null));
    }

    @Test
    public void builderWithNoneSpreadingAndCanvasTransformTest() throws IOException, InterruptedException {
        AbstractGradientBuilder<RadialGradientPoint> gradientBuilder = new StrategyBasedRadialGradientBuilder()
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .setSpread(GradientSpreadMethod.NONE);

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI / 3, 400f, 550f);
        generateAndComparePdfs("noneSpreadingCanvasTransform.pdf", canvasTransform, gradientBuilder);
    }

    private void generateAndComparePdfs(String fileName, AffineTransform transform,
            AbstractGradientBuilder<RadialGradientPoint> gradientBuilder) throws InterruptedException, IOException {
        String outPdfPath = DESTINATION_FOLDER + fileName;
        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdfPath))) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            if (transform != null) {
                canvas.concatMatrix(transform);
            }

            Rectangle toDraw = new Rectangle(50f, 450f, 500f, 300f);
            canvas.setFillColor(gradientBuilder.buildColor(toDraw, transform, pdfDoc))
                    .setStrokeColor(ColorConstants.BLACK)
                    .rectangle(toDraw)
                    .fillStroke();
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outPdfPath, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff"));
    }
}

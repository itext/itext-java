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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
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
 * This test class focuses on different types of linear gradient coordinates vector strategies.
 * Tests related to stop colors work omitted here as they would be equivalent to tests in
 * {@link LinearGradientBuilderTest}
 */
@Tag("IntegrationTest")
public class StrategyBasedLinearGradientBuilderTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/colors/gradients/StrategyBasedLinearGradientBuilderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/colors/gradients/StrategyBasedLinearGradientBuilderTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void noSettersTest() {
        Assertions.assertNull(new StrategyBasedLinearGradientBuilder()
                .buildColor(new Rectangle(50f, 450f, 500f, 300f), null, null));
    }

    @Test
    public void noRectangleTest() {
        Assertions.assertNull(new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .buildColor(null, null, null));
    }

    @Test
    public void noStrategyProvidedTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("noStrategyProvided.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToRightTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_RIGHT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToRight.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToLeftTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_LEFT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToLeft.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToBottomTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_BOTTOM)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToBottom.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToBottomRightTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_BOTTOM_RIGHT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToBottomRight.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToBottomLeftTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_BOTTOM_LEFT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToBottomLeft.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToTopTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_TOP)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToTop.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToTopRightTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_TOP_RIGHT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToTopRight.pdf", null, gradientBuilder);
    }

    @Test
    public void builderToTopLeftTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_TOP_LEFT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderToTopLeft.pdf", null, gradientBuilder);
    }

    @Test
    public void builderZeroAngleTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsCentralRotationAngle(0d)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderZeroAngle.pdf", null, gradientBuilder);
    }

    @Test
    public void builderPositiveAngleTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsCentralRotationAngle(Math.PI/3)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderPositiveAngle.pdf", null, gradientBuilder);
    }

    @Test
    public void builderNegativeAngleTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsCentralRotationAngle(-Math.PI/3)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderNegativeAngle.pdf", null, gradientBuilder);
    }

    @Test
    public void builderWithNoneSpreadingAndCanvasTransformTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_RIGHT)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI/3, 400f, 550f);
        generateAndComparePdfs("noneSpreadingCanvasTransform.pdf", canvasTransform, gradientBuilder);
    }

    @Test
    public void builderWithToCornerAndInnerStopsAndNoneSpreadingTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_TOP_RIGHT)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.3d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.4, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("toCornerInnerStopsNoneSpreading.pdf", null, gradientBuilder);
    }

    @Test
    public void builderWithToCornerAndInnerStopsAndPadSpreadingTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_TOP_RIGHT)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.3d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.4, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("toCornerInnerStopsPadSpreading.pdf", null, gradientBuilder);
    }

    @Test
    public void builderWithToCornerAndInnerStopsAndReflectSpreadingTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_TOP_RIGHT)
                .setSpreadMethod(GradientSpreadMethod.REFLECT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.3d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.4, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("toCornerInnerStopsReflectSpreading.pdf", null, gradientBuilder);
    }

    @Test
    public void builderWithToCornerAndInnerStopsAndRepeatSpreadingTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_TOP_RIGHT)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.3d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.4, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("toCornerInnerStopsRepeatSpreading.pdf", null, gradientBuilder);
    }

    private void generateAndComparePdfs(String fileName, AffineTransform transform,
            AbstractLinearGradientBuilder gradientBuilder) throws InterruptedException, IOException {
        String outPdfPath = destinationFolder + fileName;
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
                .compareByContent(outPdfPath, sourceFolder + "cmp_" + fileName, destinationFolder, "diff"));
    }
}

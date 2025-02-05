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
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class LinearGradientBuilderTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/colors/gradients/LinearGradientBuilderTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/colors/gradients/LinearGradientBuilderTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    public void buildWithNullArgumentsAndWithoutSettersTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder();

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithOneStopTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithOneStopTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoStopsTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsAtTheBeginningTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoStopsAtTheBeginningTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsAtTheEndTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoStopsAtTheEndTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsInTheMiddleTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoStopsInTheMiddleTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsBeforeTheBeginningTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), -0.1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  -0.2d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoStopsBeforeTheBeginningTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsAfterTheEndTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 1.2d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoStopsAfterTheEndTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void padCaseWithVeryCloseCornerStopsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.01d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.99d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("padCaseWithVeryCloseCornerStopsTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithDoublingStopsAtEndsAndPadTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), 1.2, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), 1.2, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithDoublingStopsAtEndsAndPadTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithDoublingStopsAtEndsAndEndsOfCoordinatesAndPadTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), 1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), 1.2, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), 1.2, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithDoublingStopsAtEndsAndEndsOfCoordinatesAndPadTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithoutCoordinatesTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithoutCoordinatesTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithZeroVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithZeroVectorTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndWithoutStopsTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD);

        Assertions.assertNull(gradientBuilder.buildColor(null, null, null));
    }

    @Test
    public void buildWithNullArgumentsAndNoneSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("buildWithNullArgumentsAndNoneSpreadingTest.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndPadSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("buildWithNullArgumentsAndPadSpreadingTest.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndReflectSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.REFLECT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("buildWithNullArgumentsAndReflectSpreadingTest.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndRepeatSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("buildWithNullArgumentsAndRepeatSpreadingTest.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void builderWithNoneSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderWithNoneSpreadingTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithNoneSpreadingAndCanvasTransformTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI/3, 400f, 550f);
        generateAndComparePdfs("builderWithNoneSpreadingAndCanvasTransformTest.pdf", targetBoundingBox, canvasTransform, gradientBuilder);
    }

    @Test
    public void builderWithNoneSpreadingAndAllTransformsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AffineTransform gradientTransform = AffineTransform.getTranslateInstance(150, -50);
        gradientTransform.scale(0.5, 1.5);
        gradientTransform.rotate(Math.PI/3, 400f, 550f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setCurrentSpaceToGradientVectorSpaceTransformation(gradientTransform)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI/3, 400f, 550f);
        generateAndComparePdfs("builderWithNoneSpreadingAndAllTransformsTest.pdf", targetBoundingBox, canvasTransform, gradientBuilder);
    }

    @Test
    public void builderWithPadSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderWithPadSpreadingTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithReflectSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.REFLECT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderWithReflectSpreadingTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderWithRepeatSpreadingTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndAllTransformsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AffineTransform gradientTransform = AffineTransform.getTranslateInstance(150, -50);
        gradientTransform.scale(0.5, 1.5);
        gradientTransform.rotate(Math.PI/3, 400f, 550f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setCurrentSpaceToGradientVectorSpaceTransformation(gradientTransform)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI/3, 400f, 550f);
        generateAndComparePdfs("builderWithRepeatSpreadingAndAllTransformsTest.pdf", targetBoundingBox, canvasTransform, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToRightVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getRight() + 100f, 0f,
                        targetBoundingBox.getRight() + 300f, 0f)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("builderWithRepeatSpreadingAndToRightVectorTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToLeftVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getRight() + 300f, 0f,
                        targetBoundingBox.getRight() + 100f, 0f)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("builderWithRepeatSpreadingAndToLeftVectorTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToTopVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(0f, targetBoundingBox.getBottom() - 300f,
                        0f, targetBoundingBox.getBottom() - 100f)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("builderWithRepeatSpreadingAndToTopVectorTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToBottomVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(0f, targetBoundingBox.getBottom() - 100f,
                        0f, targetBoundingBox.getBottom() - 300f)
                .setSpreadMethod(GradientSpreadMethod.REPEAT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("builderWithRepeatSpreadingAndToBottomVectorTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndAbsoluteOnCoordinatesHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopAndAbsoluteOnCoordinatesHintTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeOnCoordinatesHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.2f, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopAndRelativeOnCoordinatesHintTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeBetweenColorsHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.2f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopAndRelativeBetweenColorsHintTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeBetweenColorsZeroHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopAndRelativeBetweenColorsZeroHintTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeBetweenColorsOneHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(1f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopAndRelativeBetweenColorsOneHintTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithPadSpreadingAndRelativeBetweenColorsZeroHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE).setHint(0f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithPadSpreadingAndRelativeBetweenColorsZeroHintTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithPadSpreadingAndRelativeBetweenColorsOneHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE).setHint(1f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithPadSpreadingAndRelativeBetweenColorsOneHintTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndNoneHintTypeTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.2f, HintOffsetType.NONE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopAndNoneHintTypeTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithReflectSpreadingAndStopsOutsideCoordinatesTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.REFLECT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), -0.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithReflectSpreadingAndStopsOutsideCoordinatesTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithSingleAutoStopsAtStartAndEndTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.1, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 200d, OffsetType.ABSOLUTE))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.1, HintOffsetType.RELATIVE_BETWEEN_COLORS));

        generateAndComparePdfs("buildWithSingleAutoStopsAtStartAndEndTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithSingleAutoStopsAtStartAndEndWithHintsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.1, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 200d, OffsetType.ABSOLUTE))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.9, HintOffsetType.RELATIVE_ON_GRADIENT));

        generateAndComparePdfs("buildWithSingleAutoStopsAtStartAndEndWithHintsTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithMultipleAutoStopsAtStartAndEndWithHintsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 200d, OffsetType.ABSOLUTE))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()));

        generateAndComparePdfs("buildWithMultipleAutoStopsAtStartAndEndWithHintsTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopsInTheMiddleTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopsInTheMiddleTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopsInTheMiddleWithHintsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE).setHint(0.2d, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.7d, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithAutoStopsInTheMiddleWithHintsTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithDecreasingOffsetsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.5d, OffsetType.RELATIVE).setHint(0.4d, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.6d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 100d, OffsetType.ABSOLUTE).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.9d, OffsetType.RELATIVE).setHint(120d, HintOffsetType.ABSOLUTE_ON_GRADIENT))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithDecreasingOffsetsTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithZeroColorsLengthAndReflect() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpreadMethod(GradientSpreadMethod.REFLECT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.8d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.2d, OffsetType.RELATIVE));

        generateAndComparePdfs("builderWithZeroColorsLengthAndReflect.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsBeforeTheBeginningAndNoneTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), -10d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  -5d, OffsetType.RELATIVE));

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithTwoStopsAfterEndAndNoneTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  10d, OffsetType.RELATIVE));

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithTwoEqualOffsetsStopsAndNoneTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  0.5d, OffsetType.RELATIVE));

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithTwoStopsInCenterAndNoneTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.2d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  0.8d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoEqualOffsetsStopsTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsOutsideAndNoneTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.NONE)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), -1.5d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  2.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("buildWithTwoStopsOutsideAndNoneTest.pdf", targetBoundingBox, null, gradientBuilder);
    }

    private void generateAndComparePdfs(String fileName, Rectangle toDraw, AffineTransform transform,
            AbstractLinearGradientBuilder gradientBuilder) throws InterruptedException, IOException {
        String outPdfPath = destinationFolder + fileName;
        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdfPath))) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            if (transform != null) {
                canvas.concatMatrix(transform);
            }

            canvas.setFillColor(gradientBuilder.buildColor(toDraw, transform, pdfDoc))
                    .setStrokeColor(ColorConstants.BLACK)
                    .rectangle(toDraw)
                    .fillStroke();
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outPdfPath, sourceFolder + "cmp_" + fileName, destinationFolder, "diff"));
    }

    private void generateAndComparePdfsWithoutArgumentToBuild(String fileName, Rectangle toDraw,
            AbstractLinearGradientBuilder gradientBuilder) throws InterruptedException, IOException {
        String outPdfPath = destinationFolder + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(outPdfPath);
        try (PdfDocument pdfDoc = new PdfDocument(writer)) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            canvas.setFillColor(gradientBuilder.buildColor(null, null, pdfDoc))
                    .setStrokeColor(ColorConstants.BLACK)
                    .rectangle(toDraw)
                    .fillStroke();
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outPdfPath, sourceFolder + "cmp_" + fileName, destinationFolder, "diff"));
    }
}

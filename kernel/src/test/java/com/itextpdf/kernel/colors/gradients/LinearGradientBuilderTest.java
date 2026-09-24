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
import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class LinearGradientBuilderTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/kernel/colors/gradients/LinearGradientBuilderTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/kernel/colors/gradients/LinearGradientBuilderTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void buildWithNullArgumentsAndWithoutSettersTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder();

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithOneStopTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("oneStop.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoStops.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsAtTheBeginningTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoStopsAtTheBeginning.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsAtTheEndTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoStopsAtTheEnd.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsInTheMiddleTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoStopsInTheMiddle.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsBeforeTheBeginningTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), -0.1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  -0.2d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoStopsBeforeTheBeginning.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsAfterTheEndTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 1.2d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoStopsAfterTheEnd.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void padCaseWithVeryCloseCornerStopsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.01d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.99d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("padCaseVeryCloseCornerStops.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithDoublingStopsAtEndsAndPadTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), 1.2, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), 1.2, OffsetType.RELATIVE));

        generateAndComparePdfs("doublingStopsAtEndsPad.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithDoublingStopsAtEndsAndEndsOfCoordinatesAndPadTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), -0.2, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), 1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.ORANGE.getColorValue(), 1.2, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.MAGENTA.getColorValue(), 1.2, OffsetType.RELATIVE));

        generateAndComparePdfs("doublingStopsAtEndsEndsOfCoordinatesPad.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithoutCoordinatesTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("withoutCoordinates.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithZeroVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("zeroVector.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndWithoutStopsTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD);

        Assertions.assertNull(gradientBuilder.buildColor(null, null, null));
    }

    @Test
    public void buildWithNullArgumentsAndNoneSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("nullArgumentsNoneSpreading.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndPadSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("nullArgumentsPadSpreading.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndReflectSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.REFLECT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("nullArgumentsReflectSpreading.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void buildWithNullArgumentsAndRepeatSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfsWithoutArgumentToBuild("nullArgumentsRepeatSpreading.pdf", targetBoundingBox, gradientBuilder);
    }

    @Test
    public void builderWithNoneSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("noneSpreading.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithNoneSpreadingAndCanvasTransformTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI/3, 400f, 550f);
        generateAndComparePdfs("noneSpreadingCanvasTransform.pdf", targetBoundingBox, canvasTransform, gradientBuilder);
    }

    @Test
    public void builderWithNoneSpreadingAndAllTransformsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AffineTransform gradientTransform = AffineTransform.getTranslateInstance(150, -50);
        gradientTransform.scale(0.5, 1.5);
        gradientTransform.rotate(Math.PI/3, 400f, 550f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setCurrentSpaceToGradientVectorSpaceTransformation(gradientTransform)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI/3, 400f, 550f);
        generateAndComparePdfs("noneSpreadingAllTransforms.pdf", targetBoundingBox, canvasTransform, gradientBuilder);
    }

    @Test
    public void builderWithPadSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("padSpreading.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithReflectSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.REFLECT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("reflectSpreading.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("repeatSpreading.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndAllTransformsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        AffineTransform gradientTransform = AffineTransform.getTranslateInstance(150, -50);
        gradientTransform.scale(0.5, 1.5);
        gradientTransform.rotate(Math.PI/3, 400f, 550f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setCurrentSpaceToGradientVectorSpaceTransformation(gradientTransform)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        AffineTransform canvasTransform = AffineTransform.getTranslateInstance(50, -50);
        canvasTransform.scale(0.8, 1.1);
        canvasTransform.rotate(Math.PI/3, 400f, 550f);
        generateAndComparePdfs("repeatSpreadingAllTransforms.pdf", targetBoundingBox, canvasTransform, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToRightVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getRight() + 100f, 0f,
                        targetBoundingBox.getRight() + 300f, 0f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("repeatSpreadingToRightVector.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToLeftVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getRight() + 300f, 0f,
                        targetBoundingBox.getRight() + 100f, 0f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("repeatSpreadingToLeftVector.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToTopVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(0f, targetBoundingBox.getBottom() - 300f,
                        0f, targetBoundingBox.getBottom() - 100f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("repeatSpreadingToTopVector.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithRepeatSpreadingAndToBottomVectorTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(0f, targetBoundingBox.getBottom() - 100f,
                        0f, targetBoundingBox.getBottom() - 300f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue()));

        generateAndComparePdfs("repeatSpreadingToBottomVector.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndAbsoluteOnCoordinatesHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(100f, HintOffsetType.ABSOLUTE_ON_GRADIENT))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopAbsoluteOnCoordinatesHint.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeOnCoordinatesHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.2f, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopRelativeOnCoordinatesHint.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeBetweenColorsHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.2f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopRelativeBetweenColorsHint.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeBetweenColorsZeroHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopRelativeBetweenColorsZeroHint.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndRelativeBetweenColorsOneHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(1f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopRelativeBetweenColorsOneHint.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithPadSpreadingAndRelativeBetweenColorsZeroHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE).setHint(0f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("padSpreadingRelativeBetweenColorsZeroHint.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithPadSpreadingAndRelativeBetweenColorsOneHintTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE).setHint(1f, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("padSpreadingRelativeBetweenColorsOneHint.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopAndNoneHintTypeTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft(), targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight(), targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.1d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.2f, HintOffsetType.NONE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.9d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopNoneHintType.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithReflectSpreadingAndStopsOutsideCoordinatesTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.REFLECT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), -0.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("reflectSpreadingStopsOutsideCoordinates.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithSingleAutoStopsAtStartAndEndTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.1, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 200d, OffsetType.ABSOLUTE))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.1, HintOffsetType.RELATIVE_BETWEEN_COLORS));

        generateAndComparePdfs("singleAutoStopsAtStartAndEnd.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithSingleAutoStopsAtStartAndEndWithHintsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.1, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 200d, OffsetType.ABSOLUTE))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()).setHint(0.9, HintOffsetType.RELATIVE_ON_GRADIENT));

        generateAndComparePdfs("singleAutoStopsAtStartAndEndWithHints.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithMultipleAutoStopsAtStartAndEndWithHintsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 200d, OffsetType.ABSOLUTE))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()));

        generateAndComparePdfs("multipleAutoStopsAtStartAndEndWithHints.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopsInTheMiddleTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue()).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopsInTheMiddle.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithAutoStopsInTheMiddleWithHintsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE).setHint(0.2d, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue()))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue()).setHint(0.7d, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("autoStopsInTheMiddleWithHints.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithDecreasingOffsetsTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.5d, OffsetType.RELATIVE).setHint(0.4d, HintOffsetType.RELATIVE_ON_GRADIENT))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.6d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 100d, OffsetType.ABSOLUTE).setHint(0.3d, HintOffsetType.RELATIVE_BETWEEN_COLORS))
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.9d, OffsetType.RELATIVE).setHint(120d, HintOffsetType.ABSOLUTE_ON_GRADIENT))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("decreasingOffsets.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void builderWithZeroColorsLengthAndReflect() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 10f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 10f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.REFLECT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.8d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.2d, OffsetType.RELATIVE));

        generateAndComparePdfs("zeroColorsLengthAndReflect.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsBeforeTheBeginningAndNoneTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), -10d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  -5d, OffsetType.RELATIVE));

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithTwoStopsAfterEndAndNoneTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  10d, OffsetType.RELATIVE));

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithTwoEqualOffsetsStopsAndNoneTest() {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  0.5d, OffsetType.RELATIVE));

        Assertions.assertNull(gradientBuilder.buildColor(targetBoundingBox, null, null));
    }

    @Test
    public void buildWithTwoStopsInCenterAndNoneTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0.2d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  0.8d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoEqualOffsetsStops.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    public void buildWithTwoStopsOutsideAndNoneTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), -1.5d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(),  2.5d, OffsetType.RELATIVE));

        generateAndComparePdfs("twoStopsOutsideAndNone.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.GRADIENT_MAX_COLOR_STOPS,
                    logLevel = LogLevelConstants.WARN, count = 1)
    })
    public void buildStopsDefaultLimitRepeatTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getLeft() + 100.15f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.2d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.YELLOW.getColorValue(), 0.4d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLACK.getColorValue(), 0.6d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.WHITE.getColorValue(), 0.8d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        generateAndComparePdfs("stopsDefaultLimitRepeat.pdf", targetBoundingBox, null, gradientBuilder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.GRADIENT_MAX_COLOR_STOPS,
                    logLevel = LogLevelConstants.WARN, count = 1)
    })
    public void buildStopsLimitReachedRepeatTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getLeft() + 150f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.REPEAT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        GradientPropertiesResolver gradientPropertiesResolver = new GradientPropertiesResolver(10);
        generateAndComparePdfs("stopsLimitReachedRepeat.pdf", targetBoundingBox, null, gradientBuilder,
                gradientPropertiesResolver);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.GRADIENT_MAX_COLOR_STOPS,
                    logLevel = LogLevelConstants.WARN, count = 1)
    })
    public void buildStopsLimitReachedReflectTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getLeft() + 150f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.REFLECT)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        GradientPropertiesResolver gradientPropertiesResolver = new GradientPropertiesResolver(5);
        generateAndComparePdfs("stopsLimitReachedReflect.pdf", targetBoundingBox, null, gradientBuilder,
                gradientPropertiesResolver);
    }

    @Test
    public void buildStopsLimitReachedPadTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getLeft() + 150f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.PAD)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        GradientPropertiesResolver gradientPropertiesResolver = new GradientPropertiesResolver(1);
        generateAndComparePdfs("stopsLimitReachedPad.pdf", targetBoundingBox, null, gradientBuilder,
                gradientPropertiesResolver);
    }

    @Test
    public void buildStopsLimitReachedNoneTest() throws IOException, InterruptedException {
        Rectangle targetBoundingBox = new Rectangle(50f, 450f, 300f, 300f);
        IGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getLeft() + 150f, targetBoundingBox.getBottom() + 100f)
                .setSpread(GradientSpreadMethod.NONE)
                .addStopColor(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addStopColor(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));

        GradientPropertiesResolver gradientPropertiesResolver = new GradientPropertiesResolver(1);
        generateAndComparePdfs("stopsLimitReachedNone.pdf", targetBoundingBox, null, gradientBuilder,
                gradientPropertiesResolver);
    }

    private void generateAndComparePdfs(String fileName, Rectangle toDraw, AffineTransform transform,
            IGradientBuilder gradientBuilder) throws InterruptedException, IOException {
        generateAndComparePdfs(fileName, toDraw, transform, gradientBuilder, null);
    }

    private void generateAndComparePdfs(String fileName, Rectangle toDraw, AffineTransform transform,
            IGradientBuilder gradientBuilder,
            GradientPropertiesResolver gradientPropertiesResolver) throws InterruptedException, IOException {
        DocumentProperties properties = new DocumentProperties();
        if (gradientPropertiesResolver != null) {
            properties.registerDependency(GradientPropertiesResolver.class, () -> gradientPropertiesResolver);
        }

        String outPdfPath = DESTINATION_FOLDER + fileName;
        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outPdfPath), properties)) {
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
                .compareByContent(outPdfPath, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff"));
    }

    private void generateAndComparePdfsWithoutArgumentToBuild(String fileName, Rectangle toDraw,
            IGradientBuilder gradientBuilder) throws InterruptedException, IOException {
        String outPdfPath = DESTINATION_FOLDER + fileName;
        PdfWriter writer = CompareTool.createTestPdfWriter(outPdfPath);
        try (PdfDocument pdfDoc = new PdfDocument(writer)) {
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());

            canvas.setFillColor(gradientBuilder.buildColor(null, null, pdfDoc))
                    .setStrokeColor(ColorConstants.BLACK)
                    .rectangle(toDraw)
                    .fillStroke();
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outPdfPath, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff"));
    }
}

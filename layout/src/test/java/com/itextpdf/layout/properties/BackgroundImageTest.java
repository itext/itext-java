/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.properties;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder.GradientStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class BackgroundImageTest extends ExtendedITextTest {
    private static final float DELTA = 0.0001f;
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/BackgroundImageTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/BackgroundImageTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void backgroundImage() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

        backgroundImageGenericTest("backgroundImage", backgroundImage);
    }

    @Test
    public void copyConstructorTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage image = new BackgroundImage.Builder().setImage(xObject).build();
        Field[] imageFields = image.getClass().getDeclaredFields();
        BackgroundImage copyImage = new BackgroundImage(image);
        Field[] copyImageFields = copyImage.getClass().getDeclaredFields();
        Assertions.assertEquals(imageFields.length, copyImageFields.length);
        for (int i = 0; i < imageFields.length; i++) {
            Field imageField = imageFields[i];
            Field copyImageField = copyImageFields[i];
            Assertions.assertEquals(imageField, copyImageField);
        }
    }

    @Test
    public void backgroundImageClipOriginDefaultsTest() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        Assertions.assertEquals(BackgroundBox.BORDER_BOX, backgroundImage.getBackgroundClip());
        Assertions.assertEquals(BackgroundBox.PADDING_BOX, backgroundImage.getBackgroundOrigin());

        backgroundImageGenericTest("backgroundImage", backgroundImage);
    }

    @Test
    public void backgroundImageClipOriginTest() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject)
                .setBackgroundClip(BackgroundBox.CONTENT_BOX).setBackgroundOrigin(BackgroundBox.CONTENT_BOX).build();

        Assertions.assertEquals(BackgroundBox.CONTENT_BOX, backgroundImage.getBackgroundClip());
        Assertions.assertEquals(BackgroundBox.CONTENT_BOX, backgroundImage.getBackgroundOrigin());

        backgroundImageGenericTest("backgroundImage", backgroundImage);
    }

    @Test
    public void backgroundMultipleImagesTest() throws IOException, InterruptedException {
        List<BackgroundImage> images = Arrays.asList(
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "rock_texture.jpg")))
                        .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT, BackgroundRepeatValue.REPEAT)).setBackgroundPosition(new BackgroundPosition()).build(),
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg")))
                        .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.REPEAT, BackgroundRepeatValue.NO_REPEAT)).build());
        backgroundImageGenericTest("backgroundMultipleImages", images);
    }

    @Test
    public void backgroundImageWithLinearGradientTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setLinearGradientBuilder(gradientBuilder).build();
        backgroundImageGenericTest("backgroundImageWithLinearGradient", backgroundImage);
    }

    @Test
    public void backgroundImageWithLinearGradientAndPositionTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setLinearGradientBuilder(gradientBuilder)
                .setBackgroundPosition(new BackgroundPosition().setYShift(UnitValue.createPointValue(30)).setXShift(UnitValue.createPointValue(50))).build();
        backgroundImageGenericTest("backgroundImageWithLinearGradientAndPosition", backgroundImage);
    }

    @Test
    public void backgroundImageWithLinearGradientAndRepeatTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));
        BackgroundImage backgroundImage = new BackgroundImage.Builder()
                .setLinearGradientBuilder(gradientBuilder).setBackgroundRepeat(new BackgroundRepeat()).build();
        backgroundImageGenericTest("backgroundImageWithLinearGradientAndRepeat", backgroundImage);
    }

    @Test
    public void backgroundImageWithLinearGradientAndPositionAndRepeatTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setLinearGradientBuilder(gradientBuilder).setBackgroundRepeat(new BackgroundRepeat())
                .setBackgroundPosition(new BackgroundPosition().setYShift(UnitValue.createPointValue(30)).setXShift(UnitValue.createPointValue(50))).build();
        backgroundImageGenericTest("backgroundImageWithLinearGradientAndPositionAndRepeat", backgroundImage);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel = LogLevelConstants.WARN)
    })
    public void backgroundImageWithLinearGradientAndTransformTest() throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setLinearGradientBuilder(gradientBuilder).build();
        backgroundImageGenericTest("backgroundImageWithLinearGradientAndTransform", backgroundImage, Math.PI / 4);
    }

    @Test
    public void backgroundImageForText() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

        Assertions.assertTrue(backgroundImage.isBackgroundSpecified());

        String outFileName = DESTINATION_FOLDER + "backgroundImageForText.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageForText.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);

            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setBackgroundImage(backgroundImage);
            textElement.setFontSize(50);

            doc.add(new Paragraph(textElement));

        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithPercentWidth() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        String outFileName = DESTINATION_FOLDER + "backgroundImageWithPercentWidth.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageWithPercentWidth.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);
            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setFontSize(50);
            backgroundImage.getBackgroundSize().setBackgroundSizeToValues(UnitValue.createPercentValue(30), null);
            textElement.setBackgroundImage(backgroundImage);
            doc.add(new Paragraph(textElement));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithPercentHeight() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        String outFileName = DESTINATION_FOLDER + "backgroundImageWithPercentHeight.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageWithPercentHeight.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);
            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setFontSize(50);
            backgroundImage.getBackgroundSize().setBackgroundSizeToValues(null, UnitValue.createPercentValue(30));
            textElement.setBackgroundImage(backgroundImage);
            doc.add(new Paragraph(textElement));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithPercentHeightAndWidth() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        String outFileName = DESTINATION_FOLDER + "backgroundImageWithPercentHeightAndWidth.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageWithPercentHeightAndWidth.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);
            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setFontSize(50);
            backgroundImage.getBackgroundSize().setBackgroundSizeToValues(UnitValue.createPercentValue(20),
                    UnitValue.createPercentValue(20));
            textElement.setBackgroundImage(backgroundImage);
            doc.add(new Paragraph(textElement));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithPointWidth() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        String outFileName = DESTINATION_FOLDER + "backgroundImageWithPointWidth.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageWithPointWidth.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);
            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setFontSize(50);
            backgroundImage.getBackgroundSize().setBackgroundSizeToValues(UnitValue.createPointValue(15), null);
            textElement.setBackgroundImage(backgroundImage);
            doc.add(new Paragraph(textElement));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithPointHeight() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        String outFileName = DESTINATION_FOLDER + "backgroundImageWithPointHeight.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageWithPointHeight.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);
            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setFontSize(50);
            backgroundImage.getBackgroundSize().setBackgroundSizeToValues(null, UnitValue.createPointValue(20));
            textElement.setBackgroundImage(backgroundImage);
            doc.add(new Paragraph(textElement));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithPointHeightAndWidth() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        String outFileName = DESTINATION_FOLDER + "backgroundImageWithPointHeightAndWidth.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageWithPointHeightAndWidth.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);
            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setFontSize(50);
            backgroundImage.getBackgroundSize().setBackgroundSizeToValues(UnitValue.createPointValue(50),
                    UnitValue.createPointValue(100));
            textElement.setBackgroundImage(backgroundImage);
            doc.add(new Paragraph(textElement));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithLowWidthAndHeight() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        String outFileName = DESTINATION_FOLDER + "backgroundImageWithLowWidthAndHeight.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_backgroundImageWithLowWidthAndHeight.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            Document doc = new Document(pdfDocument);
            Text textElement = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ");
            textElement.setFontSize(50);
            backgroundImage.getBackgroundSize().setBackgroundSizeToValues(UnitValue.createPointValue(-1),
                    UnitValue.createPointValue(-1));
            textElement.setBackgroundImage(backgroundImage);
            doc.add(new Paragraph(textElement));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundImageWithoutRepeatXTest() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage
                .Builder().setImage(xObject).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT, BackgroundRepeatValue.REPEAT)).build();

        Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

        backgroundImageGenericTest("backgroundImageWithoutRepeatX", backgroundImage);
    }

    @Test
    public void backgroundImageWithoutRepeatYTest() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage
                .Builder().setImage(xObject).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.REPEAT, BackgroundRepeatValue.NO_REPEAT)).build();

        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
        Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

        backgroundImageGenericTest("backgroundImageWithoutRepeatY", backgroundImage);
    }

    @Test
    public void backgroundImageWithoutRepeatXYTest() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage
                .Builder().setImage(xObject).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT)).build();

        Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
        Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

        backgroundImageGenericTest("backgroundImageWithoutRepeatXY", backgroundImage);
    }

    @Test
    public void backgroundImageWithPositionTest() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT))
                .setBackgroundPosition(new BackgroundPosition().setXShift(new UnitValue(UnitValue.PERCENT, 80)).setYShift(new UnitValue(UnitValue.POINT, 55))).build();

        backgroundImageGenericTest("backgroundImageWithPosition", backgroundImage);
    }

    @Test
    public void backgroundImagesWithPositionTest() throws IOException, InterruptedException {
        List<BackgroundImage> images = Arrays.asList(
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "rock_texture.jpg")))
                        .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT))
                        .setBackgroundPosition(new BackgroundPosition().setXShift(new UnitValue(UnitValue.PERCENT, 100)).setYShift(new UnitValue(UnitValue.PERCENT, 100))).build(),
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg")))
                        .setBackgroundPosition(new BackgroundPosition().setXShift(new UnitValue(UnitValue.PERCENT, 0)).setYShift(new UnitValue(UnitValue.PERCENT, 100))).build());

        backgroundImageGenericTest("backgroundImagesWithPosition", images);
    }

    @Test
    public void backgroundXObject() throws IOException, InterruptedException {
        String filename = "backgroundXObject";

        String fileName = filename + ".pdf";
        String outFileName = DESTINATION_FOLDER + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(createFormXObject(pdfDocument, "itis.jpg")).build();

            Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
            Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

            backgroundXObjectGenericTest(filename, backgroundImage, pdfDocument);
        }
    }

    @Test
    public void backgroundXObjectWithoutRepeatX() throws IOException, InterruptedException {
        String filename = "backgroundXObjectWithoutRepeatX";

        String fileName = filename + ".pdf";
        String outFileName = DESTINATION_FOLDER + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage.Builder()
                    .setImage(createFormXObject(pdfDocument, "itis.jpg"))
                    .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT, BackgroundRepeatValue.REPEAT)).build();

            Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
            Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

            backgroundXObjectGenericTest(filename, backgroundImage, pdfDocument);
        }
    }

    @Test
    public void backgroundXObjectWithoutRepeatY() throws IOException, InterruptedException {
        String filename = "backgroundXObjectWithoutRepeatY";

        String fileName = filename + ".pdf";
        String outFileName = DESTINATION_FOLDER + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage
                    .Builder().setImage(createFormXObject(pdfDocument, "itis.jpg"))
                    .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.REPEAT, BackgroundRepeatValue.NO_REPEAT)).build();

            Assertions.assertEquals(BackgroundRepeatValue.REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
            Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

            backgroundXObjectGenericTest(filename, backgroundImage, pdfDocument);
        }
    }

    @Test
    public void backgroundXObjectWithoutRepeatXY() throws IOException, InterruptedException {
        String filename = "backgroundXObjectWithoutRepeatXY";

        String fileName = filename + ".pdf";
        String outFileName = DESTINATION_FOLDER + fileName;

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {
            BackgroundImage backgroundImage = new BackgroundImage
                    .Builder().setImage(createFormXObject(pdfDocument, "itis.jpg"))
                    .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT)).build();

            Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getXAxisRepeat());
            Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, backgroundImage.getRepeat().getYAxisRepeat());

            backgroundXObjectGenericTest(filename, backgroundImage, pdfDocument);
        }
    }

    @Test
    public void backgroundXObjectAndImageTest() throws IOException, InterruptedException {
        String filename = "backgroundXObjectAndImageTest";

        String fileName = filename + ".pdf";
        String outFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {

            Document doc = new Document(pdfDocument);

            String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                    "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                    "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                    "officia deserunt mollit anim id est laborum. ";


            Div div = new Div().add(new Paragraph(text + text + text));
            PdfImageXObject imageXObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
            BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(imageXObject).build();

            div.setBackgroundImage(backgroundImage);
            doc.add(div);


            BackgroundImage backgroundFormXObject = new BackgroundImage.Builder().setImage(createFormXObject(pdfDocument, "itis.jpg")).build();
            div = new Div().add(new Paragraph(text + text + text));
            div.setBackgroundImage(backgroundFormXObject);
            doc.add(div);

            pdfDocument.close();

            Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));

        }
    }

    @Test
    // There shall be rock texture picture at the left top corner with 30pt width and 60pt height
    public void backgroundXFormObjectWithBboxTest() throws IOException, InterruptedException {
        String filename = "backgroundComplicatedXFormObjectTest";

        String fileName = filename + ".pdf";
        String outFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)))) {

            Document doc = new Document(pdfDocument);

            String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                    "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                    "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                    "officia deserunt mollit anim id est laborum. ";

            BackgroundImage backgroundFormXObject = new BackgroundImage.Builder().setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT))
                    .setImage(createFormXObject(pdfDocument, "rock_texture.jpg")
                            .setBBox(new PdfArray(new Rectangle(70, -15, 50, 75)))).build();
            Div div = new Div().add(new Paragraph(text + text + text));
            div.setBackgroundImage(backgroundFormXObject);
            doc.add(div);

            pdfDocument.close();

            Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));

        }
    }

    @Test
    // There shall be default rock texture picture with 100pt width and height at the left top corner. BBox shall not do any differences.
    public void backgroundImageWithBboxTest() throws IOException, InterruptedException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "rock_texture.jpg"))
                .put(PdfName.BBox, new PdfArray(new Rectangle(70, -15, 500, 750)));
        BackgroundImage image = new BackgroundImage.Builder().setImage(xObject).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT)).build();

        backgroundImageGenericTest("backgroundImageWithBbox", image);
    }

    @Test
    public void backgroundImageWithLinearGradientAndNormalBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.NORMAL);
    }

    @Test
    public void backgroundImageWithLinearGradientAndMultiplyBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.MULTIPLY);
    }

    @Test
    public void backgroundImageWithLinearGradientAndScreenBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.SCREEN);
    }

    @Test
    public void backgroundImageWithLinearGradientAndOverlayBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.OVERLAY);
    }

    @Test
    public void backgroundImageWithLinearGradientAndDarkenBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.DARKEN);
    }

    @Test
    public void backgroundImageWithLinearGradientAndLightenBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.LIGHTEN);
    }

    @Test
    public void backgroundImageWithLinearGradientAndColorDodgeBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.COLOR_DODGE);
    }

    @Test
    public void backgroundImageWithLinearGradientAndColorBurnBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.COLOR_BURN);
    }

    @Test
    public void backgroundImageWithLinearGradientAndHardLightBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.HARD_LIGHT);
    }

    @Test
    public void backgroundImageWithLinearGradientAndSoftLightBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.SOFT_LIGHT);
    }

    @Test
    public void backgroundImageWithLinearGradientAndDifferenceBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.DIFFERENCE);
    }

    @Test
    public void backgroundImageWithLinearGradientAndExclusionBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.EXCLUSION);
    }

    @Test
    public void backgroundImageWithLinearGradientAndHueBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.HUE);
    }

    @Test
    public void backgroundImageWithLinearGradientAndSaturationBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.SATURATION);
    }

    @Test
    public void backgroundImageWithLinearGradientAndColorBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.COLOR);
    }

    @Test
    public void backgroundImageWithLinearGradientAndLuminosityBlendModeTest() throws IOException, InterruptedException {
        blendModeTest(BlendMode.LUMINOSITY);
    }

    @Test
    public void calculateImageSizeTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();

        float[] widthAndHeight = backgroundImage.calculateBackgroundImageSize(200f, 300f);

        Assertions.assertArrayEquals(new float[] {45f, 45f}, widthAndHeight, DELTA);
    }

    @Test
    public void calculateImageSizeWithCoverPropertyTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToCover();

        float[] widthAndHeight = backgroundImage.calculateBackgroundImageSize(200f, 300f);

        Assertions.assertArrayEquals(new float[] {300f, 300f}, widthAndHeight, DELTA);
    }

    @Test
    public void calculateSizeWithContainPropertyTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "pattern-grg-rrg-rgg.png"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToContain();

        float[] widthAndHeight = backgroundImage.calculateBackgroundImageSize(200f, 300f);

        Assertions.assertArrayEquals(new float[] {200f, 200.000015f}, widthAndHeight, DELTA);
    }

    @Test
    public void calculateSizeWithContainAndImageWeightMoreThatHeightTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToContain();

        float[] widthAndHeight = backgroundImage.calculateBackgroundImageSize(200f, 300f);

        Assertions.assertArrayEquals(new float[] {200f, 112.5f}, widthAndHeight, DELTA);
    }

    @Test
    public void calculateSizeWithCoverAndImageWeightMoreThatHeightTest() throws MalformedURLException {
        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setImage(xObject).build();
        backgroundImage.getBackgroundSize().setBackgroundSizeToCover();

        float[] widthAndHeight = backgroundImage.calculateBackgroundImageSize(200f, 300f);

        Assertions.assertArrayEquals(new float[] {533.3333f, 300f}, widthAndHeight, DELTA);
    }

    private void blendModeTest(BlendMode blendMode) throws IOException, InterruptedException {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.BLACK.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.WHITE.getColorValue()));
        BackgroundImage backgroundImage = new BackgroundImage.Builder().setLinearGradientBuilder(gradientBuilder).build();
        AbstractLinearGradientBuilder topGradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(GradientStrategy.TO_RIGHT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue()));
        BackgroundImage topBackgroundImage =
                new BackgroundImage.Builder().setLinearGradientBuilder(topGradientBuilder).setBackgroundBlendMode(blendMode).build();
        backgroundImageGenericTest("backgroundImageWithLinearGradientAndBlendMode_"
                + blendMode.getPdfRepresentation().getValue(), Arrays.asList(topBackgroundImage, backgroundImage));
    }

    private PdfFormXObject createFormXObject(PdfDocument pdfDocument, String pictureName) throws MalformedURLException {
        ImageData image = ImageDataFactory.create(SOURCE_FOLDER + pictureName);
        PdfFormXObject template = new PdfFormXObject(new Rectangle(image.getWidth(), image.getHeight()));
        PdfCanvas canvas = new PdfCanvas(template, pdfDocument);
        canvas.addImageFittedIntoRectangle(image, new Rectangle(0, 0, image.getWidth(), image.getHeight()), false).flush();
        canvas.release();
        template.flush();

        return template;
    }


    private void backgroundImageGenericTest(String filename, Object backgroundImage) throws IOException, InterruptedException {
        backgroundImageGenericTest(filename, backgroundImage, null);
    }

    private void backgroundImageGenericTest(String filename, Object backgroundImage, Double angle) throws IOException, InterruptedException {
        if (backgroundImage instanceof BackgroundImage) {
            Assertions.assertTrue(((BackgroundImage) backgroundImage).isBackgroundSpecified());
        } else {
            for (BackgroundImage image : (List<BackgroundImage>) backgroundImage) {
                Assertions.assertTrue((image).isBackgroundSpecified());
            }
        }

        String outFileName = DESTINATION_FOLDER + filename + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename + ".pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(FileUtil.getFileOutputStream(outFileName)));
        Document doc = new Document(pdfDocument);

        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                "officia deserunt mollit anim id est laborum. ";


        Div div = new Div().add(new Paragraph(text + text + text));

        if (angle != null) {
            div.setRotationAngle(angle.doubleValue());
        }
        if (backgroundImage instanceof BackgroundImage) {
            div.setBackgroundImage((BackgroundImage) backgroundImage);
        } else {
            div.setBackgroundImage((List<BackgroundImage>) backgroundImage);
        }
        doc.add(div);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    private void backgroundXObjectGenericTest(String filename, BackgroundImage backgroundImage, PdfDocument pdfDocument) throws IOException, InterruptedException {
        Assertions.assertTrue(backgroundImage.isBackgroundSpecified());

        String outFileName = DESTINATION_FOLDER + filename + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + filename + ".pdf";
        Document doc = new Document(pdfDocument);

        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
                "officia deserunt mollit anim id est laborum. ";


        Div div = new Div().add(new Paragraph(text + text + text));
        div.setBackgroundImage(backgroundImage);
        doc.add(div);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
}


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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.forms.fields.borders.FormBorderFactory;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class SigFieldTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/SigFieldTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/SigFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    // TODO DEVSIX-7787 Get rid of this logs
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT))
    public void basicSigFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicSigField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicSigField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField formSigField = new SigField("form SigField");
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formSigField.setDescription("form SigField");
            formSigField.setBorder(new SolidBorder(ColorConstants.YELLOW, 1));
            document.add(formSigField);

            document.add(new Paragraph(""));

            SigField flattenSigField = new SigField("flatten SigField");
            flattenSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenSigField.setDescription("flatten SigField");
            flattenSigField.setBorder(new SolidBorder(ColorConstants.PINK, 1));
            document.add(flattenSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = FormsLogMessageConstants.INPUT_FIELD_DOES_NOT_FIT))
    public void invisibleSigFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "invisibleSigField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_invisibleSigField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField formSigField = new SigField("form SigField");
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formSigField.setDescription("form SigField");
            formSigField.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(0));
            formSigField.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(0));
            formSigField.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(0));
            formSigField.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(0));
            formSigField.setWidth(0);
            document.add(formSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void customizedSigFieldTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "customizedSigField.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_customizedSigField.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField formSigField = new SigField("form SigField");
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formSigField.setSignedBy("signedBy").setLocationCaption("Location capt: ").setLocation("location")
                    .setReasonCaption("Reason capt: ").setReason("reason")
                    .setDescription("Description text that allows as to port that test and compare files by content.");
            formSigField.setFontColor(ColorConstants.BLUE);
            formSigField.setBackgroundColor(ColorConstants.YELLOW);
            formSigField.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            formSigField.setHeight(100);
            document.add(formSigField);

            document.add(new Paragraph(""));

            SigField flattenSigField = new SigField("flatten SigField");
            flattenSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenSigField.setSignedBy("signedBy").setLocationCaption("Location capt: ").setLocation("location")
                    .setReasonCaption("Reason capt: ").setReason("reason")
                    .setDescription("Description text that allows as to port that test and compare files by content.");
            flattenSigField.setFontColor(ColorConstants.BLUE);
            flattenSigField.setBackgroundColor(ColorConstants.YELLOW);
            flattenSigField.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            flattenSigField.setHeight(100);
            document.add(flattenSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    // TODO DEVSIX-7787 Get rid of this logs
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT))
    public void signatureFieldVerticalAlignmentTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "signatureFieldVerticalAlignment.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signatureFieldVerticalAlignment.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField bottomSigField = new SigField("bottomSigField");
            bottomSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            bottomSigField.setDescription("description on bottom");
            bottomSigField.setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.BOTTOM);
            bottomSigField.setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            bottomSigField.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            bottomSigField.setBorder(new SolidBorder(ColorConstants.YELLOW, 3));
            bottomSigField.setFontSize(15);
            document.add(bottomSigField);

            SigField middleSigField = new SigField("middleSigField");
            middleSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            middleSigField.setDescription("description on the middle");
            middleSigField.setRenderingMode(SigField.RenderingMode.NAME_AND_DESCRIPTION);
            middleSigField.setSignedBy("Name");
            middleSigField.setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.MIDDLE);
            middleSigField.setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            middleSigField.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            middleSigField.setBorder(new SolidBorder(ColorConstants.BLUE, 3));
            middleSigField.setFontSize(15);
            document.add(middleSigField);

            SigField topSigField = new SigField("topSigField");
            topSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            topSigField.setDescription("description on top");
            topSigField.setRenderingMode(SigField.RenderingMode.GRAPHIC_AND_DESCRIPTION);
            topSigField.setSignatureGraphic(ImageDataFactory.create(SOURCE_FOLDER + "1.png"));
            topSigField.setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.TOP);
            topSigField.setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            topSigField.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
            topSigField.setBorder(new SolidBorder(ColorConstants.PINK, 3));
            topSigField.setFontSize(15);
            document.add(topSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void sigFieldWithGraphicAndDescriptionModeTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "sigFieldWithGraphicAndDescriptionMode.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_sigFieldWithGraphicAndDescriptionMode.pdf";
        String imagePath = SOURCE_FOLDER + "1.png";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField formSigField = new SigField("SigField");
            ImageData image = ImageDataFactory.create(imagePath);
            formSigField.setSignatureGraphic(image);
            formSigField.setRenderingMode(SigField.RenderingMode.GRAPHIC_AND_DESCRIPTION);
            formSigField.setDescription("description");
            formSigField.setFontColor(ColorConstants.BLUE);
            formSigField.setFontSize(20);
            formSigField.setBackgroundColor(ColorConstants.YELLOW);
            formSigField.setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            document.add(formSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void sigFieldWithGraphicModeTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "sigFieldWithGraphicMode.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_sigFieldWithGraphicMode.pdf";
        String imagePath = SOURCE_FOLDER + "1.png";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField formSigField = new SigField("SigField");
            ImageData image = ImageDataFactory.create(imagePath);
            formSigField.setSignatureGraphic(image);
            formSigField.setRenderingMode(SigField.RenderingMode.GRAPHIC);
            formSigField.setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            document.add(formSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void sigFieldWithNameAndDescriptionModeHorizontalTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "sigFieldWithNameAndDescriptionModeHorizontal.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_sigFieldWithNameAndDescriptionModeHorizontal.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField formSigField = new SigField("SigField");
            formSigField.setProperty(Property.WIDTH, UnitValue.createPointValue(250));
            formSigField.setProperty(Property.HEIGHT, UnitValue.createPointValue(150));
            formSigField.setRenderingMode(SigField.RenderingMode.NAME_AND_DESCRIPTION);
            formSigField.setDescription("description");
            formSigField.setSignedBy("name");
            formSigField.setFontSize(20);
            formSigField.setFontColor(ColorConstants.BLUE);
            formSigField.setBackgroundColor(ColorConstants.YELLOW);
            formSigField.setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            document.add(formSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void sigFieldWithNameAndDescriptionModeVerticalTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "sigFieldWithNameAndDescriptionModeVertical.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_sigFieldWithNameAndDescriptionModeVertical.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField formSigField = new SigField("SigField");
            formSigField.setProperty(Property.WIDTH, UnitValue.createPointValue(150));
            formSigField.setProperty(Property.HEIGHT, UnitValue.createPointValue(250));
            formSigField.setRenderingMode(SigField.RenderingMode.NAME_AND_DESCRIPTION);
            formSigField.setDescription("description");
            formSigField.setSignedBy("name");
            formSigField.setFontSize(20);
            formSigField.setFontColor(ColorConstants.BLUE);
            formSigField.setBackgroundColor(ColorConstants.YELLOW);
            formSigField.setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            document.add(formSigField);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void borderBoxesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "borderBoxes.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_borderBoxes.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // BORDER_BOX
            SigField interactiveSigField = new SigField("interactiveSigField")
                    .setBorder(new SolidBorder(ColorConstants.PINK, 10));
            interactiveSigField.setWidth(200).setHeight(100);
            interactiveSigField.setInteractive(true);
            interactiveSigField.setDescription("interactive border box");
            interactiveSigField.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(interactiveSigField);

            // CONTENT_BOX
            SigField interactiveSigField2 = new SigField("interactiveSigField2")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10));
            interactiveSigField2.setWidth(200).setHeight(100);
            interactiveSigField2.setInteractive(true);
            interactiveSigField2.setDescription("interactive content box");
            interactiveSigField2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(interactiveSigField2);

            // BORDER_BOX
            SigField flattenSigField = new SigField("flattenSigField")
                    .setBorder(new SolidBorder(ColorConstants.PINK, 10));
            flattenSigField.setWidth(200).setHeight(100);
            flattenSigField.setInteractive(false);
            flattenSigField.setDescription("flatten border box");
            flattenSigField.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            document.add(flattenSigField);

            // CONTENT_BOX
            SigField flattenSigField2 = new SigField("flattenSigField2")
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10));
            flattenSigField2.setWidth(200).setHeight(100);
            flattenSigField2.setInteractive(false);
            flattenSigField2.setDescription("flatten content box");
            flattenSigField2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            document.add(flattenSigField2);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void borderTypesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "borderTypes.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_borderTypes.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            // DASHED
            SigField sigField = new SigField("SigField")
                    .setBorder(new DashedBorder(ColorConstants.PINK, 10))
                    .setBackgroundColor(ColorConstants.YELLOW);
            sigField.setSize(100);
            sigField.setInteractive(true);
            sigField.setDescription("dashed");
            document.add(sigField);

            PdfDictionary bs = new PdfDictionary();
            // UNDERLINE
            bs.put(PdfName.S, PdfAnnotation.STYLE_UNDERLINE);
            SigField sigField2 = new SigField("SigField2")
                    .setBorder(FormBorderFactory.getBorder(bs, 10f, ColorConstants.YELLOW,
                            ColorConstants.ORANGE))
                    .setBackgroundColor(ColorConstants.PINK);
            sigField2.setSize(100);
            sigField2.setInteractive(true);
            sigField2.setDescription("underline");
            document.add(sigField2);

            // INSET
            bs.put(PdfName.S, PdfAnnotation.STYLE_INSET);
            SigField sigField3 = new SigField("SigField3")
                    .setBorder(FormBorderFactory.getBorder(bs, 10f, ColorConstants.PINK,
                            ColorConstants.RED))
                    .setBackgroundColor(ColorConstants.YELLOW);
            sigField3.setSize(100);
            sigField3.setInteractive(true);
            sigField3.setDescription("inset");
            document.add(sigField3);

            // BEVELLED
            bs.put(PdfName.S, PdfAnnotation.STYLE_BEVELED);
            SigField sigField4 = new SigField("SigField4")
                    .setBorder(FormBorderFactory.getBorder(bs, 10f, ColorConstants.YELLOW,
                            ColorConstants.ORANGE))
                    .setBackgroundColor(ColorConstants.PINK);
            sigField4.setSize(100);
            sigField4.setInteractive(true);
            sigField4.setDescription("bevelled");
            document.add(sigField4);

            PdfFormCreator.getAcroForm(document.getPdfDocument(), false).flattenFields();
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void signatureOnRotatedPagesTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "signatureOnRotatedPages.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signatureOnRotatedPages.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            document.getPdfDocument().addNewPage().setRotation(90);
            document.getPdfDocument().addNewPage().setRotation(180);
            document.getPdfDocument().addNewPage().setRotation(270);

            PdfAcroForm form = PdfFormCreator.getAcroForm(document.getPdfDocument(), true);

            PdfSignatureFormField field1 = new SignatureFormFieldBuilder(document.getPdfDocument(), "sigField1")
                    .setWidgetRectangle(new Rectangle(50, 50, 400, 200)).setPage(1).createSignature();
            PdfSignatureFormField field2 = new SignatureFormFieldBuilder(document.getPdfDocument(), "sigField2")
                    .setWidgetRectangle(new Rectangle(50, 50, 400, 200)).setPage(2).createSignature();
            PdfSignatureFormField field3 = new SignatureFormFieldBuilder(document.getPdfDocument(), "sigField3")
                    .setWidgetRectangle(new Rectangle(50, 50, 400, 200)).setPage(3).createSignature();

            SigField sigField1 = new SigField("sigField1");
            sigField1.setDescription("rotation 90 rotation 90 rotation 90 rotation 90 rotation 90");
            sigField1.setFontSize(25);
            field1.getFirstFormAnnotation().setFormFieldElement(sigField1).setBorderColor(ColorConstants.GREEN);

            SigField sigField2 = new SigField("sigField2");
            sigField2.setDescription("rotation 180 rotation 180 rotation 180 rotation 180 rotation 180");
            sigField2.setFontSize(25);
            field2.getFirstFormAnnotation().setFormFieldElement(sigField2).setBorderColor(ColorConstants.GREEN);

            SigField sigField3 = new SigField("sigField3");
            sigField3.setDescription("rotation 270 rotation 270 rotation 270 rotation 270 rotation 270");
            sigField3.setFontSize(25);
            field3.getFirstFormAnnotation().setFormFieldElement(sigField3).setBorderColor(ColorConstants.GREEN);

            form.addField(field1);
            form.addField(field2);
            form.addField(field3);
            form.flattenFields();
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
    
    @Test
    public void wrongRenderingModeTest() {
        try (Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            SigField formSigField = new SigField("SigField");
            formSigField.setRenderingMode(SigField.RenderingMode.GRAPHIC_AND_DESCRIPTION);
            Assert.assertThrows(IllegalStateException.class, () -> document.add(formSigField));

            SigField formSigField2 = new SigField("SigField2");
            formSigField2.setRenderingMode(SigField.RenderingMode.GRAPHIC);
            Assert.assertThrows(IllegalStateException.class, () -> document.add(formSigField2));
        }
    }

    @Test
    public void backgroundTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "signatureFieldBackground.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signatureFieldBackground.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SigField field1 = new SigField("field1");
            field1.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field1.setDescription("scale -1").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setImage(ImageDataFactory.create(SOURCE_FOLDER + "1.png")).setImageScale(-1)
                    .setBorder(new SolidBorder(ColorConstants.RED, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            document.add(field1);

            SigField field2 = new SigField("field2");
            field2.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field2.setDescription("scale 0").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setImage(ImageDataFactory.create(SOURCE_FOLDER + "1.png")).setImageScale(0)
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            document.add(field2);

            SigField field3 = new SigField("field3");
            field3.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field3.setDescription("scale 0.5").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setImage(ImageDataFactory.create(SOURCE_FOLDER + "1.png")).setImageScale(0.5f)
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            document.add(field3);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
